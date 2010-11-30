
package com.google.android.apps.meetingscheduler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ExpandableListView;

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
  private EventTimeRetriever eventTimeRetriever = new CommonFreeTimesRetriever(
      new MockBusyTimeRetriever());

  /** Called when the activity is first created. */
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
    @SuppressWarnings("unchecked")
    List<Attendee> selectedAttendees = (List<Attendee>) intent
        .getSerializableExtra(SELECTED_ATTENDEES);

    // Calculating the available meeting times from the selectedAttendees and
    // the settings
    List<AvailableMeetingTime> availableMeetingTimes = eventTimeRetriever.getAvailableMeetingTime(
        selectedAttendees, settings);

    // Adding the available meeting times to the UI
    ExpandableListView meetingListContainer = (ExpandableListView) findViewById(R.id.meeting_list);
    meetingListContainer.setAdapter(new EventExpandableListAdapter(this, availableMeetingTimes));

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
}