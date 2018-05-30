package com.rosebay.odds.ui.favoriteOdds;

import android.annotation.SuppressLint;
import android.support.annotation.VisibleForTesting;

import com.rosebay.odds.OddsApplication;
import com.rosebay.odds.localStorage.FavoriteDao;
import com.rosebay.odds.model.Favorite;
import com.rosebay.odds.model.SingleOdd;
import com.rosebay.odds.network.FirebaseClient;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import easymvp.AbstractPresenter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FavoriteOddsPresenterImpl extends AbstractPresenter<FavoriteOddsView> implements FavoriteOddsPresenter {

    @Inject
    FavoriteDao favoriteDao;

    @Inject
    FirebaseClient firebaseClient;

    @VisibleForTesting
    FavoriteOddsView favoriteOddsView;

    List<SingleOdd> mSingleOddsList = new ArrayList<>();

    @SuppressLint("CheckResult")
    public void getAllFavorites() {
        Observable.fromCallable(() -> favoriteDao.getUserFavorites())
            .flatMapIterable(items -> {
                if (items != null) {
                    return items;
                } else {
                    throw new NullPointerException();
                }
            })
            .map(Favorite::getPostId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(result -> fetchFromFirebase(result),
                    e -> getView().noFavorites());
        }

    public void fetchFromFirebase(String post) {
        getView().onLoading();
        Disposable disposable = firebaseClient.fetchSingleOdd(post)
            .map(item -> mSingleOddsList.add(item))
            .doOnError(e -> getView().onError())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(result -> getView().setData(mSingleOddsList));
    }

    @Override
    public void onViewAttached(FavoriteOddsView view) {
        super.onViewAttached(view);
        OddsApplication.getAppComponent().inject(this);
    }

    @Override
    public void onViewDetached() {
        super.onViewDetached();
    }

    @Nullable
    @Override
    public FavoriteOddsView getView() {
        return super.getView() == null ? favoriteOddsView : super.getView();
    }
}
