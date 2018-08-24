package com.rosebay.odds.ui.createOdds


import android.support.annotation.VisibleForTesting
import com.google.firebase.database.DatabaseReference
import com.rosebay.odds.OddsApplication
import com.rosebay.odds.localStorage.VoteDao
import com.rosebay.odds.model.ImageItem
import com.rosebay.odds.model.SingleOdd
import com.rosebay.odds.model.Vote
import com.rosebay.odds.network.ImageClient
import com.rosebay.odds.util.Constants
import com.rosebay.odds.util.DaoNotUpdatedException
import easymvp.AbstractPresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

open class CreateOddsPresenterImpl @Inject constructor() : AbstractPresenter<CreateOddsView>(), CreateOddsPresenter {

    @Inject
    lateinit var imageClient: ImageClient

    @Inject
    lateinit var firebaseReference: DatabaseReference

    @Inject
    lateinit var voteDao : VoteDao

    @VisibleForTesting
    var createOddsView: CreateOddsView? = null

    override fun createOdds(singleOdd: SingleOdd) {
        singleOdd.postId = firebaseReference.child(Constants.POSTS).push().key
        firebaseReference.child(Constants.POSTS).child(singleOdd.postId).setValue(singleOdd)
        view?.onSave(singleOdd.description)
        addUserVote(singleOdd)
        view?.clearTextFields()
    }

    override fun addUserVote(singleOdd: SingleOdd) {
        val vote = Vote()
        vote.postId = singleOdd.postId
        vote.username = singleOdd.username
        vote.votedYes = singleOdd.oddsFor == 1

        Observable.fromCallable { voteDao.createVoteEntry(vote) }
                .map { it ->
                    if (it < 1L) {
                        throw DaoNotUpdatedException()
                    } else {
                        it
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { view?.onError() }
                .retry(2)
                .subscribe( {} ,
                        {view?.onError()})
    }

    override fun getImages(description: String, queryTerms: String) {
        view?.onSearch()
        view?.closeKeyboard()
        imageClient.fetchImages(description, queryTerms)
                .flatMapIterable<ImageItem> { (Iterable<ImageItem>{it.items.listIterator()})}
                .filter { item -> item.link != null }
                .map<String>{ it.link }
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response -> view?.loadImages(response) }
                ) { view?.onError() }
    }

    override fun onViewAttached(view: CreateOddsView) {
        super.onViewAttached(view)
        OddsApplication.appComponent.inject(this)
    }

    override fun onViewDetached() {
        super.onViewDetached()
    }

    override fun getView(): CreateOddsView? {
        return if (super.getView() == null) createOddsView else super.getView()
    }
}
