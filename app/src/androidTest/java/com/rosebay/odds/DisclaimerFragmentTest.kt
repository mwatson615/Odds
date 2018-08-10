package com.rosebay.odds

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.widget.AutoCompleteTextView
import com.rosebay.odds.ui.OnUsernameSavedInterface
import com.rosebay.odds.ui.disclaimer.DisclaimerFragment
import com.rosebay.odds.ui.disclaimer.DisclaimerPresenterImpl
import junit.framework.Assert.assertTrue
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class DisclaimerFragmentTest {

    @Mock
    private
    lateinit var mockPresenter: DisclaimerPresenterImpl
    @Mock
    private
    lateinit var mockCallback : OnUsernameSavedInterface

    lateinit var fragment: DisclaimerFragment
    lateinit var context: Context

    @get:Rule
    var testRule = ActivityTestRule(SingleFragmentTestActivity::class.java, false, false)

    @Before
    @Throws(Throwable::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        context = InstrumentationRegistry.getTargetContext()
        fragment = DisclaimerFragment()
        fragment.mCallback = mockCallback
        fragment.disclaimerPresenter = mockPresenter
        testRule.launchActivity(null)
        testRule.activity.setFragment(fragment)
        mockPresenter.onViewAttached(fragment)
    }

    @Test
    fun testInit() {
        verify<DisclaimerPresenterImpl>(mockPresenter).onViewAttached(fragment)
    }

    @Test
    @Throws(Throwable::class)
    fun testOnStop() {
        testRule.runOnUiThread { run { fragment.onPause() } }
        verify<DisclaimerPresenterImpl>(mockPresenter).onViewDetached()
        assertTrue(!mockPresenter.isViewAttached)
    }

    @Test
    @Throws(Throwable::class)
    fun testOnUsernameAvailable() {
        testRule.runOnUiThread { run { fragment.onUsernameAvailable(context.resources.getString(R.string.username)) } }
        onView(withId(R.id.usernameSearchProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.saveUsernameButton)).check(matches(isDisplayed())).check(matches(isEnabled())).perform(click())
        verify<DisclaimerPresenterImpl>(mockPresenter).saveUsername(anyString())
    }

    @Test
    @Throws(Throwable::class)
    fun testOnUsernameTaken() {
        testRule.runOnUiThread { fragment!!.onUsernameTaken() }
        onView(allOf<View>(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.username_taken_msg))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    @Throws(Throwable::class)
    fun testOnError() {
        testRule.runOnUiThread { fragment!!.onError() }
        onView(allOf<View>(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.bats_data_error))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    @Throws(Throwable::class)
    fun testShowSearchLayout() {
        testRule.runOnUiThread { fragment!!.showSearchLayout() }
        onView(withId(R.id.usernameSearchProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.disclaimerSearchView)).check(matches(isDisplayed()))
        onView(isAssignableFrom(AutoCompleteTextView::class.java)).perform(typeText("S"))
        onView(withId(R.id.saveUsernameButton)).check(matches(not<View>(isEnabled())))
    }

    @Test
    @Throws(Throwable::class)
    fun testShowProgressLayout() {
        testRule.runOnUiThread { run { fragment.showProgressLayout() } }
        onView(withId(R.id.usernameSearchProgressBar)).check(matches(isDisplayed()))
        onView(withId(R.id.disclaimerSearchView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.saveUsernameButton)).check(matches(not<View>(isEnabled())))
    }

    @Test
    @Throws(Throwable::class)
    fun testOnUserSaved() {
        testRule.runOnUiThread { run { fragment.onUserSaved(context.resources.getString(R.string.username)) } }
        onView(allOf<View>(withId(android.support.design.R.id.snackbar_text),
                withText(InstrumentationRegistry.getTargetContext().resources.getString(R.string.username_saved_msg,
                        context.resources.getString(R.string.username)))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        verify(mockCallback).onUsernameSaved()
    }

    @Test
    fun testSearchForUsername() {
        onView(withId(R.id.disclaimerSearchView)).check(matches(isDisplayed()))
        onView(isAssignableFrom(AutoCompleteTextView::class.java)).perform(typeText("Search"))
        onView(withId(R.id.search_mag_icon)).perform(click())
        verify<DisclaimerPresenterImpl>(mockPresenter).checkForUsername("Search")
    }

    @After
    fun tearDown() {
        mockPresenter.onViewDetached()
        fragment.onDestroy()
        testRule.finishActivity()
    }

}
