package com.sherepenko.phonebook.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.sherepenko.phonebook.R;
import com.sherepenko.phonebook.data.ContactItem;

import butterknife.BindView;

public class ContactsListAdapter extends RecyclerCursorAdapter<ContactsListAdapter.ViewHolder> {

    public ContactsListAdapter(Context context) {
        super(context);
    }

    public ContactsListAdapter(Context context, @Nullable Cursor cursor) {
        super(context, cursor);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getContext(),
                LayoutInflater.from(getContext()).inflate(R.layout.layout_contact_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @NonNull Cursor cursor) {
        holder.bindItem(new ContactItem(cursor));
    }

    public static class ViewHolder extends RecyclerViewHolder<ContactItem> {

        @BindView(R.id.thumbnailView)
        protected ImageView mThumbnailView;
        @BindView(R.id.fullNameView)
        protected TextView mFullNameView;
        @BindView(R.id.phoneNumberView)
        protected TextView mPhoneNumberView;

        public ViewHolder(Context context, View itemView) {
            super(context, itemView);
        }

        @Override
        public void bindItem(@NonNull ContactItem item) {
            super.bindItem(item);
            String fullName = mItem.getFullName();

            mFullNameView.setText(fullName);
            mPhoneNumberView.setText(mItem.getPhoneNumbers().get(0));
            mThumbnailView.setImageDrawable(TextDrawable.builder()
                    .buildRound(fullName.substring(0, 1), ColorGenerator.MATERIAL.getColor(fullName)));
        }
    }
}
