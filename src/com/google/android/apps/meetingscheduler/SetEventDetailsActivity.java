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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * TODO(alainv) Write type description
 * 
 * @since 2.2
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class SetEventDetailsActivity extends Activity {

  /** The constant to store the selectedAttendees list in an intent */
  private static final String SELECTED_ATTENDEES = "SELECTED_ATTENDEES";

  private static final String START_DATE = "START_DATE";
  private static final String END_DATE = "END_DATE";

  public static final String MESSAGE = "MESSAGE";

  private EventCreator eventCreator = new CalendarEventCreator();

  private Date startDate;
  private Date endDate;

  private ProgressDialog progressBar;

  private Handler handler = new Handler();

  List<Attendee> selectedAttendees;

  /**
   * Cancel Activity re-launch when screen orientation changes.
   */
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

    // Creating main layout
    setContentView(R.layout.set_event_details);

    // Custom title bar
    if (customTitleSupported) {
      getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.app_title_set_event_details);
    }

    getParameters();

    setSaveButtonAction();
  }

  /**
   * Set action when the Save Button is clicked.
   */
  private void setSaveButtonAction() {
    Button saveButton = (Button) findViewById(R.id.save_event_button);
    saveButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // Show a progress bar while the common free times are computed.
        progressBar = ProgressDialog.show(SetEventDetailsActivity.this, null, "Creating Event...",
            true);

        final String title = getString(R.id.event_title_text);
        final String where = getString(R.id.event_where_text);
        final String description = getString(R.id.event_description_text);
        final boolean sendEventNotifications = getBoolean(R.id.send_event_notifications_checkbox);

        new Thread(new Runnable() {
          public void run() {
            try {
              eventCreator.createEvent(title, where, description, sendEventNotifications,
                  startDate, endDate, selectedAttendees);
              setResult(RESULT_OK);
            } catch (Exception e) {
              Intent data = new Intent();

              data.putExtra(MESSAGE, e.getMessage());
              setResult(RESULT_FIRST_USER, data);
            }

            // Update the progress bar
            handler.post(new Runnable() {
              public void run() {
                if (progressBar != null)
                  progressBar.dismiss();
                SetEventDetailsActivity.this.finish();
              }
            });
          }
        }).start();
      }

      private String getString(int viewId) {
        EditText view = (EditText) findViewById(viewId);

        if (view != null)
          return view.getText().toString();
        else
          return null;
      }

      private boolean getBoolean(int viewId) {
        CheckBox view = (CheckBox) findViewById(viewId);

        if (view != null)
          return view.isChecked();
        else
          return false;
      }
    });
  }

  /**
   * Get the parameters passed into this activity.
   */
  @SuppressWarnings("unchecked")
  private void getParameters() {
    // Getting the selectedAttendees list from the intent
    final Intent intent = getIntent();
    selectedAttendees = (List<Attendee>) intent.getSerializableExtra(SELECTED_ATTENDEES);
    // Default values are "now" and "now + 1hour".
    startDate = new Date(intent.getLongExtra(START_DATE, GregorianCalendar.getInstance()
        .getTimeInMillis()));
    endDate = new Date(intent.getLongExtra(END_DATE, GregorianCalendar.getInstance()
        .getTimeInMillis() + 3600000));
  }

  /**
   * Returns an Intent that will display this Activity.
   * 
   * @param context The application Context
   * @param selectedAttendees The list of selected Attendees. Should be of a
   *          Serializable List type
   * @param startDate The start date of the event to create
   * @param endDate The end date of the event to create
   * @return An intent that will display this Activity
   * @throws NotSerializableException If the {@code selectedAttendees} is not
   *           serializable
   */
  public static Intent createViewIntent(Context context, List<Attendee> selectedAttendees,
      long startDate, long endDate) throws NotSerializableException {
    Intent intent = new Intent(context, SelectMeetingTimeActivity.class);
    if (!(selectedAttendees instanceof Serializable)) {
      Log.e(MeetingSchedulerConstants.TAG, "List<Attendee> selectedAttendees is not serializable");
      throw new NotSerializableException();
    }
    intent.putExtra(SELECTED_ATTENDEES, (Serializable) selectedAttendees);
    intent.putExtra(START_DATE, startDate);
    intent.putExtra(END_DATE, endDate);

    intent.setClass(context, SetEventDetailsActivity.class);
    return intent;
  }

}
