package com.rosebay.odds;


import android.app.Application;
import android.content.Context;

import com.rosebay.odds.dagger.component.AppComponent;
import com.rosebay.odds.dagger.component.DaggerAppComponent;
import com.rosebay.odds.dagger.module.ContextModule;

import timber.log.Timber;

public class OddsApplication extends Application {

    private static AppComponent appComponent;
    public static OddsApplication get(Context context) {
        return (OddsApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        appComponent = DaggerAppComponent.builder()
                .contextModule(new ContextModule(getApplicationContext()))
                .build();
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }
}
