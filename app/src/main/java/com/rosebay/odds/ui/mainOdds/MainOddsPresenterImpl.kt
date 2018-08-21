package com.rosebay.odds.ui.mainOdds


import android.annotation.SuppressLint
import android.support.annotation.VisibleForTesting
import android.text.TextUtils
import com.google.firebase.database.DatabaseReference
import com.rosebay.odds.OddsApplication
import com.rosebay.odds.model.SingleOdd
import com.rosebay.odds.network.FirebaseClient
import com.rosebay.odds.util.Constants
import durdinapps.rxfirebase2.DataSnapshotMapper
import durdinapps.rxfirebase2.RxFirebaseDatabase
import durdinapps.rxfirebase2.RxFirebaseQuery
import easymvp.AbstractPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

open class MainOddsPresenterImpl : AbstractPresenter<MainOddsView>(), MainOddsPresenter {

    @Inject
    lateinit var databaseReference: DatabaseReference
    @Inject
    lateinit var firebaseClient: FirebaseClient
    @VisibleForTesting
    public var viewInterface: MainOddsView? = null

    @SuppressLint("CheckResult")
    override fun fetchOdds() {
        view?.showInitialToolbarLayout()
        view?.onLoading()
        RxFirebaseDatabase.observeSingleValueEvent(databaseReference.child(Constants.POSTS),
                DataSnapshotMapper.listOf(SingleOdd::class.java))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { view?.onError() }
                .doOnSuccess { it.reverse() }
                .subscribe { result -> view?.setData(result) }
    }

    @SuppressLint("CheckResult")
    override fun fetchSearchResults(searchTerms: String) {
        if (TextUtils.isEmpty(searchTerms)) {
            view?.onNoSearchResults()
        } else {
            val newList = ArrayList<SingleOdd>()
            val reference = databaseReference.child(Constants.POSTS)
            val query = reference.orderByChild(Constants.DESCRIPTION).equalTo(searchTerms)

            RxFirebaseQuery.getInstance()
                    .filterByRefs(reference, query)
                    .asList()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        for (snapshot in response) {
                            newList.add(snapshot.getValue(SingleOdd::class.java)!!)
                        }
                        if (newList.size == 0) {
                            view?.onNoSearchResults()
                        } else {
                            view?.setData(newList)
                            view?.showBackButtonOnSearchResults()
                        }

                    }
                    ) { view?.onError() }
        }
    }

    override fun fetchSingleOdd(postId: String) {
        firebaseClient.fetchSingleOdd(postId)
                .doOnError { view?.onError() }
                .doOnSuccess { response -> view?.launchSingleOdd(response) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    override fun onViewAttached(view: MainOddsView) {
        super.onViewAttached(view)
        OddsApplication.appComponent.inject(this)
    }

    override fun onViewDetached() {
        super.onViewDetached()
    }

    override fun getView(): MainOddsView? {
        return if (super.getView() != null) super.getView() else viewInterface
    }
}

