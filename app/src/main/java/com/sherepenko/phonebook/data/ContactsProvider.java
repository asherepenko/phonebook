package com.sherepenko.phonebook.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Arrays;

import static com.sherepenko.phonebook.data.DatabaseContract.AUTHORITY;
import static com.sherepenko.phonebook.data.DatabaseContract.CONTACTS_PATH;
import static com.sherepenko.phonebook.data.DatabaseContract.ContactsEntry;

public class ContactsProvider extends ContentProvider {

    private static final String TAG = "NewsContentProvider";

    private static final String SQL_SELECT_BY_ID = BaseEntry._ID + " = ?";

    private static final String SQL_ORDER_BY_ID_ASC = BaseEntry.COLUMN_FIRST_NAME + " ASC";

    private static final long NO_ID = -1;

    private static final int CONTACTS = 0;
    private static final int CONTACT_ID = 1;

    private final UriMatcher mUriMatcher;
    private Context mContext;
    private DatabaseHelper mDatabaseHelper;

    public ContactsProvider() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(AUTHORITY, CONTACTS_PATH, CONTACTS);
        mUriMatcher.addURI(AUTHORITY, CONTACTS_PATH + "/#", CONTACT_ID);
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();
        mDatabaseHelper = new DatabaseHelper(mContext);
        return false;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
        case CONTACTS:
            return ContactsEntry.DIR_CONTENT_TYPE;
        case CONTACT_ID:
            return ContactsEntry.ITEM_CONTENT_TYPE;
        default:
            throw new IllegalArgumentException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
        Cursor cursor;

        switch (mUriMatcher.match(uri)) {
        case CONTACT_ID:
            if (TextUtils.isEmpty(selection)) {
                selection = SQL_SELECT_BY_ID;
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
            }
        case CONTACTS:
            if (TextUtils.isEmpty(orderBy)) {
                orderBy = SQL_ORDER_BY_ID_ASC;
            }

            cursor = select(ContactsEntry.TABLE_NAME, projection, selection, selectionArgs, orderBy);
            break;
        default:
            throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        Log.d(TAG, "[SELECT] uri = " + uri + "; projection = " + Arrays.toString(projection) +
                "; selection = " + selection + "; args = " + Arrays.toString(selectionArgs) + "; order = " + orderBy);

        if (cursor != null) {
            cursor.setNotificationUri(mContext.getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long rowId;
        Uri resultUri = null;

        switch (mUriMatcher.match(uri)) {
        case CONTACTS:
            if ((rowId = insert(ContactsEntry.TABLE_NAME, values)) != NO_ID) {
                resultUri = ContactsEntry.buildUri(rowId);
            }
            break;
        default:
            throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        Log.d(TAG, "[INSERT] uri = " + resultUri + "; values = " + values + "; rowId = " + rowId);
        mContext.getContentResolver().notifyChange(uri, null);

        return resultUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] valuesArray) {
        int numValues;

        switch (mUriMatcher.match(uri)) {
        case CONTACTS:
            numValues = bulkInsert(ContactsEntry.TABLE_NAME, valuesArray);
            break;
        default:
            throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        Log.d(TAG, "[INSERT] uri = " + uri + "; values = " + numValues);
        mContext.getContentResolver().notifyChange(uri, null);

        return numValues;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String where, String[] whereArgs) {
        int rowsUpdated;

        switch (mUriMatcher.match(uri)) {
        case CONTACT_ID:
            if (TextUtils.isEmpty(where)) {
                where = SQL_SELECT_BY_ID;
                whereArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
            }
        case CONTACTS:
            rowsUpdated = update(ContactsEntry.TABLE_NAME, values, where, whereArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        Log.d(TAG, "[UPDATE] uri = " + uri + "; where = " + where +
                "; args = " + Arrays.toString(whereArgs) + "; values = " + values + "; rows = " + rowsUpdated);

        if (rowsUpdated > 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, String where, String[] whereArgs) {
        int rowsDeleted;

        switch (mUriMatcher.match(uri)) {
        case CONTACT_ID:
            if (TextUtils.isEmpty(where)) {
                where = SQL_SELECT_BY_ID;
                whereArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
            }
        case CONTACTS:
            rowsDeleted = delete(ContactsEntry.TABLE_NAME, where, whereArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown uri: " + uri);
        }

        Log.d(TAG, "[DELETE] uri = " + uri + "; where = " + where +
                "; args = " + Arrays.toString(whereArgs) + "; rows = " + rowsDeleted);

        if (rowsDeleted > 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Nullable
    private Cursor select(@NonNull String table, @NonNull String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String orderBy) {
        Cursor cursor = null;
        
        try {
            cursor = mDatabaseHelper.getReadableDatabase().query(table, projection, selection, selectionArgs, null, null, orderBy);
        } catch (SQLiteException e) {
            Log.e(TAG, "Exception occurred: " + e.getMessage());
        }

        return cursor;
    }

    private long insert(@NonNull String table, @NonNull ContentValues values) {
        long rowId = NO_ID;

        try {
            rowId = mDatabaseHelper.getWritableDatabase().insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        } catch (SQLiteException e) {
            Log.e(TAG, "Exception occurred: " + e.getMessage());
        }

        return rowId;
    }

    private int bulkInsert(@NonNull String table, @NonNull ContentValues[] valuesArray) {
        int numValues = 0;
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            numValues = Stream.of(valuesArray)
                    .map(values -> db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_IGNORE))
                    .filter(rowId -> rowId != NO_ID)
                    .collect(Collectors.toList()).size();

            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.e(TAG, "Exception occurred: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

        return numValues;
    }

    private int update(@NonNull String table, @NonNull ContentValues values, @Nullable String where, @Nullable String[] whereArgs) {
        int rowsUpdated = 0;

        try {
            rowsUpdated = mDatabaseHelper.getWritableDatabase().updateWithOnConflict(table, values, where, whereArgs, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (SQLiteException e) {
            Log.e(TAG, "Exception occurred: " + e.getMessage());
        }

        return rowsUpdated;
    }

    private int delete(@NonNull String table, @NonNull String where, @Nullable String[] whereArgs) {
        int rowsDeleted = 0;

        try {
            rowsDeleted = mDatabaseHelper.getWritableDatabase().delete(table, where, whereArgs);
        } catch (SQLiteException e) {
            Log.e(TAG, "Exception occurred: " + e.getMessage());
        }

        return rowsDeleted;
    }
}
