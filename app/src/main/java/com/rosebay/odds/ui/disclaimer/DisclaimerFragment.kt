package com.rosebay.odds.ui.disclaimer

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.snackbar.Snackbar
import com.rosebay.odds.OddsApplication
import com.rosebay.odds.R
import com.rosebay.odds.ui.OnUsernameSavedInterface
import com.rosebay.odds.util.SharedPreferencesClient
import easymvp.annotation.FragmentView
import easymvp.annotation.Presenter
import javax.inject.Inject

@FragmentView(presenter = DisclaimerPresenterImpl::class)
open class DisclaimerFragment : Fragment(), DisclaimerView {

    @Inject
    @Presenter
    lateinit var disclaimerPresenter: DisclaimerPresenterImpl

    @Inject
    lateinit var sharedPreferencesClient: SharedPreferencesClient

    @BindView(R.id.disclaimerTextView)
    lateinit var mDisclaimerTextView: TextView
    @BindView(R.id.disclaimerSearchView)
    lateinit var mSearchView: SearchView
    @BindView(R.id.usernameSearchProgressBar)
    lateinit var mSearchProgressBar: ProgressBar
    @BindView(R.id.saveUsernameButton)
    lateinit var mSaveButton: Button

    var mCallback: OnUsernameSavedInterface? = null

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
        if (context is OnUsernameSavedInterface)
            mCallback = context
    }

    override fun onResume() {
        super.onResume()
        disclaimerPresenter.onViewAttached(this)
    }

    override fun onPause() {
        super.onPause()
        disclaimerPresenter.onViewDetached()
    }

    override fun onUsernameAvailable() {
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
                    disclaimerPresenter.checkForUsername(mSearchView.query.toString())
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
        mCallback!!.onUsernameSaved()
    }

    @OnClick(R.id.disclaimerSearchView)
    fun searchForUsername() {
        if (!mSearchView.query.toString().isNullOrEmpty()) {
            disclaimerPresenter.checkForUsername(mSearchView.query.toString())
        }
    }

    @OnClick(R.id.saveUsernameButton)
    fun saveUsername() {
        disclaimerPresenter.saveUsername(mSearchView.query.toString())
    }

    override fun onDetach() {
        super.onDetach()
        mCallback = null
    }

    override fun onDestroy() {
        super.onDestroy()
        mCallback = null
    }

    companion object {

        fun newInstance(): DisclaimerFragment {
            return DisclaimerFragment()
        }
    }

}
