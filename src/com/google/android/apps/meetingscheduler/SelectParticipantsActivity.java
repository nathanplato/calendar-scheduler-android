
package com.google.android.apps.meetingscheduler;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

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
  private List<SelectableAttendeeWidget> selectableAttendeeWidgets =
      new ArrayList<SelectableAttendeeWidget>();

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

    // Creating main layout
    setContentView(R.layout.main);

    // Custom title bar
    if (customTitleSupported) {
      getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.app_title);
    }

    // Adding selectable attendees
    selectableAttendeeWidgets = createSelectableAttendeeWidgetList();
    LinearLayout attendeeListContainer = (LinearLayout) findViewById(R.id.attendee_list);
    for (SelectableAttendeeWidget widget : selectableAttendeeWidgets) {
      attendeeListContainer.addView(widget);
    }

    // Adding action to the button
    Button findMeetingButton = (Button) findViewById(R.id.find_time_button);
    findMeetingButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        List<Attendee> selectedAttendees = getSelectedAttendees();
        if(selectedAttendees.size() > 0) {

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
    for (SelectableAttendeeWidget widget : selectableAttendeeWidgets) {
      if (widget.isSelected()) {
        selectedAttendees.add(widget.getAttendee());
      }
    }
    return selectedAttendees;
  }

  /**
   * Returns the list of selectable attendees widgets.
   *
   * @returns the list of selectable attendees widgets.
   */
  private List<SelectableAttendeeWidget> createSelectableAttendeeWidgetList() {
    List<SelectableAttendeeWidget> selectableAttendeeWidgets = new ArrayList<SelectableAttendeeWidget>();
    SelectableAttendeeWidget currentUser = new SelectableAttendeeWidget(attendeeRetriever
        .getCurrentUser(), getApplicationContext());
    currentUser.setSelected(true);
    selectableAttendeeWidgets.add(currentUser);
    for (Attendee attendee : attendeeRetriever.getPossibleAttendees()) {
      selectableAttendeeWidgets
          .add(new SelectableAttendeeWidget(attendee, getApplicationContext()));
    }
    return selectableAttendeeWidgets;
  }
}