package com.rosebay.odds.dagger.module

import android.content.Context
import android.content.SharedPreferences

import com.rosebay.odds.R
import com.rosebay.odds.dagger.component.OddsApplicationScope

import dagger.Module
import dagger.Provides

@Module(includes = arrayOf(ContextModule::class))
class SharedPreferencesModule {

    @Provides
    @OddsApplicationScope
    fun providesSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(context.getString(R.string.username), Context.MODE_PRIVATE)
    }

}
