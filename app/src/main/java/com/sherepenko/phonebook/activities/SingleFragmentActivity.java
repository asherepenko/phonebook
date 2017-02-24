package com.sherepenko.phonebook.activities;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.sherepenko.phonebook.R;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(getFragmentId());

        if (fragment == null) {
            manager.beginTransaction()
                    .add(getFragmentId(), createFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            supportFinishAfterTransition();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected @LayoutRes int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    protected @IdRes int getFragmentId() {
        return R.id.fragmentContainer;
    }

    protected abstract Fragment createFragment();
}
