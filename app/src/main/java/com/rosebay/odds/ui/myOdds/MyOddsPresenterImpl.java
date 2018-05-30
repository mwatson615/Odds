package com.rosebay.odds.ui.myOdds;


import android.annotation.SuppressLint;
import android.support.annotation.VisibleForTesting;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.rosebay.odds.Constants;
import com.rosebay.odds.OddsApplication;
import com.rosebay.odds.model.SingleOdd;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseQuery;
import easymvp.AbstractPresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MyOddsPresenterImpl extends AbstractPresenter<MyOddsView> implements MyOddsPresenter {

    @Inject
    DatabaseReference databaseReference;

    @VisibleForTesting
    MyOddsView myOddsView;

    @SuppressLint("CheckResult")
    @Override
    public void fetchMyOdds(String username) {
        getView().onLoading();
        List<SingleOdd> myOdds = new ArrayList<>();
        DatabaseReference reference = databaseReference.child(Constants.POSTS);
        Query query = reference.orderByChild(Constants.USERNAME).equalTo(username);

        RxFirebaseQuery.getInstance()
                .filterByRefs(reference, query)
                .asList()
                .doOnError(e -> getView().onError())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    for (DataSnapshot snapshot : response) {
                        myOdds.add(snapshot.getValue(SingleOdd.class));
                    }
                    if (getView() != null) {
                        if (myOdds.size() == 0) {
                            getView().onNoResponse();
                        } else {
                            getView().onResponse(myOdds);
                        }
                    }
                });
    }

    @Override
    public void onViewAttached(MyOddsView view) {
        super.onViewAttached(view);
        OddsApplication.getAppComponent().inject(this);
    }

    @Override
    public void onViewDetached() {
        super.onViewDetached();
    }

    @Nullable
    @Override
    public MyOddsView getView() {
        return super.getView() == null ? myOddsView : super.getView();
    }
}
