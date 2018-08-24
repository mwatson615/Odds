package com.rosebay.odds


import android.app.Application
import com.rosebay.odds.dagger.component.AppComponent
import com.rosebay.odds.dagger.component.DaggerAppComponent
import com.rosebay.odds.dagger.module.ContextModule
import timber.log.Timber

class OddsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
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

    }
}
