package com.rosebay.odds.dagger.module

import com.rosebay.odds.util.FragmentFactory
import com.rosebay.odds.util.FragmentFactoryInt
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