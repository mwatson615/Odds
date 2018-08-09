package com.rosebay.odds.dagger.module

import com.rosebay.odds.dagger.component.OddsApplicationScope
import com.rosebay.odds.ui.createOdds.CreateOddsPresenter
import com.rosebay.odds.ui.createOdds.CreateOddsPresenterImpl
import com.rosebay.odds.ui.disclaimer.DisclaimerPresenter
import com.rosebay.odds.ui.disclaimer.DisclaimerPresenterImpl
import com.rosebay.odds.ui.favoriteOdds.FavoriteOddsPresenter
import com.rosebay.odds.ui.favoriteOdds.FavoriteOddsPresenterImpl
import com.rosebay.odds.ui.mainOdds.MainOddsPresenter
import com.rosebay.odds.ui.mainOdds.MainOddsPresenterImpl
import com.rosebay.odds.ui.myOdds.MyOddsPresenter
import com.rosebay.odds.ui.myOdds.MyOddsPresenterImpl
import dagger.Module
import dagger.Provides

@Module()
class PresenterModule {

    @Provides
    @OddsApplicationScope
    fun provideMainOddsPresenter() : MainOddsPresenter {
        return MainOddsPresenterImpl()
    }

    @Provides
    @OddsApplicationScope
    fun provideDisclaimerPresenter() : DisclaimerPresenter {
        return DisclaimerPresenterImpl()
    }

    @Provides
    @OddsApplicationScope
    fun provideMyOddsPresenter() : MyOddsPresenter {
        return MyOddsPresenterImpl()
    }

    @Provides
    @OddsApplicationScope
    fun provideFavoriteOddsPresenter() : FavoriteOddsPresenter {
        return FavoriteOddsPresenterImpl()
    }

    @Provides
    @OddsApplicationScope
    fun provideCreateOddsPresenter() : CreateOddsPresenter {
        return CreateOddsPresenterImpl()
    }
}