
package com.google.android.apps.meetingscheduler;

import java.util.Comparator;

/**
 * 
 * Comparator use to sort a list of attendee. The attendees are sort
 * alphabetically and from selected to unselected.
 * 
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class AttendeeComparator implements Comparator<Attendee> {

  /**
   * Comparator instance to avoid allocating a new one each time it is used.
   */
  public static final AttendeeComparator Comparator = new AttendeeComparator();

  @Override
  /**
   * Compare 2 attendees.
   */
  public int compare(Attendee lhs, Attendee rhs) {
    if (lhs.selected == rhs.selected)
      return lhs.name.compareToIgnoreCase(rhs.name);
    else
      // Put selected on top.
      return rhs.selected.compareTo(lhs.selected);
  }
}