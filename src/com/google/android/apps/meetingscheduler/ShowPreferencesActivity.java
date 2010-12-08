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

  /** Initialise this activity **/
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);
  }
  
}
