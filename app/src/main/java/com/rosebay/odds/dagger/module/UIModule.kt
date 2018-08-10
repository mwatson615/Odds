package com.rosebay.odds.dagger.module

import com.rosebay.odds.dagger.component.OddsApplicationScope
import com.rosebay.odds.util.FragmentFactory
import com.rosebay.odds.util.FragmentFactoryInt
import dagger.Module
import dagger.Provides

@Module()
open class UIModule {

    @Provides
    @OddsApplicationScope
    open fun providesFragmentFactory() : FragmentFactoryInt {
        return FragmentFactory()
    }

}