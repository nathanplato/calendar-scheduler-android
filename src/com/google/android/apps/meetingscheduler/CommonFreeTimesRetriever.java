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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
  public List<AvailableMeetingTime> getAvailableMeetingTime(
      List<Attendee> attendees, Date startDate, Context context) {
    Map<Attendee, List<Busy>> busyTimes = busyTimeRetriever.getBusyTimes(attendees, startDate, context);
    Map<Date, List<Busy>> sortedBusyTimes = filterByDate(busyTimes);
    List<AvailableMeetingTime> result = new ArrayList<AvailableMeetingTime>();

    Settings settings = Settings.getInstance(context);
    addMissingDays(sortedBusyTimes, startDate, settings.getTimeSpan());
    for (Map.Entry<Date, List<Busy>> busyTime : sortedBusyTimes.entrySet()) {
      List<AvailableMeetingTime> availableMeetings;

      mergeBusyTimes(busyTime.getValue());
      availableMeetings = findAvailableMeetings(busyTime.getValue(),
          new DateTime(busyTime.getKey()));
      filterAvailableMeetings(availableMeetings, settings.doUseWorkingHours(),
          getDate(new DateTime(busyTime.getKey().getTime()), settings.getWorkingHoursStart()),
          getDate(new DateTime(busyTime.getKey().getTime()), settings.getWorkingHoursEnd()),
          settings.getMeetingLength());
      addAttendees(availableMeetings, attendees);

      result.addAll(availableMeetings);
    }

    return result;
  }

  /**
   * Separate busy times from the same day.
   * 
   * @param busyTimes The busy times to sort.
   * @return The separated busy times.
   */
  private Map<Date, List<Busy>> filterByDate(Map<Attendee, List<Busy>> busyTimes) {
    Map<Date, List<Busy>> result;
    result = new HashMap<Date, List<Busy>>();
    for (List<Busy> busyTime : busyTimes.values()) {
      for (Busy busy : busyTime) {
        Date day = getDate(busy.when.startTime, 0);

        List<Busy> current = result.get(day);

        if (current == null) {
          current = new ArrayList<Busy>();
          result.put(day, current);
        }
        current.add(busy);
      }
    }
    return result;
  }

  /**
   * Add days were every attendees are available.
   * 
   * @param busyTimes The list of busy times to which to add the days.
   * @param timeSpan
   */
  private void addMissingDays(Map<Date, List<Busy>> busyTimes, Date startDate, int timeSpan) {
    Calendar calendar = new GregorianCalendar();

    calendar.setTime(startDate);
    setTime(calendar, 0, 0);
    for (int i = 0; i < timeSpan; ++i) {
      if (!busyTimes.containsKey(calendar.getTime()))
        busyTimes.put(calendar.getTime(), new ArrayList<Busy>());
      calendar.add(Calendar.DAY_OF_YEAR, 1);
    }
  }

  /**
   * Merge the overlapping busy times, e.g 9:00-10:00 and 10:00-12:00 will
   * become one 9:00-12:00 busy time.
   * 
   * @param busyTimes The busy times to merge.
   */
  private void mergeBusyTimes(List<Busy> busyTimes) {
    sortBusyTime(busyTimes);

    // Merge every busy slots.
    for (int i = 0; i < busyTimes.size(); ++i) {
      Busy current = busyTimes.get(i);

      for (int j = i + 1; j < busyTimes.size();) {
        Busy next = busyTimes.get(j);

        if (compareDateTime(current.when.endTime, next.when.startTime) >= 0) {
          if (compareDateTime(current.when.endTime, next.when.endTime) < 0)
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
  private List<AvailableMeetingTime> findAvailableMeetings(List<Busy> busyTimes, DateTime day) {
    List<AvailableMeetingTime> result = new ArrayList<AvailableMeetingTime>();
    AvailableMeetingTime tmp = new AvailableMeetingTime();
    Date first = new Date();
    Date last = new Date();

    setFistAndLast(day, first, last);

    tmp.start = first;
    for (Busy busy : busyTimes) {
      tmp.end = new Date(busy.when.startTime.value);
      result.add(tmp);
      tmp = new AvailableMeetingTime();
      tmp.start = new Date(busy.when.endTime.value);
    }
    tmp.end = last;
    result.add(tmp);

    return result;
  }

  /**
   * Filter the available meetings according to the settings: the meetings
   * should be between {@code from} and {@code to} and should not last less than @
   * length} .
   * 
   * @param meetings The meetings to filter.
   * @param from The start time from which to filter meetings.
   * @param to The end time from which to filter meetings.
   * @param length The minimum length of the meetings.
   */
  private void filterAvailableMeetings(List<AvailableMeetingTime> meetings,
      boolean useWorkingHours, Date from, Date to, int length) {
    if (useWorkingHours) {
      filterStartTime(meetings, from);
      filterEndTime(meetings, to);
    }
    filterMeetingLength(meetings, length);
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
   * Filter the meetings that are before {@code from}.
   * 
   * @param meetings The meetings to filter.
   * @param from The start time from which to filter the meetings.
   */
  private void filterStartTime(List<AvailableMeetingTime> meetings, Date from) {
    while (meetings.size() > 0) {
      AvailableMeetingTime meeting = meetings.get(0);

      if (meeting.start.before(from)) {
        if (meeting.end.before(from) || meeting.end.equals(from))
          meetings.remove(0);
        else
          meeting.start = from;
      } else
        break;
    }
  }

  /**
   * Filter the meetings that are after {@code to}.
   * 
   * @param meetings The meetings to filter.
   * @param to The end time from which to filter the meetings.
   */
  private void filterEndTime(List<AvailableMeetingTime> meetings, Date to) {
    while (meetings.size() > 0) {
      AvailableMeetingTime slot = meetings.get(meetings.size() - 1);

      if (slot.end.after(to)) {
        if (slot.start.after(to) || slot.start.equals(to))
          meetings.remove(meetings.size() - 1);
        else
          slot.end = to;
      } else
        break;
    }
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

  /**
   * Set the first (00:00) and last (23:59) time of the same day as {@code from}
   * .
   * 
   * @param from The date from which to get the day.
   * @param first The date object on which to set the first time of the day.
   * @param last The date object on which to set the last time of the day.
   */
  private void setFistAndLast(DateTime from, Date first, Date last) {
    Calendar tmpCalendar = new GregorianCalendar();
    tmpCalendar.setTime(getDate(from, 0));

    first.setTime(tmpCalendar.getTimeInMillis());

    tmpCalendar.set(Calendar.HOUR_OF_DAY, 23);
    tmpCalendar.set(Calendar.MINUTE, 59);
    tmpCalendar.set(Calendar.SECOND, 59);
    tmpCalendar.set(Calendar.MILLISECOND, 999);

    last.setTime(tmpCalendar.getTimeInMillis());
  }

  /**
   * Sort the busy times by start time.
   * 
   * @param busyTime The busy times to sort.
   */
  private void sortBusyTime(List<Busy> busyTime) {
    Collections.sort(busyTime, new Comparator<Busy>() {
      @Override
      public int compare(Busy lhs, Busy rhs) {
        int compare = compareDateTime(lhs.when.startTime, rhs.when.startTime);
        if (compare == 0)
          return compareDateTime(lhs.when.endTime, rhs.when.endTime);
        return compare;
      }
    });
  }

  /**
   * Get the current day from {@code time} with the given {@code hourOfDay}.
   * 
   * @param time The DateTime object from which to read the day.
   * @param hourOfDay The hour of day to set in double, e.g 9.5 == 9:30.
   * @return The computed Date object.
   */
  private Date getDate(DateTime time, double hourOfDay) {
    Calendar calendar = new GregorianCalendar();
    int hour = (int) hourOfDay;
    int minute = (int) ((hourOfDay - hour) * 60);

    calendar.setTime(new Date(time.value));

    setTime(calendar, hour, minute);

    return calendar.getTime();
  }

  /**
   * @param calendar
   * @param hour
   * @param minute
   */
  private void setTime(Calendar calendar, int hour, int minute) {
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.clear(Calendar.SECOND);
    calendar.clear(Calendar.MILLISECOND);
  }

  /**
   * Compare 2 DateTime objects.
   * 
   * @param lhs
   * @param rhs
   * @return The comparison of the 2 DateTimes.
   */
  private int compareDateTime(DateTime lhs, DateTime rhs) {
    return (int) (lhs.value - rhs.value);
  }

}
