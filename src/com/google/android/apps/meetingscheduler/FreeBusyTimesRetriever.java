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

import android.content.Context;
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
import java.util.Date;
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
   * Constructor.
   */
  public FreeBusyTimesRetriever() {
  }

  @Override
  public Map<Attendee, List<Busy>> getBusyTimes(List<Attendee> attendees, Date startDate,
      Context context) {
    Map<Attendee, List<Busy>> result = new HashMap<Attendee, List<Busy>>();
    Map<String, Attendee> batchIds = new HashMap<String, Attendee>();
    CalendarService service = CalendarServiceManager.getInstance().getService();
    FreeBusyList batchRequest = createBatchRequest(attendees, batchIds);
    CalendarUrl url = createBatchUrl(startDate, Settings.getInstance().getTimeSpan());

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
   * Create the URL to which to send the batch request.
   * 
   * @param startDate The date from which the request start.
   * @param timeSpan The number of days for which to request.
   * @return The URL to the batch request.
   */
  private CalendarUrl createBatchUrl(Date startDate, int timeSpan) {
    CalendarUrl url = UrlFactory.getFreeBusyBatchFeedUrl();

    url.startMin = getDateTime(startDate, 0);
    url.startMax = getDateTime(startDate, timeSpan);
    return url;
  }

  /**
   * Create the batch request to send to the Calendar API.
   * 
   * @param attendees The attendees for whom to request the busy times.
   * @param batchIds The map to store the batch IDs corresponding to the
   *          attendees.
   * @return The batch request to send to the Calendar API.
   */
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

  /**
   * Create a single batch request for an attendee.
   * 
   * @param email The attendee for which to create the batch request.
   * @return The batch request for the attendee.
   */
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
   * @param startDate The date from which to compute the DateTime.
   * @param daysToAdd The number of days to add to the result.
   * 
   * @return The new DateTime object initialized at the current day +
   *         {@code daysToAdd}.
   */
  private DateTime getDateTime(Date startDate, int daysToAdd) {
    Calendar calendar = new GregorianCalendar(CalendarServiceManager.getInstance().getTimeZone());

    calendar.setTime(startDate);
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
