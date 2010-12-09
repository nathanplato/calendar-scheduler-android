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
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Activity screen to show and set the app preferences
 * 
 * @author Prashant Tiwari
 */
public class ShowPreferencesActivity extends PreferenceActivity implements
    OnSharedPreferenceChangeListener {
  private ListPreference meetingLengthPref;
  private String meetingLengthKey;
  private ListPreference timeSpanPref;
  private String timeSpanKey;
  private CheckBoxPreference skipWeekendsPref;
  private String skipWeekendsKey;
  private CheckBoxPreference useWorkingHoursPref;
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

    meetingLengthKey = getString(R.string.meeting_length_list_pref);
    meetingLengthPref = (ListPreference) getPreferenceScreen().findPreference(
        meetingLengthKey);
    timeSpanKey = getString(R.string.time_span_list_pref);
    timeSpanPref = (ListPreference) getPreferenceScreen().findPreference(
        timeSpanKey);
    skipWeekendsKey = getString(R.string.skip_weekends_chkbox_pref);
    skipWeekendsPref = (CheckBoxPreference) getPreferenceScreen()
        .findPreference(skipWeekendsKey);
    useWorkingHoursKey = getString(R.string.use_working_hours_chkbox_pref);
    useWorkingHoursPref = (CheckBoxPreference) getPreferenceScreen()
        .findPreference(useWorkingHoursKey);
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
    if (key.equals(meetingLengthKey)) {
      meetingLengthPref.setSummary(meetingLengthPref.getEntry());
    } else if (key.equals(timeSpanKey)) {
      timeSpanPref.setSummary(timeSpanPref.getEntry());
    } else if (key.equals(useWorkingHoursKey)) {
      setUseWorkingHoursSummary();
      enableDisableWorkingHoursPreferences();
    } else if (key.equals(useCalendarSettingsKey)) {
      setUseCalendarSettingsSummary();
      enableDisableUseCalendarSettingPreferences();
    } else if (key.equals(skipWeekendsKey)) {
      setSkipWeekendsSummary();
    } else if (key.equals(workingHoursStartPref)) {
      setWorkingHoursStartSummary();
    } else if (key.equals(workingHoursEndPref)) {
      setWorkingHoursEndSummary();
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
    
    if (meetingLengthPref.getEntry() != null
        && meetingLengthPref.getEntry().length() > 0) {
      meetingLengthPref.setSummary(meetingLengthPref.getEntry());
    }
    
    if (timeSpanPref.getEntry() != null && timeSpanPref.getEntry().length() > 0) {
      timeSpanPref.setSummary(timeSpanPref.getEntry());
    } else {
      timeSpanPref.setSummary(getString(R.string.time_span_summary));
    }
    
    setSkipWeekendsSummary();
    
    setUseCalendarSettingsSummary();
    
    setUseWorkingHoursSummary();
    
    setWorkingHoursStartSummary();
    
    setWorkingHoursEndSummary();
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
  
  /**
   * Sets the summary for the skipWeekends checkbox preference
   */
  private void setSkipWeekendsSummary() {
    if (skipWeekendsPref.isChecked()) {
      skipWeekendsPref
          .setSummary(getString(R.string.skip_weekends_summary_checked));
    } else {
      skipWeekendsPref
          .setSummary(getString(R.string.skip_weekends_summary_unchecked));
    }
  }
  
  /**
   * Sets the summary for the useCalendarSettings checkbox preference
   */
  private void setUseCalendarSettingsSummary() {
    if (useCalendarSettingsPref.isChecked()) {
      useCalendarSettingsPref
          .setSummary(getString(R.string.use_calendar_settings_summary_checked));
    } else {
      useCalendarSettingsPref
          .setSummary(getString(R.string.use_calendar_settings_summary_unchecked));
    }
  }
  
  /**
   * Sets the summary for the useWorkingHours checkbox preference
   */
  private void setUseWorkingHoursSummary() {
    if (useWorkingHoursPref.isChecked()) {
      useWorkingHoursPref
          .setSummary(getString(R.string.use_working_hours_summary_checked));
    } else {
      useWorkingHoursPref
          .setSummary(getString(R.string.use_working_hours_summary_unchecked));
    }
  }
  
  /**
   * Sets the summary for the workingHoursStart preference
   */
  private void setWorkingHoursStartSummary() {
    if (workingHoursStartPref.getText() != null
        && workingHoursStartPref.getText().length() > 0) {
      workingHoursStartPref.setSummary(workingHoursStartPref.getText()
          + " hours");
    } else {
      workingHoursStartPref
          .setSummary(getString(R.string.working_hours_start_summary));
    }
  }
  
  /**
   * Sets the summary for the workingHoursEnd preference
   */
  private void setWorkingHoursEndSummary() {
    if (workingHoursEndPref.getText() != null
        && workingHoursEndPref.getText().length() > 0) {
      workingHoursEndPref.setSummary(workingHoursEndPref.getText()
          + " hours");
    } else {
      workingHoursEndPref
          .setSummary(getString(R.string.working_hours_end_summary));
    }
  }
}
