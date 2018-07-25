package com.rosebay.odds.dagger.component;

import com.rosebay.odds.dagger.module.ClientModule;
import com.rosebay.odds.dagger.module.NetworkModule;
import com.rosebay.odds.dagger.module.PresenterModule;
import com.rosebay.odds.dagger.module.RoomModule;
import com.rosebay.odds.ui.createOdds.CreateOddsFragment;
import com.rosebay.odds.ui.createOdds.CreateOddsPresenterImpl;
import com.rosebay.odds.ui.disclaimer.DisclaimerFragment;
import com.rosebay.odds.ui.disclaimer.DisclaimerPresenterImpl;
import com.rosebay.odds.ui.favoriteOdds.FavoriteOddsPresenterImpl;
import com.rosebay.odds.ui.mainOdds.MainActivity;
import com.rosebay.odds.ui.mainOdds.MainOddsFragment;
import com.rosebay.odds.ui.mainOdds.MainOddsPresenterImpl;
import com.rosebay.odds.ui.myOdds.MyOddsFragment;
import com.rosebay.odds.ui.myOdds.MyOddsPresenterImpl;
import com.rosebay.odds.ui.singleOdd.SingleOddFragment;
import com.rosebay.odds.ui.singleOdd.SingleOddPresenterImpl;

import dagger.Component;

@OddsApplicationScope
@Component(modules = {ClientModule.class, RoomModule.class, NetworkModule.class, PresenterModule.class})
public interface AppComponent {

    void inject(CreateOddsPresenterImpl createOddsPresenterImpl);

    void inject(SingleOddPresenterImpl singleOddPresenterImpl);

    void inject(MainOddsPresenterImpl mainOddsPresenter);

    void inject(FavoriteOddsPresenterImpl favoriteOddsPresenter);

    void inject(MyOddsPresenterImpl myOddsPresenter);

    void inject(CreateOddsFragment createOddsFragment);

    void inject(MainActivity mainActivity);

    void inject(DisclaimerPresenterImpl disclaimerPresenter);

    void inject(DisclaimerFragment disclaimerFragment);

    void inject(SingleOddFragment singleOddFragment);

    void inject(MyOddsFragment myOddsFragment);

    void inject(MainOddsFragment mainOddsFragment);

}
