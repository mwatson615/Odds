package com.rosebay.odds.ui.disclaimer;

import android.annotation.SuppressLint;
import android.support.annotation.VisibleForTesting;

import com.google.firebase.database.DatabaseReference;
import com.rosebay.odds.Constants;
import com.rosebay.odds.OddsApplication;
import com.rosebay.odds.network.FirebaseClient;

import javax.annotation.Nullable;
import javax.inject.Inject;

import durdinapps.rxfirebase2.RxFirebaseDatabase;
import easymvp.AbstractPresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class DisclaimerPresenterImpl extends AbstractPresenter<DisclaimerView> implements DisclaimerPresenter {

    @Inject
    FirebaseClient firebaseClient;

    @Inject
    DatabaseReference databaseReference;

    @VisibleForTesting
    DisclaimerView disclaimerView;

    @SuppressLint("CheckResult")
    @Override
    public void checkForUsername(String username) {
        getView().showProgressLayout();
        RxFirebaseDatabase.observeSingleValueEvent(databaseReference.child(Constants.USERS).orderByValue().equalTo(username))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            if (response.getValue() == null) {
                                getView().onUsernameAvailable(username);
                                getView().showSearchLayout();
                            } else {
                                getView().onUsernameTaken();
                                getView().showSearchLayout();
                            }
                        },
                        throwable -> getView().onError());
    }

    @SuppressLint("CheckResult")
    @Override
    public void saveUsername(String username) {
        firebaseClient.addUser(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> getView().onUserSaved(username),
                        e -> getView().onError());

    }

    @Override
    public void onViewAttached(DisclaimerView view) {
        super.onViewAttached(view);
        OddsApplication.getAppComponent().inject(this);
    }

    @Override
    public void onViewDetached() {
        super.onViewDetached();
    }

    @Nullable
    @Override
    public DisclaimerView getView() {
        return super.getView() == null ? disclaimerView : super.getView();
    }

}
