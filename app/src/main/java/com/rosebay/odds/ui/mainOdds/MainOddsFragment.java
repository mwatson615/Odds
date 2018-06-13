package com.rosebay.odds.ui.mainOdds;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rosebay.odds.Constants;
import com.rosebay.odds.R;
import com.rosebay.odds.model.SingleOdd;
import com.rosebay.odds.ui.singleOdd.SingleOddFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import easymvp.annotation.FragmentView;
import easymvp.annotation.Presenter;

@FragmentView(presenter = MainOddsPresenterImpl.class)
public class MainOddsFragment extends Fragment implements MainOddsView, MainOddsAdapter.ClickListener {

    @Presenter
    MainOddsPresenterImpl mainOddsPresenter;

    @BindView(R.id.home_recycler_view)
    RecyclerView mHomeRecyclerView;
    @BindView(R.id.searchButton)
    ImageButton mSearchButton;
    @BindView(R.id.searchView)
    android.support.v7.widget.SearchView mSearchView;
    @BindView(R.id.search_close_btn)
    AppCompatImageView mSearchViewBtn;
    @BindView(R.id.closeSearchButton)
    ImageButton mCloseSearchButton;
    @BindView(R.id.appName)
    TextView appNameTextView;
    @BindView(R.id.beginSearchButton)
    ImageButton mBeginSearchButton;
    @BindView(R.id.backToMainButton)
    ImageButton mBackToMainButton;
    @BindView(R.id.mainProgressBar)
    ProgressBar mProgressBar;

    MainOddsAdapter mMainOddsAdapter;

    public static MainOddsFragment newInstance() {
        return new MainOddsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main_odds,
                container, false);
        ButterKnife.bind(this, root);
        showInitialToolbarLayout();
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHomeRecyclerView.setHasFixedSize(true);
        mHomeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        mainOddsPresenter.fetchOdds();
    }

    @Override
    public void onStop() {
        super.onStop();
        mainOddsPresenter.onViewDetached();
    }

    @Override
    public void setData(List<SingleOdd> singleOddList) {
        mHomeRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mMainOddsAdapter = new MainOddsAdapter(singleOddList, MainOddsFragment.this);
        mHomeRecyclerView.setAdapter(mMainOddsAdapter);
        mMainOddsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showInitialToolbarLayout() {
        mSearchView.setVisibility(View.INVISIBLE);
        mCloseSearchButton.setVisibility(View.INVISIBLE);
        mBeginSearchButton.setVisibility(View.INVISIBLE);
        mBackToMainButton.setVisibility(View.INVISIBLE);
        appNameTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoading() {
        mHomeRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @OnClick (R.id.searchButton)
    public void prepareSearch() {
        mSearchView.setVisibility(View.VISIBLE);
        mSearchViewBtn.setEnabled(false);
        mSearchViewBtn.setImageDrawable(null);
        mSearchView.setIconified(false);
        mSearchView.setFocusable(true);
        mSearchView.requestFocus();
        appNameTextView.setVisibility(View.INVISIBLE);
        mCloseSearchButton.setVisibility(View.VISIBLE);
        mBeginSearchButton.setVisibility(View.VISIBLE);
        mSearchButton.setVisibility(View.INVISIBLE);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearch();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @OnClick (R.id.closeSearchButton)
    public void closeSearch() {
        mSearchView.setVisibility(View.INVISIBLE);
        mSearchView.setIconified(false);
        mSearchView.setFocusable(false);
        mSearchView.clearFocus();
        appNameTextView.setVisibility(View.VISIBLE);
        mCloseSearchButton.setVisibility(View.INVISIBLE);
        mBeginSearchButton.setVisibility(View.INVISIBLE);
        mSearchButton.setVisibility(View.VISIBLE);
    }

    @OnClick (R.id.beginSearchButton)
    public void conductSearch() {
        onSearch();
    }

    public void onSearch() {
        mSearchView.setIconified(false);
        mSearchView.setFocusable(false);
        mSearchView.clearFocus();
        mainOddsPresenter.fetchSearchResults(mSearchView.getQuery().toString());
    }

    @OnClick (R.id.backToMainButton)
    public void reloadMainOdds() {
        mainOddsPresenter.fetchOdds();
        mSearchButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClicked(SingleOdd singleOdd) {
        mainOddsPresenter.fetchSingleOdd(singleOdd.getPostId());
    }

    @Override
    public void launchSingleOdd(SingleOdd singleOdd) {
        SingleOddFragment fragment = SingleOddFragment.newInstance();
        Bundle args = new Bundle();
        args.putSerializable(Constants.SINGLE_ODD_KEY, singleOdd);
        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(fragment.getTag());
        fragmentTransaction.commit();
    }

    @Override
    public void onError() {
        Snackbar.make(mHomeRecyclerView, R.string.bats_data_error, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onNoSearchResults() {
        Snackbar.make(mHomeRecyclerView, R.string.no_search_results_msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showBackButtonOnSearchResults() {
        mCloseSearchButton.setVisibility(View.INVISIBLE);
        mBackToMainButton.setVisibility(View.VISIBLE);
    }

    @VisibleForTesting
    public void attach(MainOddsPresenterImpl presenter) {
        mainOddsPresenter = presenter;
        mainOddsPresenter.onViewAttached(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
