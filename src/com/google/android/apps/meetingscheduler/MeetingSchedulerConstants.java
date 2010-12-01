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
 * Constants used by the Meeting Scheduler application.
 * 
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class MeetingSchedulerConstants {
  /**
   * Should be used by all log statements
   */
  public static final String TAG = "Meeting Scheduler";

  public static final String VERSION = "1.0";

  /*
   * onActivityResult request codes:
   */
  public static final int GET_LOGIN = 0;
  public static final int AUTHENTICATED = 1;

  /**
   * The type of account that we can use for gdata operations.
   */
  public static final String ACCOUNT_TYPE = "com.google";

}
