package com.sherepenko.phonebook.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.support.annotation.NonNull;

public final class DatabaseContract {

    public static final String AUTHORITY = "com.sherepenko.phonebook.app";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final String CONTACTS_PATH = "contacts";

    public static final class ContactsEntry implements BaseEntry {
        public static final Uri CONTENT_URI = BASE_URI.buildUpon().appendPath(CONTACTS_PATH).build();

        public static final String TABLE_NAME = "contacts";

        public static final String DIR_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + CONTACTS_PATH;
        public static final String ITEM_CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + CONTACTS_PATH;

        @NonNull
        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
