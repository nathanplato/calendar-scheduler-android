
package com.google.android.apps.meetingscheduler;

import java.util.Comparator;

public class AttendeeComparator implements Comparator<Attendee> {

  @Override
  public int compare(Attendee lhs, Attendee rhs) {
    if (lhs.selected == rhs.selected)
      return lhs.name.compareTo(rhs.name);
    else
      // Put selected on top.
      return rhs.selected.compareTo(lhs.selected);
  }
}