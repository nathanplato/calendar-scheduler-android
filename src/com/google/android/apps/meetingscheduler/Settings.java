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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Settings and configuration for the Meeting Scheduler
 * 
 * @author Nicolas Garnier
 */
public class Settings {
  
  private static Settings settings;

  /** Length of the meeting to find in minutes */
  private static int meetingLength;

  /** How long in the future do we have to look for in days */
  private static int timeSpan;

  /**
   * True if we need to take into consideration some working hours instead of
   * matching any time in the day
   */
  private static boolean useWorkingHours;

  /**
   * True if don't return results on weekend.
   */
  private static boolean skipWeekends;

  /**
   * True if we should use the Google Calendar working hour setting of each
   * participant or false if we should just use the times manually set.
   */
  private static boolean useCalendarSettings;

  /**
   * Time the working hours start in hours from midnight (0=midnight, 9.5 =
   * 9:30am, 23 = 11pm)
   */
  private static double workingHoursStart;

  /**
   * Time the working hours end in hours from midnight (0=midnight), 9.5 =
   * 9:30am, 23 = 11pm)
   */
  private static double workingHoursEnd;

  /**
   * Can't get Settings directly, use getInstance instead
   */
  private Settings() {
  }
  
  /**
   * Get an instance of the Settings bean
   * @return An instance of Settings
   */
  public static Settings getInstance(Context context) {
    if (settings == null)
      settings = new Settings();
    getSettings(context);
    return settings;
  }

  public int getMeetingLength() {
    return meetingLength;
  }

  public int getTimeSpan() {
    return timeSpan;
  }

  public boolean doUseWorkingHours() {
    return useWorkingHours;
  }

  public boolean doSkipWeekends() {
    return skipWeekends;
  }

  public boolean doUseCalendarSettings() {
    return useCalendarSettings;
  }

  public double getWorkingHoursStart() {
    return workingHoursStart;
  }

  public double getWorkingHoursEnd() {
    return workingHoursEnd;
  }
  
  /**
   * Gets the settings from the Preferences screen
   * @param context The application context
   */
  private static void getSettings(Context context) {
    SharedPreferences prefs = PreferenceManager
        .getDefaultSharedPreferences(context);
    
    String meeting_length_list_pref = prefs.getString(context
        .getString(R.string.meeting_length_list_pref), context
        .getString(R.string.meeting_length_default_value));
    meetingLength = Integer.parseInt(meeting_length_list_pref);
    
    String time_span_list_pref = prefs.getString(context
        .getString(R.string.time_span_list_pref), context
        .getString(R.string.time_span_default_value));
    timeSpan = Integer.parseInt(time_span_list_pref);
    
    Boolean skip_weekends_chkbox_pref = prefs.getBoolean(context
        .getString(R.string.skip_weekends_chkbox_pref), Boolean
        .getBoolean(context.getString(R.string.skip_weekends_default_value)));
    skipWeekends = skip_weekends_chkbox_pref.booleanValue();
    
    Boolean use_working_hours_chkbox_pref = prefs.getBoolean(context
        .getString(R.string.use_working_hours_chkbox_pref),
        Boolean.getBoolean(context
            .getString(R.string.use_working_hours_default_value)));
    useWorkingHours = use_working_hours_chkbox_pref.booleanValue();
    
    Boolean use_calendar_settings_chkbox_pref = prefs.getBoolean(context
        .getString(R.string.use_calendar_settings_chkbox_pref), Boolean
        .getBoolean(context
            .getString(R.string.use_calendar_settings_default_value)));
    useCalendarSettings = use_calendar_settings_chkbox_pref.booleanValue();
    
    String working_hours_start_text_pref = prefs.getString(context
        .getString(R.string.working_hours_start_text_pref), context
        .getString(R.string.working_hours_start_default_value));
    workingHoursStart = Double.parseDouble(working_hours_start_text_pref);
    
    String working_hours_end_text_pref = prefs.getString(context
        .getString(R.string.working_hours_end_text_pref), context
        .getString(R.string.working_hours_end_default_value));
    workingHoursEnd = Double.parseDouble(working_hours_end_text_pref);
  }

}
