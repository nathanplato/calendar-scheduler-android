
package com.google.android.apps.meetingscheduler;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class SelectableAttendeeAdapter extends ArrayAdapter<Attendee> {

  private int resource;

  public SelectableAttendeeAdapter(Context context, int resource, List<Attendee> items) {
    super(context, resource, items);
    this.resource = resource;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final ArrayAdapter<Attendee> adapter = this;
    Attendee item = getItem(position);
    LinearLayout attendeeView;

    if (convertView == null) {
      attendeeView = new LinearLayout(getContext());
      String inflater = Context.LAYOUT_INFLATER_SERVICE;
      LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
      vi.inflate(resource, attendeeView, true);
    } else {
      attendeeView = (LinearLayout) convertView;
    }

    attendeeView.setTag(item);
    attendeeView.setClickable(true);
    attendeeView.setFocusable(true);

    attendeeView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Attendee attendee = (Attendee) v.getTag();

        attendee.selected = !attendee.selected;
        adapter.sort(new AttendeeComparator());
        adapter.notifyDataSetChanged();
      }
    });

    TextView nameView = (TextView) attendeeView.findViewById(R.id.attendee_name);
    ImageView photoView = (ImageView) attendeeView.findViewById(R.id.attendee_photo);
    CheckBox checkBoxView = (CheckBox) attendeeView.findViewById(R.id.attendee_checkbox);

    nameView.setText(item.name);

    // TODO(alainv): Change this or use other type to store attendee's photo.
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
