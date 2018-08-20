package Resources

import android.support.test.InstrumentationRegistry
import com.rosebay.odds.OddsApplication
import com.rosebay.odds.dagger.component.AppComponent
import com.rosebay.odds.dagger.module.ClientModule
import com.rosebay.odds.dagger.module.ContextModule
import com.rosebay.odds.dagger.module.PresenterModule
import com.rosebay.odds.dagger.module.UIModule
import it.cosenonjaviste.daggermock.DaggerMockRule


fun espressoDaggerMockRule() = DaggerMockRule<AppComponent>(AppComponent::class.java,
        ContextModule(app), ClientModule(), UIModule(), PresenterModule())
        .set {component ->
            app.setComponent(component)
        }!!

val app: OddsApplication get() = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as OddsApplication