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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Adapts the Meeting data to the ExpendableListView.
 *
 * @author Nicolas Garnier
 */
public class EventExpandableListAdapter extends BaseExpandableListAdapter {

  /** The Application Context */
  private Context context;

  /** The lit of AvailableMeetingTime mapped by Days */
  private Map<Date, List<AvailableMeetingTime>> sortedEventsByDays;

  /** The sorted list of Days with AvailableMeetingTime in them */
  private List<Date> sortedDays;

  /** Inflater used to create Views from layouts */
  private LayoutInflater inflater;

  /**
   * Constructs a new EventExpandableListAdapter given the List of Dates
   *
   * @param context The context of the application
   * @param availableMeetingTimes All the times for which a meeting is possible
   *          for the attendees
   */
  public EventExpandableListAdapter(Context context,
      List<AvailableMeetingTime> availableMeetingTimes) {
    this.context = context;

    sortedEventsByDays = sortEventsByDay(availableMeetingTimes);
    sortedDays = asSortedList(sortedEventsByDays.keySet());

    inflater = LayoutInflater.from(context);
  }

  /**
   * Sorts a Collection and returns it as a Sorted List.
   *
   * @param c the collection to sort
   * @return The sorted collection as a List
   */
  public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
    List<T> list = new ArrayList<T>(c);
    java.util.Collections.sort(list);
    return list;
  }

  /**
   * Parses the lit of AvailableMeetingTime to map then by day they take place
   * in the current time zone.
   *
   * @param availableMeetingTimes The list of AvailableMeetingTime
   * @return lists of AvailableMeetingTime mapped by day they take place
   */
  private Map<Date, List<AvailableMeetingTime>> sortEventsByDay(
      List<AvailableMeetingTime> availableMeetingTimes) {
    Map<Date, List<AvailableMeetingTime>> sortedEventsByDays = new HashMap<Date, List<AvailableMeetingTime>>();

    for (AvailableMeetingTime availableMeetingTime : availableMeetingTimes) {
      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime(availableMeetingTime.start);
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.clear(Calendar.HOUR);
      calendar.clear(Calendar.MINUTE);
      calendar.clear(Calendar.SECOND);
      calendar.clear(Calendar.MILLISECOND);
      Date day = calendar.getTime();

      List<AvailableMeetingTime> meetingTimes = sortedEventsByDays.get(day);
      if (meetingTimes == null) {
        meetingTimes = new ArrayList<AvailableMeetingTime>();
        sortedEventsByDays.put(day, meetingTimes);
      }
      meetingTimes.add(availableMeetingTime);
    }
    return sortedEventsByDays;
  }

  public AvailableMeetingTime getChild(int groupPosition, int childPosition) {
    return sortedEventsByDays.get(sortedDays.get(groupPosition)).get(childPosition);
  }

  public long getChildId(int groupPosition, int childPosition) {
    return childPosition;
  }

  public int getChildrenCount(int groupPosition) {
    try {
      return sortedEventsByDays.get(sortedDays.get(groupPosition)).size();
    } catch (Exception e) {
    }
    return 0;
  }

  public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
      View convertView, ViewGroup parent) {
    // Creating the Widget from layout
    View view = inflater.inflate(R.layout.meeting_time_result_entry, null);
    // Setting time of meeting
    final TextView text = (TextView) view.findViewById(R.id.meeting_time_item_text);
    final Date startDate = getChild(groupPosition, childPosition).start;
    final Date endDate = getChild(groupPosition, childPosition).end;
    final List<Attendee> attendees = getChild(groupPosition, childPosition).attendees;
    String dateStart = DateUtils.formatDateTime(context, startDate.getTime(),
        DateUtils.FORMAT_SHOW_TIME);
    String dateEnd = DateUtils.formatDateTime(context, endDate.getTime(),
        DateUtils.FORMAT_SHOW_TIME);
    text.setText(dateStart + " - " + dateEnd);
    // Adding Action to button
    Button button = (Button) view.findViewById(R.id.meeting_time_create_button);
    button.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        // TODO: fire an intent of the Google Calendar App to create an event.
        //       If the Calendar App is not installed fire the intent below
        //       which redirects to the Web UI to create an event.
        String attendeesEmails = attendees.get(0).email;
        for (int i = 1; i < attendees.size(); i++) {
          attendeesEmails += "," + attendees.get(i).email;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String createEventUrl = "https://www.google.com/calendar/render?" + "action=TEMPLATE"
            + "&dates=" + sdf.format(startDate) + "/" + sdf.format(endDate) + "&add="
            + attendeesEmails + "&crm=BUSY" + "&gsessionid=OK" + "&sf=true" + "&output=xml";
        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(createEventUrl));

        context.startActivity(browse);
      }
    });
    return view;
  }

  public Date getGroup(int groupPosition) {
    return sortedDays.get(groupPosition);
  }

  public int getGroupCount() {
    return sortedDays.size();
  }

  public long getGroupId(int groupPosition) {
    return groupPosition;
  }

  public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
    View view = inflater.inflate(R.layout.meeting_time_result_group_title, null);
    TextView title = (TextView) view.findViewById(R.id.meeting_time_group_title);
    String date = DateUtils.formatDateTime(context, getGroup(groupPosition).getTime(),
        DateUtils.FORMAT_SHOW_DATE + DateUtils.FORMAT_SHOW_WEEKDAY + DateUtils.FORMAT_SHOW_YEAR);
    title.setText(date + " (" + getChildrenCount(groupPosition) + ")");
    return view;
  }

  public boolean isChildSelectable(int groupPosition, int childPosition) {
    return true;
  }

  public boolean hasStableIds() {
    return true;
  }

}
