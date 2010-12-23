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

import android.accounts.Account;
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
  private int meetingLength;

  /** How long in the future do we have to look for in days */
  private int timeSpan;

  /**
   * True if we need to take into consideration some working hours instead of
   * matching any time in the day
   */
  private boolean useWorkingHours;

  /**
   * True if don't return results on weekend.
   */
  private boolean skipWeekends;

  /**
   * True if we should use the Google Calendar working hour setting of each
   * participant or false if we should just use the times manually set.
   */
  private boolean useCalendarSettings;

  /**
   * Time the working hours start in hours from midnight (0=midnight, 9.5 =
   * 9:30am, 23 = 11pm)
   */
  private String workingHoursStart;

  /**
   * Time the working hours end in hours from midnight (0=midnight), 9.5 =
   * 9:30am, 23 = 11pm)
   */
  private String workingHoursEnd;

  /**
   * User selected account.
   */
  private Account account;

  /**
   * Can't get Settings directly, use getInstance instead
   */
  private Settings() {
  }

  /**
   * Get an instance of the Settings bean. Must call
   * {@link #initInstance(Context, Runnable)} before.
   * 
   * @return An instance of Settings
   */
  public static Settings getInstance() {
    return settings;
  }

  public static void initInstance(Context context, Runnable handleSettings) {
    if (settings == null)
      settings = new Settings();
    settings.getSettings(context, handleSettings);
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

  public String getWorkingHoursStart() {
    return workingHoursStart;
  }

  public String getWorkingHoursEnd() {
    return workingHoursEnd;
  }

  public Account getAccount() {
    return account;
  }

  public void changeAccount(final Context context, final Runnable handleSettings) {
    AccountChooser.getInstance().Reset();
    getAccount(context, null, handleSettings);
  }

  public void reload(final Context context) {
    getSettings(context, null);
  }

  /**
   * Gets the settings from the Preferences screen
   * 
   * @param context The application context
   */
  private void getSettings(Context context, final Runnable handleSettings) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

    String meeting_length_list_pref = prefs.getString(
        context.getString(R.string.meeting_length_list_pref),
        context.getString(R.string.meeting_length_default_value));
    meetingLength = Integer.parseInt(meeting_length_list_pref);

    String time_span_list_pref = prefs.getString(context.getString(R.string.time_span_list_pref),
        context.getString(R.string.time_span_default_value));
    timeSpan = Integer.parseInt(time_span_list_pref);

    Boolean skip_weekends_chkbox_pref = prefs.getBoolean(
        context.getString(R.string.skip_weekends_chkbox_pref),
        Boolean.getBoolean(context.getString(R.string.skip_weekends_default_value)));
    skipWeekends = skip_weekends_chkbox_pref.booleanValue();

    Boolean use_working_hours_chkbox_pref = prefs.getBoolean(
        context.getString(R.string.use_working_hours_chkbox_pref),
        Boolean.getBoolean(context.getString(R.string.use_working_hours_default_value)));
    useWorkingHours = use_working_hours_chkbox_pref.booleanValue();

    Boolean use_calendar_settings_chkbox_pref = prefs.getBoolean(
        context.getString(R.string.use_calendar_settings_chkbox_pref),
        Boolean.getBoolean(context.getString(R.string.use_calendar_settings_default_value)));
    useCalendarSettings = use_calendar_settings_chkbox_pref.booleanValue();

    workingHoursStart = prefs.getString(context.getString(R.string.working_hours_start_text_pref),
        context.getString(R.string.working_hours_start_default_value));

    workingHoursEnd = prefs.getString(context.getString(R.string.working_hours_end_text_pref),
        context.getString(R.string.working_hours_end_default_value));

    String oldAccount = prefs.getString(context.getString(R.string.selected_account_text_pref),
        null);
    getAccount(context, oldAccount, handleSettings);
  }

  /**
   * @param context
   * @param oldAccount TODO
   * @param handleSettings
   */
  private void getAccount(final Context context, String oldAccount, final Runnable handleSettings) {
    AccountChooser.getInstance().chooseAccount(context, oldAccount,
        new AccountChooser.AccountHandler() {
          @Override
          public void handleAccountSelected(Account result) {
            if (result != null) {
              account = result;
              saveAccount(context);
            }
            if (handleSettings != null)
              handleSettings.run();
          }
        });
  }

  /**
   * @param context
   */
  private void saveAccount(final Context context) {
    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
    editor.putString(context.getString(R.string.selected_account_text_pref), account.name);
    editor.commit();
  }
}
