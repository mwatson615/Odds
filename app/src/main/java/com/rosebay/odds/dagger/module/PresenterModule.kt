package com.rosebay.odds.dagger.module

import com.rosebay.odds.dagger.component.OddsApplicationScope
import com.rosebay.odds.ui.mainOdds.MainOddsPresenterImpl
import dagger.Module
import dagger.Provides

@Module()
class PresenterModule {

    @Provides
    @OddsApplicationScope
    fun provideMainOddsPresenter() : MainOddsPresenterImpl {
        return MainOddsPresenterImpl()
    }

}