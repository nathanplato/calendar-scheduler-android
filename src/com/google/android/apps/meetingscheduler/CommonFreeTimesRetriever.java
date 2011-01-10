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

import com.google.api.client.util.DateTime;
import com.google.api.data.calendar.v2.model.Busy;
import com.google.api.data.gdata.v2.model.When;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

/**
 * Compute the common free times from the busy times fetched from the
 * BusyTimesRetriever.
 * 
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class CommonFreeTimesRetriever implements EventTimeRetriever {

  /**
   * The BusyTimesRetriever from which to retrieve the busy time.
   */
  private BusyTimesRetriever busyTimeRetriever;

  /**
   * Default constructor. Use default BusyTimesRetriever.
   */
  public CommonFreeTimesRetriever() {
    // TODO(alainv): Set default busyTimeRetriever.
  }

  /**
   * Constructor.
   * 
   * @param busyTimeRetriever The BusyTimesRetriever to use for fetching busy
   *          times.
   */
  public CommonFreeTimesRetriever(BusyTimesRetriever busyTimeRetriever) {
    this.busyTimeRetriever = busyTimeRetriever;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.android.apps.meetingscheduler.EventTimeRetriever#
   * getAvailableMeetingTime(java.util.List,
   * com.google.android.apps.meetingscheduler.Settings)
   */
  @Override
  public List<AvailableMeetingTime> getAvailableMeetingTime(List<Attendee> attendees,
      Date startDate, Context context) {
    Map<Attendee, List<Busy>> busyTimes = busyTimeRetriever.getBusyTimes(attendees, startDate,
        context);
    Settings settings = Settings.getInstance();
    List<Busy> listBusyTimes = cleanBusyTimes(busyTimes, startDate, settings);
    List<AvailableMeetingTime> result = findAvailableMeetings(listBusyTimes);

    filterMeetingLength(result, settings.getMeetingLength());
    splitAvailableMeetings(result);

    addAttendees(result, attendees);

    return result;
  }

  /**
   * Add weekends and non-working hours as busy times if requested and merge all
   * the busy times.
   * @param busyTimes The busy times to clean
   * @param startDate The date from which to start cleaning
   * @param settings The setting to use for cleaning the busy times
   * 
   * @return A list of cleaned busy times.
   */
  private List<Busy> cleanBusyTimes(Map<Attendee, List<Busy>> busyTimes, Date startDate,
      Settings settings) {
    List<Busy> listBusyTimes = new ArrayList<Busy>();

    for (List<Busy> busy : busyTimes.values()) {
      listBusyTimes.addAll(busy);
    }

    if (settings.doSkipWeekends()) {
      addWeekends(listBusyTimes, startDate, settings.getTimeSpan());
    }
    if (settings.doUseWorkingHours()) {
      addWorkingHours(listBusyTimes, startDate, settings.getTimeSpan(),
          DateUtils.getCalendar(settings.getWorkingHoursStart()),
          DateUtils.getCalendar(settings.getWorkingHoursEnd()));
    }

    mergeBusyTimes(listBusyTimes);
    return listBusyTimes;
  }

  /**
   * Add weekends as busy times to the list of busy times.
   * 
   * @param busyTimes The list of busy times to which to add the weekends
   * @param startDate The start date from which
   * @param timeSpan
   */
  private void addWeekends(List<Busy> busyTimes, Date startDate, int timeSpan) {
    Calendar calendar = new GregorianCalendar(CalendarServiceManager.getInstance().getTimeZone());

    calendar.setTime(startDate);

    for (int i = 0; i < timeSpan; ++i) {
      int day = calendar.get(Calendar.DAY_OF_WEEK);

      if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
        Busy toAdd = new Busy();

        toAdd.when = new When();
        DateUtils.setTime(calendar, 0, 0, 0, 0);
        toAdd.when.startTime = new DateTime(calendar.getTime());
        DateUtils.setTime(calendar, 23, 59, 59, 999);
        toAdd.when.endTime = new DateTime(calendar.getTime());
        busyTimes.add(toAdd);
      }
      calendar.add(Calendar.DAY_OF_YEAR, 1);
    }
  }

  /**
   * Add non-working hours as busy times to the list of busy times.
   * 
   * @param busyTimes The busy times to which to add the non-working hours
   * @param startDate The start date from which to start adding busy times
   * @param timeSpan The number of day for which to add busy times
   * @param min The starting working hour
   * @param max The ending working hour
   */
  private void addWorkingHours(List<Busy> busyTimes, Date startDate, int timeSpan, Calendar min,
      Calendar max) {
    Calendar current = new GregorianCalendar(CalendarServiceManager.getInstance().getTimeZone());

    current.setTime(startDate);

    DateUtils.setTime(current, min);

    if (current.getTime().after(startDate)) {
      Busy toAdd = new Busy();

      toAdd.when = new When();
      toAdd.when.startTime = new DateTime(startDate.getTime());
      toAdd.when.endTime = new DateTime(current.getTime());
      busyTimes.add(toAdd);
    }

    for (int i = 0; i < timeSpan; ++i) {
      DateUtils.setTime(current, max);
      Busy toAdd = new Busy();

      toAdd.when = new When();
      toAdd.when.startTime = new DateTime(current.getTime());
      current.add(Calendar.DAY_OF_YEAR, 1);
      DateUtils.setTime(current, min);
      toAdd.when.endTime = new DateTime(current.getTime());
      busyTimes.add(toAdd);
    }
  }

  /**
   * Merge the overlapping busy times, e.g 9:00-10:00 and 10:00-12:00 will
   * become one 9:00-12:00 busy time.
   * 
   * @param busyTimes The busy times to merge.
   */
  private void mergeBusyTimes(List<Busy> busyTimes) {
    DateUtils.sortBusyTime(busyTimes);

    // Merge every busy slots.
    for (int i = 0; i < busyTimes.size(); ++i) {
      Busy current = busyTimes.get(i);

      for (int j = i + 1; j < busyTimes.size();) {
        Busy next = busyTimes.get(j);

        if (DateUtils.compareDateTime(current.when.endTime, next.when.startTime) >= 0) {
          if (DateUtils.compareDateTime(current.when.endTime, next.when.endTime) < 0)
            current.when.endTime = next.when.endTime;
          busyTimes.remove(j);
        } else
          break;
      }
    }
  }

  /**
   * Find the available meetings from the list of busy times. The busy times are
   * considered to be on the same day, sorted and merged.
   * 
   * @param busyTimes The busy times from which to compute the available meeting
   * @return The available meetings time from 00:00 to 23:59 of the same day.
   */
  private List<AvailableMeetingTime> findAvailableMeetings(List<Busy> busyTimes) {
    List<AvailableMeetingTime> result = new ArrayList<AvailableMeetingTime>();

    for (int i = 0; i < busyTimes.size() - 1;) {
      AvailableMeetingTime tmp = new AvailableMeetingTime();

      tmp.start = new Date(busyTimes.get(i).when.endTime.value);
      tmp.end = new Date(busyTimes.get(++i).when.startTime.value);
      result.add(tmp);
    }

    return result;
  }

  /**
   * Split multiple day-meeting times into multiple one-day meeting times.
   * 
   * @param busyTimes The busy times to clean.
   */
  private void splitAvailableMeetings(List<AvailableMeetingTime> meetings) {
    for (int i = 0; i < meetings.size();) {
      AvailableMeetingTime current = meetings.get(i);

      if (!DateUtils.isSameDay(current.start, current.end)) {
        List<AvailableMeetingTime> splitted = splitMeetingTimes(current.start, current.end);

        meetings.remove(i);
        meetings.addAll(i, splitted);
        i += splitted.size();
      } else
        ++i;
    }
  }

  /**
   * Add the list of attendees to the available meetings.
   * 
   * @param meetings The meetings to which to add the attendees.
   * @param attendees The attendees to add to the meetings.
   */
  private void addAttendees(List<AvailableMeetingTime> meetings, List<Attendee> attendees) {
    for (AvailableMeetingTime meeting : meetings) {
      meeting.attendees = attendees;
    }
  }

  /**
   * Split a busy time into a set of busy time, each for one day.
   * 
   * @param startDate
   * @param endDate
   * @return
   */
  private List<AvailableMeetingTime> splitMeetingTimes(Date startDate, Date endDate) {
    List<AvailableMeetingTime> result = new ArrayList<AvailableMeetingTime>();
    Calendar currentDay = new GregorianCalendar(CalendarServiceManager.getInstance().getTimeZone());

    currentDay.setTime(startDate);
    DateUtils.setTime(currentDay, 23, 59, 59, 999);

    result.add(new AvailableMeetingTime(startDate, currentDay.getTime()));

    while (true) {
      DateUtils.setTime(currentDay, 0, 0, 0, 0);
      currentDay.add(Calendar.DAY_OF_YEAR, 1);
      Date currentStart = currentDay.getTime();

      if (DateUtils.isSameDay(currentStart, endDate))
        break;

      DateUtils.setTime(currentDay, 23, 59, 59, 999);
      result.add(new AvailableMeetingTime(currentStart, currentDay.getTime()));
    }

    result.add(new AvailableMeetingTime(currentDay.getTime(), endDate));

    return result;
  }

  /**
   * Filter the meetings which length are less than {@code length}.
   * 
   * @param meetings The meetings to filter.
   * @param length The minimum length of the meetings.
   */
  private void filterMeetingLength(List<AvailableMeetingTime> meetings, int length) {
    for (int i = 0; i < meetings.size();) {
      int meetingLength = getMeetingLength(meetings.get(i));

      if (meetingLength >= length)
        ++i;
      else
        meetings.remove(i);
    }
  }

  /**
   * Compute the length of the {@code meeting} in minutes.
   * 
   * @param meeting The meeting from which to compute the length.
   * @return The length of the meeting in minutes.
   */
  private int getMeetingLength(AvailableMeetingTime meeting) {
    long difference = meeting.end.getTime() - meeting.start.getTime();

    return (int) difference / 60000;
  }

}
