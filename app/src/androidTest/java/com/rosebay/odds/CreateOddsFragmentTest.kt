package com.rosebay.odds

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.PickerActions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.widget.DatePicker
import com.rosebay.odds.model.SingleOdd
import com.rosebay.odds.ui.createOdds.CreateOddsFragment
import com.rosebay.odds.ui.createOdds.CreateOddsPresenterImpl
import com.rosebay.odds.ui.createOdds.CreateOddsView
import com.rosebay.odds.ui.createOdds.ImagePagerAdapter
import com.rosebay.odds.util.SharedPreferencesClient
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.util.*

@RunWith(AndroidJUnit4::class)
class CreateOddsFragmentTest {

    @Mock
    lateinit var mockPresenter: CreateOddsPresenterImpl
    @Mock
    var mockPagerAdapter: ImagePagerAdapter? = null
    @Mock
    var mockClient: SharedPreferencesClient? = null
    @InjectMocks
    lateinit var fragment: CreateOddsFragment
    private var testImageList: List<String>? = null

    @get:Rule
    var testRule = ActivityTestRule(SingleFragmentActivity::class.java)

    @Before
    @Throws(Throwable::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        testImageList = createImageStringList()
        fragment.createOddsPresenter = mockPresenter
        testRule.activity.setFragment(fragment)
        `when`(mockPagerAdapter!!.getCurrentUrl(anyInt())).thenReturn("url")
        `when`(mockClient!!.getUsername(anyString())).thenReturn("username")
        `when`<CreateOddsView>(mockPresenter.view).thenReturn(fragment)
        Espresso.closeSoftKeyboard()
    }

    @Test
    fun testInit() {
        verify<CreateOddsPresenterImpl>(mockPresenter).onViewAttached(fragment)
    }

    @Test
    @Throws(Throwable::class)
    fun testOnPause() {
        testRule.runOnUiThread { run { fragment.onPause() } }
        verify<CreateOddsPresenterImpl>(mockPresenter).onViewDetached()
    }

