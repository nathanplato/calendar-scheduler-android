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

import com.google.api.data.calendar.v2.model.Busy;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Allows the retrieval of busy times.
 * 
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public interface BusyTimesRetriever {

  /**
   * Returns the list of busy times for the given attendees.
   * 
   * @param attendees The list of attendees for which to retrieve the busy
   *          times.
   * @param startDate the date from which to start querying busy times.
   * @return The list of busy times
   */
  public Map<Attendee, List<Busy>> getBusyTimes(List<Attendee> attendees,
      Date startDate);

}
