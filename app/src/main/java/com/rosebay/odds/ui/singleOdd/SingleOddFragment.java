package com.rosebay.odds.ui.singleOdd;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rosebay.odds.Constants;
import com.rosebay.odds.OddsApplication;
import com.rosebay.odds.R;
import com.rosebay.odds.model.SingleOdd;
import com.rosebay.odds.ui.NavigationInterface;
import com.rosebay.odds.util.SharedPreferencesClient;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import easymvp.annotation.FragmentView;
import easymvp.annotation.Presenter;

@FragmentView(presenter = SingleOddPresenterImpl.class)
public class SingleOddFragment extends Fragment implements SingleOddView {

    @Inject
    SharedPreferencesClient sharedPreferencesClient;

    @Presenter
    SingleOddPresenterImpl singleOddPresenter;

    @BindView(R.id.imageURLImageView)
    ImageView mImageView;
    @BindView(R.id.descriptionSingleOdd)
    TextView mDescription;
    @BindView(R.id.percentageSingleOdd)
    TextView mPercentage;
    @BindView(R.id.singleOddCreationTextView)
    TextView mSingleOddCreationTextView;
    @BindView(R.id.oddsForSingleOdd)
    TextView mOddsForTextView;
    @BindView(R.id.oddsAgainstSingleOdd)
    TextView mOddsAgainstTextView;
    @BindView(R.id.addToFavoritesButton)
    ImageButton mAddToFavoritesButton;
    @BindView(R.id.voteYesButton)
    Button mVoteYesButton;
    @BindView(R.id.voteNoButton)
    Button mVoteNoButton;
    @BindView(R.id.voteLayout)
    LinearLayout mVoteLayout;

    private SingleOdd mSingleOdd;

    public static SingleOddFragment newInstance() {
        return new SingleOddFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_single_odd, container, false);
        ButterKnife.bind(this, root);
        OddsApplication.getAppComponent().inject(this);
        mSingleOdd = (SingleOdd) getArguments().getSerializable(Constants.SINGLE_ODD_KEY);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        singleOddPresenter.checkForFavorite(mSingleOdd.getPostId());
        singleOddPresenter.checkIfVoted(mSingleOdd.getPostId());
        singleOddPresenter.loadOddsData(mSingleOdd);
    }

    @Override
    public void onPause() {
        super.onPause();
        singleOddPresenter.onViewDetached();
    }

    @Override
    public void setPercentage(int percentage) {
        mPercentage.setText(String.format(getString(R.string.percentage_text), percentage));
    }

    @Override
    public void setOddsFor(int oddsFor) {
        mOddsForTextView.setText(String.valueOf(oddsFor));
    }

    @Override
    public void setOddsAgainst(int oddsAgainst) {
        mOddsAgainstTextView.setText(String.valueOf(oddsAgainst));
    }

    @Override
    public void setImageUrl(String imageUrl) {
        Uri uri = Uri.parse(imageUrl);
        Picasso.with(getContext()).load(uri).fit().into(mImageView);
    }

    @Override
    public void setCreationInfo(String username, String creationDate) {
        mSingleOddCreationTextView.setText(getString(R.string.created_by_date, username, creationDate));
    }

    @Override
    public void setDescription(String description) {
        mDescription.setText(description);
    }

    @Override
    public void onAddedToFavorites() {
        Snackbar.make(mOddsAgainstTextView, R.string.added_to_favorites_msg, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.voteYesButton)
    public void voteYes() {
        singleOddPresenter.voteYes(mSingleOdd.getPostId());
    }

    @OnClick(R.id.voteNoButton)
    public void voteNo() {
        singleOddPresenter.voteNo(mSingleOdd.getPostId());
    }

    @OnClick(R.id.addToFavoritesButton)
    public void addToFavorites() {
        singleOddPresenter.addToFavorites(sharedPreferencesClient.getUsername(getString(R.string.username)), mSingleOdd.getPostId());
    }

    @Override
    public void onVoteSuccess() {
        Snackbar.make(mOddsAgainstTextView, R.string.vote_has_been_saved_msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void disableVoteButtons() {
        mVoteNoButton.setEnabled(false);
        mVoteYesButton.setEnabled(false);
    }

    @Override
    public void enableVoteButtons() {
        mVoteYesButton.setEnabled(true);
        mVoteNoButton.setEnabled(true);
    }

    @Override
    public void disableFavoritesButton() {
        mAddToFavoritesButton.setEnabled(false);
        DrawableCompat.setTint(mAddToFavoritesButton.getDrawable(), ContextCompat.getColor(getContext(), R.color.accent));
    }

    @Override
    public void enableFavoritesButton() {
        mAddToFavoritesButton.setEnabled(true);
        DrawableCompat.setTint(mAddToFavoritesButton.getDrawable(), ContextCompat.getColor(getContext(), R.color.primaryTextColor));
    }

    @Override
    public void onError() {
        Snackbar.make(mOddsAgainstTextView, R.string.bats_data_error, Snackbar.LENGTH_SHORT).show();
    }

    @VisibleForTesting
    public void attach(SingleOddPresenterImpl presenter) {
        singleOddPresenter = presenter;
        singleOddPresenter.onViewAttached(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = OddsApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
