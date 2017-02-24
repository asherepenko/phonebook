package com.sherepenko.phonebook.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sherepenko.phonebook.data.DataItem;

import butterknife.ButterKnife;

import static com.sherepenko.phonebook.Constants.ACTION_SHOW_DIALOG;
import static com.sherepenko.phonebook.Constants.EXTRA_DATA_ITEM;

public abstract class RecyclerViewHolder<DI extends DataItem> extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final String TAG = "RecyclerViewHolder";

    protected DI mItem;

    private final Context mContext;

    public RecyclerViewHolder(Context context, @NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = context;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(ACTION_SHOW_DIALOG);
        intent.putExtra(EXTRA_DATA_ITEM, mItem);
        mContext.sendBroadcast(intent);
    }

    public void bindItem(@NonNull DI item) {
        mItem = item;
    }
}
