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

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

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
  private String workingHoursStartKey;
  private Preference workingHoursStartPref;
  private String workingHoursEndKey;
  private Preference workingHoursEndPref;
  private SharedPreferences preferences;
  private final int WORKING_HOURS_START_ID = 0;
  private final int WORKING_HOURS_END_ID = 1;
  private boolean is24HourFormat = false;
  private int workingHoursStartHours;
  private int workingHoursStartMinutes;
  private int workingHoursEndHours;
  private int workingHoursEndMinutes;

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

    preferences = PreferenceManager.getDefaultSharedPreferences(this);

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

    workingHoursStartKey = getString(R.string.working_hours_start_text_pref);
    workingHoursStartPref = (Preference) getPreferenceScreen().findPreference(
        workingHoursStartKey);
    workingHoursStartPref
        .setOnPreferenceClickListener(new OnPreferenceClickListener() {
          @Override
          public boolean onPreferenceClick(Preference preference) {
            showDialog(WORKING_HOURS_START_ID);
            return true;
          }
        });

    workingHoursEndKey = getString(R.string.working_hours_end_text_pref);
    workingHoursEndPref = (Preference) getPreferenceScreen().findPreference(
        workingHoursEndKey);
    workingHoursEndPref
        .setOnPreferenceClickListener(new OnPreferenceClickListener() {
          @Override
          public boolean onPreferenceClick(Preference preference) {
            showDialog(WORKING_HOURS_END_ID);
            return true;
          }
        });

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
    }
  }

  /*
   * Called when this activity is visible
   */
  @Override
  protected void onResume() {
    super.onResume();
    is24HourFormat = DateFormat.is24HourFormat(this);

    // Set up a listener to listen to any preference changes
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

    // Unregister the preference listener
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
      useCalendarSettingsPref.setEnabled(false);
      workingHoursStartPref.setEnabled(false);
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
    String workingHoursStart = preferences.getString(workingHoursStartKey,
        getString(R.string.working_hours_start_default_value));

    String[] startTime = workingHoursStart.split("\\.");

    workingHoursStartHours = Integer.parseInt(startTime[0]);
    workingHoursStartMinutes = Integer.parseInt(startTime[1]);

    setTimeSummary(workingHoursStartPref, workingHoursStartHours,
        workingHoursStartMinutes);
  }

  /**
   * Sets the summary for the workingHoursEnd preference
   */
  private void setWorkingHoursEndSummary() {
    String workingHoursEnd = preferences.getString(workingHoursEndKey,
        getString(R.string.working_hours_end_default_value));

    String[] endTime = workingHoursEnd.split("\\.");

    workingHoursEndHours = Integer.parseInt(endTime[0]);
    workingHoursEndMinutes = Integer.parseInt(endTime[1]);

    setTimeSummary(workingHoursEndPref, workingHoursEndHours,
        workingHoursEndMinutes);
  }

  /**
   * Get the listener to workingHoursStart TimePickerDialog changes
   */
  private TimePickerDialog.OnTimeSetListener workingHoursStartTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
      setTimeSummary(workingHoursStartPref, hourOfDay, minute);
      workingHoursStartHours = hourOfDay;
      workingHoursStartMinutes = minute;
    }
  };

  /**
   * Get the listener to workingHoursEnd TimePickerDialog changes
   */
  private TimePickerDialog.OnTimeSetListener workingHoursEndTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
      setTimeSummary(workingHoursEndPref, hourOfDay, minute);
      workingHoursEndHours = hourOfDay;
      workingHoursEndMinutes = minute;
    }
  };

  /**
   * Sets the summary display for the working hours start and end times
   * 
   * @param preference
   * @param hourOfDay
   * @param minute
   */
  private void setTimeSummary(Preference preference, int hourOfDay, int minute) {
    StringBuilder time = new StringBuilder();
    if (is24HourFormat) {
      time.append(Integer.toString(hourOfDay).length() == 2 ? hourOfDay
          : "0" + hourOfDay);
      time.append(":");
      time.append(Integer.toString(minute).length() == 2 ? minute : "0"
          + minute);
    } else {
      int hour = hourOfDay <= 12 ? hourOfDay : hourOfDay - 12;
      time.append(Integer.toString(hour).length() == 2 ? hour : "0"
          + hour);
      time.append(":");
      time.append(Integer.toString(minute).length() == 2 ? minute : "0"
          + minute);
      time.append(hourOfDay < 12 ? " AM" : " PM");
    }
    preference.setSummary(time.toString());
  }

  /**
   * Called once when showDialog is called to create and show TimePickerDialogs
   */
  @Override
  protected Dialog onCreateDialog(int id) {
    switch (id) {
    case WORKING_HOURS_START_ID:
      return new TimePickerDialog(this, workingHoursStartTimeSetListener,
          workingHoursStartHours, workingHoursStartMinutes, is24HourFormat);

    case WORKING_HOURS_END_ID:
      return new TimePickerDialog(this, workingHoursEndTimeSetListener,
          workingHoursEndHours, workingHoursEndMinutes, is24HourFormat);
    }
    return null;
  }

  /**
   * Called every time showDialog is called
   */
  @Override
  protected void onPrepareDialog(int id, Dialog dialog) {
    super.onPrepareDialog(id, dialog);
    switch (id) {
    case WORKING_HOURS_START_ID:
      ((TimePickerDialog) dialog).updateTime(workingHoursStartHours,
          workingHoursStartMinutes);
      break;
    case WORKING_HOURS_END_ID:
      ((TimePickerDialog) dialog).updateTime(workingHoursEndHours,
          workingHoursEndMinutes);
    }
  }

  /**
   * Called when this activity is no longer visible to persist preferences
   */
  @Override
  protected void onStop() {
    super.onStop();
    Log.i(MeetingSchedulerConstants.TAG, "Saving preferences...");
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(meetingLengthKey, meetingLengthPref.getValue());
    editor.putString(timeSpanKey, timeSpanPref.getValue());
    editor.putBoolean(skipWeekendsKey, skipWeekendsPref.isChecked());
    editor.putBoolean(useWorkingHoursKey, useWorkingHoursPref.isChecked());
    editor.putBoolean(useCalendarSettingsKey, useCalendarSettingsPref
        .isChecked());
    editor.putString(workingHoursStartKey, workingHoursStartHours + "."
        + workingHoursStartMinutes);
    editor.putString(workingHoursEndKey, workingHoursEndHours + "."
        + workingHoursEndMinutes);
    editor.commit();
    Log.i(MeetingSchedulerConstants.TAG, "Successfully saved preferences");
  }
}
