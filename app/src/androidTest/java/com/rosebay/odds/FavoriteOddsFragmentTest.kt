package com.rosebay.odds

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.view.View

import com.rosebay.odds.model.SingleOdd
import com.rosebay.odds.ui.favoriteOdds.FavoriteOddsFragment
import com.rosebay.odds.ui.favoriteOdds.FavoriteOddsPresenterImpl

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

import java.util.ArrayList

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.hasDescendant
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.internal.util.Checks.checkNotNull
import com.rosebay.odds.MyOddsFragmentTest.atPosition
import org.hamcrest.CoreMatchers.allOf
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class FavoriteOddsFragmentTest {

    @Mock
    lateinit var mockPresenter: FavoriteOddsPresenterImpl
    @InjectMocks
    lateinit var fragment: FavoriteOddsFragment
    private lateinit var testList: List<SingleOdd>

    @get:Rule
    var rule = ActivityTestRule(SingleFragmentActivity::class.java)

    @Before
    @Throws(Throwable::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        testList = createTestList()
        fragment.favoriteOddsPresenter = mockPresenter
        rule.activity.setFragment(fragment)
    }

    @Test
    @Throws(Throwable::class)
    fun testInit() {
        onView(withId(R.id.favoritesProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
        onView(withId(R.id.favoritesRecyclerView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
        onView(withId(R.id.noFavoriteOddsTextView)).check(matches(isDisplayed()))
        verify<FavoriteOddsPresenterImpl>(mockPresenter).getAllFavorites()
    }

    @Test
    @Throws(Throwable::class)
    fun testSetData() {
        rule.runOnUiThread { run { fragment.setData(testList) } }
        onView(withId(R.id.favoritesRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.noFavoriteOddsTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.favoritesProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.favoritesRecyclerView))
                .check(matches(atPosition(0, hasDescendant(withText(testList[0].description)))))
        onView(withId(R.id.favoritesRecyclerView))
                .check(matches(atPosition(0, hasDescendant(withText(testList[0].oddsAgainst.toString())))))
        onView(withId(R.id.favoritesRecyclerView))
                .check(matches(atPosition(0, hasDescendant(withText(testList[0].oddsFor.toString())))))
        onView(withId(R.id.favoritesRecyclerView))
                .check(matches(atPosition(0, hasDescendant(withText(InstrumentationRegistry.getTargetContext().resources
                        .getString(R.string.percentage_text, testList!![0].percentage))))))
    }

    @Test
    @Throws(Throwable::class)
    fun testNoFavorites() {
        rule.runOnUiThread { run { fragment.noFavorites() } }
        onView(withId(R.id.favoritesRecyclerView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.favoritesProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.noFavoriteOddsTextView)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Throwable::class)
    fun testOnLoading() {
        rule.runOnUiThread { run { fragment.onLoading() } }
        onView(withId(R.id.favoritesRecyclerView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
        onView(withId(R.id.noFavoriteOddsTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
        onView(withId(R.id.favoritesProgressBar)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Throwable::class)
    fun testOnError() {
        rule.runOnUiThread { run { fragment.onError() } }
        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.bats_data_error))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @After
    fun tearDown() {
        rule.finishActivity()
    }

    private fun createTestList(): List<SingleOdd> {
        val oddList = ArrayList<SingleOdd>()
        val singleOdd = SingleOdd()
        singleOdd.percentage = 50
        singleOdd.oddsAgainst = 30
        singleOdd.dueDate = "12/27/1978"
        singleOdd.oddsFor = 25
        singleOdd.dateSubmitted = "01/01/2018"
        singleOdd.imageUrl = "https://images.pexels.com/photos/937465/pexels-photo-937465.jpeg?auto=compress&cs=tinysrgb&h=350"
        singleOdd.description = "The Buddha"
        singleOdd.postId = "12345"
        oddList.add(singleOdd)
        return oddList
    }

    companion object {

        fun atPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> {
            checkNotNull(itemMatcher)
            return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
                override fun describeTo(description: Description) {
                    description.appendText("has item at position $position: ")
                    itemMatcher.describeTo(description)
                }

                override fun matchesSafely(view: RecyclerView): Boolean {
                    val viewHolder = view.findViewHolderForAdapterPosition(position)
                    // has no item on such position
                    return viewHolder != null && itemMatcher.matches(viewHolder.itemView)
                }
            }
        }
    }

}
