package com.sherepenko.phonebook.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sherepenko.phonebook.R;
import com.sherepenko.phonebook.activities.NewContactActivity;
import com.sherepenko.phonebook.adapters.ContactsListAdapter;
import com.sherepenko.phonebook.data.DatabaseHelper;
import com.sherepenko.phonebook.dialogs.ContactDetailDialog;
import com.sherepenko.phonebook.views.DividerItemDecoration;

import java.util.Arrays;

import butterknife.BindView;

import static com.sherepenko.phonebook.Constants.ACTION_SHOW_DIALOG;
import static com.sherepenko.phonebook.Constants.EXTRA_DATA_ITEM;
import static com.sherepenko.phonebook.data.DatabaseContract.ContactsEntry;

public class ContactsListFragment extends RecyclerViewFragment<ContactsListAdapter, Cursor> {

    private static final String TAG = "ContactsListFragment";

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.actionButton)
    protected FloatingActionButton mActionButton;

    protected Uri mContentUri;
    protected String[] mProjection;
    protected String mSelection;
    protected String[] mSelectionArgs;
    protected String mSortOrder;

    private final BroadcastReceiver mShowDialogReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
            case ACTION_SHOW_DIALOG:
                ContactDetailDialog.showDialog(getChildFragmentManager(), intent.getParcelableExtra(EXTRA_DATA_ITEM));
                break;
            default:
                throw new IllegalArgumentException("Unknown intent action: " + intent.getAction());
            }
        }
    };

    public static ContactsListFragment newInstance() {
        ContactsListFragment fragment = new ContactsListFragment();

        Bundle args = new Bundle();
        args.putParcelable(DatabaseHelper.CONTENT_URI_KEY, ContactsEntry.CONTENT_URI);
        args.putStringArray(DatabaseHelper.PROJECTION_KEY, ContactsEntry.TABLE_COLUMNS);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLoaderId(R.id.loader_contacts);
        mContentUri = getArguments().getParcelable(DatabaseHelper.CONTENT_URI_KEY);
        mProjection = getArguments().getStringArray(DatabaseHelper.PROJECTION_KEY);
        mSelection = getArguments().getString(DatabaseHelper.SELECTION_KEY);
        mSelectionArgs = getArguments().getStringArray(DatabaseHelper.SELECTION_ARGS_KEY);
        mSortOrder = getArguments().getString(DatabaseHelper.SORT_ORDER_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts_list, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mShowDialogReceiver, new IntentFilter(ACTION_SHOW_DIALOG));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mShowDialogReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DatabaseHelper.CONTENT_URI_KEY, mContentUri);
        outState.putStringArray(DatabaseHelper.PROJECTION_KEY, mProjection);
        outState.putString(DatabaseHelper.SELECTION_KEY, mSelection);
        outState.putStringArray(DatabaseHelper.SELECTION_ARGS_KEY, mSelectionArgs);
        outState.putString(DatabaseHelper.SORT_ORDER_KEY, mSortOrder);
    }

    @NonNull
    @Override
    public final Loader<Cursor> onCreateLoader(int id, @NonNull Bundle args) {
        Uri contentUri = args.getParcelable(DatabaseHelper.CONTENT_URI_KEY);
        String[] projection = args.getStringArray(DatabaseHelper.PROJECTION_KEY);
        String selection = args.getString(DatabaseHelper.SELECTION_KEY);
        String[] selectionArgs = args.getStringArray(DatabaseHelper.SELECTION_ARGS_KEY);
        String sortOrder = getArguments().getString(DatabaseHelper.SORT_ORDER_KEY);

        Log.d(TAG, "Create Loader: uri = " + contentUri + "; projection = " + Arrays.toString(projection) +
                "; selection = " + selection + "; args = " + Arrays.toString(selectionArgs) + "; order = " + sortOrder);

        return new CursorLoader(getActivity(), contentUri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, @Nullable Cursor cursor) {
        if (cursor != null) {
            Log.d(TAG, "Loading finished: uri = " + mContentUri + "; count = " + cursor.getCount());
            mAdapter.changeCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @NonNull
    @Override
    protected Bundle getLoaderArgs() {
        Bundle args = new Bundle();
        args.putParcelable(DatabaseHelper.CONTENT_URI_KEY, mContentUri);
        args.putStringArray(DatabaseHelper.PROJECTION_KEY, mProjection);
        args.putString(DatabaseHelper.SELECTION_KEY, mSelection);
        args.putStringArray(DatabaseHelper.SELECTION_ARGS_KEY, mSelectionArgs);
        args.putString(DatabaseHelper.SORT_ORDER_KEY, mSortOrder);
        return args;
    }

    @NonNull
    @Override
    protected ContactsListAdapter createAdapter() {
        return new ContactsListAdapter(getActivity());
    }

    @Override
    protected void addItemDecoration() {
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                R.drawable.list_divider,
                DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onActivityCreatedImpl(@Nullable Bundle savedInstanceState) {
        super.onActivityCreatedImpl(savedInstanceState);

        if (savedInstanceState != null) {
            mContentUri = savedInstanceState.getParcelable(DatabaseHelper.CONTENT_URI_KEY);
            mProjection = savedInstanceState.getStringArray(DatabaseHelper.PROJECTION_KEY);
            mSelection = savedInstanceState.getString(DatabaseHelper.SELECTION_KEY);
            mSelectionArgs = savedInstanceState.getStringArray(DatabaseHelper.SELECTION_ARGS_KEY);
            mSortOrder = savedInstanceState.getString(DatabaseHelper.SORT_ORDER_KEY);
        }

        setupToolbar();
        setupActionButton();
    }

    protected void setupToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
    }

    protected void setupActionButton() {
        mActionButton.setOnClickListener(view ->
                startActivity(new Intent(getActivity(), NewContactActivity.class)));
    }
}
