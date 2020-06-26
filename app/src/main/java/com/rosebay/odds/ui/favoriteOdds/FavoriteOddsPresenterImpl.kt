package com.rosebay.odds.ui.favoriteOdds

import androidx.annotation.VisibleForTesting
import com.rosebay.odds.OddsApplication
import com.rosebay.odds.localStorage.FavoriteDao
import com.rosebay.odds.model.Favorite
import com.rosebay.odds.network.FirebaseClient
import com.rosebay.odds.util.EmptyResponseException
import easymvp.AbstractPresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.CompositeException
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

open class FavoriteOddsPresenterImpl @Inject constructor() : AbstractPresenter<FavoriteOddsView>(), FavoriteOddsPresenter {

    @Inject
    lateinit var favoriteDao: FavoriteDao

    @Inject
    lateinit var firebaseClient: FirebaseClient

    @VisibleForTesting
    var favoriteOddsView: FavoriteOddsView? = null

    override fun getAllFavorites() {
        view?.onLoading()
        Observable.fromCallable { favoriteDao.userFavorites }
                .map { it ->
                    if (it.isEmpty()) {
                        throw EmptyResponseException()
                    } else {
                        it
                    }
                }
                .flatMapIterable<Favorite> { items -> items}
                .map { it -> firebaseClient.fetchSingleOdd(it.postId) }
                .flatMap { item -> item.toObservable() }
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( { result -> view?.setData(result) },
                    { e ->
                        when (e) {
                            is EmptyResponseException -> {
                                view?.noFavorites()
                            }
                            is CompositeException -> for (exception in e.exceptions) {
                                if (exception is EmptyResponseException) {
                                    view?.noFavorites()
                                }
                            }
                            else ->  {
                                view?.onError()
                            }
                        }
                    })
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
