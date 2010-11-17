
package com.google.android.apps.meetingscheduler;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class SelectParticipantsActivity extends Activity {

  /** The Attendee Retriever */
  private AttendeeRetriever attendeeRetriever = new MockAttendeeRetriever();

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    LinearLayout attendeeListContainer = (LinearLayout) findViewById(R.id.attendee_list);
    for (Attendee attendee : attendeeRetriever.getPossibleAttendees()) {
      attendeeListContainer.addView(new SelectableAttendeeWidget(attendee, getApplicationContext()));
    }

  }
}