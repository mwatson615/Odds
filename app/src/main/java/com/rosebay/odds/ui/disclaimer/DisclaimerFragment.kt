package com.rosebay.odds.ui.disclaimer

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.rosebay.odds.OddsApplication
import com.rosebay.odds.R
import com.rosebay.odds.ui.OnUsernameSavedInterface
import com.rosebay.odds.util.SharedPreferencesClient
import easymvp.annotation.FragmentView
import easymvp.annotation.Presenter
import javax.inject.Inject

@FragmentView(presenter = DisclaimerPresenterImpl::class)
open class DisclaimerFragment : Fragment(), DisclaimerView {

    @Presenter
    var disclaimerPresenter: DisclaimerPresenterImpl? = null

    @Inject
    lateinit var sharedPreferencesClient: SharedPreferencesClient

    @BindView(R.id.disclaimerTextView)
    lateinit var mDisclaimerTextView: TextView
    @BindView(R.id.disclaimerSearchView)
    lateinit var mSearchView: android.support.v7.widget.SearchView
    @BindView(R.id.usernameSearchProgressBar)
    lateinit var mSearchProgressBar: ProgressBar
    @BindView(R.id.saveUsernameButton)
    lateinit var mSaveButton: Button

    lateinit var mCallback: OnUsernameSavedInterface

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_disclaimer_username, container, false)
        ButterKnife.bind(this, root)
        showSearchLayout()
        mSaveButton.isEnabled = false
        return root
    }

    override fun onAttach(context: Context) {
        OddsApplication.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        disclaimerPresenter?.onViewAttached(this)
    }

    override fun onPause() {
        super.onPause()
        disclaimerPresenter?.onViewDetached()
    }

    override fun onUsernameAvailable(username: String) {
        mSearchProgressBar.visibility = View.GONE
        mSaveButton.isEnabled = true
    }

    override fun onUsernameTaken() {
        Snackbar.make(mDisclaimerTextView, R.string.username_taken_msg, Snackbar.LENGTH_SHORT).show()
    }

    override fun onError() {
        Snackbar.make(mDisclaimerTextView, R.string.bats_data_error, Snackbar.LENGTH_SHORT).show()
    }

    override fun showSearchLayout() {
        mSearchProgressBar.visibility = View.GONE
        mSearchView.visibility = View.VISIBLE
        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (!TextUtils.isEmpty(query)) {
                    disclaimerPresenter?.checkForUsername(mSearchView.query.toString())
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                mSaveButton.isEnabled = false
                return false
            }
        })
    }

    override fun showProgressLayout() {
        mSearchProgressBar.visibility = View.VISIBLE
        mSearchView.visibility = View.GONE
        mSaveButton.isEnabled = false
    }

    override fun onUserSaved(username: String) {
        sharedPreferencesClient.saveUsername(getString(R.string.username), username)
        Snackbar.make(mDisclaimerTextView, String.format(getString(R.string.username_saved_msg), username), Snackbar.LENGTH_SHORT).show()
        mCallback.onUsernameSaved()
    }

    @OnClick(R.id.disclaimerSearchView)
    fun searchForUsername() {
        if (mSearchView.query.toString() != "") {
            disclaimerPresenter?.checkForUsername(mSearchView.query.toString())
        }
    }

    @OnClick(R.id.saveUsernameButton)
    fun saveUsername() {
        disclaimerPresenter?.saveUsername(mSearchView.query.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
//        val refWatcher = OddsApplication.getRefWatcher(activity!!)
//        refWatcher.watch(this)
    }

    companion object {

        fun newInstance(): DisclaimerFragment {
            return DisclaimerFragment()
        }
    }

}
