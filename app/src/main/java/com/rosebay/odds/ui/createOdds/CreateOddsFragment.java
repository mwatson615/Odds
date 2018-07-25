package com.rosebay.odds.ui.createOdds;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.VisibleForTesting;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rosebay.odds.OddsApplication;
import com.rosebay.odds.R;
import com.rosebay.odds.model.SingleOdd;
import com.rosebay.odds.util.SharedPreferencesClient;
import com.squareup.leakcanary.RefWatcher;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import easymvp.annotation.FragmentView;
import easymvp.annotation.Presenter;

@FragmentView(presenter = CreateOddsPresenterImpl.class)
public class CreateOddsFragment extends Fragment implements CreateOddsView, DatePickerDialog.OnDateSetListener {

    @Presenter
    public CreateOddsPresenterImpl createOddsPresenter;

    @Inject
    SharedPreferencesClient sharedPreferencesClient;

    @BindView(R.id.createOddsButton)
    Button mCreateOddsButton;
    @BindView(R.id.noCheckBox)
    CheckBox noCheckBox;
    @BindView(R.id.yesCheckBox)
    CheckBox yesCheckBox;
    @BindView(R.id.descriptionEditText)
    EditText mDescriptionEditText;
    @BindView(R.id.imageViewPager)
    ViewPager mImageViewPager;
    @BindView(R.id.forOrAgainstLinearLayout)
    LinearLayout mForOrAgainstLinearLayout;
    @BindView(R.id.instructions_label)
    TextView mInstructionsLabelTextView;
    @BindView(R.id.imageSearchProgressBar)
    ProgressBar mImageSearchProgressBar;
    @BindView(R.id.imageSearchTermsEditText)
    EditText mImageSearchTermsEditText;
    @BindView(R.id.imageSearchTermsLayout)
    ConstraintLayout mImageSearchTermsLayout;
    @BindView(R.id.createOddsButtonLayout)
    LinearLayout mCreateOddsButtonLayout;
    @BindView(R.id.searchButton)
    Button mSearchButton;
    @BindView(R.id.looksGoodButton)
    Button mLooksGoodButton;
    @BindView(R.id.makeChangesButton)
    Button mMakeChangesButton;
    @BindView(R.id.dateLayout)
    LinearLayout mDateLayout;
    @BindView(R.id.noDateButton)
    Button mNoDateButton;
    @BindView(R.id.pickADateButton)
    Button mPickADateButton;
    @BindView(R.id.dueDateTextView)
    TextView mDueDateTextView;

    ImagePagerAdapter mImagePagerAdapter;
    String mDueDate = "";
    Calendar calendar;

