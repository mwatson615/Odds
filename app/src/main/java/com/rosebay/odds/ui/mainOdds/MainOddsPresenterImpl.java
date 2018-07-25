package com.rosebay.odds.ui.mainOdds;


import android.annotation.SuppressLint;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.rosebay.odds.Constants;
import com.rosebay.odds.OddsApplication;
import com.rosebay.odds.model.SingleOdd;
import com.rosebay.odds.network.FirebaseClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import durdinapps.rxfirebase2.DataSnapshotMapper;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import durdinapps.rxfirebase2.RxFirebaseQuery;
import easymvp.AbstractPresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainOddsPresenterImpl extends AbstractPresenter<MainOddsView> implements MainOddsPresenter {

    @Inject
    public DatabaseReference databaseReference;
    @Inject
    public FirebaseClient firebaseClient;
    @VisibleForTesting
    public MainOddsView view;

    @SuppressLint("CheckResult")
    public void fetchOdds() {
        getView().showInitialToolbarLayout();
        getView().onLoading();
        RxFirebaseDatabase.observeSingleValueEvent(databaseReference.child(Constants.POSTS),
            DataSnapshotMapper.listOf(SingleOdd.class))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError(error -> getView().onError())
            .doOnSuccess(Collections::reverse)
            .subscribe(result -> getView().setData(result));
    }

    @SuppressLint("CheckResult")
    @Override
    public void fetchSearchResults(String searchTerms) {
        if (TextUtils.isEmpty(searchTerms)) {
            getView().onNoSearchResults();
        } else {
            List<SingleOdd> newList = new ArrayList<>();
            DatabaseReference reference = databaseReference.child(Constants.POSTS);
            Query query = reference.orderByChild(Constants.DESCRIPTION).equalTo(searchTerms);

            RxFirebaseQuery.getInstance()
                    .filterByRefs(reference, query)
                    .asList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                                for (DataSnapshot snapshot : response) {
                                    newList.add(snapshot.getValue(SingleOdd.class));
                                }
                                if (newList.size() == 0) {
                                    getView().onNoSearchResults();
                                } else {
                                    getView().setData(newList);
                                    getView().showBackButtonOnSearchResults();
                                }

                            },
                            throwable -> getView().onError());
        }
    }

    @Override
    public void fetchSingleOdd(String postId) {
            firebaseClient.fetchSingleOdd(postId)
                    .doOnError(e -> getView().onError())
                    .doOnSuccess(response -> getView().launchSingleOdd(response))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
    }

    @Override
    public void onViewAttached (MainOddsView view) {
        super.onViewAttached(view);
        OddsApplication.getAppComponent().inject(this);
    }

    @Override
    public void onViewDetached() {
        super.onViewDetached();
    }

    @Override
    public MainOddsView getView() {
        return super.getView() != null ? super.getView() : view;
    }
}

