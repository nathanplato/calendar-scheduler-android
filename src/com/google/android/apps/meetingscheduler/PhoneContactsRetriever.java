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
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Get the contacts from the phone for the selected account.
 * 
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class PhoneContactsRetriever implements AttendeeRetriever {

  private Activity activity;
  private Account account;

  public PhoneContactsRetriever(Activity activity, Account account) {
    this.activity = activity;
    this.account = account;
  }

  @Override
  public List<Attendee> getPossibleAttendees() {
    List<Attendee> result = new ArrayList<Attendee>();
    ContentResolver cr = activity.getContentResolver();
    Cursor cur = cr.query(ContactsContract.RawContacts.CONTENT_URI, new String[] {
        RawContacts.CONTACT_ID, Contacts.DISPLAY_NAME }, RawContacts.ACCOUNT_NAME + " = '"
        + account.name + "' AND " + RawContacts.DELETED + " = '0'", null, null);

    if (cur.getCount() > 0) {
      while (cur.moveToNext()) {
        long id = cur.getLong(cur.getColumnIndex(Email.CONTACT_ID));
        String email = getEmail(cr, id);

        if (email != null) {
          String name = cur.getString(cur.getColumnIndex(Contacts.DISPLAY_NAME));
          String imageUri = getPhotoUri(cr, id);

          result.add(new Attendee(name, email, imageUri));
        }
      }
      cur.close();
    } else
      Log.e(MeetingSchedulerConstants.TAG, "No contacts found.");

    Attendee current = getCurrentUser();
    current.selected = true;
    result.add(current);

    return result;
  }

  @Override
  public Attendee getCurrentUser() {
    return new Attendee(account.name, account.name, null);
  }

  /**
   * Get the correct email address to use for the current contact.
   * 
   * @param cr
   * @param id
   * @return
   */
  private String getEmail(ContentResolver cr, long id) {
    Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, new String[] {
        Email.DATA, Email.IS_PRIMARY }, Email.CONTACT_ID + " = '" + id + "'", null,
        Email.IS_PRIMARY + " DESC");
    String result = null;

    if (cur.getCount() > 0) {
      while (cur.moveToNext()) {
        String email = cur.getString(cur.getColumnIndex(Email.DATA));

        if (isSameDomain(account.name, email))
          return email;
        else if (isSameDomain("@gmail.com", email) && result == null)
          result = email;
      }
      if (result == null) {
        if (cur.moveToFirst()) {
          result = cur.getString(cur.getColumnIndex(Email.DATA));
        }
      }
      cur.close();
    }

    return result;
  }

  /**
   * Check if two emails are of the same domain.
   * 
   * @param lhs
   * @param rhs
   * @return
   */
  private boolean isSameDomain(String lhs, String rhs) {
    return lhs.substring(lhs.indexOf('@')).equalsIgnoreCase(rhs.substring(rhs.indexOf('@')));
  }

  /**
   * Get the contact's Photo URI if it exists.
   * 
   * @param cr
   * @param id
   * @return The contact's Photo URI.
   */
  private String getPhotoUri(ContentResolver cr, long id) {
    Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, id);
    Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
    Cursor cursor = cr.query(photoUri, new String[] { Contacts.Photo.DATA15 }, null, null, null);
    String result = null;

    if (cursor != null) {
      if (cursor.moveToFirst()) {
        byte[] data = cursor.getBlob(0);
        if (data != null) {
          result = photoUri.toString();
        }
      }
      cursor.close();
    }

    return result;
  }

}
