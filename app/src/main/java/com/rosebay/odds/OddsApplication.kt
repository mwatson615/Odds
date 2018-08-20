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

    private lateinit var refWatcher: RefWatcher

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) return
        refWatcher = LeakCanary.install(this)
        appComponent = DaggerAppComponent
                .builder()
                .contextModule(ContextModule(this))
                .build()

        plantTimber()
    }

    fun setComponent(component: AppComponent) {
        appComponent = component
    }

    fun getComponent() : AppComponent {
        return appComponent
    }

    private fun plantTimber() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    companion object {

        lateinit var appComponent: AppComponent

        fun getRefWatcher(context: Context): RefWatcher {
            val application: OddsApplication = context.applicationContext as OddsApplication
            return application.refWatcher
        }

    }
}
