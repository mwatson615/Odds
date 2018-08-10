package com.rosebay.odds


import android.app.Application
import android.support.annotation.VisibleForTesting
import com.rosebay.odds.dagger.component.AppComponent
import com.rosebay.odds.dagger.component.DaggerAppComponent
import com.rosebay.odds.dagger.module.ContextModule
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import timber.log.Timber

class OddsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
                .builder()
                .contextModule(ContextModule(this))
                .build()

        plantTimber()
        if (LeakCanary.isInAnalyzerProcess(this)) return
        initLeakCanary()
    }

    private fun initLeakCanary() {
        if (BuildConfig.DEBUG) LeakCanary.install(this)
    }

    @VisibleForTesting
    fun setComponent(component: AppComponent) {
        appComponent = component
    }

    private fun plantTimber() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    companion object {

        lateinit var appComponent: AppComponent

        lateinit var refWatcher: RefWatcher

    }
}
