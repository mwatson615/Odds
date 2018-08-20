package com.rosebay.odds

import Resources.espressoDaggerMockRule
import android.content.Context
import android.support.test.InstrumentationRegistry
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
import com.rosebay.odds.ui.myOdds.MyOddsFragment
import com.rosebay.odds.ui.myOdds.MyOddsPresenterImpl
import com.rosebay.odds.util.SharedPreferencesClient
import junit.framework.Assert.assertEquals
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.util.*

@RunWith(AndroidJUnit4::class)
class MyOddsFragmentTest {

    @Mock
    lateinit var mockPresenter: MyOddsPresenterImpl
    @Mock
    private
    lateinit var mockPrefClient : SharedPreferencesClient
    @InjectMocks
    lateinit var fragment: MyOddsFragment

    private lateinit var testListMyOdds: List<SingleOdd>
    private lateinit var context : Context
    private lateinit var appContext: Context
    private lateinit var username : String

    @get:Rule
    val daggerRule = espressoDaggerMockRule()

    @get:Rule
    val rule = ActivityTestRule(SingleFragmentTestActivity::class.java, false, false)

    @Before
    @Throws(Throwable::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        context = InstrumentationRegistry.getTargetContext()
        appContext = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        testListMyOdds = createTestList()
        rule.launchActivity(null)
        rule.activity.setFragment(fragment)
        fragment.onAttach(rule.activity as Context)
        fragment.myOddsPresenter = mockPresenter
        username = context.resources.getString(R.string.username)
        `when`(mockPrefClient.getUsername(any())).thenReturn(username)
    }

    @Test
    @Throws(Throwable::class)
    fun testInitialFetch() {
        assertEquals(mockPresenter, fragment.myOddsPresenter)
        onView(withId(R.id.myOddsRecyclerView)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        onView(withId(R.id.noMyOddsTextView)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        onView(withId(R.id.myOddsProgressBar)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        onView(withId(R.id.fragmentMyOdds)).check(matches(isDisplayed()))
        verify<MyOddsPresenterImpl>(mockPresenter).onViewAttached(fragment)
        verify<MyOddsPresenterImpl>(mockPresenter).fetchMyOdds(anyString())
    }

    @Test
    fun testOnLoading() {
        rule.runOnUiThread { run { fragment.onLoading() } }
        onView(withId(R.id.myOddsProgressBar)).check(matches(isDisplayed()))
        onView(withId(R.id.myOddsRecyclerView)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        onView(withId(R.id.noMyOddsTextView)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @Test
    @Throws(Throwable::class)
    fun testOnPause() {
     rule.runOnUiThread { run { fragment.onPause() } }
        verify<MyOddsPresenterImpl>(mockPresenter).onViewDetached()
    }

    @Test
    @Throws(Throwable::class)
    fun testLoadingWithOdds() {
        rule.runOnUiThread { run { fragment.onResponse(testListMyOdds) } }
        onView(withId(R.id.noMyOddsTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.myOddsRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.myOddsRecyclerView))
                .check(matches(atPosition(0, hasDescendant(withText(testListMyOdds[0].description)))))
        onView(withId(R.id.myOddsRecyclerView))
                .check(matches(atPosition(0, hasDescendant(withText(testListMyOdds[0].oddsAgainst.toString())))))
        onView(withId(R.id.myOddsRecyclerView))
                .check(matches(atPosition(0, hasDescendant(withText(testListMyOdds[0].oddsFor.toString())))))
        onView(withId(R.id.myOddsRecyclerView))
                .check(matches(atPosition(0, hasDescendant(withText(InstrumentationRegistry.getTargetContext().resources
                        .getString(R.string.percentage_text, testListMyOdds[0].percentage))))))
    }

    @Test
    @Throws(Throwable::class)
    fun testLoadingWithoutOdds() {
        rule.runOnUiThread { run { fragment.onNoResponse() } }
        onView(withId(R.id.noMyOddsTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.myOddsRecyclerView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.myOddsProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
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
        fragment.onDetach()
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
                    return viewHolder != null && itemMatcher.matches(viewHolder.itemView)
                }
            }
        }
    }

}
