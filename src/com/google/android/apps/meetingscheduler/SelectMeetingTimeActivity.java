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
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.api.data.calendar.v2.CalendarApiInfo;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Activity Screen where the user selects the meeting time between the meeting
 * times proposed.
 * 
 * @author Nicolas Garnier
 */
public class SelectMeetingTimeActivity extends Activity {

  /** The constant to store the selectedAttendees list in an intent */
  private static final String SELECTED_ATTENDEES = "SELECTED_ATTENDEES";

  /** The application settings */
  // TODO: Change this so it is saved in memory and also add a settings
  // configuration page accessible by the menu.
  // private Settings settings = new Settings();

  /** The application settings */
  private EventTimeRetriever eventTimeRetriever;

  private List<Attendee> selectedAttendees;

  private List<AvailableMeetingTime> availableMeetingTimes;

  private AuthManager auth;

  private ProgressDialog progressBar;

  private Handler handler = new Handler();

  /** The date from which to start to look for available meeting times */
  private Calendar startDate;

  /**
   * Cancel Activity re-launch when screen orientation changes.
   */
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }

  /** Called when the activity is first created. */
  @SuppressWarnings("unchecked")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

    // Creating main layout
    setContentView(R.layout.select_meeting_time);

    // Custom title bar
    if (customTitleSupported) {
      getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.app_title_select_time);
    }

    // Getting the selectedAttendees list from the intent
    final Intent intent = getIntent();
    selectedAttendees = (List<Attendee>) intent.getSerializableExtra(SELECTED_ATTENDEES);

    // Create a new Authentication Manager to authenticate in the Calendar API.
    auth = new AuthManager(this, MeetingSchedulerConstants.GET_LOGIN, null, true,
        CalendarApiInfo.AUTH_TOKEN_TYPE);

    availableMeetingTimes = new ArrayList<AvailableMeetingTime>();

    startDate = GregorianCalendar.getInstance();
    startDate.add(Calendar.DAY_OF_YEAR, 1);

    setFindMoreButton();

    authenticate();
  }

  /**
   * Returns an Intent that will display this Activity.
   * 
   * @param context The application Context
   * @param selectedAttendees The list of selected Attendees. Should be of a
   *          Serializable List type
   * @return An intent that will display this Activity
   * @throws NotSerializableException If the {@code selectedAttendees} is not
   *           serializable
   */
  public static Intent createViewIntent(Context context, List<Attendee> selectedAttendees)
      throws NotSerializableException {
    Intent intent = new Intent(context, SelectMeetingTimeActivity.class);
    if (!(selectedAttendees instanceof Serializable)) {
      Log.e(MeetingSchedulerConstants.TAG, "List<Attendee> selectedAttendees is not serializable");
      throw new NotSerializableException();
    }
    intent.putExtra(SELECTED_ATTENDEES, (Serializable) selectedAttendees);
    Log.e(MeetingSchedulerConstants.TAG,
        "Successfully serialized List<Attendee> selectedAttendees in the intent");
    intent.setClass(context, SelectMeetingTimeActivity.class);
    return intent;
  }

  /**
   * Called when the authentication has finished.
   */
  @Override
  public void onActivityResult(int requestCode, int resultCode, final Intent results) {
    super.onActivityResult(requestCode, resultCode, results);
    switch (requestCode) {
    case MeetingSchedulerConstants.GET_LOGIN:
      if (resultCode == RESULT_OK && auth != null) {
        auth.authResult(resultCode, results);
      }
      break;
    case MeetingSchedulerConstants.AUTHENTICATED:
      if (resultCode == RESULT_OK && auth != null)
        authenticated();
      break;
    case MeetingSchedulerConstants.CREATE_EVENT:
      System.err.println("ON ACTIVITY RESULT: " + resultCode);
      if (resultCode == RESULT_OK) {
        Toast.makeText(this, getString(R.string.event_creation_success), Toast.LENGTH_SHORT).show();
      } else if (resultCode == RESULT_FIRST_USER && results != null) {
        Toast.makeText(
            this,
            getString(R.string.event_creation_failure) + ": "
                + results.getStringExtra(SetEventDetailsActivity.MESSAGE), Toast.LENGTH_LONG)
            .show();
      }
      break;
    }
  }

  private void setFindMoreButton() {
    Button findMore = (Button) findViewById(R.id.find_more_meeting_time_button);

    findMore.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (auth.getAuthToken() != null) {
          startDate.add(Calendar.DAY_OF_YEAR, Settings.getInstance(getApplicationContext())
              .getTimeSpan());
          findMeetings();
        }
      }
    });
  }

  /**
   * Authenticates into the Calendar API using the selected account.
   */
  private void authenticate() {
    auth.doLogin(new Runnable() {
      public void run() {
        onActivityResult(MeetingSchedulerConstants.AUTHENTICATED, RESULT_OK, null);
      }
    }, false);

  }

  /**
   * Called when the authentication succeeded. Request the available meeting
   * times and display the result on the screen.
   */
  private void authenticated() {
    if (auth.getAuthToken() == null) {
      authenticate();
    } else {
      CalendarServiceManager.getInstance().setAuthToken(auth.getAuthToken());

      eventTimeRetriever = new CommonFreeTimesRetriever(new FreeBusyTimesRetriever());
      findMeetings();
    }
  }

  /**
   * Find available meetings time.
   */
  private void findMeetings() {
    // Retrieves the common free time on a seperate thread.
    new Thread(new Runnable() {
      public void run() {
        // Calculating the available meeting times from the selectedAttendees
        // and
        // the settings
        final List<AvailableMeetingTime> newTimes = eventTimeRetriever.getAvailableMeetingTime(
            selectedAttendees, startDate.getTime(), getApplicationContext());

        // Update the progress bar
        handler.post(new Runnable() {
          public void run() {
            populateMeetings(newTimes);
            if (progressBar != null)
              progressBar.dismiss();
          }
        });
      }
    }).start();
    // Show a progress bar while the common free times are computed.
    progressBar = ProgressDialog.show(this, null,
        "Please wait while querying attendees availabilities...", true);
  }

  /**
   * Displays the available meeting times on the screen.
   * 
   * @param availableMeetingTimes The meeting times to display.
   */
  private void populateMeetings(List<AvailableMeetingTime> newTimes) {
    availableMeetingTimes.addAll(newTimes);
    // Adding the available meeting times to the UI
    ExpandableListView meetingListContainer = (ExpandableListView) findViewById(R.id.meeting_list);
    meetingListContainer.setAdapter(new EventExpandableListAdapter(this, availableMeetingTimes,
        Settings.getInstance(this).getMeetingLength()));
  }

}