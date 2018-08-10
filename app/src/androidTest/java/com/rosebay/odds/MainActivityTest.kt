package com.rosebay.odds

import Resources.espressoDaggerMockRule
import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.rosebay.odds.ui.mainOdds.MainActivity
import com.rosebay.odds.util.FragmentFactoryInt
import com.rosebay.odds.util.SharedPreferencesClient
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val daggerRule = espressoDaggerMockRule()
    @get:Rule
    val rule = ActivityTestRule(MainActivity::class.java, false, false)
    @Mock
    private
    lateinit var mockSharedPrefsClient: SharedPreferencesClient
    @Mock
    private
    lateinit var mockFragmentFactory: FragmentFactoryInt

    private lateinit var context: Context
    private lateinit var username : String
    private lateinit var mainActivity : MainActivity

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getTargetContext()
        username = context.resources.getString(R.string.username)
        `when`(mockSharedPrefsClient.getUsername(anyString())).thenReturn(username)
        rule.launchActivity(null)
        mainActivity = rule.activity
        Espresso.closeSoftKeyboard()
    }

    @Test
    @Throws(Throwable::class)
    fun testLoadInitialHasUsernameShowMain() {
        onView(withId(R.id.adView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.home_menu)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        verify(mockFragmentFactory).getMainOddsFragment()
    }

    @Test
    @Throws(Throwable::class)
    fun testLoadInitialNullShowDisclaimer() {
        `when`(mockSharedPrefsClient.getUsername(anyString())).thenReturn(null)
        rule.runOnUiThread { run { mainActivity.loadInitialView() } }
        onView(withId(R.id.adView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.home_menu)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
    }

    @Test
    @Throws(Throwable::class)
    fun testShowMyOddsFragment() {
        onView(withId(R.id.myOdds)).check(matches(isClickable())).perform(click())
        verify(mockFragmentFactory).getMyOddsFragment()
    }

    @Test
    @Throws(Throwable::class)
    fun testShowCreateOddsFragment() {
        onView(withId(R.id.createOdds)).check(matches(isClickable())).perform(click())
        verify(mockFragmentFactory).getCreateOddsFragment()
    }

    @Test
    @Throws(Throwable::class)
    fun testShowFavoriteOddsFragment() {
        onView(withId(R.id.favOdds)).check(matches(isClickable())).perform(click())
        verify(mockFragmentFactory).getFavoriteOddsFragment()
    }

    @Test
    @Throws(Throwable::class)
    fun testOnUserNameSaved() {
        rule.runOnUiThread { run { mainActivity.onUsernameSaved() } }
        onView(withId(R.id.adView)).check(matches(isDisplayed()))
        onView(withId(R.id.home_menu)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        rule.finishActivity()
    }
}
