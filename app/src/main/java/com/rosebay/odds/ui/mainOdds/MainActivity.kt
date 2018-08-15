package com.rosebay.odds.ui.mainOdds

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.rosebay.odds.BuildConfig
import com.rosebay.odds.OddsApplication
import com.rosebay.odds.R
import com.rosebay.odds.model.SingleOdd
import com.rosebay.odds.ui.CreateSingleOddInterface
import com.rosebay.odds.ui.OnUsernameSavedInterface
import com.rosebay.odds.ui.singleOdd.SingleOddFragment
import com.rosebay.odds.util.Constants
import com.rosebay.odds.util.FragmentFactoryInt
import com.rosebay.odds.util.SharedPreferencesClient
import javax.inject.Inject


class MainActivity : AppCompatActivity(), CreateSingleOddInterface, OnUsernameSavedInterface {

    @Inject
    lateinit var usernamePreferencesClient: SharedPreferencesClient
    @Inject
    lateinit var fragmentFactory: FragmentFactoryInt

    @BindView(R.id.home_menu)
    lateinit var mBottomMenu: BottomNavigationView
    @BindView(R.id.adView)
    lateinit var mAdView: AdView

    private var backPressed: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        OddsApplication.appComponent.inject(this)
        setManager(supportFragmentManager)
        MobileAds.initialize(this, BuildConfig.AdMobID)
        val request = AdRequest.Builder()
                .build()
        mAdView.loadAd(request)
        loadInitialView()
        mBottomMenu.setOnNavigationItemSelectedListener { navListener(it)}
    }

    private fun navListener(item: MenuItem) : Boolean {
        when (item.itemId) {
            R.id.home -> fragmentFactory.getMainOddsFragment()
            R.id.createOdds -> fragmentFactory.getCreateOddsFragment()
            R.id.favOdds -> fragmentFactory.getFavoriteOddsFragment()
            R.id.myOdds -> fragmentFactory.getMyOddsFragment()
            else -> return false
        }
        return true
    }

    fun loadInitialView() {
        if (!usernamePreferencesClient.getUsername(resources.getString(R.string.username)).isNullOrEmpty()) {
            fragmentFactory.getMainOddsFragment()
            mBottomMenu.visibility = View.VISIBLE
        } else {
            mBottomMenu.visibility = View.INVISIBLE
            fragmentFactory.getDisclaimerFragment()
        }
    }

    override fun onUsernameSaved() {
        mBottomMenu.visibility = View.VISIBLE
        fragmentFactory.getMainOddsFragment()
    }

    override fun getSingleOddsFragment(singleOdd: SingleOdd) {
        val fragment = SingleOddFragment.newInstance()
        val args = Bundle()
        args.putSerializable(Constants.SINGLE_ODD_KEY, singleOdd)
        args.putString(Constants.USERNAME, usernamePreferencesClient.getUsername(resources.getString(R.string.username)))
        fragment.arguments = args
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(fragment.tag)
        fragmentTransaction.commit()
    }

    override fun onBackPressed() {
        if (backPressed + 1000 > System.currentTimeMillis() && supportFragmentManager.backStackEntryCount == 1) {
            moveTaskToBack(true)
        } else if (supportFragmentManager.backStackEntryCount == 1) {
            Snackbar.make(mBottomMenu, R.string.back_btn_msg, Snackbar.LENGTH_SHORT).show()
        } else {
            mBottomMenu.menu.getItem(0).isChecked = true
            fragmentFactory.getMainOddsFragment()
        }
        backPressed = System.currentTimeMillis()
    }

    override fun onDestroy() {
        if (mAdView != null) {
            mAdView.destroy()
        }
        super.onDestroy()
        val refWatcher = OddsApplication.getRefWatcher(this)
        refWatcher.watch(this)
    }

    companion object {

        private lateinit var supportFragmentManager : FragmentManager
        fun setManager(fragmentManager: FragmentManager) {
            supportFragmentManager = fragmentManager
        }

        fun create(fragment: Fragment) {
            if (fragment is com.rosebay.odds.ui.mainOdds.MainOddsFragment) supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            val transaction = supportFragmentManager.beginTransaction().addToBackStack(null)
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }

}

