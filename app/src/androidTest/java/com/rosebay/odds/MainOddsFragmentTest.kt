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
import com.rosebay.odds.model.SingleOdd
import com.rosebay.odds.ui.NavigationInterface
import com.rosebay.odds.ui.mainOdds.MainOddsFragment
import com.rosebay.odds.ui.mainOdds.MainOddsPresenterImpl
import com.rosebay.odds.ui.singleOdd.SingleOddFragment
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.util.*

@RunWith(AndroidJUnit4::class)
class MainOddsFragmentTest {

    @Mock
    lateinit var mockPresenter: MainOddsPresenterImpl

    lateinit var fragment: MainOddsFragment
    private lateinit var singleOddList: List<SingleOdd>
    lateinit var targetContext: Context

    @get:Rule
    var rule = ActivityTestRule(SingleFragmentActivity::class.java, false, true)

    @Before
    @Throws(Throwable::class)
    fun setup() {
        MockitoAnnotations.initMocks(this)
        targetContext = InstrumentationRegistry.getTargetContext()
        singleOddList = createTestOdd()
        fragment = MainOddsFragment()
        fragment.mainOddsPresenter = mockPresenter
        rule.activity.setFragment(fragment)
        mockPresenter.onViewAttached(fragment)
    }

    @Test
    @Throws(Throwable::class)
    fun testInitialLayout() {
        rule.runOnUiThread { run { fragment.setData(singleOddList) } }
        onView(withId(R.id.home_recycler_view)).check(matches(isDisplayed()))
        onView(withId(R.id.searchButton)).check(matches(isDisplayed()))
        onView(withId(R.id.searchView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
        onView(withId(R.id.closeSearchButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
        onView(withId(R.id.beginSearchButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
        onView(withId(R.id.backToMainButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
        onView(withId(R.id.closeSearchButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
        onView(withId(R.id.appName)).check(matches(isDisplayed()))
    }

    @Test
    fun testPrepareSearch() {
        onView(withId(R.id.searchButton)).perform(click())
        onView(withId(R.id.searchView)).check(matches(isDisplayed()))
        onView(withId(R.id.appName)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
        onView(withId(R.id.closeSearchButton)).check(matches(isDisplayed()))
        onView(withId(R.id.beginSearchButton)).check(matches(isDisplayed()))
    }

    @Test
    fun testCloseSearch() {
        onView(withId(R.id.searchButton)).perform(click())
        onView(withId(R.id.closeSearchButton)).perform(click())
        onView(withId(R.id.searchView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
        onView(withId(R.id.appName)).check(matches(isDisplayed()))
        onView(withId(R.id.closeSearchButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
        onView(withId(R.id.beginSearchButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
    }

    @Test
    fun testConductSearch() {
        onView(withId(R.id.searchButton)).perform(click())
        onView(withId(R.id.searchView)).check(matches(isDisplayed()))
        onView(withId(R.id.searchView)).check(matches(hasFocus()))
        onView(withId(R.id.searchView)).perform(click())
        onView(isAssignableFrom(AutoCompleteTextView::class.java)).perform(typeText("Search"))
        onView(withId(R.id.beginSearchButton)).check(matches(isClickable())).perform(click())
        onView(withId(R.id.searchView)).check(matches(not<View>(hasFocus())))
        verify<MainOddsPresenterImpl>(mockPresenter).fetchSearchResults(anyString())
    }

    @Test
    @Throws(Throwable::class)
    fun testReloadMainOdds() {
        rule.runOnUiThread { run { fragment.showBackButtonOnSearchResults() } }
        onView(withId(R.id.backToMainButton)).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.searchView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
        verify<MainOddsPresenterImpl>(mockPresenter, times(2)).fetchOdds()
    }

    @Test
    @Throws(Throwable::class)
    fun testOnItemClicked() {
        rule.runOnUiThread { run { fragment.onItemClicked(singleOddList[0]) } }
        verify<MainOddsPresenterImpl>(mockPresenter).fetchSingleOdd(singleOddList[0].postId)
    }

    @Test
    @Throws(Throwable::class)
    fun testOnError() {
        rule.runOnUiThread { run { fragment.onError() } }
        onView(allOf<View>(withId(android.support.design.R.id.snackbar_text),
                withText(targetContext.resources.getString(R.string.bats_data_error)))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    @Throws(Throwable::class)
    fun testOnNoSearchResults() {
        rule.runOnUiThread { run { fragment.onNoSearchResults() } }
        onView(allOf<View>(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.no_search_results_msg))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    @Throws(Throwable::class)
    fun testShowBackButtonOnSearchResults() {
        rule.runOnUiThread { run { fragment.showBackButtonOnSearchResults() }}
        onView(withId(R.id.closeSearchButton)).check(matches(not<View>(isDisplayed())))
        onView(withId(R.id.backToMainButton)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        fragment.onDestroy()
        rule.finishActivity()
    }

    fun createTestOdd(): List<SingleOdd> {
        val oddList = ArrayList<SingleOdd>()
        val singleOdd = SingleOdd()
        singleOdd.percentage = 50
        singleOdd.oddsAgainst = 25
        singleOdd.dueDate = "12/27/1978"
        singleOdd.oddsFor = 25
        singleOdd.dateSubmitted = "01/01/2018"
        singleOdd.imageUrl = "https://images.pexels.com/photos/937465/pexels-photo-937465.jpeg?auto=compress&cs=tinysrgb&h=350"
        singleOdd.description = "The Buddha"
        singleOdd.postId = "12345"
        oddList.add(singleOdd)
        return oddList
    }
}
