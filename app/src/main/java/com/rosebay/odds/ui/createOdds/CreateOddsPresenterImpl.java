package com.rosebay.odds.ui.createOdds;


import android.support.annotation.VisibleForTesting;

import com.google.firebase.database.DatabaseReference;
import com.rosebay.odds.Constants;
import com.rosebay.odds.OddsApplication;
import com.rosebay.odds.model.ImageItem;
import com.rosebay.odds.model.ImageResponse;
import com.rosebay.odds.model.SingleOdd;
import com.rosebay.odds.network.ImageClient;

import javax.annotation.Nullable;
import javax.inject.Inject;

import easymvp.AbstractPresenter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CreateOddsPresenterImpl extends AbstractPresenter<CreateOddsView> implements CreateOddsPresenter {

    @Inject
    ImageClient imageClient;

    @Inject
    DatabaseReference firebaseReference;

    @VisibleForTesting
    CreateOddsView createOddsView;

    @Override
    public void createOdds(SingleOdd singleOdd) {
        singleOdd.setPostId(firebaseReference.child(Constants.POSTS).push().getKey());
        firebaseReference.child(Constants.POSTS).child(singleOdd.getPostId()).setValue(singleOdd);
        getView().onSave(singleOdd.getDescription());
        getView().clearTextFields();
    }

    @Override
    public void getImages(String description, String queryTerms) {
            getView().onSearch();
            getView().closeKeyboard();
            Disposable urlResponseSingle = imageClient.fetchImages(description, queryTerms)
                    .flatMapIterable(ImageResponse::getItems)
                    .filter(item -> item.getLink() != null)
                    .map(ImageItem::getLink)
                    .toList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        getView().loadImages(response);
                            },
                            throwable -> getView().onError());
    }

    @Override
    public void onViewAttached(CreateOddsView view) {
        super.onViewAttached(view);
        OddsApplication.getAppComponent().inject(this);
    }

    @Override
    public void onViewDetached() {
        super.onViewDetached();
    }

    @Nullable
    @Override
    public CreateOddsView getView() {
        return super.getView() == null ? createOddsView : super.getView();
    }
}
