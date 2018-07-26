package com.rosebay.odds.ui.mainOdds;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.rosebay.odds.BuildConfig;
import com.rosebay.odds.Constants;
import com.rosebay.odds.OddsApplication;
import com.rosebay.odds.R;
import com.rosebay.odds.model.SingleOdd;
import com.rosebay.odds.ui.CreateSingleOddInterface;
import com.rosebay.odds.ui.OnUsernameSavedInterface;
import com.rosebay.odds.ui.createOdds.CreateOddsFragment;
import com.rosebay.odds.ui.disclaimer.DisclaimerFragment;
import com.rosebay.odds.ui.favoriteOdds.FavoriteOddsFragment;
import com.rosebay.odds.ui.myOdds.MyOddsFragment;
import com.rosebay.odds.ui.singleOdd.SingleOddFragment;
import com.rosebay.odds.util.SharedPreferencesClient;
import com.squareup.leakcanary.RefWatcher;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnUsernameSavedInterface, CreateSingleOddInterface {

    @Inject
    SharedPreferencesClient usernamePreferencesClient;

    @BindView(R.id.home_menu)
    BottomNavigationView mBottomMenu;
    @BindView(R.id.adView)
    AdView mAdView;

    long backPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        OddsApplication.getAppComponent().inject(this);
        MobileAds.initialize(this, BuildConfig.AdMobID);
        AdRequest request = new AdRequest.Builder()
                .build();
        mAdView.loadAd(request);
        loadInitialView(usernamePreferencesClient.getUsername(getResources().getString(R.string.username)));
        mBottomMenu.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    getMainOddsFragment();
                    return true;

                case R.id.createOdds:
                    getCreateOddsFragment();
                    return true;

                case R.id.favOdds:
                    getFavoriteOddsFragment();
                    return true;

                case R.id.myOdds:
                    getMyOddsFragment();
                    return true;

                default:
                    return true;
            }
        });
    }

    public void loadInitialView(String username) {
        if (username != null) {
            getMainOddsFragment();
            mBottomMenu.setVisibility(View.VISIBLE);
        } else {
            mBottomMenu.setVisibility(View.INVISIBLE);
            showDisclaimerDialog();
        }
    }

    @Override
    public void onUsernameSaved() {
        mBottomMenu.setVisibility(View.VISIBLE);
        getMainOddsFragment();
    }


    public void showDisclaimerDialog() {
        DisclaimerFragment fragment = DisclaimerFragment.Companion.newInstance();
        createFragment(fragment);
    }

    public void createFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().addToBackStack(null);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public void getSingleOddsFragment(SingleOdd singleOdd) {
        SingleOddFragment fragment = SingleOddFragment.newInstance();
        Bundle args = new Bundle();
        args.putSerializable(Constants.SINGLE_ODD_KEY, singleOdd);
        fragment.setArguments(args);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(fragment.getTag());
        fragmentTransaction.commit();
    }

    public void getMainOddsFragment() {
        MainOddsFragment fragment = MainOddsFragment.newInstance();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        createFragment(fragment);
    }

    public void getMyOddsFragment() {
        MyOddsFragment fragment = MyOddsFragment.Companion.newInstance();
        createFragment(fragment);
    }

    public void getCreateOddsFragment() {
        CreateOddsFragment fragment = CreateOddsFragment.newInstance();
        createFragment(fragment);
    }

    public void getFavoriteOddsFragment() {
        FavoriteOddsFragment fragment = FavoriteOddsFragment.newInstance();
        createFragment(fragment);
    }

    @Override
    public void onBackPressed() {
        if ((backPressed + 1000 > System.currentTimeMillis()) && (getSupportFragmentManager().getBackStackEntryCount() == 1)) {
            moveTaskToBack(true);
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 1){
            Snackbar.make(mBottomMenu, R.string.back_btn_msg, Snackbar.LENGTH_SHORT).show();
        } else {
            mBottomMenu.getMenu().getItem(0).setChecked(true);
            getMainOddsFragment();
        }
        backPressed = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
            RefWatcher refWatcher = OddsApplication.getRefWatcher(getApplication());
            refWatcher.watch(this);
    }

}

