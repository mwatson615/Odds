package com.rosebay.odds.ui.singleOdd


import android.annotation.SuppressLint
import android.support.annotation.VisibleForTesting
import com.rosebay.odds.util.Constants
import com.rosebay.odds.OddsApplication
import com.rosebay.odds.localStorage.FavoriteDao
import com.rosebay.odds.localStorage.VoteDao
import com.rosebay.odds.model.Favorite
import com.rosebay.odds.model.SingleOdd
import com.rosebay.odds.model.Vote
import com.rosebay.odds.network.CloudFunctionsClient
import com.rosebay.odds.util.Mockable
import easymvp.AbstractPresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject


open class SingleOddPresenterImpl : AbstractPresenter<SingleOddView>(), SingleOddPresenter {

    @Inject
    lateinit var cloudFunctionsClient: CloudFunctionsClient

    @Inject
    lateinit var favoriteDao: FavoriteDao

    @Inject
    lateinit var voteDao: VoteDao

    @VisibleForTesting
    lateinit var singleOddView: SingleOddView

    @SuppressLint("CheckResult")
    override fun addToFavorites(username: String, postId: String) {
        view?.disableFavoritesButton()
        val favorite = Favorite()
        favorite.username = username
        favorite.postId = postId

        Observable.fromCallable { favoriteDao.createFavorite(favorite) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    view?.enableFavoritesButton()
                    view?.onError()
                }
                .subscribe { view?.onAddedToFavorites() }
    }

    override fun voteYes(postId: String) {
        view?.disableVoteButtons()
        val map = HashMap<String, String>()
        map[Constants.POST_ID] = postId
        val voteYes = cloudFunctionsClient.submitVoteYes(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    loadNumbersData(response)
                    addUserVote(response.postId, response.username, true)
                }
                ) {
                    view?.onError()
                    view?.enableVoteButtons()
                }
    }

    override fun voteNo(postId: String) {
        view?.disableVoteButtons()
        val map = HashMap<String, String>()
        map[Constants.POST_ID] = postId
        val voteYes = cloudFunctionsClient.submitNoVote(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    loadNumbersData(response)
                    addUserVote(response.postId, response.username, false)
                }
                ) {
                    view?.onError()
                    view?.enableVoteButtons()
                }
    }

    @SuppressLint("CheckResult")
    fun addUserVote(postId: String, username: String, votedYes: Boolean?) {
        val vote = Vote()
        vote.postId = postId
        vote.username = username
        vote.votedYes = votedYes

        Observable.fromCallable { voteDao.createVoteEntry(vote) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ view?.onVoteSuccess() }
                ) { view?.onError() }
    }

    override fun checkForFavorite(postId: String) {
        favoriteDao.findFavByPostID(postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete { view?.enableFavoritesButton() }
                .doOnSuccess { view?.disableFavoritesButton() }
                .doOnError { view?.onError() }
                .subscribe()
    }

    override fun checkIfVoted(postId: String) {
        voteDao.findVoteByPostID(postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete { view?.enableVoteButtons() }
                .doOnError { view?.onError() }
                .doOnSuccess { view?.disableVoteButtons() }
                .subscribe()
    }

    override fun loadOddsData(singleOdd: SingleOdd) {
        view?.setPercentage(singleOdd.percentage)
        view?.setOddsFor(singleOdd.oddsFor)
        view?.setOddsAgainst(singleOdd.oddsAgainst)
        view?.setDescription(singleOdd.description)
        view?.setCreationInfo(singleOdd.username, singleOdd.dateSubmitted)
        view?.setImageUrl(singleOdd.imageUrl)

    }

    fun loadNumbersData(response: SingleOdd) {
        view?.setOddsAgainst(response.oddsAgainst)
        view?.setOddsFor(response.oddsFor)
        view?.setPercentage(response.percentage)
    }

    override fun onViewAttached(view: SingleOddView) {
        super.onViewAttached(view)
        OddsApplication.appComponent.inject(this)
    }

    override fun onViewDetached() {
        super.onViewDetached()
    }

    override fun getView(): SingleOddView? {
        return if (super.getView() == null) singleOddView else super.getView()
    }

}