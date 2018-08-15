package com.rosebay.odds.dagger.component

import com.rosebay.odds.dagger.module.*
import com.rosebay.odds.ui.createOdds.CreateOddsFragment
import com.rosebay.odds.ui.createOdds.CreateOddsPresenterImpl
import com.rosebay.odds.ui.disclaimer.DisclaimerFragment
import com.rosebay.odds.ui.disclaimer.DisclaimerPresenterImpl
import com.rosebay.odds.ui.favoriteOdds.FavoriteOddsFragment
import com.rosebay.odds.ui.favoriteOdds.FavoriteOddsPresenterImpl
import com.rosebay.odds.ui.mainOdds.MainActivity
import com.rosebay.odds.ui.mainOdds.MainOddsFragment
import com.rosebay.odds.ui.mainOdds.MainOddsPresenterImpl
import com.rosebay.odds.ui.myOdds.MyOddsFragment
import com.rosebay.odds.ui.myOdds.MyOddsPresenterImpl
import com.rosebay.odds.ui.singleOdd.SingleOddFragment
import com.rosebay.odds.ui.singleOdd.SingleOddPresenterImpl
import dagger.Component

@OddsApplicationScope
@Component(modules = arrayOf(ClientModule::class, RoomModule::class, NetworkModule::class, UIModule::class, PresenterModule::class))
interface AppComponent {

    fun inject(createOddsPresenterImpl: CreateOddsPresenterImpl)

    fun inject(singleOddPresenterImpl: SingleOddPresenterImpl)

    fun inject(mainOddsPresenter: MainOddsPresenterImpl)

    fun inject(favoriteOddsPresenter: FavoriteOddsPresenterImpl)

    fun inject(myOddsPresenter: MyOddsPresenterImpl)

    fun inject(createOddsFragment: CreateOddsFragment)

    fun inject(mainActivity: MainActivity)

    fun inject(disclaimerPresenter: DisclaimerPresenterImpl)

    fun inject(disclaimerFragment: DisclaimerFragment)

    fun inject(singleOddFragment: SingleOddFragment)

    fun inject(favoriteOddsFragment: FavoriteOddsFragment)

    fun inject(myOddsFragment: MyOddsFragment)

    fun inject(mainOddsFragment: MainOddsFragment)

}
