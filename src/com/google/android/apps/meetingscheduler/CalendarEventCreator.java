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

import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.util.DateTime;
import com.google.api.data.calendar.v2.CalendarService;
import com.google.api.data.calendar.v2.model.Event;
import com.google.api.data.gdata.v2.model.Value;
import com.google.api.data.gdata.v2.model.When;
import com.google.api.data.gdata.v2.model.Where;
import com.google.api.data.gdata.v2.model.Who;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * TODO(alainv) Write type description
 * 
 * @since 2.2
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class CalendarEventCreator implements EventCreator {

  @Override
  public void createEvent(String title, String where, String description,
      boolean sendEventNotifications, Date start, Date end, List<Attendee> attendees)
      throws UnknownError, IOException {
    CalendarService cs = CalendarServiceManager.getInstance().getService();
    String feedUrl = "https://www.google.com/calendar/feeds/default/private/full";
    Event newEvent = new Event();

    newEvent.title = title;
    newEvent.content = description;

    Where newWhere = new Where();
    newWhere.rel = Where.REL_EVENT;
    newWhere.description = where;
    newWhere.label = where;
    newEvent.where = new ArrayList<Where>();
    newEvent.where.add(newWhere);

    When when = new When();
    when.startTime = new DateTime(start, CalendarServiceManager.getInstance().getTimeZone());
    when.endTime = new DateTime(end, CalendarServiceManager.getInstance().getTimeZone());

    newEvent.when = new ArrayList<When>();
    newEvent.when.add(when);

    newEvent.sendEventNotifications = new Value();
    newEvent.sendEventNotifications.value = String.valueOf(sendEventNotifications).toLowerCase();

    newEvent.who = new ArrayList<Who>();
    for (Attendee attendee : attendees) {
      Who who = new Who();
      who.email = attendee.email;
      newEvent.who.add(who);
    }

    Log.d(MeetingSchedulerConstants.TAG, "Creating event: " + when.startTime.toStringRfc3339()
        + " - " + when.endTime.toStringRfc3339() + " (" + attendees.size() + " attendee(s))");
    if (cs.executeInsert(newEvent, new GoogleUrl(feedUrl)) == null)
      throw new UnknownError();
  }

}
