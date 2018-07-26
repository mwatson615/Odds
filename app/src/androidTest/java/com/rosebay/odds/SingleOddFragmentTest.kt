package com.rosebay.odds

import android.content.Context
import android.os.Bundle
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4

import com.rosebay.odds.model.SingleOdd
import com.rosebay.odds.ui.mainOdds.MainActivity
import com.rosebay.odds.ui.singleOdd.SingleOddFragment
import com.rosebay.odds.ui.singleOdd.SingleOddPresenterImpl

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isEnabled
import android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.view.View
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class SingleOddFragmentTest {

    @Mock
    lateinit var mockPresenter: SingleOddPresenterImpl
    @InjectMocks
    lateinit var fragment: SingleOddFragment
    private var singleOdd: SingleOdd? = null
    private var targetContext: Context? = null

    @get:Rule
    var rule = ActivityTestRule(SingleFragmentActivity::class.java)

    @Before
    @Throws(Throwable::class)
    fun setup() {
        MockitoAnnotations.initMocks(this)
        targetContext = InstrumentationRegistry.getTargetContext()
        fragment.singleOddPresenter = mockPresenter
        fragment.arguments = createTestSingleOdd()
        singleOdd = fragment.arguments!!.getSerializable(Constants.SINGLE_ODD_KEY) as SingleOdd
        rule.activity.setFragment(fragment)
    }

    @Test
    @Throws(Throwable::class)
    fun testInitialPresenterCalls() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        verify<SingleOddPresenterImpl>(mockPresenter).checkForFavorite(anyString())
        verify<SingleOddPresenterImpl>(mockPresenter).checkIfVoted(anyString())
        verify<SingleOddPresenterImpl>(mockPresenter).loadOddsData(any(SingleOdd::class.java))
    }

    @Test
    @Throws(Throwable::class)
    fun testDisableFavoritesButton() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        rule.runOnUiThread { run { fragment.disableFavoritesButton() } }
        onView(withId(R.id.addToFavoritesButton)).check(matches(not<View>(isEnabled())))
    }

    @Test
    @Throws(Throwable::class)
    fun testEnableFavoritesButton() {
        rule.runOnUiThread { run { fragment.enableFavoritesButton() } }
        onView(withId(R.id.addToFavoritesButton)).check(matches(isEnabled()))
    }

    @Test
    @Throws(Throwable::class)
    fun testSetPercentage() {
        rule.runOnUiThread { run { fragment.setPercentage(singleOdd!!.percentage) } }
        onView(withId(R.id.percentageSingleOdd)).check(matches(withText(
                targetContext!!.resources.getString(R.string.percentage_text, singleOdd!!.percentage))))
    }

    @Test
    @Throws(Throwable::class)
    fun testSetOddsFor() {
        rule.runOnUiThread { run { fragment.setOddsFor(singleOdd!!.oddsFor) } }
        onView(withId(R.id.oddsForSingleOdd)).check(matches(withText(singleOdd!!.oddsFor.toString())))
    }

    @Test
    @Throws(Throwable::class)
    fun testSetOddsAgainst() {
        rule.runOnUiThread { run { fragment.setOddsAgainst(singleOdd!!.oddsAgainst) } }
        onView(withId(R.id.oddsAgainstSingleOdd)).check(matches(withText(singleOdd!!.oddsAgainst.toString())))
    }

    @Test
    @Throws(Throwable::class)
    fun testSetCreationInfo() {
        rule.runOnUiThread {
            fragment.setCreationInfo(singleOdd!!.username,
                    singleOdd!!.dateSubmitted)
        }
        onView(withId(R.id.singleOddCreationTextView)).check(matches(withText(
                targetContext!!.resources.getString(R.string.created_by_date, singleOdd!!.username, singleOdd!!.dateSubmitted))))
    }

    @Test
    @Throws(Throwable::class)
    fun testSetDescription() {
        rule.runOnUiThread { run { fragment.setDescription(singleOdd!!.description) } }
        onView(withId(R.id.descriptionSingleOdd)).check(matches(withText(singleOdd!!.description)))
    }

    @Test
    @Throws(Throwable::class)
    fun testOnAddedToFavorites() {
        rule.runOnUiThread { run { fragment.onAddedToFavorites() } }
        onView(allOf<View>(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.added_to_favorites_msg))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    @Throws(Throwable::class)
    fun testOnVoteYes() {
        onView(withId(R.id.voteYesButton)).check(matches(isEnabled()))
        onView(withId(R.id.voteNoButton)).check(matches(isEnabled()))
        onView(withId(R.id.voteYesButton)).perform(click())
        verify<SingleOddPresenterImpl>(mockPresenter).voteYes(anyString())
    }

    @Test
    @Throws(Throwable::class)
    fun testOnNoVote() {
        onView(withId(R.id.voteYesButton)).check(matches(isEnabled()))
        onView(withId(R.id.voteNoButton)).check(matches(isEnabled()))
        onView(withId(R.id.voteNoButton)).perform(click())
        verify<SingleOddPresenterImpl>(mockPresenter).voteNo(anyString())
    }

    @Test
    @Throws(Throwable::class)
    fun testAddToFavorites() {
        rule.runOnUiThread { run { fragment.enableFavoritesButton() } }
        onView(withId(R.id.addToFavoritesButton)).perform(click())
        verify<SingleOddPresenterImpl>(mockPresenter).addToFavorites(any(), anyString())
    }

    @Test
    @Throws(Throwable::class)
    fun testOnVoteSuccess() {
        rule.runOnUiThread { run { fragment.onVoteSuccess() } }
        onView(allOf<View>(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.vote_has_been_saved_msg))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    @Throws(Throwable::class)
    fun testDisableVoteButtons() {
        onView(withId(R.id.voteYesButton)).check(matches(isEnabled()))
        onView(withId(R.id.voteNoButton)).check(matches(isEnabled()))
        rule.runOnUiThread { run { fragment.disableVoteButtons() } }
        onView(withId(R.id.voteYesButton)).check(matches(not<View>(isEnabled())))
        onView(withId(R.id.voteNoButton)).check(matches(not<View>(isEnabled())))
    }

    @Test
    @Throws(Throwable::class)
    fun testEnableVoteButtons() {
        rule.runOnUiThread { run { fragment.enableVoteButtons() } }
        onView(withId(R.id.voteYesButton)).check(matches(isEnabled()))
        onView(withId(R.id.voteNoButton)).check(matches(isEnabled()))
    }

    @Test
    @Throws(Throwable::class)
    fun testOnError() {
        rule.runOnUiThread { run { fragment.onError() } }
        onView(allOf<View>(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.bats_data_error))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @After
    fun tearDown() {
        rule.finishActivity()
    }

    private fun createTestSingleOdd(): Bundle {
        val singleOdd = SingleOdd()
        val args = Bundle()
        singleOdd.percentage = 50
        singleOdd.oddsAgainst = 25
        singleOdd.dueDate = "12/27/1978"
        singleOdd.oddsFor = 25
        singleOdd.dateSubmitted = "01/01/2018"
        singleOdd.imageUrl = "https://images.pexels.com/photos/937465/pexels-photo-937465.jpeg?auto=compress&cs=tinysrgb&h=350"
        singleOdd.description = "The Buddha"
        singleOdd.postId = "12345"
        args.putSerializable(Constants.SINGLE_ODD_KEY, singleOdd)
        return args
    }

}
