package com.rosebay.odds


import android.app.Application
import android.content.Context
import com.rosebay.odds.dagger.component.AppComponent
import com.rosebay.odds.dagger.component.DaggerAppComponent
import com.rosebay.odds.dagger.module.ContextModule
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import timber.log.Timber

class OddsApplication : Application() {

    lateinit var refWatcher: RefWatcher

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
                .builder()
                .contextModule(ContextModule((this)))
                .build()
        plantTimber()
        if (LeakCanary.isInAnalyzerProcess(this)) return
        initLeakCanary()
    }

    private fun initLeakCanary() {
        if (BuildConfig.DEBUG) LeakCanary.install(this)
    }

    private fun plantTimber() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    companion object {

        lateinit var appComponent: AppComponent

        fun getRefWatcher(context: Context): RefWatcher {
            val application = context.applicationContext as OddsApplication
            return application.refWatcher
        }
    }
}
