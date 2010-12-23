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
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter.FilterListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity Screen where the user selects the meeting attendees.
 * 
 * @author Nicolas Garnier
 */
public class SelectParticipantsActivity extends Activity {

  /** List of attendees that are selectable */
  private List<Attendee> attendees = new ArrayList<Attendee>();

  /** Application settings */
  private Settings settings;

  /** Selected user account */
  private Account account;

  /** ArrayAdapter for the attendees */
  private SelectableAttendeeAdapter attendeeAdapter;

  private EditText editText;

  private ProgressDialog progressBar;

  private Handler handler = new Handler();

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
    setContentView(R.layout.select_participants);

    // Custom title bar
    if (customTitleSupported) {
      getWindow()
          .setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.app_title_select_participants);
    }

    // Adding action to the button
    addFindMeetingButtonListener();

    setAttendeeListView();

    getSettings();
  }

  /**
   * Get Settings
   */
  private void getSettings() {
    Settings.initInstance(this, new Runnable() {
      @Override
      public void run() {
        settings = Settings.getInstance();

        applySettings();
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
          Log.i(MeetingSchedulerConstants.TAG,
              "Find meeting button pressed - about to launch SelectMeeting activity");

          // the results are called on widgetActivityCallback
          try {
            startActivity(SelectMeetingTimeActivity.createViewIntent(getApplicationContext(),
                selectedAttendees));

          } catch (NotSerializableException e) {
            Log.e(MeetingSchedulerConstants.TAG,
                "Intent is not run because of a NotSerializableException. "
                    + "Probably the selectedAttendees list which is not serializable.");
          }
          Log.i(MeetingSchedulerConstants.TAG,
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

    editText = getEditTextFilter();
    attendeeListView.addHeaderView(editText);

    attendeeAdapter = new SelectableAttendeeAdapter(this, attendees);
    attendeeAdapter.sort();

    attendeeListView.setAdapter(attendeeAdapter);

    // Adding click event to attendees Widgets
    attendeeListView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // We use position -1 to ignore the header.
        Attendee attendee = (Attendee) attendeeListView.getItemAtPosition(position);
        attendee.selected = !attendee.selected;
        attendeeAdapter.sort();
      }
    });
  }

  /**
   * Retrieve the list of attendees.
   */
  private void retrieveAttendees() {
    // Retrieves the attendees on a seperate thread.
    new Thread(new Runnable() {
      public void run() {
        AttendeeRetriever attendeeRetriever = new PhoneContactsRetriever(
            SelectParticipantsActivity.this, account);
        final List<Attendee> newAttendees = attendeeRetriever.getPossibleAttendees();

        // Update the progress bar
        handler.post(new Runnable() {
          public void run() {
            if (newAttendees != null) {
              attendees.clear();
              attendees.addAll(newAttendees);

              attendeeAdapter.sort();
              attendeeAdapter.notifyDataSetChanged();
            }

            if (progressBar != null)
              progressBar.dismiss();
          }
        });
      }
    }).start();
    // Show a progress bar while the common free times are computed.
    progressBar = ProgressDialog.show(this, null, getString(R.string.retrieve_contacts_wait_text),
        true);
  }

  /**
   * Add on text changed listener to filter the attendee list view.
   */
  private EditText getEditTextFilter() {
    editText = (EditText) getLayoutInflater().inflate(R.layout.participants_text_filter, null);

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
    startActivity(ShowPreferencesActivity.createViewIntent(getApplicationContext()));
    Log.i(MeetingSchedulerConstants.TAG, "Successfully launched ShowPreferencesActivity");
  }

  /**
   * Sets the help text beside the Find Meetings button
   */
  private void setFindMeetingButtonText() {
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

    if (settings != null) {
      settings.reload(this);
      applySettings();
    }
  }

  /**
   * 
   */
  private void applySettings() {
    if (settings.getAccount() == null) {
      finish();
      return;
    }
    if (!settings.getAccount().equals(account)) {
      account = settings.getAccount();
      retrieveAttendees();
    }
    setFindMeetingButtonText();
  }
}
