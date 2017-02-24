package com.sherepenko.phonebook.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sherepenko.phonebook.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhoneNumbersAdapter extends RecyclerView.Adapter<PhoneNumbersAdapter.ViewHolder> {

    private static final String PHONE_SCHEME = "tel";

    private final Context mContext;
    private final List<String> mPhoneNumbers;

    public PhoneNumbersAdapter(Context context, List<String> phoneNumbers) {
        mContext = context;
        mPhoneNumbers = phoneNumbers;
    }

    @Override
    public PhoneNumbersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(mContext,
                LayoutInflater.from(mContext).inflate(R.layout.layout_phone_number, parent, false));
    }

    @Override
    public void onBindViewHolder(PhoneNumbersAdapter.ViewHolder holder, int position) {
        holder.setPhoneNumber(mPhoneNumbers.get(position));
    }

    @Override
    public int getItemCount() {
        return mPhoneNumbers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.phoneNumberView)
        protected TextView mPhoneNumberView;

        private final Context mContext;

        private String mPhoneNumber;

        public ViewHolder(Context context, View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = context;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                mContext.startActivity(new Intent(Intent.ACTION_CALL, Uri.fromParts(PHONE_SCHEME, mPhoneNumber, null)));
            }
        }

        public void setPhoneNumber(String phoneNumber) {
            mPhoneNumber = phoneNumber;
            mPhoneNumberView.setText(mPhoneNumber);
        }
    }
}
