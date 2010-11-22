
package com.google.android.apps.meetingscheduler;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

//TODO(alainv): Write Javadoc for this class
public class SelectableAttendeeAdapter extends ArrayAdapter<Attendee> {

  public SelectableAttendeeAdapter(Context context, List<Attendee> items) {
    super(context, R.layout.selectable_attendee, items);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    Attendee item = getItem(position);
    LinearLayout attendeeView;

    if (convertView == null) {
      attendeeView = new LinearLayout(getContext());
      String inflater = Context.LAYOUT_INFLATER_SERVICE;
      LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
      vi.inflate(R.layout.selectable_attendee, attendeeView, true);
    } else {
      attendeeView = (LinearLayout) convertView;
    }

    attendeeView.setTag(item);

    TextView nameView = (TextView) attendeeView.findViewById(R.id.attendee_name);
    ImageView photoView = (ImageView) attendeeView.findViewById(R.id.attendee_photo);
    CheckBox checkBoxView = (CheckBox) attendeeView.findViewById(R.id.attendee_checkbox);

    nameView.setText(item.name);

    // TODO(alainv): Change this or use other type to store attendee's photo,
    // e.g URI?.
    if (item.photo == null) {
      photoView.setImageResource(R.drawable.attendee_icon);
    } else {
      photoView.setImageDrawable(item.photo.getDrawable());
    }

    checkBoxView.setChecked(item.selected);
    if (checkBoxView.isChecked()) {
      attendeeView.setBackgroundResource(R.color.selected_attendee_background);
    } else {
      attendeeView.setBackgroundColor(Color.TRANSPARENT);
    }

    return attendeeView;
  }
}
