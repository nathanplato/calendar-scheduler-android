
package com.google.android.apps.meetingscheduler;

import android.accounts.Account;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Filter.FilterListener;
import android.widget.ListView;

import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity Screen where the user selects the meeting attendees.
 *
 * @author Nicolas Garnier
 */
public class SelectParticipantsActivity extends Activity {

  /** The Attendee Retriever */
  // TODO: Change this to a fully-working not mock implementation, if this needs
  // asynchronous calls we should probably use an AsyncTask
  private AttendeeRetriever attendeeRetriever;// = new MockAttendeeRetriever();

  /** List of attendees that are selectable */
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

    AccountChooser.getInstance().chooseAccount(this, new AccountChooser.AccountHandler() {
      @Override
      public void handleAccountSelected(Account account) {
        // Set the attendee retriever with the selected account.
        attendeeRetriever = new PhoneContactsRetriever(SelectParticipantsActivity.this, account);

        // Adding selectable attendees
        retrieveAttendee();
      }
    });

    // Adding listener to the EditText filter.
    addEditTextListener();

    // Adding action to the button
    addFindMeetingButtonListener();
  }

  /**
   * Add the OnClckListner to the findMeetingButton.
   */
  private void addFindMeetingButtonListener() {
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
        } else {
          Toast toast = Toast.makeText(getApplicationContext(),
              "You have to select at least 1 participant", 1000);
          toast.show();
        }
      }
    });
  }

  /**
   * Populate the list of attendees into the activity's ListView.
   */
  private void setAttendeeListView() {
    ListView attendeeListView = (ListView) findViewById(R.id.attendee_list);

    attendeeAdapter = new SelectableAttendeeAdapter(this, attendees);
    attendeeAdapter.sort();

    attendeeListView.setAdapter(attendeeAdapter);

    // Adding click event to attendees Widgets
    attendeeListView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Attendee attendee = attendeeAdapter.getItem(position);
        attendee.selected = !attendee.selected;
        attendeeAdapter.sort();
      }
    });
  }

  /**
   * Retrieve the list of attendees.
   */
  private void retrieveAttendee() {
    attendees = attendeeRetriever.getPossibleAttendees();

    // TODO: might need to move this in the asynchronous call's callback.
    if (attendees != null)
      setAttendeeListView();
  }

  /**
   * Add on text changed listener to filter the attendee list view.
   */
  private void addEditTextListener() {
    EditText editText = (EditText) findViewById(R.id.filter);

    editText.addTextChangedListener(new TextWatcher() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (attendeeAdapter != null) {
          attendeeAdapter.getFilter().filter(s, new FilterListener() {
            @Override
            /**
             * Sort the array once the filter has been completed.
             */
            public void onFilterComplete(int count) {
              attendeeAdapter.sort();
            }
          });
        }

      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {
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

    if (attendees != null) {
      for (Attendee attendee : attendees) {
        if (attendee.selected)
          selectedAttendees.add(attendee);
      }
    }

    return selectedAttendees;
  }

}