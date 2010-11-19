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

import android.widget.ImageView;

import java.io.Serializable;

/**
 * Represent an attendee or a potential attendee to a meeting.
 * 
 * @since 2.2
 * @author Nicolas Garnier (nivco@google.com)
 */
public class Attendee implements Serializable {

  /** For serialization purposes */
  private static final long serialVersionUID = 1L;

  /** Photo of the participant */
  public ImageView photo;

  /** Display name of the participant */
  public String name;

  /** Email of the calendar of the participant */
  public String email;

  /** Is the attendee selected? */
  public Boolean selected;

  /**
   * Default Constructor.
   */
  public Attendee() {
  }

  /**
   * Constructor that initializes the attributes.
   * 
   * @param name The name of the attendee
   * @param email The email of the calendar of the attendee
   * @param photo The photo of the attendee
   */
  public Attendee(String name, String email, ImageView photo) {
    this.name = name;
    this.email = email;
    this.photo = photo;
    this.selected = false;
  }

  @Override
  public String toString() {
    return name;
  }
}
