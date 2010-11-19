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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Selectable Attendee List item.
 *
 * @since 2.2
 * @author Nicolas Garnier (nivco@google.com)
 */
public class AvailableMeetingTime implements Serializable, Comparable<AvailableMeetingTime> {

  /** For Serialization purposes */
  private static final long serialVersionUID = 1L;

  /** The start time of the event */
  public Date start;

  /** The end time of the event */
  public Date end;

  /** The list of attendees to the event */
  public List<Attendee> attendees;

  /**
   * Default Constructor.
   */
  public AvailableMeetingTime(){
  }

  /**
   * Constructor which initializes the start and end Date.
   *
   * @param start The Start date of the event
   * @param end The End date of the event
   */
  public AvailableMeetingTime(Date start, Date end){
    this.start = start;
    this.end = end;
  }

  /**
   * Constructor which initializes the start and end Date.
   *
   * @param start The Start date of the event
   * @param end The End date of the event
   * @param attendees The Attendees that are available for the meeting
   */
  public AvailableMeetingTime(Date start, Date end, List<Attendee> attendees){
    this.start = start;
    this.end = end;
    this.attendees = attendees;
  }

  public int compareTo(AvailableMeetingTime another) {
    int compare = start.compareTo(another.start);
    if(compare == 0) {
      return end.compareTo(another.end);
    } else {
      return compare;
    }
  }
}
