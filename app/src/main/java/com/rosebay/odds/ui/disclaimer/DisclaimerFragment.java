package com.rosebay.odds.ui.disclaimer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rosebay.odds.OddsApplication;
import com.rosebay.odds.R;
import com.rosebay.odds.util.SharedPreferencesClient;
import com.squareup.leakcanary.RefWatcher;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import easymvp.annotation.FragmentView;
import easymvp.annotation.Presenter;

@FragmentView(presenter = DisclaimerPresenterImpl.class)
public class DisclaimerFragment extends Fragment implements DisclaimerView {

    @Presenter
    DisclaimerPresenterImpl disclaimerPresenter;

    @Inject
    SharedPreferencesClient sharedPreferencesClient;

    @BindView(R.id.disclaimerTextView)
    TextView mDisclaimerTextView;
    @BindView(R.id.disclaimerSearchView)
    android.support.v7.widget.SearchView mSearchView;
    @BindView(R.id.usernameSearchProgressBar)
    ProgressBar mSearchProgressBar;
    @BindView(R.id.saveUsernameButton)
    Button mSaveButton;

    OnUsernameSavedListener mCallback;

    public static DisclaimerFragment newInstance() {
        return new DisclaimerFragment();
    }

    public interface OnUsernameSavedListener {
        void onUsernameSaved();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_disclaimer_username, container, false);
        ButterKnife.bind(this, root);
        OddsApplication.getAppComponent().inject(this);
        showSearchLayout();
        mSaveButton.setEnabled(false);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        disclaimerPresenter.onViewAttached(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        disclaimerPresenter.onViewDetached();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (OnUsernameSavedListener) context;
    }

    @Override
    public void onUsernameAvailable(String username) {
        mSearchProgressBar.setVisibility(View.GONE);
        mSaveButton.setEnabled(true);
    }

    @Override
    public void onUsernameTaken() {
        Snackbar.make(mDisclaimerTextView, R.string.username_taken_msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        Snackbar.make(mDisclaimerTextView, R.string.bats_data_error, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showSearchLayout() {
        mSearchProgressBar.setVisibility(View.GONE);
        mSearchView.setVisibility(View.VISIBLE);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    disclaimerPresenter.checkForUsername(mSearchView.getQuery().toString());
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSaveButton.setEnabled(false);
                return false;
            }
        });
    }

    @Override
    public void showProgressLayout() {
        mSearchProgressBar.setVisibility(View.VISIBLE);
        mSearchView.setVisibility(View.GONE);
        mSaveButton.setEnabled(false);
    }

    @Override
    public void onUserSaved(String username) {
        sharedPreferencesClient.saveUsername(getString(R.string.username), username);
        Snackbar.make(mDisclaimerTextView, String.format(getString(R.string.username_saved_msg), username), Snackbar.LENGTH_SHORT).show();
        mCallback.onUsernameSaved();
    }

    @OnClick (R.id.disclaimerSearchView)
    public void searchForUsername() {
        if (!mSearchView.getQuery().toString().equals("")) {
            disclaimerPresenter.checkForUsername(mSearchView.getQuery().toString());
        }
    }

    @OnClick (R.id.saveUsernameButton)
    public void saveUsername() {
        disclaimerPresenter.saveUsername(mSearchView.getQuery().toString());
    }

    @VisibleForTesting
    public void attach(DisclaimerPresenterImpl presenter) {
        disclaimerPresenter = presenter;
        disclaimerPresenter.onViewAttached(this);
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
