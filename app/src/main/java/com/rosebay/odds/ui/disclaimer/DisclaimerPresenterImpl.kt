package com.rosebay.odds.ui.disclaimer

import android.annotation.SuppressLint
import android.support.annotation.VisibleForTesting
import com.google.firebase.database.DatabaseReference
import com.rosebay.odds.Constants
import com.rosebay.odds.OddsApplication
import com.rosebay.odds.network.FirebaseClient
import durdinapps.rxfirebase2.RxFirebaseDatabase
import easymvp.AbstractPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DisclaimerPresenterImpl : AbstractPresenter<DisclaimerView>(), DisclaimerPresenter {

    @Inject
    lateinit var firebaseClient: FirebaseClient

    @Inject
    lateinit var databaseReference: DatabaseReference

    @VisibleForTesting
    lateinit var disclaimerView: DisclaimerView

    @SuppressLint("CheckResult")
    override fun checkForUsername(username: String) {
        view?.showProgressLayout()
        RxFirebaseDatabase.observeSingleValueEvent(databaseReference.child(Constants.USERS).orderByValue().equalTo(username))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    if (response.value == null) {
                        view?.onUsernameAvailable(username)
                        view?.showSearchLayout()
                    } else {
                        view?.onUsernameTaken()
                        view?.showSearchLayout()
                    }
                }
                ) { view?.onError() }
    }

    @SuppressLint("CheckResult")
    override fun saveUsername(username: String) {
        firebaseClient.addUser(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ view?.onUserSaved(username) }
                ) { view?.onError() }

    }

    override fun onViewAttached(view: DisclaimerView) {
        super.onViewAttached(view)
        OddsApplication.appComponent.inject(this)
    }

    override fun onViewDetached() {
        super.onViewDetached()
    }

    override fun getView(): DisclaimerView? {
        return if (super.getView() == null) disclaimerView else super.getView()
    }

}
