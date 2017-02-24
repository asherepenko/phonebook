package com.sherepenko.phonebook.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.sherepenko.phonebook.R;
import com.sherepenko.phonebook.data.ContactItem;
import com.sherepenko.phonebook.data.DataItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sherepenko.phonebook.Constants.DATA_ITEM_KEY;
import static com.sherepenko.phonebook.data.DatabaseContract.ContactsEntry;

public class NewContactFragment extends Fragment {

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.firstNameView)
    protected EditText mFirstNameView;
    @BindView(R.id.lastNameView)
    protected EditText mLastNameView;
    @BindView(R.id.phoneNumbersLayout)
    protected LinearLayout mPhoneNumbersLayout;
    @BindView(R.id.addButton)
    protected Button mAddButton;

    private DataItem mItem;

    private String mFirstName;
    private String mLastName;

    private Map<Integer, String> mPhoneNumbers = new LinkedHashMap<>();

    public static NewContactFragment newInstance(DataItem item) {
        NewContactFragment fragment = new NewContactFragment();
        Bundle args = new Bundle();
        args.putParcelable(DATA_ITEM_KEY, item);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        mItem = getArguments().getParcelable(DATA_ITEM_KEY);

        if (mItem == null) {
            mItem = new ContactItem();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupToolbar();
        setupFirstNameView();
        setupLastNameView();
        setupPhoneNumbersLayout();
        setupActionButtons();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.new_contact_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem removeContactItem = menu.findItem(R.id.action_remove_contact);

        if (mItem.getId() == DataItem.NO_ID) {
            removeContactItem.setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_add_contact:
            List<String> phoneNumbers = Stream.of(mPhoneNumbers)
                    .map(Map.Entry::getValue)
                    .filter(phoneNumber -> phoneNumber != null)
                    .collect(Collectors.toCollection(ArrayList::new));

            if (!TextUtils.isEmpty(mFirstName) && !TextUtils.isEmpty(mLastName) && !phoneNumbers.isEmpty()) {
                mItem.setFirstName(mFirstName);
                mItem.setLastName(mLastName);
                mItem.setPhoneNumbers(phoneNumbers);

                if (mItem.getId() != DataItem.NO_ID) {
                    getActivity().getContentResolver().update(mItem.getUri(), mItem.toValues(), null, null);
                    Toast.makeText(getActivity(), R.string.contact_updated, Toast.LENGTH_SHORT).show();
                } else {
                    getActivity().getContentResolver().insert(ContactsEntry.CONTENT_URI, mItem.toValues());
                    Toast.makeText(getActivity(), R.string.contact_added, Toast.LENGTH_SHORT).show();
                }
                getActivity().supportFinishAfterTransition();
            } else {
                Toast.makeText(getActivity(), R.string.error_message, Toast.LENGTH_LONG).show();
            }
            return true;
        case R.id.action_remove_contact:
            if (mItem.getId() != DataItem.NO_ID) {
                getActivity().getContentResolver().delete(mItem.getUri(), null, null);
                Toast.makeText(getActivity(), R.string.contact_removed, Toast.LENGTH_SHORT).show();
                getActivity().supportFinishAfterTransition();
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    protected void setupToolbar() {
        mToolbar.setTitle(mItem.getId() == DataItem.NO_ID ?
                getString(R.string.new_contact) :
                getString(R.string.edit_contact));

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
    }

    protected void setupFirstNameView() {
        mFirstNameView.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                mFirstName = s.toString();
            }
        });

        if (!TextUtils.isEmpty(mItem.getFirstName())) {
            mFirstName = mItem.getFirstName();
            mFirstNameView.setText(mFirstName);
        }
    }

    protected void setupLastNameView() {
        mLastNameView.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                mLastName = s.toString();
            }
        });

        if (!TextUtils.isEmpty(mItem.getLastName())) {
            mLastName = mItem.getLastName();
            mLastNameView.setText(mLastName);
        }
    }

    protected void setupPhoneNumbersLayout() {
        List<String> phoneNumbers = mItem.getPhoneNumbers();

        if (!phoneNumbers.isEmpty()) {
            Stream.of(mItem.getPhoneNumbers()).forEach(this::addPhoneNumberView);
        } else {
            addPhoneNumberView();
        }
    }

    protected void setupActionButtons() {
        mAddButton.setOnClickListener(view -> addPhoneNumberView());
    }

    private void addPhoneNumberView() {
        addPhoneNumberView(null);
    }

    private void addPhoneNumberView(@Nullable String phoneNumber) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(
                getResources().getDimensionPixelSize(R.dimen.view_margin_horizontal),
                getResources().getDimensionPixelSize(R.dimen.view_margin_vertical),
                getResources().getDimensionPixelSize(R.dimen.view_margin_horizontal),
                getResources().getDimensionPixelSize(R.dimen.view_margin_vertical));

        View view = View.inflate(getActivity(), R.layout.layout_edit_phone_number, null);
        EditText phoneNumberView = ButterKnife.findById(view, R.id.phoneNumberView);
        final int viewId = View.generateViewId();
        phoneNumberView.setId(viewId);

        phoneNumberView.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                mPhoneNumbers.put(viewId, s.toString());
            }
        });

        if (phoneNumber != null) {
            mPhoneNumbers.put(viewId, phoneNumber);
            phoneNumberView.setText(phoneNumber);
        }

        mPhoneNumbersLayout.addView(view, params);
    }

    private static class TextWatcherAdapter implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
