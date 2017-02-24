package com.sherepenko.phonebook.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sherepenko.phonebook.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class RecyclerViewFragment<RVA extends RecyclerView.Adapter, D> extends LoaderFragment<D> {

    private static final String TAG = "RecyclerViewFragment";

    @BindView(R.id.recyclerView)
    protected RecyclerView mRecyclerView;

    protected RVA mAdapter;

    protected int mFirstVisiblePos;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public final void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupRecyclerView();

        onActivityCreatedImpl(savedInstanceState);

        initLoader(getLoaderId(), getLoaderArgs());
    }

    @Override
    public void onDestroy() {
        if (mRecyclerView != null) {
            mRecyclerView.clearOnScrollListeners();
        }
        super.onDestroy();
    }

    protected void onActivityCreatedImpl(@Nullable Bundle savedInstanceState) {
        // used in nested classes
    }

    protected void setupRecyclerView() {
        mAdapter = createAdapter();

        mRecyclerView.setLayoutManager(createLayoutManager());
        mRecyclerView.setItemAnimator(createItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(mFirstVisiblePos);

        addItemDecoration();
    }

    @NonNull
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @NonNull
    protected RecyclerView.ItemAnimator createItemAnimator() {
        return new DefaultItemAnimator();
    }

    protected void addItemDecoration() {
    }

    @NonNull
    protected abstract Bundle getLoaderArgs();

    @NonNull
    protected abstract RVA createAdapter();
}
