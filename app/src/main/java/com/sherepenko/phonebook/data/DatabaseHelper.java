package com.sherepenko.phonebook.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.sherepenko.phonebook.data.DatabaseContract.ContactsEntry;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String CONTENT_URI_KEY = "content_uri";
    public static final String PROJECTION_KEY = "projection";
    public static final String SELECTION_KEY = "selection";
    public static final String SELECTION_ARGS_KEY = "selection_args";
    public static final String SORT_ORDER_KEY = "sort_order";

    private static final String TAG = "DatabaseHelper";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE %s ("  +
            BaseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + "," +
            BaseEntry.COLUMN_FIRST_NAME + " TEXT"                 + "," +
            BaseEntry.COLUMN_LAST_NAME + " TEXT"                  + "," +
            BaseEntry.COLUMN_PHONE_NUMBERS + " TEXT"              + ")";

    private static final String SQL_INSERT_TABLE = "INSERT INTO %s (%s) SELECT %s FROM %s";
    private static final String SQL_RENAME_TABLE = "ALTER TABLE %s RENAME TO %s";
    private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS %s";
    private static final String SQL_COLUMN_NAMES = "SELECT * FROM %s LIMIT 1";

    private static final String COMMA_SEPARATOR = ",";
    private static final String TMP_TABLE_PREFIX = "tmp_";

    private static final String DATABASE_NAME = "PhoneBook.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db, ContactsEntry.TABLE_NAME);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade(): oldVersion = " + oldVersion + "; newVersion = " + newVersion);

        if (newVersion > oldVersion) {
            upgradeTable(db, ContactsEntry.TABLE_NAME);
        } else {
            onCreate(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private void createTable(SQLiteDatabase db, String tableName) {
        try {
            db.execSQL(String.format(SQL_CREATE_TABLE, tableName));
        } catch (SQLiteException e) {
            Log.e(TAG, "Could not create new table " + tableName + ": " + e.getMessage());
        }
    }

    private void deleteTable(SQLiteDatabase db, String tableName) {
        try {
            db.execSQL(String.format(SQL_DELETE_TABLE, tableName));
        } catch (SQLiteException e) {
            Log.e(TAG, "Could not delete table " + tableName + ": " + e.getMessage());
        }
    }

    private void renameTable(SQLiteDatabase db, String oldTableName, String newTableName) {
        try{
            db.execSQL(String.format(SQL_RENAME_TABLE, oldTableName, newTableName));
        } catch (SQLiteException e) {
            Log.e(TAG, "Could not rename table " + oldTableName + " to " + newTableName + ": " + e.getMessage());
        }
    }

    private void upgradeTable(SQLiteDatabase db, String tableName) {
        String tmpTableName = TMP_TABLE_PREFIX + tableName;

        renameTable(db, tableName, tmpTableName);
        deleteTable(db, tableName);
        createTable(db, tableName);

        List<String> oldColumns = getColumnNames(db, tmpTableName);
        List<String> newColumns = getColumnNames(db, tableName);

        String columnNames = Stream.of(newColumns)
                .filter(oldColumns::contains)
                .collect(Collectors.joining(COMMA_SEPARATOR));

        Log.d(TAG, "\"" + tmpTableName + "\" column names: " + oldColumns);
        Log.d(TAG, "\"" + tableName + "\" column names: " + newColumns);

        copyContent(db, tmpTableName, columnNames, tableName, columnNames);

        deleteTable(db, tmpTableName);
    }

    private void copyContent(SQLiteDatabase db, String fromTable, String fromColumns, String toTable, String toColumns) {
        try {
            db.execSQL(String.format(SQL_INSERT_TABLE, toTable, toColumns, fromColumns, fromTable));
        } catch (SQLiteException e) {
            Log.e(TAG, "Could not copy " + fromTable + " [" + fromColumns + "] to " + toTable + " [" + toColumns + "]: " + e.getMessage());
        }
    }

    @NonNull
    private List<String> getColumnNames(SQLiteDatabase db, String tableName) {
        List<String> columnNames = new ArrayList<>();
        Cursor cursor = db.rawQuery(String.format(SQL_COLUMN_NAMES, tableName), null);

        if (cursor != null) {
            columnNames.addAll(Arrays.asList(cursor.getColumnNames()));
            cursor.close();
        }

        return columnNames;
    }
}
