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
 * TODO(alainv) Write type description
 * 
 * @since 2.2
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class CommonFreeTimesRetriever implements EventTimeRetriever {

  private BusyTimesRetriever busyTimeRetriever;

  public CommonFreeTimesRetriever() {
    // TODO(alainv): Set default busyTimeRetriever.
  }

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
      Settings settings) {
    Map<Attendee, List<Busy>> busyTimes = busyTimeRetriever.getBusyTimes(attendees, settings);
    Map<Date, List<Busy>> sortedBusyTimes = sortBusyTimes(busyTimes);
    List<AvailableMeetingTime> result = new ArrayList<AvailableMeetingTime>();

    for (Map.Entry<Date, List<Busy>> busyTime : sortedBusyTimes.entrySet()) {
      List<AvailableMeetingTime> availableMeetings;

      mergeBusyTimes(busyTime.getValue());
      availableMeetings = findAvailableMeetings(busyTime.getValue());
      filterAvailableMeetings(availableMeetings,
          getDate(new DateTime(busyTime.getKey().getTime()), settings.workingHoursStart),
          getDate(new DateTime(busyTime.getKey().getTime()), settings.workingHoursEnd),
          settings.meetingLength);

      result.addAll(availableMeetings);
    }

    return result;
  }

  private Map<Date, List<Busy>> sortBusyTimes(Map<Attendee, List<Busy>> busyTimes) {
    Map<Date, List<Busy>> result;

    result = filterByDate(busyTimes);
    for (List<Busy> busyTime : result.values()) {
      sortBusyTime(busyTime);
    }

    return result;
  }

  private void mergeBusyTimes(List<Busy> busyTimes) {
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

  private List<AvailableMeetingTime> findAvailableMeetings(List<Busy> busyTimes) {
    List<AvailableMeetingTime> result = new ArrayList<AvailableMeetingTime>();
    AvailableMeetingTime tmp = new AvailableMeetingTime();
    Date first = new Date();
    Date last = new Date();

    setFistAndLast(busyTimes.get(0).when.startTime, first, last);

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

  private void filterAvailableMeetings(List<AvailableMeetingTime> meetings, Date from, Date to,
      int length) {
    filterStartTime(meetings, from);
    filterEndTime(meetings, to);
    filterMeetingLength(meetings, length);
  }

  /**
   * @param meetings
   * @param from
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
   * @param meetings
   * @param to
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
   * @param meetings
   * @param length
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
   * @param meeting
   */
  private int getMeetingLength(AvailableMeetingTime meeting) {
    long difference = meeting.end.getTime() - meeting.start.getTime();

    return (int) difference / 60000;
  }

  /**
   * @param from
   * @param first
   * @param last
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
   * @param busyTime
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
   * @param busyTimes
   * @return
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

  private Date getDate(DateTime time, double hourOfDay) {
    Calendar calendar = new GregorianCalendar();
    int hour = (int) hourOfDay;
    int minute = (int) ((hourOfDay - hour) * 60);

    calendar.setTime(new Date(time.value));

    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.clear(Calendar.SECOND);
    calendar.clear(Calendar.MILLISECOND);

    return calendar.getTime();
  }

  private int compareDateTime(DateTime lhs, DateTime rhs) {
    return (int) (lhs.value - rhs.value);
  }

}
