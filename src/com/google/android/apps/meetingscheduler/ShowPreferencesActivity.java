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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Activity screen to show and set the app preferences
 * 
 * @author Prashant Tiwari
 */
public class ShowPreferencesActivity extends PreferenceActivity {
  
  /**
   * Returns an Intent that will display this Activity.
   * 
   * @param context The application Context
   * @return An intent that will display this Activity
   */
  public static Intent createViewIntent(Context context) {
    Intent intent = new Intent(context, ShowPreferencesActivity.class);
    intent.setClass(context, ShowPreferencesActivity.class);
    return intent;
  }

  /** Called when this Activity is first created **/
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);

    SharedPreferences prefs = PreferenceManager
        .getDefaultSharedPreferences(getBaseContext());
    String meetingLength = prefs.getString(
        getString(R.string.meeting_length_list_pref),
        getString(R.string.meeting_length_default_value));
    Settings.getInstance().setMeetingLength(Integer.parseInt(meetingLength));

    String timeSpan = prefs.getString(getString(R.string.time_span_list_pref),
        getString(R.string.time_span_default_value));
    Settings.getInstance().setTimeSpan(Integer.parseInt(timeSpan));

    Boolean skipWeekends = prefs.getBoolean(
        getString(R.string.skip_weekends_chkbox_pref), Boolean
            .getBoolean(getString(R.string.skip_weekends_default_value)));
    Settings.getInstance().setSkipWeekends(skipWeekends.booleanValue());

    Boolean useWorkingHours = prefs.getBoolean(this
        .getString(R.string.use_working_hours_chkbox_pref), Boolean
        .getBoolean(getString(R.string.use_working_hours_default_value)));
    Settings.getInstance().setUseWorkingHours(useWorkingHours.booleanValue());

    Boolean useCalendarSettings = prefs
        .getBoolean(
            getString(R.string.use_calendar_settings_chkbox_pref),
            Boolean
                .getBoolean(getString(R.string.use_calendar_settings_default_value)));
    Settings.getInstance().setUseCalendarSettings(
        useCalendarSettings.booleanValue());

    String workingHoursStart = prefs.getString(
        getString(R.string.working_hours_start_text_pref),
        getString(R.string.working_hours_start_default_value));
    Settings.getInstance().setWorkingHoursStart(
        Double.parseDouble(workingHoursStart));

    String workingHoursEnd = prefs.getString(
        getString(R.string.working_hours_end_text_pref),
        getString(R.string.working_hours_end_default_value));
    Settings.getInstance().setWorkingHoursEnd(
        Double.parseDouble(workingHoursEnd));
  }
  
}
