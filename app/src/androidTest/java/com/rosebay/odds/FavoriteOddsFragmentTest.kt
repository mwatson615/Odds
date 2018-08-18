package com.rosebay.odds

import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.internal.util.Checks.checkNotNull
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.view.View
import com.rosebay.odds.model.SingleOdd
import com.rosebay.odds.ui.favoriteOdds.FavoriteOddsFragment
import com.rosebay.odds.ui.favoriteOdds.FavoriteOddsPresenterImpl
import junit.framework.Assert.assertEquals
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.util.*

@RunWith(AndroidJUnit4::class)
class FavoriteOddsFragmentTest {

    @Mock
    lateinit var mockPresenter: FavoriteOddsPresenterImpl
    @InjectMocks
    lateinit var fragment: FavoriteOddsFragment
    private lateinit var testList: List<SingleOdd>

    @get:Rule
    var rule = ActivityTestRule(SingleFragmentTestActivity::class.java, false, false)

    @Before
    @Throws(Throwable::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        testList = createTestList()
        doNothing().`when`(mockPresenter).getAllFavorites()
        rule.launchActivity(null)
        rule.activity.setFragment(fragment)
        fragment.favoriteOddsPresenter = mockPresenter
    }

    @Test
    fun testInit() {
        assertEquals(mockPresenter, fragment.favoriteOddsPresenter)
        run {fragment.onResume()}
        verify<FavoriteOddsPresenterImpl>(mockPresenter).getAllFavorites()
        verify(mockPresenter).onViewAttached(fragment)
    }

    @Test
    fun testOnPause() {
        run { fragment.onPause() }
        verify(mockPresenter).onViewDetached()
    }

    @Ignore
    @Test
    @Throws(Throwable::class)
    fun testInitialLayout() {
        onView(withId(R.id.favoritesProgressBar)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        onView(withId(R.id.favoritesRecyclerView)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        onView(withId(R.id.noFavoriteOddsTextView)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @Ignore
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
                        .getString(R.string.percentage_text, testList[0].percentage))))))
    }

    @Test
    @Throws(Throwable::class)
    fun testNoFavorites() {
        rule.runOnUiThread { run { fragment.noFavorites() } }
        onView(withId(R.id.favoritesRecyclerView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.favoritesProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.noFavoriteOddsTextView)).check(matches(isDisplayed()))
    }

    @Ignore
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
