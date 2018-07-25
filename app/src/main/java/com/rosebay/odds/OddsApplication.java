package com.rosebay.odds;


import android.app.Application;
import android.content.Context;

import com.rosebay.odds.dagger.component.AppComponent;
import com.rosebay.odds.dagger.component.DaggerAppComponent;
import com.rosebay.odds.dagger.module.ContextModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import timber.log.Timber;

public class OddsApplication extends Application {

    public static RefWatcher getRefWatcher(Context context) {
        OddsApplication application = (OddsApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private static AppComponent appComponent;
    private RefWatcher refWatcher;
    public static OddsApplication get(Context context) {
        return (OddsApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        refWatcher = LeakCanary.install(this);
        appComponent = DaggerAppComponent.builder()
                .contextModule(new ContextModule(getApplicationContext()))
                .build();
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }
}
