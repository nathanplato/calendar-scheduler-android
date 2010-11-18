
package com.google.android.apps.meetingscheduler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity Screen where the user selects the meeting time betweent he meeting
 * times proposed.
 *
 * @since 2.2
 * @author Nicolas Garnier (nivco@google.com)
 */
public class SelectMeetingTimeActivity extends Activity {

  /** The constant to store the selectedAttendees list in an intent */
  private static final String SELECTED_ATTENDEES = "SELECTED_ATTENDEES";

  /** The list of selected Attendees */
  private List<Attendee> selectedAttendees;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    Log.i("Meeting Scheduler", "OKOK");

    // Creating main layout
    setContentView(R.layout.select_meeting_time);

    // Custom title bar
    if (customTitleSupported) {
      getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.app_title_select_time);
    }

    // Getting the selectedAttendees list
    final Intent intent = getIntent();
    @SuppressWarnings("unchecked")
    List<Attendee> selectedAttendees = (List<Attendee>) intent
        .getSerializableExtra(SELECTED_ATTENDEES);
    this.selectedAttendees = selectedAttendees;

    // Adding the available meeting times to the UI
    LinearLayout meetingListContainer = (LinearLayout) findViewById(R.id.meeting_list);
    Map<Date, List<AvailableMeetingTime>> availableMeetingTimeWidgets = createAvailableMeetingTimeWidgets();
    List<Date> sortedDatesWithMeetings = asSortedList(availableMeetingTimeWidgets.keySet());
    for (Date date : sortedDatesWithMeetings) {
      ExpandableListView dateContainer = new ExpandableListView(getApplicationContext());
      meetingListContainer.addView(dateContainer);
    }

  }

  /**
   * Sort a Collection based on its default comparator.
   *
   * @param c The collection to sort
   * @return The sorted collection as a List
   */
  public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
    List<T> list = new ArrayList<T>(c);
    java.util.Collections.sort(list);
    return list;
  }

  /**
   * @return
   */
  private Map<Date, List<AvailableMeetingTime>> createAvailableMeetingTimeWidgets() {
    // TODO Auto-generated method stub
    return new HashMap<Date, List<AvailableMeetingTime>>();
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