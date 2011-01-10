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

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * TODO(alainv) Write type description
 * 
 * @since 2.2
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class DateUtils {

  /**
   * Sort the busy times by start time.
   * 
   * @param busyTime The busy times to sort.
   */
  public static void sortBusyTime(List<Busy> busyTime) {
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
   * Get the current day from {@code dateTime} with the given
   * {@code hoursDotMinutes}.
   * 
   * @param dateTime The DateTime object from which to read the day.
   * @param hoursDotMinutes The hour and minutes of day as a String in hh.MM
   *          format
   * @return The computed Date object.
   */
  public static Date getDate(DateTime dateTime, String hoursDotMinutes) {
    Calendar calendar = new GregorianCalendar(CalendarServiceManager.getInstance().getTimeZone());
    calendar.setTime(new Date(dateTime.value));
    String[] time = hoursDotMinutes.split("\\.");
    setTime(calendar, Integer.parseInt(time[0]), Integer.parseInt(time[1]), 0, 0);
    return calendar.getTime();
  }

  /**
   * Check if two dates are on the same day.
   * 
   * @param lhs
   * @param rhs
   * @return True if {@code lhs} and {@code rhs} are on the same day.
   */
  public static boolean isSameDay(Date lhs, Date rhs) {
    Calendar clhs = new GregorianCalendar(CalendarServiceManager.getInstance().getTimeZone());
    Calendar crhs = new GregorianCalendar(CalendarServiceManager.getInstance().getTimeZone());

    clhs.setTime(lhs);
    crhs.setTime(rhs);
    return clhs.get(Calendar.DAY_OF_YEAR) == crhs.get(Calendar.DAY_OF_YEAR)
        && clhs.get(Calendar.YEAR) == crhs.get(Calendar.YEAR);
  }

  /**
   * Parse a string formatted as "HH.MM" and returns a Calendar object set with
   * the time.
   * 
   * @param hoursDotMinutes The string to parse
   * @return The newly created Calendar object with the parsed time.
   */
  public static Calendar getCalendar(String hoursDotMinutes) {
    Calendar calendar = new GregorianCalendar(CalendarServiceManager.getInstance().getTimeZone());

    String[] time = hoursDotMinutes.split("\\.");
    setTime(calendar, Integer.parseInt(time[0]), Integer.parseInt(time[1]), 0, 0);
    return calendar;
  }

  /**
   * Set the time of the {@code calendar}.
   * 
   * @param calendar The calendar to which to set the time
   * @param hour The hour to set
   * @param minute The minute to set
   * @param second The second to set
   * @param millisecond The millisecond to set
   */
  public static void setTime(Calendar calendar, int hour, int minute, int second, int millisecond) {
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, second);
    calendar.set(Calendar.MILLISECOND, millisecond);
  }

  /**
   * Set the time from {@code source} to {@code target} and not modify the date.
   * 
   * @param target The Calendar object to which set the time.
   * @param source The Calendar object from which to read the time.
   */
  public static void setTime(Calendar target, Calendar source) {
    target.set(Calendar.HOUR_OF_DAY, source.get(Calendar.HOUR_OF_DAY));
    target.set(Calendar.MINUTE, source.get(Calendar.MINUTE));
    target.set(Calendar.SECOND, source.get(Calendar.SECOND));
    target.set(Calendar.MILLISECOND, source.get(Calendar.MILLISECOND));
  }

  /**
   * Compare 2 DateTime objects.
   * 
   * @param lhs
   * @param rhs
   * @return The comparison of the 2 DateTimes.
   */
  public static int compareDateTime(DateTime lhs, DateTime rhs) {
    return (int) (lhs.value - rhs.value);
  }
}
