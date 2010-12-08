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
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Activity screen to show and set the app preferences
 * 
 * @author Prashant Tiwari
 */
public class ShowPreferencesActivity extends PreferenceActivity implements
    OnSharedPreferenceChangeListener {
  private String useWorkingHoursKey;
  private String useCalendarSettingsKey;
  private CheckBoxPreference useCalendarSettingsPref;
  private EditTextPreference workingHoursStartPref;
  private EditTextPreference workingHoursEndPref;
  private SharedPreferences preferences;

  /**
   * Returns an Intent that will display this Activity.
   * 
   * @param context
   *          The application Context
   * @return An intent that will display this Activity
   */
  public static Intent createViewIntent(Context context) {
    Intent intent = new Intent(context, ShowPreferencesActivity.class);
    intent.setClass(context, ShowPreferencesActivity.class);
    return intent;
  }

  /**
   * Initialise this activity
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);

    preferences = PreferenceManager
        .getDefaultSharedPreferences(getApplicationContext());

    useWorkingHoursKey = getString(R.string.use_working_hours_chkbox_pref);
    useCalendarSettingsKey = getString(R.string.use_calendar_settings_chkbox_pref);
    useCalendarSettingsPref = (CheckBoxPreference) getPreferenceScreen()
        .findPreference(useCalendarSettingsKey);
    workingHoursStartPref = (EditTextPreference) getPreferenceScreen()
        .findPreference(getString(R.string.working_hours_start_text_pref));
    workingHoursEndPref = (EditTextPreference) getPreferenceScreen()
        .findPreference(getString(R.string.working_hours_end_text_pref));

    enableDisableUseCalendarSettingPreferences();
    enableDisableWorkingHoursPreferences();
  }

  /**
   * Called when the value of a preference changes
   */
  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
      String key) {
    if (key.equals(useWorkingHoursKey)) {
      enableDisableWorkingHoursPreferences();
    }

    if (key.equals(useCalendarSettingsKey)) {
      enableDisableUseCalendarSettingPreferences();
    }

  }

  /*
   * Called when this activity is visible
   */
  @Override
  protected void onResume() {
    super.onResume();

    // Set up a listener whenever a key changes
    getPreferenceScreen().getSharedPreferences()
        .registerOnSharedPreferenceChangeListener(this);
  }

  /*
   * Called when another activity comes in front of this activity
   */
  @Override
  protected void onPause() {
    super.onPause();

    // Unregister the listener whenever a key changes
    getPreferenceScreen().getSharedPreferences()
        .unregisterOnSharedPreferenceChangeListener(this);
  }

  /**
   * Enables or disables preferences fields based on the value of the
   * UseWorkingHoursSettings preference
   */
  private void enableDisableWorkingHoursPreferences() {
    if (preferences.getBoolean(useWorkingHoursKey, true)) {
      useCalendarSettingsPref.setEnabled(true);
      enableDisableUseCalendarSettingPreferences();
    } else {
      // useCalendarSettingsPref.setChecked(false);
      useCalendarSettingsPref.setEnabled(false);
      // workingHoursStartPref.setText("-1");
      workingHoursStartPref.setEnabled(false);
      // workingHoursEndPref.setText("-1");
      workingHoursEndPref.setEnabled(false);
    }
  }

  /**
   * Enables or disables preferences fields based on the value of the
   * UseCalendarSettings preference
   */
  private void enableDisableUseCalendarSettingPreferences() {
    if (preferences.getBoolean(useCalendarSettingsKey, false)) {
      workingHoursStartPref.setEnabled(false);
      workingHoursEndPref.setEnabled(false);
    } else {
      workingHoursStartPref.setEnabled(true);
      workingHoursEndPref.setEnabled(true);
    }
  }

}
