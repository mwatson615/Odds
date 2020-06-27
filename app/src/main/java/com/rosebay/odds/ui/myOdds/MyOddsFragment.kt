package com.rosebay.odds.ui.myOdds

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.snackbar.Snackbar
import com.rosebay.odds.OddsApplication
import com.rosebay.odds.R
import com.rosebay.odds.model.SingleOdd
import com.rosebay.odds.util.SharedPreferencesClient
import easymvp.annotation.FragmentView
import easymvp.annotation.Presenter
import javax.inject.Inject

@FragmentView(presenter = MyOddsPresenterImpl::class)
open class MyOddsFragment : Fragment(), MyOddsView {

    @Inject
    @Presenter
    lateinit var myOddsPresenter: MyOddsPresenterImpl

    @Inject
    lateinit var preferencesClient: SharedPreferencesClient

    @BindView(R.id.myOddsRecyclerView)
    lateinit var myOddsRecyclerView: RecyclerView
    @BindView(R.id.noMyOddsTextView)
    lateinit var mNoMyOddsTextView: TextView
    @BindView(R.id.myOddsProgressBar)
    lateinit var mProgressBar: ProgressBar

    lateinit var myOddsAdapter: MyOddsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_my_odds,
                container, false)
        ButterKnife.bind(this, root)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myOddsRecyclerView.visibility = View.INVISIBLE
        mNoMyOddsTextView.visibility = View.INVISIBLE
        mProgressBar.visibility = View.INVISIBLE
        myOddsRecyclerView.setHasFixedSize(true)
        myOddsRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    override fun onAttach(context: Context) {
        OddsApplication.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onResponse(response: List<SingleOdd>) {
        mProgressBar.visibility = View.GONE
        mNoMyOddsTextView.visibility = View.GONE
        myOddsRecyclerView.visibility = View.VISIBLE
        myOddsAdapter = MyOddsAdapter(response, context)
        myOddsRecyclerView.adapter = myOddsAdapter
        myOddsAdapter.notifyDataSetChanged()
    }

    override fun onNoResponse() {
        mProgressBar.visibility = View.GONE
        myOddsRecyclerView.visibility = View.GONE
        mNoMyOddsTextView.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        myOddsPresenter.onViewAttached(this)
        val username = if (preferencesClient.getUsername(resources.getString(R.string.username)) != null)
            preferencesClient.getUsername(resources.getString(R.string.username)) else ""
        myOddsPresenter.fetchMyOdds(username)
    }

    override fun onPause() {
        super.onPause()
        myOddsPresenter.onViewDetached()
    }

    override fun onError() {
        Snackbar.make(myOddsRecyclerView, R.string.bats_data_error, Snackbar.LENGTH_SHORT).show()
    }

    override fun onLoading() {
        mProgressBar.visibility = View.VISIBLE
        myOddsRecyclerView.visibility = View.INVISIBLE
        mNoMyOddsTextView.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {

        fun newInstance(): MyOddsFragment {
            return MyOddsFragment()
        }
    }
}

