package com.rosebay.odds.ui.myOdds;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rosebay.odds.OddsApplication;
import com.rosebay.odds.R;
import com.rosebay.odds.model.SingleOdd;
import com.rosebay.odds.util.SharedPreferencesClient;
import com.squareup.leakcanary.RefWatcher;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import easymvp.annotation.FragmentView;
import easymvp.annotation.Presenter;

@FragmentView(presenter = MyOddsPresenterImpl.class)
public class MyOddsFragment extends Fragment implements MyOddsView {

    @Presenter
    MyOddsPresenterImpl myOddsPresenter;

    @Inject
    SharedPreferencesClient preferencesClient;

    @BindView(R.id.myOddsRecyclerView)
    RecyclerView myOddsRecyclerView;
    @BindView(R.id.noMyOddsTextView)
    TextView mNoMyOddsTextView;
    @BindView(R.id.myOddsProgressBar)
    ProgressBar mProgressBar;

    MyOddsAdapter myOddsAdapter;

    public static MyOddsFragment newInstance() {
        return new MyOddsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_odds,
                container, false);
        ButterKnife.bind(this, root);
        OddsApplication.getAppComponent().inject(this);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myOddsRecyclerView.setHasFixedSize(true);
        myOddsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onResponse(List<SingleOdd> response) {
        mProgressBar.setVisibility(View.GONE);
        mNoMyOddsTextView.setVisibility(View.GONE);
        myOddsRecyclerView.setVisibility(View.VISIBLE);
        myOddsAdapter = new MyOddsAdapter(response, getContext());
        myOddsRecyclerView.setAdapter(myOddsAdapter);
        myOddsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNoResponse() {
        mProgressBar.setVisibility(View.GONE);
        myOddsRecyclerView.setVisibility(View.GONE);
        mNoMyOddsTextView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onResume() {
        super.onResume();
        myOddsPresenter.onViewAttached(this);
        myOddsPresenter.fetchMyOdds(preferencesClient.getUsername(getString(R.string.username)));
    }

    @Override
    public void onPause() {
        super.onPause();
        myOddsPresenter.onViewDetached();
    }

    @VisibleForTesting
    public void attach(MyOddsPresenterImpl presenter) {
        myOddsPresenter = presenter;
        myOddsPresenter.onViewAttached(this);

    }

    @Override
    public void onError() {
        Snackbar.make(myOddsRecyclerView, R.string.bats_data_error, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        myOddsRecyclerView.setVisibility(View.INVISIBLE);
        mNoMyOddsTextView.setVisibility(View.INVISIBLE);
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

