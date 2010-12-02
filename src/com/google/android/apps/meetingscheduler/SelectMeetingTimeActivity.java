
package com.google.android.apps.meetingscheduler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.widget.ExpandableListView;

import com.google.api.data.calendar.v2.CalendarApiInfo;

import java.io.NotSerializableException;
import java.io.Serializable;
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
  private Settings settings = new Settings();

  /** The application settings */
  // TODO: Change this to a fully-working not mock implementation, if this needs
  // asynchronous calls we should probably use an AsyncTask
  private EventTimeRetriever eventTimeRetriever;

  private List<Attendee> selectedAttendees;

  private AuthManager auth;

  private ProgressDialog progressBar;

  private Handler handler = new Handler();

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

    auth = new AuthManager(this, MeetingSchedulerConstants.GET_LOGIN, null, true,
        CalendarApiInfo.AUTH_TOKEN_TYPE);

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
      Log.e("Meeting Scheduler", "List<Attendee> selectedAttendees is not serializable");
      throw new NotSerializableException();
    }
    intent.putExtra(SELECTED_ATTENDEES, (Serializable) selectedAttendees);
    Log.e("Meeting Scheduler",
        "Successfully serialized List<Attendee> selectedAttendees in the intent");
    intent.setClass(context, SelectMeetingTimeActivity.class);
    return intent;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, final Intent results) {
    super.onActivityResult(requestCode, resultCode, results);
    switch (requestCode) {
    case MeetingSchedulerConstants.GET_LOGIN: {
      if (resultCode == RESULT_OK && auth != null) {
        auth.authResult(resultCode, results);
      }
      break;
    }
    case MeetingSchedulerConstants.AUTHENTICATED: {
      if (resultCode == RESULT_OK && auth != null)
        authenticated();
      break;
    }
    }
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
   * times and display the result on the scren
   */
  private void authenticated() {
    if (auth.getAuthToken() == null) {
      authenticate();
    } else {
      eventTimeRetriever = new CommonFreeTimesRetriever(new FreeBusyTimesRetriever(
          auth.getAuthToken()));

      new Thread(new Runnable() {
        public void run() {
          // Calculating the available meeting times from the selectedAttendees
          // and
          // the settings
          final List<AvailableMeetingTime> availableMeetingTimes = eventTimeRetriever
              .getAvailableMeetingTime(selectedAttendees, settings);

          // Update the progress bar
          handler.post(new Runnable() {
            public void run() {
              populateMeetings(availableMeetingTimes);
              progressBar.dismiss();
            }
          });
        }
      }).start();
      progressBar = ProgressDialog.show(this, null,
          "Please wait while querying attendees availabilities...", true);
    }
  }

  /**
   * Displays the available meeting times on the screen.
   * 
   * @param availableMeetingTimes The meeting times to display.
   */
  private void populateMeetings(List<AvailableMeetingTime> availableMeetingTimes) {
    // Adding the available meeting times to the UI
    ExpandableListView meetingListContainer = (ExpandableListView) findViewById(R.id.meeting_list);
    meetingListContainer.setAdapter(new EventExpandableListAdapter(this, availableMeetingTimes,
        settings.meetingLength));
  }

}