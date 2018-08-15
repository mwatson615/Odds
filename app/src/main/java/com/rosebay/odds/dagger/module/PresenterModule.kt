package com.rosebay.odds.dagger.module

import com.rosebay.odds.dagger.component.OddsApplicationScope
import com.rosebay.odds.ui.createOdds.CreateOddsPresenterImpl
import com.rosebay.odds.ui.disclaimer.DisclaimerPresenterImpl
import com.rosebay.odds.ui.favoriteOdds.FavoriteOddsPresenterImpl
import com.rosebay.odds.ui.mainOdds.MainOddsPresenterImpl
import com.rosebay.odds.ui.myOdds.MyOddsPresenterImpl
import dagger.Module
import dagger.Provides

@Module()
class PresenterModule {

    @Provides
    @OddsApplicationScope
    fun provideMainOddsPresenter() : MainOddsPresenterImpl {
        return MainOddsPresenterImpl()
    }

    @Provides
    @OddsApplicationScope
    fun provideDisclaimerPresenter() : DisclaimerPresenterImpl {
        return DisclaimerPresenterImpl()
    }

    @Provides
    @OddsApplicationScope
    fun provideMyOddsPresenter() : MyOddsPresenterImpl {
        return MyOddsPresenterImpl()
    }

    @Provides
    @OddsApplicationScope
    fun provideFavoriteOddsPresenter() : FavoriteOddsPresenterImpl {
        return FavoriteOddsPresenterImpl()
    }

    @Provides
    @OddsApplicationScope
    fun provideCreateOddsPresenter() : CreateOddsPresenterImpl {
        return CreateOddsPresenterImpl()
    }
}