    @Test
    fun testShowInitialLayout() {
        onView(withId(R.id.instructions_label)).check(matches(isDisplayed()))
        onView(withId(R.id.descriptionEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.imageViewPager)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.forOrAgainstLinearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.looksGoodButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.imageSearchProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.createOddsButtonLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.dateLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.dueDateTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    @Throws(Throwable::class)
    fun testOnSearch() {
        testRule.runOnUiThread { run { fragment.onSearch() } }
        onView(withId(R.id.imageSearchProgressBar)).check(matches(isDisplayed()))
        onView(withId(R.id.descriptionEditText)).check(matches(not<View>(hasFocus())))
        onView(withId(R.id.imageSearchTermsEditText)).check(matches(not<View>(hasFocus())))
        onView(withId(R.id.instructions_label)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
    }

    @Test
    @Throws(Throwable::class)
    fun testStartAgain() {
        testRule.runOnUiThread { run { fragment.startAgain() } }
        onView(withId(R.id.descriptionEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.imageSearchTermsLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.yesCheckBox)).check(matches(not<View>(isChecked())))
        onView(withId(R.id.noCheckBox)).check(matches(not<View>(isChecked())))
        onView(withId(R.id.instructions_label)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Throwable::class)
    fun testLoadImages() {
        testRule.runOnUiThread { run { fragment.loadImages(testImageList) } }
        onView(withId(R.id.instructions_label)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.imageViewPager)).check(matches(isDisplayed()))
        onView(withId(R.id.imageSearchProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.looksGoodButton)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Throwable::class)
    fun testOnCheckedCheckbox() {
        testRule.runOnUiThread { run { fragment.showForOrAgainstLayout() } }
        onView(withId(R.id.yesCheckBox)).perform(click()).check(matches(isChecked()))
        onView(withId(R.id.dateLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.yesCheckBox)).perform(click())
        onView(withId(R.id.dateLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    @Throws(Throwable::class)
    fun testOnUnCheckCheckbox() {
        testRule.runOnUiThread { run { fragment.showForOrAgainstLayout() } }
        onView(withId(R.id.noCheckBox)).perform(click()).check(matches(isChecked()))
        onView(withId(R.id.dateLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.noCheckBox)).perform(click())
        onView(withId(R.id.dateLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))

    }

    @Test
    @Throws(Throwable::class)
    fun testSubmitNewOdd() {
        testRule.runOnUiThread { run { fragment.showSubmitLayout() } }
        onView(withId(R.id.createOddsButton)).check(matches(isDisplayed())).perform(click())
        verify<CreateOddsPresenterImpl>(mockPresenter).createOdds(ArgumentMatchers.any(SingleOdd::class.java))
    }

    @Test
    fun testSearchForImages() {
        onView(withId(R.id.searchButton)).check(matches(isDisplayed())).perform(click())
        verify<CreateOddsPresenterImpl>(mockPresenter).getImages(anyString(), anyString())
    }

    @Test
    @Throws(Throwable::class)
    fun testShowForOrAgainstLayout() {
        testRule.runOnUiThread { run { fragment.loadImages(testImageList) } }
        onView(withId(R.id.looksGoodButton)).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.descriptionEditText)).check(matches(not<View>(hasFocus())))
        onView(withId(R.id.imageSearchTermsLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.forOrAgainstLinearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    @Throws(Throwable::class)
    fun testMakeChanges() {
        testRule.runOnUiThread { run { fragment.showSubmitLayout() } }
        onView(withId(R.id.makeChangesButton)).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.descriptionEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.imageSearchTermsLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.yesCheckBox)).check(matches(not<View>(isChecked())))
        onView(withId(R.id.noCheckBox)).check(matches(not<View>(isChecked())))
        onView(withId(R.id.instructions_label)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Throwable::class)
    fun testVoteNo() {
        testRule.runOnUiThread { run { fragment.showForOrAgainstLayout() } }
        onView(withId(R.id.noCheckBox)).check(matches(isDisplayed())).perform(click()).check(matches(isChecked()))
    }

    @Test
    @Throws(Throwable::class)
    fun testVoteYes() {
        testRule.runOnUiThread { run { fragment.showForOrAgainstLayout() } }
        onView(withId(R.id.yesCheckBox)).check(matches(isDisplayed())).perform(click()).check(matches(isChecked()))
    }

    @Test
    @Throws(Throwable::class)
    fun testPickADate() {
        testRule.runOnUiThread { run { fragment.showForOrAgainstLayout() } }
        onView(withId(R.id.yesCheckBox)).perform(click())
        onView(withId(R.id.pickADateButton)).check(matches(isDisplayed())).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).check(matches(isDisplayed()))
                .perform(PickerActions.setDate(1978, 1, 31))
        onView(withId(android.R.id.button1)).perform(click())
        onView(withId(R.id.dueDateTextView)).check(matches(withText("We will know for sure on 01/31/1978")))
    }

    @Test
    @Throws(Throwable::class)
    fun testClearTextFields() {
        onView(withId(R.id.descriptionEditText)).perform(typeText("Word"))
        onView(withId(R.id.imageSearchTermsEditText)).perform(typeText("terms"))
        testRule.runOnUiThread { fragment!!.clearTextFields() }
        onView(withId(R.id.descriptionEditText)).check(matches(withText("")))
        onView(withId(R.id.imageSearchTermsEditText)).check(matches(withText("")))
    }

    @Test
    @Throws(Throwable::class)
    fun testNoDate() {
        testRule.runOnUiThread { run { fragment.showForOrAgainstLayout() } }
        onView(withId(R.id.noCheckBox)).perform(click())
        onView(withId(R.id.noDateButton)).check(matches(isDisplayed())).perform(click())
        onView(withId(R.id.forOrAgainstLinearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.dateLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.createOddsButtonLayout)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Throwable::class)
    fun testShowSubmitLayout() {
        testRule.runOnUiThread { run { fragment.showSubmitLayout() } }
        onView(withId(R.id.forOrAgainstLinearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.dateLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.createOddsButtonLayout)).check(matches(isDisplayed()))
    }

    @Test
    @Throws(Throwable::class)
    fun testOnError() {
        testRule.runOnUiThread { run { fragment.onError() } }
        onView(withId(R.id.imageSearchProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.descriptionEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.imageSearchTermsLayout)).check(matches(isDisplayed()))
        onView(withId(R.id.yesCheckBox)).check(matches(not<View>(isChecked())))
        onView(withId(R.id.noCheckBox)).check(matches(not<View>(isChecked())))
        onView(withId(R.id.instructions_label)).check(matches(isDisplayed()))
        onView(allOf<View>(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.bats_data_error))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    @Throws(Throwable::class)
    fun testOnSave() {
        val description = "post"
        testRule.runOnUiThread { run { fragment.onSave(description) } }
        onView(withId(R.id.instructions_label)).check(matches(isDisplayed()))
        onView(withId(R.id.descriptionEditText)).check(matches(isDisplayed()))
        onView(withId(R.id.imageViewPager)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.forOrAgainstLinearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.looksGoodButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.imageSearchProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.createOddsButtonLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.dateLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(withId(R.id.dueDateTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        onView(allOf<View>(withId(android.support.design.R.id.snackbar_text),
                withText(InstrumentationRegistry.getTargetContext().resources.getString(R.string.odds_have_been_saved_msg, description))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @After
    fun tearDown() {
        testRule.finishActivity()
    }

    fun createImageStringList(): List<String> {
        val stringList = ArrayList<String>()
        stringList.add("https://images.pexels.com/photos/937465/pexels-photo-937465.jpeg?auto=compress&cs=tinysrgb&h=350")
        return stringList
    }

}
