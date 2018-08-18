package com.rosebay.odds.ui.favoriteOdds

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.rosebay.odds.OddsApplication
import com.rosebay.odds.R
import com.rosebay.odds.model.SingleOdd
import com.rosebay.odds.util.Mockable
import easymvp.annotation.FragmentView
import easymvp.annotation.Presenter
import javax.inject.Inject

@Mockable
@FragmentView(presenter = FavoriteOddsPresenterImpl::class)
class FavoriteOddsFragment : Fragment(), FavoriteOddsView {

    @Inject
    @Presenter
    lateinit var favoriteOddsPresenter: FavoriteOddsPresenterImpl

    lateinit var favoriteOddsAdapter: FavoriteOddsAdapter

    @BindView(R.id.fragmentFavoriteOdds)
    lateinit var mLayout: ConstraintLayout
    @BindView(R.id.favoritesRecyclerView)
    lateinit var mFavoritesRecyclerView: RecyclerView
    @BindView(R.id.noFavoriteOddsTextView)
    lateinit var mNoFavoritesTextView: TextView
    @BindView(R.id.favoritesProgressBar)
    lateinit var mProgressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_favorite_odds, container, false)
        ButterKnife.bind(this, root)
        OddsApplication.appComponent.inject(this)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNoFavoritesTextView.visibility = View.INVISIBLE
        mProgressBar.visibility = View.INVISIBLE
        mFavoritesRecyclerView.visibility = View.INVISIBLE
        mFavoritesRecyclerView.setHasFixedSize(true)
        mFavoritesRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    override fun onLoading() {
        mProgressBar.visibility = View.VISIBLE
        mFavoritesRecyclerView.visibility = View.INVISIBLE
        mNoFavoritesTextView.visibility = View.INVISIBLE
    }

    override fun setData(singleOdds: List<SingleOdd>) {
        mFavoritesRecyclerView.visibility = View.VISIBLE
        mNoFavoritesTextView.visibility = View.GONE
        mProgressBar.visibility = View.GONE
        favoriteOddsAdapter = FavoriteOddsAdapter(singleOdds, context)
        mFavoritesRecyclerView.adapter = favoriteOddsAdapter
        favoriteOddsAdapter.notifyDataSetChanged()
    }

    override fun noFavorites() {
        mProgressBar.visibility = View.GONE
        mFavoritesRecyclerView.visibility = View.GONE
        mNoFavoritesTextView.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        favoriteOddsPresenter.onViewAttached(this)
        favoriteOddsPresenter.getAllFavorites()
    }

    override fun onPause() {
        super.onPause()
        favoriteOddsPresenter.onViewDetached()
    }

    override fun onError() {
        Snackbar.make(mLayout, R.string.bats_data_error, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        val refWatcher = OddsApplication.getRefWatcher(activity!!)
        refWatcher.watch(this)
    }

    companion object {

        fun newInstance(): FavoriteOddsFragment {
            return FavoriteOddsFragment()
        }
    }
}