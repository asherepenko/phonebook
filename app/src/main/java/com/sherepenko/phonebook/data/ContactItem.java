package com.sherepenko.phonebook.data;

import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import static com.sherepenko.phonebook.data.DatabaseContract.ContactsEntry;

public class ContactItem extends DataItem {

    public ContactItem() {
    }

    public ContactItem(@NonNull Cursor c) {
        super(c);
    }

    public ContactItem(@NonNull Parcel in) {
        super(in);
    }

    @Override
    public Uri getUri() {
        return ContactsEntry.buildUri(mId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<ContactItem> CREATOR = new Parcelable.Creator<ContactItem>() {
        @NonNull
        public ContactItem createFromParcel(Parcel in) {
            return new ContactItem(in);
        }

        @NonNull
        public ContactItem[] newArray(int size) {
            return new ContactItem[size];
        }
    };
}
