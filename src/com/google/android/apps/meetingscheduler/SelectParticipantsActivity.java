
package com.google.android.apps.meetingscheduler;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter.FilterListener;
import android.widget.ListView;

import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Activity Screen where the user selects the meeting attendees.
 * 
 * @since 2.2
 * @author Nicolas Garnier (nivco@google.com)
 */
public class SelectParticipantsActivity extends Activity {

  /** The Attendee Retriever */
  private AttendeeRetriever attendeeRetriever = new MockAttendeeRetriever();

  /** List of widgets used to select attendees */
  private List<Attendee> attendees = new ArrayList<Attendee>();

  /** ArrayAdapter for the attendees */
  private SelectableAttendeeAdapter attendeeAdapter;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

    // Creating main layout
    setContentView(R.layout.select_participants);

    // Custom title bar
    if (customTitleSupported) {
      getWindow()
          .setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.app_title_select_participants);
    }

    // Adding selectable attendees
    attendees = attendeeRetriever.getPossibleAttendees();
    Collections.sort(attendees, AttendeeComparator.Comparator);

    attendeeAdapter = new SelectableAttendeeAdapter(this, R.layout.selectable_attendee, attendees);

    ListView attendeeListView = (ListView) findViewById(R.id.attendee_list);
    attendeeListView.setAdapter(attendeeAdapter);

    // Adding listener to the EditText filter.
    EditText editText = (EditText) findViewById(R.id.filter);

    editText.addTextChangedListener(new TextWatcher() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

        attendeeAdapter.getFilter().filter(s, new FilterListener() {
          @Override
          public void onFilterComplete(int count) {
            attendeeAdapter.sort(AttendeeComparator.Comparator);
            attendeeAdapter.notifyDataSetChanged();
          }
        });

      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });

    // Adding action to the button
    Button findMeetingButton = (Button) findViewById(R.id.find_time_button);
    findMeetingButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        List<Attendee> selectedAttendees = getSelectedAttendees();
        if (selectedAttendees.size() > 0) {
          Log.i("Meeting Scheduler",
              "Find meeting button pressed - about to launch SelectMeeting activity");
          // the results are called on widgetActivityCallback
          try {
            startActivity(SelectMeetingTimeActivity.createViewIntent(getApplicationContext(),
                selectedAttendees));

          } catch (NotSerializableException e) {
            Log.e("Meeting Scheduler", "Intent isnot run because of a NotSerializableException. "
                + "Probably the selectedAttendees list which is not serializable.");
          }
          Log.i("Meeting Scheduler",
              "Find meeting button pressed - successfully launched SelectMeeting activity");
        }
      }
    });
  }

  /**
   * Returns the list of currently selected attendees.
   * 
   * @return the list of currently selected attendees
   */
  private List<Attendee> getSelectedAttendees() {
    List<Attendee> selectedAttendees = new ArrayList<Attendee>();

    for (Attendee attendee : attendees) {
      if (attendee.selected)
        selectedAttendees.add(attendee);
    }
    return selectedAttendees;
  }

}