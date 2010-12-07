package com.google.android.apps.meetingscheduler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SetPreferencesActivity extends PreferenceActivity {
  /**
   * 
   * @param context
   * @return
   */
  public static Intent createViewIntent(Context context) {
    Intent intent = new Intent(context, SetPreferencesActivity.class);
    intent.setClass(context, SetPreferencesActivity.class);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);
  }
  
  
}
