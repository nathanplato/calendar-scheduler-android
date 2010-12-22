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

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Allows creation of events.
 * 
 * @since 2.2
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public interface EventCreator {

  public void createEvent(String title, String where, String description, boolean sendEventNotifications, Date start, Date end, List<Attendee> attendees)
      throws UnknownError, IOException;
}
