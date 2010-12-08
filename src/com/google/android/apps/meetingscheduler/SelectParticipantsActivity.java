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

import android.accounts.Account;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Filter.FilterListener;

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
  private AttendeeRetriever attendeeRetriever;

  /** List of attendees that are selectable */
  private List<Attendee> attendees = new ArrayList<Attendee>();

  /** ArrayAdapter for the attendees */
  private SelectableAttendeeAdapter attendeeAdapter;

  private EditText editText;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

    // Creating main layout
    setContentView(R.layout.select_participants);

    // Custom title bar
    if (customTitleSupported) {
      getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
          R.layout.app_title_select_participants);
    }

    // Adding action to the button
    addFindMeetingButtonListener();

    AccountChooser.getInstance().chooseAccount(this,
        new AccountChooser.AccountHandler() {
          @Override
          public void handleAccountSelected(Account account) {
            if (account != null) {
              // Set the attendee retriever with the selected account.
              attendeeRetriever = new PhoneContactsRetriever(
                  SelectParticipantsActivity.this, account);

              // Adding selectable attendees
              retrieveAttendee();
            } else {
              SelectParticipantsActivity.this.finish();
            }
          }
        });
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
          Log
              .i(MeetingSchedulerConstants.TAG,
                  "Find meeting button pressed - about to launch SelectMeeting activity");

          // the results are called on widgetActivityCallback
          try {
            startActivity(SelectMeetingTimeActivity.createViewIntent(
                getApplicationContext(), selectedAttendees));

          } catch (NotSerializableException e) {
            Log
                .e(
                    MeetingSchedulerConstants.TAG,
                    "Intent is not run because of a NotSerializableException. "
                        + "Probably the selectedAttendees list which is not serializable.");
          }
          Log
              .i(MeetingSchedulerConstants.TAG,
                  "Find meeting button pressed - successfully launched SelectMeeting activity");
        } else {
          Toast toast = Toast.makeText(getApplicationContext(),
              "You must select at least 1 participant", 1000);
          toast.show();
        }
      }
    });
  }

  /**
   * Populate the list of attendees into the activity's ListView.
   */
  private void setAttendeeListView() {
    final ListView attendeeListView = (ListView) findViewById(R.id.attendee_list);

    attendeeAdapter = new SelectableAttendeeAdapter(this, attendees);
    attendeeAdapter.sort();

    editText = getEditTextFilter();

    attendeeListView.addHeaderView(editText);

    attendeeListView.setAdapter(attendeeAdapter);

    // Adding click event to attendees Widgets
    attendeeListView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id) {
        // We use position -1 to ignore the header.
        Attendee attendee = (Attendee) attendeeListView
            .getItemAtPosition(position);
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

    if (attendees != null)
      setAttendeeListView();
  }

  /**
   * Add on text changed listener to filter the attendee list view.
   */
  private EditText getEditTextFilter() {
    editText = (EditText) getLayoutInflater().inflate(
        R.layout.participants_text_filter, null);

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
      public void beforeTextChanged(CharSequence s, int start, int count,
          int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });

    return editText;
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

  /**
   * Initialize the contents of the Activity's options menu.
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.settings, menu);
    return true;
  }

  /**
   * Called whenever an item in the options menu is selected.
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.menu.settings:
      showPreferences();
      return true;
    default:
      return super.onOptionsItemSelected(item);
    }
  }

  /**
   * Starts the ShowPreferencesActivity
   */
  private void showPreferences() {
    startActivity(ShowPreferencesActivity
        .createViewIntent(getApplicationContext()));
    Log.i(MeetingSchedulerConstants.TAG,
        "Successfully launched ShowPreferencesActivity");
  }

  /**
   * Sets the help text beside the Find Meetings button
   */
  private void setFindMeetingButtonText() {
    Settings settings = Settings.getInstance(getApplicationContext());
    StringBuilder string = new StringBuilder();

    int meetingLength = settings.getMeetingLength();
    String meetingLengthText = null;
    if (meetingLength < 60) {
      meetingLengthText = meetingLength + " minute";
    } else {
      meetingLengthText = (float) meetingLength / 60 + " hour";
    }
    string.append("Fix " + meetingLengthText + " meetings\n");

    int timeSpan = settings.getTimeSpan();
    String timeSpanText = null;
    if (timeSpan == 7 || timeSpan == 14) {
      timeSpanText = timeSpan / 7 + " week(s)";
    } else {
      timeSpanText = "the next one month";
    }
    string.append("over " + timeSpanText + "\n");

    boolean useWorkingHours = settings.doUseWorkingHours();
    String useWorkingHoursText = null;
    if (useWorkingHours) {
      useWorkingHoursText = "within working hours";
    } else {
      useWorkingHoursText = "regardless of working hours";
    }
    string.append(useWorkingHoursText);
    
    TextView settingsText = (TextView) findViewById(R.id.find_time_button_text);
    settingsText.setText(string.toString());
  }

  /**
   * Update the settings text whenever this activity resumes
   */
  @Override
  protected void onResume() {
    super.onResume();
    setFindMeetingButtonText();
  }

}