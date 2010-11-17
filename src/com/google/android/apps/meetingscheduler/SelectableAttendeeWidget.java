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
import android.graphics.Color;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Selectable Attendee List item.
 *
 * @since 2.2
 * @author Nicolas Garnier (nivco@google.com)
 */
public class SelectableAttendeeWidget extends LinearLayout {

  private ImageView photo;
  private Attendee attendee;
  private CheckBox checkBox;
  private TextView tv;

  public SelectableAttendeeWidget(Attendee attendee, Context context) {
    super(context);
    this.attendee = attendee;
    setOrientation(VERTICAL);

    LinearLayout horizontalLayout = new LinearLayout(context);
    LayoutParams lpHl = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    horizontalLayout.setLayoutParams(lpHl);
    addView(horizontalLayout);

    // Adding the Photo
    if(attendee.photo == null){
      photo = new ImageView(context);
      photo.setImageResource(R.drawable.attendee_icon);
    }else{
      photo = attendee.photo;
    }
    horizontalLayout.addView(photo);

    // Adding the name
    tv = new TextView(context);
    tv.setText(attendee.name + " (" + attendee.email + ")");
    tv.setPadding(15, 0, 0, 0);
    LayoutParams lpTv = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    lpTv.gravity = Gravity.CENTER_VERTICAL;
    lpTv.weight = 1;
    tv.setLayoutParams(lpTv);
    tv.setSingleLine(true);
    tv.setEllipsize(TruncateAt.MARQUEE);
    horizontalLayout.addView(tv);

    // Adding the checkbox
    checkBox = new CheckBox(context);
    LayoutParams lpCb = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    lpCb.gravity = Gravity.RIGHT;
    checkBox.setLayoutParams(lpCb);
    horizontalLayout.addView(checkBox);
    checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setBackgroundColor();
      }
    });

    // Adding the nice little border
    LinearLayout border = new LinearLayout(context);
    border.setBackgroundColor(Color.argb(100, 50, 50, 50));
    LayoutParams lpLl = new LayoutParams(LayoutParams.FILL_PARENT, 1);
    border.setLayoutParams(lpLl);
    addView(border);

    // On click trigger the checkbox
    setClickable(true);
    setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        checkBox.setChecked(!checkBox.isChecked());
        setBackgroundColor();
      }
    });
  }

  /**
   * Sets the background color according to the selected state of the Checkbox.
   */
  private void setBackgroundColor() {
    if (checkBox.isChecked()) {
      setBackgroundColor(Color.argb(100, 200, 200, 200));
    } else {
      setBackgroundColor(Color.TRANSPARENT);
    }
  }

  /**
   * Returns the attendee selectable by this widget.
   *
   * @return The attendee selectable by this widget
   */
  public Attendee getAttendee() {
    return attendee;
  }

  /**
   * Returns true if the Attendee is selected for the meeting.
   *
   * @return true if the Attendee is selected for the meeting
   */
  @Override
  public boolean isSelected(){
    return checkBox.isChecked();
  }

  /**
   * Sets if the Attendee is selected for the meeting.
   *
   * @param selected true if the Attendee is selected for the meeting
   */
  @Override
  public void setSelected(boolean selected){
    checkBox.setChecked(selected);
    setBackgroundColor();
  }

}
