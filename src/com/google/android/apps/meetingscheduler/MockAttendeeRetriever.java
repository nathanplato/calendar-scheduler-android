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
import java.util.List;

/**
 * Mock Attendee Retriever just returns a list of attendees manually build for
 * testing.
 * 
 * @author Nicolas Garnier
 */
public class MockAttendeeRetriever implements AttendeeRetriever {

  public List<Attendee> getPossibleAttendees() {
    List<Attendee> attendees = new ArrayList<Attendee>();
    attendees.add(new Attendee("alainv", "alainv@google.com", null));
    attendees.add(new Attendee("nivco", "nivco@google.com", null));
    attendees.add(new Attendee("pying", "pying@google.com", null));
    attendees.add(new Attendee("prashantt", "prashantt@google.com", null));
    return attendees;
  }

  public Attendee getCurrentUser() {
    return new Attendee("Nicolas Garnier", "nivco@google.com", null);
  }
}
