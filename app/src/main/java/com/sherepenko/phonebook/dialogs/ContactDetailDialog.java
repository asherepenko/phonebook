package com.sherepenko.phonebook.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.sherepenko.phonebook.R;
import com.sherepenko.phonebook.activities.NewContactActivity;
import com.sherepenko.phonebook.adapters.PhoneNumbersAdapter;
import com.sherepenko.phonebook.data.DataItem;
import com.sherepenko.phonebook.views.DividerItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sherepenko.phonebook.Constants.DATA_ITEM_KEY;
import static com.sherepenko.phonebook.Constants.EXTRA_DATA_ITEM;

public class ContactDetailDialog extends BottomSheetDialogFragment {

    public static final String TAG = "ContactDetailDialog";

    @BindView(R.id.toolbar)
    protected Toolbar mToolbar;
    @BindView(R.id.editButton)
    protected ImageButton mEditButton;
    @BindView(R.id.removeButton)
    protected ImageButton mRemoveButton;
    @BindView(R.id.recyclerView)
    protected RecyclerView mRecyclerView;

    private View mContentView;

    private DataItem mItem;

    public static ContactDetailDialog newInstance(@NonNull DataItem item) {
        ContactDetailDialog fragment = new ContactDetailDialog();

        Bundle args = new Bundle();
        args.putParcelable(DATA_ITEM_KEY, item);

        fragment.setArguments(args);
        return fragment;
    }

    public static void showDialog(FragmentManager manager, @NonNull DataItem item) {
        newInstance(item).show(manager, TAG);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItem = getArguments().getParcelable(DATA_ITEM_KEY);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        ButterKnife.bind(this, getContentView());

        setupDialog(dialog);

        setupToolbar();
        setupActions();
        setupRecyclerView();
    }


    protected void setupDialog(Dialog dialog) {
        View contentView = getContentView();
        dialog.setContentView(contentView);

        View parent = (View) contentView.getParent();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        params.height = metrics.heightPixels;
        BottomSheetBehavior.from(parent).setPeekHeight(metrics.heightPixels);

        contentView.measure(0, 0);
        parent.setFitsSystemWindows(true);
        parent.setLayoutParams(params);
    }

    protected void setupToolbar() {
        mToolbar.setTitle(mItem.getFullName());
    }

    protected void setupActions() {
        mEditButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), NewContactActivity.class);
            intent.putExtra(EXTRA_DATA_ITEM, mItem);
            startActivity(intent);
            dismiss();
        });

        mRemoveButton.setOnClickListener(view -> {
            getActivity().getContentResolver().delete(mItem.getUri(), null, null);
            Toast.makeText(getActivity(), R.string.contact_removed, Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }

    protected void setupRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new PhoneNumbersAdapter(getActivity(), mItem.getPhoneNumbers()));

        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                R.drawable.list_divider,
                DividerItemDecoration.VERTICAL));
    }

    private View getContentView() {
        if (mContentView == null) {
            mContentView = View.inflate(getContext(), R.layout.layout_contact_detail, null);
        }

        return mContentView;
    }
}
