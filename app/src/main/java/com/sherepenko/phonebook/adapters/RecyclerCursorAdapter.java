package com.sherepenko.phonebook.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.sherepenko.phonebook.data.BaseEntry;
import com.sherepenko.phonebook.data.DataItem;

public abstract class RecyclerCursorAdapter<VH extends RecyclerViewHolder> extends RecyclerView.Adapter<VH> {

    private static final String TAG = "RecyclerCursorAdapter";

    private static final int NO_COLUMN_INDEX = -1;
    private final Context mContext;
    private Cursor mCursor;
    private boolean mCursorValid;

    private int mIdColumnIndex;

    public RecyclerCursorAdapter(Context context) {
        this(context, null);
    }

    public RecyclerCursorAdapter(Context context, @Nullable Cursor cursor) {
        mContext = context;
        initCursor(cursor);
        setHasStableIds(true);
    }

    @Override
    public final int getItemCount() {
        return mCursorValid ? mCursor.getCount() : 0;
    }

    @Override
    public final long getItemId(int position) {
        if (mCursorValid && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mIdColumnIndex);
        }

        return DataItem.NO_ID;
    }

    @Override
    public final void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @SuppressLint("RecyclerView")
    @Override
    public final void onBindViewHolder(VH holder, int position) {
        if (!mCursorValid) {
            throw new IllegalStateException("Cursor isn't valid");
        }

        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to " + position + " position");
        }

        onBindViewHolder(holder, mCursor);
    }

    @Nullable
    public final Cursor swapCursor(@Nullable Cursor newCursor) {
        if (mCursor == newCursor) {
            return null;
        }

        Cursor oldCursor = mCursor;

        initCursor(newCursor);
        notifyDataSetChanged();

        return oldCursor;
    }

    public final void changeCursor(@Nullable Cursor newCursor) {
        Cursor oldCursor = swapCursor(newCursor);

        if (oldCursor != null) {
            oldCursor.close();
        }
    }


    public abstract void onBindViewHolder(VH holder, @NonNull Cursor cursor);

    protected void initCursor(@Nullable Cursor cursor) {
        mCursor = cursor;
        mCursorValid = mCursor != null;
        mIdColumnIndex = mCursorValid ? mCursor.getColumnIndex(BaseEntry._ID) : NO_COLUMN_INDEX;
    }

    @NonNull
    protected final Context getContext() {
        return mContext;
    }
}
