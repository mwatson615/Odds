package com.rosebay.odds.ui.mainOdds

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView

import com.rosebay.odds.OddsApplication
import com.rosebay.odds.R
import com.rosebay.odds.model.SingleOdd
import com.rosebay.odds.ui.CreateSingleOddInterface
import com.squareup.leakcanary.RefWatcher
import java.util.Objects

import javax.inject.Inject

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import easymvp.annotation.FragmentView
import easymvp.annotation.Presenter

@FragmentView(presenter = MainOddsPresenterImpl::class)
class MainOddsFragment : Fragment(), MainOddsView, MainOddsAdapter.ClickListener {

    @Inject
    @Presenter
    lateinit var mainOddsPresenter: MainOddsPresenterImpl

    @BindView(R.id.home_recycler_view)
    lateinit var mHomeRecyclerView: RecyclerView
    @BindView(R.id.searchButton)
    lateinit var mSearchButton: ImageButton
    @BindView(R.id.searchView)
    lateinit var mSearchView: android.support.v7.widget.SearchView
    @BindView(R.id.search_close_btn)
    lateinit var mSearchViewBtn: AppCompatImageView
    @BindView(R.id.closeSearchButton)
    lateinit var mCloseSearchButton: ImageButton
    @BindView(R.id.appName)
    lateinit var appNameTextView: TextView
    @BindView(R.id.beginSearchButton)
    lateinit var mBeginSearchButton: ImageButton
    @BindView(R.id.backToMainButton)
    lateinit var mBackToMainButton: ImageButton
    @BindView(R.id.mainProgressBar)
    lateinit var mProgressBar: ProgressBar

    lateinit var mMainOddsAdapter: MainOddsAdapter
    var createSingleOddInterface: CreateSingleOddInterface? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_main_odds,
                container, false)
        ButterKnife.bind(this, root)
        showInitialToolbarLayout()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mHomeRecyclerView.setHasFixedSize(true)
        mHomeRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    override fun onAttach(context: Context?) {
        OddsApplication.appComponent.inject(this)
        super.onAttach(context)
        if (context is CreateSingleOddInterface)
            createSingleOddInterface = context
    }

    override fun onResume() {
        super.onResume()
        mainOddsPresenter.onViewAttached(this)
        mainOddsPresenter.fetchOdds()
    }

    override fun onPause() {
        super.onPause()
        mainOddsPresenter.onViewDetached()
    }

    override fun setData(singleOddList: List<SingleOdd>) {
        mHomeRecyclerView.visibility = View.VISIBLE
        mProgressBar.visibility = View.GONE
        mMainOddsAdapter = MainOddsAdapter(singleOddList, this@MainOddsFragment)
        mHomeRecyclerView.adapter = mMainOddsAdapter
        mMainOddsAdapter.notifyDataSetChanged()
    }

    override fun showInitialToolbarLayout() {
        mSearchView.visibility = View.INVISIBLE
        mCloseSearchButton.visibility = View.INVISIBLE
        mBeginSearchButton.visibility = View.INVISIBLE
        mBackToMainButton.visibility = View.INVISIBLE
        appNameTextView.visibility = View.VISIBLE
    }

    override fun onLoading() {
        mHomeRecyclerView.visibility = View.GONE
        mProgressBar.visibility = View.VISIBLE
    }

    @OnClick(R.id.searchButton)
    fun prepareSearch() {
        mSearchView.visibility = View.VISIBLE
        mSearchViewBtn.isEnabled = false
        mSearchViewBtn.setImageDrawable(null)
        mSearchView.isIconified = false
        mSearchView.isFocusable = true
        mSearchView.requestFocus()
        appNameTextView.visibility = View.INVISIBLE
        mCloseSearchButton.visibility = View.VISIBLE
        mBeginSearchButton.visibility = View.VISIBLE
        mSearchButton.visibility = View.INVISIBLE
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                onSearch()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    @OnClick(R.id.closeSearchButton)
    fun closeSearch() {
        mSearchView.visibility = View.INVISIBLE
        mSearchView.isIconified = false
        mSearchView.isFocusable = false
        mSearchView.clearFocus()
        appNameTextView.visibility = View.VISIBLE
        mCloseSearchButton.visibility = View.INVISIBLE
        mBeginSearchButton.visibility = View.INVISIBLE
        mSearchButton.visibility = View.VISIBLE
    }

    @OnClick(R.id.beginSearchButton)
    fun conductSearch() {
        onSearch()
    }

    fun onSearch() {
        mSearchView.isIconified = false
        mSearchView.isFocusable = false
        mSearchView.clearFocus()
        mainOddsPresenter.fetchSearchResults(mSearchView.query.toString())
    }

    @OnClick(R.id.backToMainButton)
    fun reloadMainOdds() {
        mainOddsPresenter.fetchOdds()
        mSearchButton.visibility = View.VISIBLE
    }

    override fun onItemClicked(singleOdd: SingleOdd) {
        mainOddsPresenter.fetchSingleOdd(singleOdd.postId)
    }

    override fun launchSingleOdd(singleOdd: SingleOdd) {
        createSingleOddInterface?.getSingleOddsFragment(singleOdd)
    }

    override fun onError() {
        Snackbar.make(mHomeRecyclerView, R.string.bats_data_error, Snackbar.LENGTH_SHORT).show()
    }

    override fun onNoSearchResults() {
        Snackbar.make(mHomeRecyclerView, R.string.no_search_results_msg, Snackbar.LENGTH_SHORT).show()
    }

    override fun showBackButtonOnSearchResults() {
        mCloseSearchButton.visibility = View.INVISIBLE
        mBackToMainButton.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        val refWatcher = OddsApplication.getRefWatcher(Objects.requireNonNull<FragmentActivity>(activity))
        refWatcher.watch(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {

        fun newInstance(): MainOddsFragment {
            return MainOddsFragment()
        }
    }
}
