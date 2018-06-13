package com.rosebay.odds.ui.mainOdds;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.rosebay.odds.BuildConfig;
import com.rosebay.odds.OddsApplication;
import com.rosebay.odds.R;
import com.rosebay.odds.ui.createOdds.CreateOddsFragment;
import com.rosebay.odds.ui.disclaimer.DisclaimerFragment;
import com.rosebay.odds.ui.favoriteOdds.FavoriteOddsFragment;
import com.rosebay.odds.ui.myOdds.MyOddsFragment;
import com.rosebay.odds.util.SharedPreferencesClient;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements DisclaimerFragment.OnUsernameSavedListener {

    @Inject
    SharedPreferencesClient usernamePreferencesClient;

    @BindView(R.id.home_menu)
    BottomNavigationView mBottomMenu;
    @BindView(R.id.adView)
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        OddsApplication.getAppComponent().inject(this);
        MobileAds.initialize(this, BuildConfig.AdMobID);
        //TODO : CHANGE AD DETAILS
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
        DisclaimerFragment fragment = DisclaimerFragment.newInstance();
        createFragment(fragment);
    }

    public void createFragment(Fragment fragment) {
        getSupportFragmentManager().popBackStack();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    public void getMainOddsFragment() {
        MainOddsFragment fragment = MainOddsFragment.newInstance();
        createFragment(fragment);
    }

    public void getMyOddsFragment() {
        MyOddsFragment fragment = MyOddsFragment.newInstance();
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
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

}

