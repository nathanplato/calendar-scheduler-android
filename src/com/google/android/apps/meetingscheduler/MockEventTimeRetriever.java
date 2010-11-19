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

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Retrieves fake available meeting time for testing purposes.
 *
 * @author Nicolas Garnier (nivco@google.com)
 */
public class MockEventTimeRetriever implements EventTimeRetriever {

  public List<AvailableMeetingTime> getAvailableMeetingTime(List<Attendee> attendees,
      Settings settings) {
    List<AvailableMeetingTime> availableMeetingTimes = new ArrayList<AvailableMeetingTime>();
    availableMeetingTimes.add(new AvailableMeetingTime(
        new GregorianCalendar(2010, 11, 20, 10, 00).getTime(),
        new GregorianCalendar(2010, 11, 20, 11, 00).getTime(), attendees));
    availableMeetingTimes.add(new AvailableMeetingTime(
        new GregorianCalendar(2010, 11, 20, 8, 00).getTime(),
        new GregorianCalendar(2010, 11, 20, 9, 00).getTime(), attendees));
    availableMeetingTimes.add(new AvailableMeetingTime(
        new GregorianCalendar(2010, 11, 22, 10, 00).getTime(),
        new GregorianCalendar(2010, 11, 22, 11, 00).getTime(), attendees));
    availableMeetingTimes.add(new AvailableMeetingTime(
        new GregorianCalendar(2010, 11, 22, 11, 00).getTime(),
        new GregorianCalendar(2010, 11, 22, 12, 00).getTime(), attendees));
    availableMeetingTimes.add(new AvailableMeetingTime(
        new GregorianCalendar(2010, 11, 23, 14, 00).getTime(),
        new GregorianCalendar(2010, 11, 23, 15, 00).getTime(), attendees));
    availableMeetingTimes.add(new AvailableMeetingTime(
        new GregorianCalendar(2010, 11, 23, 12, 00).getTime(),
        new GregorianCalendar(2010, 11, 23, 13, 00).getTime(), attendees));
    availableMeetingTimes.add(new AvailableMeetingTime(
        new GregorianCalendar(2010, 11, 24, 12, 00).getTime(),
        new GregorianCalendar(2010, 11, 24, 13, 00).getTime(), attendees));
    availableMeetingTimes.add(new AvailableMeetingTime(
        new GregorianCalendar(2010, 11, 24, 13, 00).getTime(),
        new GregorianCalendar(2010, 11, 24, 14, 00).getTime(), attendees));
    availableMeetingTimes.add(new AvailableMeetingTime(
        new GregorianCalendar(2010, 11, 24, 15, 00).getTime(),
        new GregorianCalendar(2010, 11, 24, 16, 00).getTime(), attendees));
    return availableMeetingTimes;
  }
}
