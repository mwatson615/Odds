package com.rosebay.odds.dagger.module

import android.content.Context

import com.rosebay.odds.dagger.component.OddsApplicationScope

import dagger.Module
import dagger.Provides

@Module
class ContextModule(private val context: Context) {

    @Provides
    @OddsApplicationScope
    fun providesContext(): Context = context
}