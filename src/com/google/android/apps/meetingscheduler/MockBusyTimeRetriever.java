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
import com.google.api.data.gdata.v2.model.When;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock Busy Times Retriever just returns a list of busy times manually build
 * for testing.
 * 
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class MockBusyTimeRetriever implements BusyTimesRetriever {

  @Override
  public Map<Attendee, List<Busy>> getBusyTimes(List<Attendee> attendees, Settings settings,
      Date startDate) {
    Map<Attendee, List<Busy>> result = new HashMap<Attendee, List<Busy>>();
    List<List<Busy>> mockBusyTimes = getMockBusyTimes();
    int maxSize = mockBusyTimes.size();
    int i = 0;

    for (Attendee attendee : attendees) {
      result.put(attendee, new ArrayList<Busy>(mockBusyTimes.get(i)));

      // Loop around when we reach the last mock busy times.
      i = (i + 1) % maxSize;
    }

    return result;
  }

  /**
   * Create a {@link Busy} object from {@code start} to {@code end}.
   * 
   * @param start The start time of the {@link Busy} object.
   * @param end The end time of the {@link Busy} object.
   * @return The computed {@link Busy} object.
   */
  private Busy createBusy(DateTime start, DateTime end) {
    Busy result = new Busy();

    result.when = new When();
    result.when.startTime = start;
    result.when.endTime = end;

    return result;
  }

  /**
   * Returns a list of manually created busy times.
   * 
   * @return The list of manually created busy times.
   */
  private List<List<Busy>> getMockBusyTimes() {
    List<List<Busy>> result = new ArrayList<List<Busy>>();

    List<Busy> tmp = new ArrayList<Busy>();
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-15T22:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-15T22:30:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-17T17:45:00.000Z"),
        DateTime.parseRfc3339("2010-11-17T19:15:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-17T19:30:00.000Z"),
        DateTime.parseRfc3339("2010-11-17T21:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-18T16:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-18T17:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-19T19:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-19T20:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-20T00:30:00.000Z"),
        DateTime.parseRfc3339("2010-11-20T01:30:00.000Z")));
    result.add(tmp);
    tmp = new ArrayList<Busy>();
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-16T09:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-16T09:30:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-17T10:15:00.000Z"),
        DateTime.parseRfc3339("2010-11-17T10:45:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-17T10:45:00.000Z"),
        DateTime.parseRfc3339("2010-11-17T11:15:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-18T12:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-18T13:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-18T16:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-18T17:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-18T16:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-18T17:00:00.000Z")));
    result.add(tmp);
    tmp = new ArrayList<Busy>();
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-15T20:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-15T21:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-17T15:30:00.000Z"),
        DateTime.parseRfc3339("2010-11-17T16:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-17T19:30:00.000Z"),
        DateTime.parseRfc3339("2010-11-17T21:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-18T05:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-18T05:30:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-18T16:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-18T17:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-18T19:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-18T20:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-18T20:15:00.000Z"),
        DateTime.parseRfc3339("2010-11-18T21:15:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-19T00:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-19T04:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-19T00:30:00.000Z"),
        DateTime.parseRfc3339("2010-11-19T01:30:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-19T15:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-19T17:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-19T22:30:00.000Z"),
        DateTime.parseRfc3339("2010-11-19T23:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-20T00:30:00.000Z"),
        DateTime.parseRfc3339("2010-11-20T01:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-20T00:30:00.000Z"),
        DateTime.parseRfc3339("2010-11-20T01:30:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-20T02:30:00.000Z"),
        DateTime.parseRfc3339("2010-11-20T05:00:00.000Z")));
    result.add(tmp);
    tmp = new ArrayList<Busy>();
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-15T17:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-15T18:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-15T20:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-15T21:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-15T20:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-15T21:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-15T22:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-15T22:30:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-16T16:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-16T16:30:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-16T16:30:00.000Z"),
        DateTime.parseRfc3339("2010-11-16T17:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-16T20:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-16T20:30:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-16T21:30:00.000Z"),
        DateTime.parseRfc3339("2010-11-16T22:30:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-16T23:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-17T00:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-18T00:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-18T01:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-18T16:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-18T17:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-18T17:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-18T18:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-18T17:30:00.000Z"),
        DateTime.parseRfc3339("2010-11-18T18:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-18T20:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-18T21:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-19T00:30:00.000Z"),
        DateTime.parseRfc3339("2010-11-19T01:30:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-19T02:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-19T04:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-19T17:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-19T17:30:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-19T20:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-19T21:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-19T20:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-19T21:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-19T22:45:00.000Z"),
        DateTime.parseRfc3339("2010-11-19T23:30:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-20T00:30:00.000Z"),
        DateTime.parseRfc3339("2010-11-20T01:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-20T00:30:00.000Z"),
        DateTime.parseRfc3339("2010-11-20T01:30:00.000Z")));
    result.add(tmp);
    tmp = new ArrayList<Busy>();
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-15T09:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-15T09:30:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-15T10:30:00.000Z"),
        DateTime.parseRfc3339("2010-11-15T11:30:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-15T20:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-15T21:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-16T04:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-16T05:30:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-16T10:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-16T11:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-17T15:30:00.000Z"),
        DateTime.parseRfc3339("2010-11-17T16:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-18T05:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-18T05:30:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-18T05:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-18T05:30:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-18T09:30:00.000Z"),
        DateTime.parseRfc3339("2010-11-18T11:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-18T16:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-18T17:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-19T00:30:00.000Z"),
        DateTime.parseRfc3339("2010-11-19T01:30:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-19T06:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-19T07:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-19T09:50:00.000Z"),
        DateTime.parseRfc3339("2010-11-19T12:00:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-19T11:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-19T12:30:00.000Z")));
    tmp.add(createBusy(DateTime.parseRfc3339("2010-11-19T12:00:00.000Z"),
        DateTime.parseRfc3339("2010-11-19T13:30:00.000Z")));
    result.add(tmp);

    return result;

  }

}
