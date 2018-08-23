package com.rosebay.odds.ui.mainOdds


import android.annotation.SuppressLint
import android.support.annotation.VisibleForTesting
import com.google.firebase.database.DatabaseReference
import com.rosebay.odds.OddsApplication
import com.rosebay.odds.model.SingleOdd
import com.rosebay.odds.network.CloudFunctionsClient
import com.rosebay.odds.network.FirebaseClient
import com.rosebay.odds.util.Constants
import durdinapps.rxfirebase2.DataSnapshotMapper
import durdinapps.rxfirebase2.RxFirebaseDatabase
import easymvp.AbstractPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

open class MainOddsPresenterImpl : AbstractPresenter<MainOddsView>(), MainOddsPresenter {

    @Inject
    lateinit var databaseReference: DatabaseReference
    @Inject
    lateinit var cloudFunctionClient : CloudFunctionsClient
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

    override fun fetchSearchResults(searchTerms: String) {
        view?.onLoading()
        cloudFunctionClient.search(searchTerms)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( {it ->
                    if (it.isEmpty()) {
                        view?.onNoSearchResults()
                    } else {
                        view?.setData(it)
                    }
                },
                        {
                            view?.onError()
                        })
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

