package com.rosebay.odds.ui.favoriteOdds;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import com.rosebay.odds.util.Mockable;
import com.squareup.leakcanary.RefWatcher;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import easymvp.annotation.FragmentView;
import easymvp.annotation.Presenter;

@Mockable
@FragmentView(presenter = FavoriteOddsPresenterImpl.class)
public class FavoriteOddsFragment extends Fragment implements FavoriteOddsView {

    @Presenter
    public FavoriteOddsPresenterImpl favoriteOddsPresenter;

    FavoriteOddsAdapter favoriteOddsAdapter;

    @BindView(R.id.fragmentFavoriteOdds)
    ConstraintLayout mLayout;
    @BindView(R.id.favoritesRecyclerView)
    RecyclerView mFavoritesRecyclerView;
    @BindView(R.id.noFavoriteOddsTextView)
    TextView mNoFavoritesTextView;
    @BindView(R.id.favoritesProgressBar)
    ProgressBar mProgressBar;

    public static FavoriteOddsFragment newInstance() {
        return new FavoriteOddsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorite_odds, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNoFavoritesTextView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mFavoritesRecyclerView.setVisibility(View.INVISIBLE);
        mFavoritesRecyclerView.setHasFixedSize(true);
        mFavoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mFavoritesRecyclerView.setVisibility(View.INVISIBLE);
        mNoFavoritesTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setData(List<SingleOdd> singleOdds) {
        mFavoritesRecyclerView.setVisibility(View.VISIBLE);
        mNoFavoritesTextView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        favoriteOddsAdapter = new FavoriteOddsAdapter(singleOdds, getContext());
        mFavoritesRecyclerView.setAdapter(favoriteOddsAdapter);
        favoriteOddsAdapter.notifyDataSetChanged();
    }

    @Override
    public void noFavorites() {
        mProgressBar.setVisibility(View.GONE);
        mFavoritesRecyclerView.setVisibility(View.GONE);
        mNoFavoritesTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        favoriteOddsPresenter.onViewAttached(this);
        favoriteOddsPresenter.getAllFavorites();
    }

    @Override
    public void onPause() {
        super.onPause();
        favoriteOddsPresenter.onViewDetached();
    }

    @Override
    public void onError() {
        Snackbar.make(mLayout, R.string.bats_data_error, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = OddsApplication.Companion.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
