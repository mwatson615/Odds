package com.rosebay.odds.dagger.module;

import android.content.Context;

import com.rosebay.odds.dagger.component.OddsApplicationScope;

import javax.annotation.Nonnull;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    private final Context context;

    public ContextModule(@Nonnull Context context) {
        this.context = context;
    }

    @Provides
    @OddsApplicationScope
    public Context providesContext(){
        return context;
    }

}
