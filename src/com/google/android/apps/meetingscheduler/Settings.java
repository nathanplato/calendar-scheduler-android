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

/**
 * Settings and configuration for the Meeting Scheduler
 * 
 * @author Nicolas Garnier
 */
public class Settings {
  
  private static Settings settings;

  /** Length of the meeting to find in minutes */
  private int meetingLength = 60;

  /** How long in the future do we have to look for in days */
  private int timeSpan = 7;

  /**
   * True if we need to take into consideration some working hours instead of
   * matching any time in the day
   */
  private boolean useWorkingHours = true;

  /**
   * True if don't return results on weekend.
   */
  private boolean skipWeekends = true;

  /**
   * True if we should use the Google Calendar working hour setting of each
   * participant or false if we should just use the times manually set.
   */
  private boolean useCalendarSettings = false;

  /**
   * Time the working hours start in hours from midnight (0=midnight, 9.5 =
   * 9:30am, 23 = 11pm)
   */
  private double workingHoursStart = 9;

  /**
   * Time the working hours end in hours from midnight (0=midnight), 9.5 =
   * 9:30am, 23 = 11pm)
   */
  private double workingHoursEnd = 17.5;

  /**
   * Can't get Settings directly, use getInstance instead
   */
  private Settings() {
  }
  
  /**
   * Get an instance of the Settings bean
   * @return An instance of Settings
   */
  public static Settings getInstance() {
    if (settings == null)
      settings = new Settings();
    
    return settings;
  }

  public int getMeetingLength() {
    return meetingLength;
  }

  public void setMeetingLength(int meetingLength) {
    this.meetingLength = meetingLength;
  }

  public int getTimeSpan() {
    return timeSpan;
  }

  public void setTimeSpan(int timeSpan) {
    this.timeSpan = timeSpan;
  }

  public boolean doUseWorkingHours() {
    return useWorkingHours;
  }

  public void setUseWorkingHours(boolean useWorkingHours) {
    this.useWorkingHours = useWorkingHours;
  }

  public boolean doSkipWeekends() {
    return skipWeekends;
  }

  public void setSkipWeekends(boolean skipWeekends) {
    this.skipWeekends = skipWeekends;
  }

  public boolean doUseCalendarSettings() {
    return useCalendarSettings;
  }

  public void setUseCalendarSettings(boolean useCalendarSettings) {
    this.useCalendarSettings = useCalendarSettings;
  }

  public double getWorkingHoursStart() {
    return workingHoursStart;
  }

  public void setWorkingHoursStart(double workingHoursStart) {
    this.workingHoursStart = workingHoursStart;
  }

  public double getWorkingHoursEnd() {
    return workingHoursEnd;
  }

  public void setWorkingHoursEnd(double workingHoursEnd) {
    this.workingHoursEnd = workingHoursEnd;
  }

}
