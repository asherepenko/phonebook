package com.sherepenko.phonebook.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;

public abstract class LoaderFragment<D> extends Fragment implements LoaderManager.LoaderCallbacks<D> {

    private static final String TAG = "LoaderFragment";

    protected int mLoaderId;

    protected void initLoader(int id, @NonNull Bundle args) {
        getLoaderManager().initLoader(id, args, this);
    }

    protected void restartLoader(int id, @NonNull Bundle args) {
        getLoaderManager().restartLoader(id, args, this);
    }

    protected void setLoaderId(int id) {
        mLoaderId = id;
    }

    protected int getLoaderId() {
        return mLoaderId;
    }
}
