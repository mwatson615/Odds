package com.rosebay.odds.ui.createOdds


import android.support.annotation.VisibleForTesting
import com.google.firebase.database.DatabaseReference
import com.rosebay.odds.Constants
import com.rosebay.odds.model.ImageItem
import com.rosebay.odds.model.SingleOdd
import com.rosebay.odds.network.ImageClient
import easymvp.AbstractPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CreateOddsPresenterImpl : AbstractPresenter<CreateOddsView>(), CreateOddsPresenter {

    @Inject
    lateinit var imageClient: ImageClient

    @Inject
    lateinit var firebaseReference: DatabaseReference

    @VisibleForTesting
    lateinit var createOddsView: CreateOddsView

    override fun createOdds(singleOdd: SingleOdd) {
        singleOdd.postId = firebaseReference.child(Constants.POSTS).push().key
        firebaseReference.child(Constants.POSTS).child(singleOdd.postId).setValue(singleOdd)
        view!!.onSave(singleOdd.description)
        view!!.clearTextFields()
    }

    override fun getImages(description: String, queryTerms: String) {
        view?.onSearch()
        view?.closeKeyboard()
        val urlResponseSingle = imageClient.fetchImages(description, queryTerms)
                .flatMapIterable<ImageItem> { (Iterable<ImageItem>{it.items.listIterator()})}
                .filter { item -> item.link != null }
                .map<String>{ it.link }
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response -> view?.loadImages(response) }
                ) { view?.onError() }
    }

//    Function<ImageResponse, Iterable<ImageItem>> { it.getItems() }

    override fun onViewAttached(view: CreateOddsView) {
        super.onViewAttached(view)
    }

    override fun onViewDetached() {
        super.onViewDetached()
    }

    override fun getView(): CreateOddsView? {
        return if (super.getView() == null) createOddsView else super.getView()
    }
}
