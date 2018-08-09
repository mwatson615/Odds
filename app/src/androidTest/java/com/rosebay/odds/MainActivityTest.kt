package com.rosebay.odds

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
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Mock
    lateinit var mockSharedPrefsClient: SharedPreferencesClient
    @Mock
    lateinit var mockFragmentFactory: FragmentFactoryInt

    lateinit var mainActivity: MainActivity
    private lateinit var context: Context
    private lateinit var username : String

    @get:Rule
    val rule = ActivityTestRule(MainActivity::class.java, false, false)

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        context = InstrumentationRegistry.getTargetContext()
        username = context.resources.getString(R.string.username)
        `when`(mockSharedPrefsClient.getUsername(anyString())).thenReturn(username)
        mainActivity = rule.launchActivity(null)
        Espresso.closeSoftKeyboard()
    }

    @Test
    fun testInit() {
        mainActivity.usernamePreferencesClient = mockSharedPrefsClient
        mainActivity.fragmentFactory = mockFragmentFactory
        run { mainActivity.loadInitialView() }
        onView(withId(R.id.adView)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Throwable::class)
    fun testLoadInitialHasUsernameShowMain() {
        run { mainActivity.loadInitialView() }
        onView(withId(R.id.adView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.home_menu)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        verify(mockFragmentFactory).getMainOddsFragment()
    }

    @Test
    @Throws(Throwable::class)
    fun testLoadInitialNullShowDisclaimer() {
//        mainActivity = rule.activity
        `when`(mockSharedPrefsClient.getUsername(anyString())).thenReturn(null)
        run { mainActivity.loadInitialView() }
        onView(withId(R.id.adView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        onView(withId(R.id.home_menu)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
    }

    @Test
    @Throws(Throwable::class)
    fun testShowMyOddsFragment() {
//        rule.runOnUiThread { run { mainActivity.loadInitialView() } }
        onView(withId(R.id.myOdds)).check(matches(isClickable())).perform(click())
        verify(mockFragmentFactory).getMyOddsFragment()
    }

    @Test
    @Throws(Throwable::class)
    fun testShowCreateOddsFragment() {
//        rule.runOnUiThread { run { mainActivity.loadInitialView() } }
        onView(withId(R.id.createOdds)).check(matches(isClickable())).perform(click())
        verify(mockFragmentFactory).getCreateOddsFragment()
    }

    @Test
    @Throws(Throwable::class)
    fun testShowFavoriteOddsFragment() {
//        rule.runOnUiThread { run { mainActivity.loadInitialView() } }
        onView(withId(R.id.favOdds)).check(matches(isClickable())).perform(click())
        verify(mockFragmentFactory).getFavoriteOddsFragment()
    }

    @Test
    @Throws(Throwable::class)
    fun testOnUserNameSaved() {
//        rule.runOnUiThread { run { mainActivity.onUsernameSaved() } }
        onView(withId(R.id.adView)).check(matches(isDisplayed()))
        onView(withId(R.id.home_menu)).check(matches(isDisplayed()))

    }

    @After
    fun tearDown() {
//        mainActivity = null
    }
}