    public static CreateOddsFragment newInstance() {
        return new CreateOddsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_create_odds,
                container, false);
        ButterKnife.bind(this, root);
        OddsApplication.getAppComponent().inject(this);
        showInitialLayout();
        calendar = Calendar.getInstance();
        return root;
    }

    public void showInitialLayout() {
        mInstructionsLabelTextView.setVisibility(View.VISIBLE);
        mDescriptionEditText.setVisibility(View.VISIBLE);
        mImageViewPager.setVisibility(View.GONE);
        hideOnInitialState();
    }

    @Override
    public void onSearch() {
        mImageSearchProgressBar.setVisibility(View.VISIBLE);
        mDescriptionEditText.clearFocus();
        mImageSearchTermsEditText.clearFocus();
        mImageViewPager.setVisibility(View.GONE);
        mInstructionsLabelTextView.setVisibility(View.INVISIBLE);
    }

    public void startAgain() {
        mDescriptionEditText.setVisibility(View.VISIBLE);
        mImageSearchTermsLayout.setVisibility(View.VISIBLE);
        yesCheckBox.setChecked(false);
        noCheckBox.setChecked(false);
        hideOnInitialState();
    }

    public void hideOnInitialState() {
        mForOrAgainstLinearLayout.setVisibility(View.GONE);
        mLooksGoodButton.setVisibility(View.GONE);
        mImageSearchProgressBar.setVisibility(View.GONE);
        mCreateOddsButtonLayout.setVisibility(View.GONE);
        mDateLayout.setVisibility(View.GONE);
        mDueDateTextView.setVisibility(View.GONE);
    }

    @Override
    public void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        IBinder binder = getView().getWindowToken();
        inputMethodManager.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void clearTextFields() {
        mDescriptionEditText.setText(null);
        mImageSearchTermsEditText.setText(null);
        yesCheckBox.setChecked(false);
        noCheckBox.setChecked(false);
    }

    @Override
    public void loadImages(List<String> imageItemList) {
        mInstructionsLabelTextView.setVisibility(View.GONE);
        mImageViewPager.setVisibility(View.VISIBLE);
        mImageSearchProgressBar.setVisibility(View.GONE);
        mLooksGoodButton.setVisibility(View.VISIBLE);
        mImagePagerAdapter = new ImagePagerAdapter(this.getContext(), imageItemList);
        mImageViewPager.setAdapter(mImagePagerAdapter);
        mImagePagerAdapter.notifyDataSetChanged();
    }

    public void onCheckedCheckbox() {
        if (yesCheckBox.isChecked() || noCheckBox.isChecked()) {
            mDateLayout.setVisibility(View.VISIBLE);
        } else {
            mDateLayout.setVisibility(View.GONE);
        }
    }

    @OnClick (R.id.searchButton)
    public void searchForImages() {
        createOddsPresenter.getImages(mDescriptionEditText.getText().toString(), mImageSearchTermsEditText.getText().toString());
    }

    @OnClick (R.id.looksGoodButton)
    public void showForOrAgainstLayout() {
        mDescriptionEditText.clearFocus();
        mImageSearchTermsLayout.setVisibility(View.GONE);
        mForOrAgainstLinearLayout.setVisibility(View.VISIBLE);
        closeKeyboard();
    }

    @OnClick (R.id.makeChangesButton)
    public void makeChanges() {
        startAgain();
    }

    @OnClick (R.id.noCheckBox)
    public void voteNo() {
        yesCheckBox.setChecked(false);
        onCheckedCheckbox();
    }

    @OnClick (R.id.yesCheckBox)
    public void voteYes() {
        noCheckBox.setChecked(false);
        onCheckedCheckbox();
    }

    @OnClick (R.id.pickADateButton)
    public void pickADate() {
        showDatePicker();
    }

    @OnClick (R.id.noDateButton)
    public void noDate() {
        showSubmitLayout();
    }

    public void showSubmitLayout() {
        mForOrAgainstLinearLayout.setVisibility(View.GONE);
        mDateLayout.setVisibility(View.GONE);
        mCreateOddsButtonLayout.setVisibility(View.VISIBLE);
    }

    @OnClick (R.id.createOddsButton)
    public void submitNewOdd() {
        createOddsPresenter.createOdds(createNewOdd());
    }

    private SingleOdd createNewOdd() {
        SingleOdd singleOdd = new SingleOdd();
        singleOdd.setDescription(mDescriptionEditText.getText().toString());
        singleOdd.setUsername(sharedPreferencesClient.getUsername(getString(R.string.username)));
        singleOdd.setOddsFor(yesCheckBox.isChecked() ? 1 : 0);
        singleOdd.setOddsAgainst(noCheckBox.isChecked() ? 1 : 0);
        singleOdd.setPercentage(singleOdd.getOddsFor() == 1 ? 100 : 0);
        singleOdd.setDateSubmitted(setCurrentDate());
        singleOdd.setDueDate(mDueDate);
        singleOdd.setImageUrl(mImagePagerAdapter.getCurrentUrl(mImageViewPager.getCurrentItem()));
        return singleOdd;
    }

    public String setCurrentDate() {
        Date today = new Date();
        DateFormat dateFormat = SimpleDateFormat.getDateInstance();
        return dateFormat.format(today);
    }

    @Override
    public void onError() {
        mImageSearchProgressBar.setVisibility(View.GONE);
        startAgain();
        Snackbar.make(mCreateOddsButton, R.string.bats_data_error, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onSave(String description) {
        Snackbar.make(mCreateOddsButton, String.format(getString(R.string.odds_have_been_saved_msg), description), Snackbar.LENGTH_LONG).show();
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    private void getDueDate(int month, int dayOfMonth, int year) {
        calendar.set(year, month, dayOfMonth);
        Date date = calendar.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        mDueDateTextView.setVisibility(View.VISIBLE);
        mDueDate = dateFormat.format(date);
        mDueDateTextView.setText(String.format(getString(R.string.due_date_answer), mDueDate));
        showSubmitLayout();
    }

    public void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), this::onDateSet, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        getDueDate(month, dayOfMonth, year);
    }

    @Override
    public void onPause() {
        super.onPause();
        createOddsPresenter.onViewDetached();
    }

    @Override
    public void onResume() {
        super.onResume();
        createOddsPresenter.onViewAttached(this);
    }

    @VisibleForTesting
    public void attach(CreateOddsPresenterImpl presenter) {
        createOddsPresenter = presenter;
        createOddsPresenter.onViewAttached(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = OddsApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

}
