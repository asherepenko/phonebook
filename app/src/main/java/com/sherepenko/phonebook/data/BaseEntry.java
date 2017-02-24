package com.sherepenko.phonebook.data;

import android.provider.BaseColumns;

public interface BaseEntry extends BaseColumns {
    String COLUMN_FIRST_NAME = "first_name";
    String COLUMN_LAST_NAME = "last_name";
    String COLUMN_PHONE_NUMBERS = "phone_numbers";

    String[] TABLE_COLUMNS = {
            _ID,
            COLUMN_FIRST_NAME,
            COLUMN_LAST_NAME,
            COLUMN_PHONE_NUMBERS
    };
}
