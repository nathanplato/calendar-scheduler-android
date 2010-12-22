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

import com.google.api.data.calendar.v2.CalendarService;
import com.google.api.data.calendar.v2.model.SettingsList;
import com.google.api.data.calendar.v2.model.SettingsProperty;

import java.io.IOException;
import java.util.TimeZone;

/**
 * TODO(alainv) Write type description
 * 
 * @since 2.2
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class CalendarServiceManager {
  private static CalendarServiceManager instance;

  private String authToken;
  private CalendarService service;
  private TimeZone timezone;

  static public CalendarServiceManager getInstance() {
    if (instance == null)
      instance = new CalendarServiceManager();
    return instance;
  }

  public void setAuthToken(String authToken) {
    if (this.authToken != authToken && authToken != null) {
      this.authToken = authToken;
      this.timezone = null;
      if (service == null)
        service = new CalendarService(MeetingSchedulerConstants.TAG + " "
            + MeetingSchedulerConstants.VERSION);
      service.setClientLoginAuthenticationToken(this.authToken);
    }
  }

  public CalendarService getService() {
    return service;
  }

  public TimeZone getTimeZone() {
    if (timezone == null) {
      try {
        SettingsList settings = service.getSettings();

        timezone = TimeZone.getTimeZone(settings.getProperty(SettingsProperty.PROPERTY_TIMEZONE));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return timezone == null ? TimeZone.getDefault() : timezone;
  }
}
