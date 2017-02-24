package com.sherepenko.phonebook.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public abstract class DataItem implements Parcelable, Comparable<DataItem> {

    public static final long NO_ID = -1;

    protected final long mId;

    protected String mFirstName;
    protected String mLastName;
    protected List<String> mPhoneNumbers = new ArrayList<>();

    public DataItem() {
        this(NO_ID);
    }

    public DataItem(@NonNull Cursor c) {
        this(c.getLong(c.getColumnIndex(BaseEntry._ID)));

        mFirstName = c.getString(c.getColumnIndex(BaseEntry.COLUMN_FIRST_NAME));
        mLastName = c.getString(c.getColumnIndex(BaseEntry.COLUMN_LAST_NAME));
        mPhoneNumbers = new Gson().fromJson(
                c.getString(c.getColumnIndex(BaseEntry.COLUMN_PHONE_NUMBERS)),
                new TypeToken<List<String>>() {}.getType());
    }

    public DataItem(@NonNull Parcel in) {
        this(in.readLong());
        mFirstName = in.readString();
        mLastName = in.readString();
        in.readStringList(mPhoneNumbers);
    }

    private DataItem(long id) {
        mId = id;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(mId);
        out.writeString(mFirstName);
        out.writeString(mLastName);
        out.writeStringList(mPhoneNumbers);
    }

    @Override
    public int compareTo(@NonNull DataItem item) {
        return mId > item.getId() ? 1 : (mId < item.getId() ? -1 : 0);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (other == null) {
            return false;
        } else if (getClass() != other.getClass()) {
            return false;
        }

        DataItem item = (DataItem) other;

        return mId == item.getId() &&
                TextUtils.equals(mFirstName, item.getFirstName()) &&
                TextUtils.equals(mLastName, item.getLastName());
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (int)(mId ^ (mId >>> 32));
        result = 31 * result + (mFirstName != null ? mFirstName.hashCode() : 0);
        result = 31 * result + (mLastName != null ? mLastName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ID: " + mId + "; " +
                "First Name: " + mFirstName + "; " +
                "Last Name: " + mLastName;
    }

    public final long getId() {
        return mId;
    }

    public final String getFirstName() {
        return mFirstName;
    }

    public final void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public final String getLastName() {
        return mLastName;
    }

    public final void setLastName(String lastName) {
        mLastName = lastName;
    }

    public final String getFullName() {
        return (mFirstName + " " + mLastName).trim();
    }

    public final List<String> getPhoneNumbers() {
        return mPhoneNumbers;
    }

    public final void addPhoneNumber(String phoneNumber) {
        mPhoneNumbers.add(phoneNumber);
    }

    public final void setPhoneNumbers(@NonNull List<String> phoneNumbers) {
        mPhoneNumbers = phoneNumbers;
    }

    @NonNull
    public ContentValues toValues() {
        ContentValues values = new ContentValues();
        values.put(BaseEntry.COLUMN_FIRST_NAME, mFirstName);
        values.put(BaseEntry.COLUMN_LAST_NAME, mLastName);
        values.put(BaseEntry.COLUMN_PHONE_NUMBERS, new Gson().toJson(mPhoneNumbers));

        return values;
    }

    public abstract Uri getUri();
}
