package com.rosebay.odds.ui.favoriteOdds

import android.annotation.SuppressLint
import android.support.annotation.VisibleForTesting
import com.rosebay.odds.OddsApplication
import com.rosebay.odds.localStorage.FavoriteDao
import com.rosebay.odds.model.Favorite
import com.rosebay.odds.model.SingleOdd
import com.rosebay.odds.network.FirebaseClient
import easymvp.AbstractPresenter
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import javax.inject.Inject

open class FavoriteOddsPresenterImpl : AbstractPresenter<FavoriteOddsView>(), FavoriteOddsPresenter {

    @Inject
    lateinit var favoriteDao: FavoriteDao

    @Inject
    lateinit var firebaseClient: FirebaseClient

    @VisibleForTesting
    lateinit var favoriteOddsView: FavoriteOddsView

    override fun getAllFavorites() {
        Observable.fromCallable { favoriteDao.userFavorites }
                .map { it ->
                    if (it.isEmpty()) {
                        throw Throwable()
                    } else {
                        it
                    }
                }
                .flatMapIterable<Favorite> { items -> items}
                .map { it.postId }

                .doOnSubscribe { view?.onLoading() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( { result -> fetchFromFirebase(result) }
                ) { view?.noFavorites() }
    }

    fun fetchFromFirebase(post: String) {
        view?.onLoading()
        firebaseClient.fetchSingleOdd(post)
                .map { item -> item }
                .toObservable()
                .toList()
                .doOnError { view?.onError() }
                .doOnSuccess { it ->  view?.setData(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    override fun onViewAttached(view: FavoriteOddsView) {
            super.onViewAttached(view)
            OddsApplication.appComponent.inject(this)
        }

    override fun onViewDetached() {
            super.onViewDetached()
        }

    override fun getView(): FavoriteOddsView? {
        return if (super.getView() == null) favoriteOddsView else super.getView()
    }
}
