package com.rosebay.odds.ui.myOdds


import android.annotation.SuppressLint
import android.support.annotation.VisibleForTesting
import com.google.firebase.database.DatabaseReference
import com.rosebay.odds.OddsApplication
import com.rosebay.odds.model.SingleOdd
import com.rosebay.odds.util.Constants
import com.rosebay.odds.util.EmptyResponseException
import durdinapps.rxfirebase2.RxFirebaseQuery
import easymvp.AbstractPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

open class MyOddsPresenterImpl @Inject constructor() : AbstractPresenter<MyOddsView>(), MyOddsPresenter {

    @Inject
    lateinit var databaseReference: DatabaseReference

    @VisibleForTesting
    lateinit var myOddsView: MyOddsView

    @SuppressLint("CheckResult")
    override fun fetchMyOdds(username: String) {
        view?.onLoading()
        val myOdds = ArrayList<SingleOdd>()
        val reference = databaseReference.child(Constants.POSTS)
        val query = reference.orderByChild(Constants.USERNAME).equalTo(username)

        RxFirebaseQuery.getInstance()
                .filterByRefs(reference, query)
                .asList()
                .map { it ->
                    if (it.isEmpty()) {
                        throw EmptyResponseException()
                    } else {
                        it
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( { response ->
                    for (snapshot in response) {
                        myOdds.add(snapshot.getValue(SingleOdd::class.java)!!)
                    }
                }, {
                  e ->
                    if (e is EmptyResponseException) {
                        view?.onNoResponse()
                    } else {
                        view?.onError()
                    }
                })
    }

    override fun onViewAttached(view: MyOddsView) {
        super.onViewAttached(view)
        OddsApplication.appComponent.inject(this)
    }

    override fun onViewDetached() {
        super.onViewDetached()
    }

    override fun getView(): MyOddsView? {
        return if (super.getView() == null) myOddsView else super.getView()
    }
}
