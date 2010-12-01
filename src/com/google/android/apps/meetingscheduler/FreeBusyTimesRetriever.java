/*
 * Copyright (c) 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.android.apps.meetingscheduler;

import android.util.Log;

import com.google.api.client.util.DateTime;
import com.google.api.data.calendar.v2.CalendarService;
import com.google.api.data.calendar.v2.CalendarUrl;
import com.google.api.data.calendar.v2.UrlFactory;
import com.google.api.data.calendar.v2.model.Busy;
import com.google.api.data.calendar.v2.model.FreeBusy;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Retrieves the busy times from the Google Calendar API.
 * 
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class FreeBusyTimesRetriever implements BusyTimesRetriever {

  /**
   * The ClientLogin authentication to use when querying the Google Calendar
   * API.
   */
  private String authToken;

  /**
   * Constructor.
   * 
   * @param authToken The ClientLogin authentication to use when querying the
   *          Google Calendar API.
   */
  public FreeBusyTimesRetriever(String authToken) {
    this.authToken = authToken;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.google.android.apps.meetingscheduler.BusyTimesRetriever#getBusyTimes
   * (java.util.List, com.google.android.apps.meetingscheduler.Settings)
   */
  @Override
  public Map<Attendee, List<Busy>> getBusyTimes(List<Attendee> attendees, Settings settings) {
    // TODO Auto-generated method stub
    Map<Attendee, List<Busy>> result = new HashMap<Attendee, List<Busy>>();
    CalendarService service = getService();
    DateTime startMin = getDateTime(1);
    DateTime startMax = getDateTime(settings.timeSpan);

    for (Attendee attendee : attendees) {
      List<Busy> busyTimes = getBusyTimes(service, attendee, startMin, startMax);

      if (busyTimes != null)
        result.put(attendee, busyTimes);
    }

    return result;
  }

  /**
   * Initialize a new CalendarService.
   * 
   * @return An initialized CalendarService.
   */
  private CalendarService getService() {
    CalendarService result = new CalendarService(MeetingSchedulerConstants.TAG + " "
        + MeetingSchedulerConstants.VERSION);

    result.setClientLoginAuthenticationToken(authToken);

    return result;
  }

  /**
   * Retrieve the busy times for the given {@code attendee} between
   * {@code startMin} and {@code startMax}.
   * 
   * TODO(alainv): use batch request instead of multiple single requests.
   * 
   * @param service The service to use to query the Calendar API.
   * @param attendee The attendee for which to query the busy times.
   * @param startMin The date from which to query the busy times.
   * @param startMax The date until which to query the busy times.
   * @return
   */
  private List<Busy> getBusyTimes(CalendarService service, Attendee attendee, DateTime startMin,
      DateTime startMax) {
    CalendarUrl url = UrlFactory.getUserFreeBusyFeedUrl(attendee.email);

    url.startMin = startMin;
    url.startMax = startMax;
    try {
      FreeBusy freeBusy = service.executeGet(url, null, false, FreeBusy.class);

      return freeBusy.busyTimes;
    } catch (IOException e) {
      Log.e(MeetingSchedulerConstants.TAG,
          "IOException occured while retrieving freebusy information for " + attendee.email + ": "
              + e.getMessage());
    }

    return null;
  }

  /**
   * Create a new DateTime object initialized at the current day +
   * {@code daysToAdd}.
   * 
   * @param daysToAdd The number of days to add to the result.
   * @return The new DateTime object initialized at the current day +
   *         {@code daysToAdd}.
   */
  private DateTime getDateTime(int daysToAdd) {
    Calendar calendar = GregorianCalendar.getInstance();

    // Clear time component.
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.clear(Calendar.HOUR);
    calendar.clear(Calendar.MINUTE);
    calendar.clear(Calendar.SECOND);
    calendar.clear(Calendar.MILLISECOND);

    calendar.add(Calendar.DAY_OF_YEAR, daysToAdd);

    return new DateTime(calendar.getTime());
  }
}
