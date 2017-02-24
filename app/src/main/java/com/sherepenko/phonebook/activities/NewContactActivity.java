package com.sherepenko.phonebook.activities;

import android.support.v4.app.Fragment;

import com.sherepenko.phonebook.fragments.NewContactFragment;

import static com.sherepenko.phonebook.Constants.EXTRA_DATA_ITEM;

public class NewContactActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return NewContactFragment.newInstance(
                getIntent().getParcelableExtra(EXTRA_DATA_ITEM));
    }
}
