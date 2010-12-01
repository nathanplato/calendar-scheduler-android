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
import com.google.api.data.calendar.v2.model.FreeBusyList;
import com.google.api.data.gdata.v2.model.Link;
import com.google.api.data.gdata.v2.model.batch.BatchOperation;

import java.io.IOException;
import java.util.ArrayList;
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

  private static final String BASE_FREEBUSY_ID = "http://www.google.com/calendar/feeds/default/freebusy/";

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
    Map<Attendee, List<Busy>> result = new HashMap<Attendee, List<Busy>>();
    Map<String, Attendee> batchIds = new HashMap<String, Attendee>();
    CalendarService service = getService();
    FreeBusyList batchRequest = createBatchRequest(attendees, batchIds);
    CalendarUrl url = createBatchUrl(settings.timeSpan);

    try {
      FreeBusyList freeBusyFeed = service.executeBatch(batchRequest, url);

      for (FreeBusy entry : freeBusyFeed.entries) {
        Attendee attendee = batchIds.get(entry.batchId);

        if (attendee != null) {
          List<Busy> busyTimes = entry.busyTimes;

          if (busyTimes == null)
            busyTimes = new ArrayList<Busy>();
          result.put(attendee, busyTimes);
        } else
          Log.e(MeetingSchedulerConstants.TAG, "Unknown batch ID: " + entry.batchId);
      }
    } catch (IOException e) {
      Log.e(MeetingSchedulerConstants.TAG,
          "IOException occured while retrieving freebusy information: " + e.getMessage());
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

  private CalendarUrl createBatchUrl(int timeSpan) {
    CalendarUrl url = UrlFactory.getFreeBusyBatchFeedUrl();

    url.startMin = getDateTime(1);
    url.startMax = getDateTime(timeSpan);
    return url;
  }

  private FreeBusyList createBatchRequest(List<Attendee> attendees, Map<String, Attendee> batchIds) {
    FreeBusyList result = new FreeBusyList();

    result.batchOperation = new BatchOperation();
    result.batchOperation.type = BatchOperation.OPERATION_QUERY;
    result.entries = new ArrayList<FreeBusy>();

    for (Attendee attendee : attendees) {
      result.entries.add(createSingleBatchRequest(attendee.email));
      batchIds.put(attendee.email, attendee);
    }

    return result;
  }

  private FreeBusy createSingleBatchRequest(String email) {
    FreeBusy result = new FreeBusy();
    Link link = new Link();

    link.rel = "self";
    link.href = UrlFactory.getUserFreeBusyFeedUrl(email).toString();
    result.links = new ArrayList<Link>();
    result.links.add(link);

    result.batchId = email;
    result.id = BASE_FREEBUSY_ID + email;

    return result;
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
