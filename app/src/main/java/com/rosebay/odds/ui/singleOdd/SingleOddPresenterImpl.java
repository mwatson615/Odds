package com.rosebay.odds.ui.singleOdd;


import android.annotation.SuppressLint;
import android.support.annotation.VisibleForTesting;

import com.rosebay.odds.Constants;
import com.rosebay.odds.OddsApplication;
import com.rosebay.odds.localStorage.FavoriteDao;
import com.rosebay.odds.localStorage.VoteDao;
import com.rosebay.odds.model.Favorite;
import com.rosebay.odds.model.SingleOdd;
import com.rosebay.odds.model.Vote;
import com.rosebay.odds.network.CloudFunctionsClient;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;

import easymvp.AbstractPresenter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SingleOddPresenterImpl extends AbstractPresenter<SingleOddView> implements SingleOddPresenter {

    @Inject
    CloudFunctionsClient cloudFunctionsClient;

    @Inject
    FavoriteDao favoriteDao;

    @Inject
    VoteDao voteDao;

    @VisibleForTesting
    SingleOddView singleOddView;

    @SuppressLint("CheckResult")
    @Override
    public void addToFavorites(String username, String postId) {
        getView().disableFavoritesButton();
        Favorite favorite = new Favorite();
        favorite.setUsername(username);
        favorite.setPostId(postId);

        Observable.fromCallable(() -> favoriteDao.createFavorite(favorite))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(e -> {
                    getView().enableFavoritesButton();
                    getView().onError();
                })
                .subscribe(response -> getView().onAddedToFavorites());
    }

    @Override
    public void voteYes(String postId) {
        getView().disableVoteButtons();
        Map<String, String> map = new HashMap<>();
        map.put(Constants.POST_ID, postId);
        Disposable voteYes = cloudFunctionsClient.submitVoteYes(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    loadNumbersData(response);
                    addUserVote(response.getPostId(), response.getUsername(), true);
                        },
                        e -> {
                            getView().onError();
                            getView().enableVoteButtons();
                        });
    }

    @Override
    public void voteNo(String postId) {
        getView().disableVoteButtons();
        Map<String, String> map = new HashMap<>();
        map.put(Constants.POST_ID, postId);
        Disposable voteYes = cloudFunctionsClient.submitNoVote(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    loadNumbersData(response);
                    addUserVote(response.getPostId(), response.getUsername(), false);
                        },
                        e -> {
                    getView().onError();
                    getView().enableVoteButtons();
                        });
    }

    @SuppressLint("CheckResult")
    public void addUserVote(String postId, String username, Boolean votedYes) {
        Vote vote = new Vote();
        vote.setPostId(postId);
        vote.setUsername(username);
        vote.setVotedYes(votedYes);

        Observable.fromCallable(() -> voteDao.createVoteEntry(vote))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> getView().onVoteSuccess(),
                        e -> getView().onError());
    }

    @Override
    public void checkForFavorite(String postId) {
            favoriteDao.findFavByPostID(postId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> getView().enableFavoritesButton())
                    .doOnSuccess(response -> getView().disableFavoritesButton())
                    .doOnError(e -> getView().onError())
                    .subscribe();
    }

    @Override
    public void checkIfVoted(String postId) {
            voteDao.findVoteByPostID(postId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> getView().enableVoteButtons())
                    .doOnError(e -> getView().onError())
                    .doOnSuccess(response -> getView().disableVoteButtons())
                    .subscribe();
    }

    @Override
    public void loadOddsData(SingleOdd singleOdd) {
        getView().setPercentage(singleOdd.getPercentage());
        getView().setOddsFor(singleOdd.getOddsFor());
        getView().setOddsAgainst(singleOdd.getOddsAgainst());
        getView().setDescription(singleOdd.getDescription());
        getView().setDueDate(singleOdd.getDueDate());
        getView().setImageUrl(singleOdd.getImageUrl());

    }

    public void loadNumbersData(SingleOdd response) {
        getView().setOddsAgainst(response.getOddsAgainst());
        getView().setOddsFor(response.getOddsFor());
        getView().setPercentage(response.getPercentage());
    }

    @Override
    public void onViewAttached(SingleOddView view) {
        super.onViewAttached(view);
        OddsApplication.getAppComponent().inject(this);
    }

    @Override
    public void onViewDetached() {
        super.onViewDetached();
    }

    @Nullable
    @Override
    public SingleOddView getView() {
        return super.getView() == null ? singleOddView : super.getView();
    }

}
