package com.rosebay.odds.dagger.module

import com.rosebay.odds.FragmentFactory
import com.rosebay.odds.FragmentFactoryInt
import com.rosebay.odds.dagger.component.OddsApplicationScope
import dagger.Module
import dagger.Provides

@Module()
class UIModule {

    @Provides
    @OddsApplicationScope
    fun providesFragmentFactory() : FragmentFactoryInt {
        return FragmentFactory()
    }

}