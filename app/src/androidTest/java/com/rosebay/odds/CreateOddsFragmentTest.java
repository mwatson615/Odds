package com.rosebay.odds;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.DatePicker;

import com.rosebay.odds.model.SingleOdd;
import com.rosebay.odds.ui.createOdds.CreateOddsFragment;
import com.rosebay.odds.ui.createOdds.CreateOddsPresenterImpl;
import com.rosebay.odds.ui.createOdds.ImagePagerAdapter;
import com.rosebay.odds.ui.mainOdds.MainActivity;
import com.rosebay.odds.util.SharedPreferencesClient;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class CreateOddsFragmentTest {

    @Mock
    CreateOddsPresenterImpl mockPresenter;
    @Mock
    ImagePagerAdapter mockPagerAdapter;
    @Mock
    SharedPreferencesClient mockClient;
    @InjectMocks
    CreateOddsFragment createOddsFragment;
    private List<String> testImageList;

    @Rule
    public ActivityTestRule<MainActivity> testRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Throwable {
        MockitoAnnotations.initMocks(this);
        testImageList = createImageStringList();
        testRule.getActivity().createFragment(createOddsFragment);
        testRule.runOnUiThread(() -> createOddsFragment.attach(mockPresenter));
        when(mockPagerAdapter.getCurrentUrl(anyInt())).thenReturn("url");
        when(mockClient.getUsername(anyString())).thenReturn("username");
        when(mockPresenter.getView()).thenReturn(createOddsFragment);
        Espresso.closeSoftKeyboard();
    }

    @Test
    public void testInit() {
        verify(mockPresenter).onViewAttached(createOddsFragment);
    }

    @Test
    public void testOnPause() throws Throwable {
        testRule.runOnUiThread(() -> createOddsFragment.onPause());
        verify(mockPresenter).onViewDetached();
    }

    @Test
    public void testShowInitialLayout() {
        onView(withId(R.id.instructions_label)).check(matches(isDisplayed()));
        onView(withId(R.id.descriptionEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.imageViewPager)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.forOrAgainstLinearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.looksGoodButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.imageSearchProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.createOddsButtonLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.dateLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.dueDateTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void testOnSearch() throws Throwable {
        testRule.runOnUiThread(() -> createOddsFragment.onSearch());
        onView(withId(R.id.imageSearchProgressBar)).check(matches(isDisplayed()));
        onView(withId(R.id.descriptionEditText)).check(matches(not(hasFocus())));
        onView(withId(R.id.imageSearchTermsEditText)).check(matches(not(hasFocus())));
        onView(withId(R.id.instructions_label)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.adView)).check(matches(isDisplayed()));
    }

    @Test
    public void testStartAgain() throws Throwable {
        testRule.runOnUiThread(() -> createOddsFragment.startAgain());
        onView(withId(R.id.adView)).check(matches(isDisplayed()));
        onView(withId(R.id.descriptionEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.imageSearchTermsLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.yesCheckBox)).check(matches(not(isChecked())));
        onView(withId(R.id.noCheckBox)).check(matches(not(isChecked())));
        onView(withId(R.id.instructions_label)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoadImages() throws Throwable {
        testRule.runOnUiThread(() -> createOddsFragment.loadImages(testImageList));
        onView(withId(R.id.instructions_label)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.imageViewPager)).check(matches(isDisplayed()));
        onView(withId(R.id.imageSearchProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.looksGoodButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnCheckedCheckbox() throws Throwable {
        testRule.runOnUiThread(() -> createOddsFragment.showForOrAgainstLayout());
        onView(withId(R.id.yesCheckBox)).perform(click()).check(matches(isChecked()));
        onView(withId(R.id.dateLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.yesCheckBox)).perform(click());
        onView(withId(R.id.dateLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void testOnUnCheckCheckbox() throws Throwable {
        testRule.runOnUiThread(() -> createOddsFragment.showForOrAgainstLayout());
        onView(withId(R.id.noCheckBox)).perform(click()).check(matches(isChecked()));
        onView(withId(R.id.dateLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.noCheckBox)).perform(click());
        onView(withId(R.id.dateLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

    }

    @Test
    public void testSubmitNewOdd() throws Throwable {
        testRule.runOnUiThread(() -> createOddsFragment.showSubmitLayout());
        onView(withId(R.id.createOddsButton)).check(matches(isDisplayed())).perform(click());
        verify(mockPresenter).createOdds(ArgumentMatchers.any(SingleOdd.class));
    }

    @Test
    public void testSearchForImages() {
        onView(withId(R.id.searchButton)).check(matches(isDisplayed())).perform(click());
        verify(mockPresenter).getImages(anyString(), anyString());
    }

    @Test
    public void testShowForOrAgainstLayout() throws Throwable {
        testRule.runOnUiThread(() -> createOddsFragment.loadImages(testImageList));
        onView(withId(R.id.looksGoodButton)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.descriptionEditText)).check(matches(not(hasFocus())));
        onView(withId(R.id.imageSearchTermsLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.forOrAgainstLinearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testMakeChanges() throws Throwable {
        testRule.runOnUiThread(() -> createOddsFragment.showSubmitLayout());
        onView(withId(R.id.makeChangesButton)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.adView)).check(matches(isDisplayed()));
        onView(withId(R.id.descriptionEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.imageSearchTermsLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.yesCheckBox)).check(matches(not(isChecked())));
        onView(withId(R.id.noCheckBox)).check(matches(not(isChecked())));
        onView(withId(R.id.instructions_label)).check(matches(isDisplayed()));
    }

    @Test
    public void testVoteNo() throws Throwable {
        testRule.runOnUiThread(() -> createOddsFragment.showForOrAgainstLayout());
        onView(withId(R.id.noCheckBox)).check(matches(isDisplayed())).perform(click()).check(matches(isChecked()));
    }

    @Test
    public void testVoteYes() throws Throwable {
        testRule.runOnUiThread(() -> createOddsFragment.showForOrAgainstLayout());
        onView(withId(R.id.yesCheckBox)).check(matches(isDisplayed())).perform(click()).check(matches(isChecked()));
    }

    @Test
    public void testPickADate() throws Throwable {
        testRule.runOnUiThread(() -> createOddsFragment.showForOrAgainstLayout());
        onView(withId(R.id.yesCheckBox)).perform(click());
        onView(withId(R.id.pickADateButton)).check(matches(isDisplayed())).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).check(matches(isDisplayed()))
                .perform(PickerActions.setDate(1978, 1, 31));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.dueDateTextView)).check(matches(withText("We will know for sure on 01/31/1978")));
    }

    @Test
    public void testClearTextFields() throws Throwable {
        onView(withId(R.id.descriptionEditText)).perform(typeText("Word"));
        onView(withId(R.id.imageSearchTermsEditText)).perform(typeText("terms"));
        testRule.runOnUiThread(() -> createOddsFragment.clearTextFields());
        onView(withId(R.id.descriptionEditText)).check(matches(withText("")));
        onView(withId(R.id.imageSearchTermsEditText)).check(matches(withText("")));
    }

    @Test
    public void testNoDate() throws Throwable {
        testRule.runOnUiThread(() -> createOddsFragment.showForOrAgainstLayout());
        onView(withId(R.id.noCheckBox)).perform(click());
        onView(withId(R.id.noDateButton)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.forOrAgainstLinearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.dateLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.createOddsButtonLayout)).check(matches(isDisplayed()));
    }

    @Test
    public void testShowSubmitLayout() throws Throwable {
        testRule.runOnUiThread(() -> createOddsFragment.showSubmitLayout());
        onView(withId(R.id.forOrAgainstLinearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.dateLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.createOddsButtonLayout)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnError() throws Throwable {
        testRule.runOnUiThread(() -> createOddsFragment.onError());
        onView(withId(R.id.imageSearchProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.adView)).check(matches(isDisplayed()));
        onView(withId(R.id.descriptionEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.imageSearchTermsLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.yesCheckBox)).check(matches(not(isChecked())));
        onView(withId(R.id.noCheckBox)).check(matches(not(isChecked())));
        onView(withId(R.id.instructions_label)).check(matches(isDisplayed()));
        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.bats_data_error))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testOnSave() throws Throwable {
        String description = "post";
        testRule.runOnUiThread(() -> createOddsFragment.onSave(description));
        onView(withId(R.id.instructions_label)).check(matches(isDisplayed()));
        onView(withId(R.id.descriptionEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.imageViewPager)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.forOrAgainstLinearLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.looksGoodButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.imageSearchProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.createOddsButtonLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.dateLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.dueDateTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText(InstrumentationRegistry.getTargetContext().getResources().getString(R.string.odds_have_been_saved_msg, description))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @After
    public void tearDown() {
        testRule.finishActivity();
    }

    public List<String> createImageStringList() {
        List<String> stringList = new ArrayList<>();
        stringList.add("https://images.pexels.com/photos/937465/pexels-photo-937465.jpeg?auto=compress&cs=tinysrgb&h=350");
        return stringList;
    }

}
