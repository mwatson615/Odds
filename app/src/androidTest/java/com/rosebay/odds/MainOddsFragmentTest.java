package com.rosebay.odds;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.AutoCompleteTextView;

import com.rosebay.odds.model.SingleOdd;
import com.rosebay.odds.ui.mainOdds.MainActivity;
import com.rosebay.odds.ui.mainOdds.MainOddsFragment;
import com.rosebay.odds.ui.mainOdds.MainOddsPresenterImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class MainOddsFragmentTest {

    @Mock
    MainOddsPresenterImpl mockPresenter;
    @InjectMocks
    MainOddsFragment mainOddsFragment;
    private List<SingleOdd> singleOddList = new ArrayList<>();
    private Context targetContext;

    @Rule
    public ActivityTestRule<MainActivity> mainOddsFragmentRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup() throws Throwable {
        MockitoAnnotations.initMocks(this);
        singleOddList = createTestOdd();
        targetContext = InstrumentationRegistry.getTargetContext();
        mainOddsFragmentRule.getActivity().createFragment(mainOddsFragment);
        mainOddsFragmentRule.runOnUiThread(() -> mainOddsFragment.attach(mockPresenter));
    }

    @Test
    public void testInit() throws Throwable {
        mainOddsFragmentRule.runOnUiThread(() -> mainOddsFragment.onResume());
        verify(mockPresenter).fetchOdds();
    }

    @Test
    public void testOnStop() throws Throwable {
        mainOddsFragmentRule.runOnUiThread(() -> mainOddsFragment.onStop());
        verify(mockPresenter).onViewDetached();
    }

    @Test
    public void testInitialLayout() throws Throwable {
        mainOddsFragmentRule.runOnUiThread(() -> mainOddsFragment.setData(singleOddList));
        onView(withId(R.id.home_recycler_view)).check(matches(isDisplayed()));
        onView(withId(R.id.searchButton)).check(matches(isDisplayed()));
        onView(withId(R.id.searchView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.closeSearchButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.beginSearchButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.backToMainButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.closeSearchButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.appName)).check(matches(isDisplayed()));
    }

    @Test
    public void testPrepareSearch() {
        onView(withId(R.id.searchButton)).perform(click());
        onView(withId(R.id.searchView)).check(matches(isDisplayed()));
        onView(withId(R.id.appName)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.closeSearchButton)).check(matches(isDisplayed()));
        onView(withId(R.id.beginSearchButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testCloseSearch() {
        onView(withId(R.id.searchButton)).perform(click());
        onView(withId(R.id.closeSearchButton)).perform(click());
        onView(withId(R.id.searchView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.appName)).check(matches(isDisplayed()));
        onView(withId(R.id.closeSearchButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.beginSearchButton)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
    }

    @Test
    public void testConductSearch() {
        onView(withId(R.id.searchButton)).perform(click());
        onView(withId(R.id.searchView)).check(matches(isDisplayed()));
        onView(withId(R.id.searchView)).check(matches(hasFocus()));
        onView(withId(R.id.searchView)).perform(click());
        onView(isAssignableFrom(AutoCompleteTextView.class)).perform(typeText("Search"));
        onView(withId(R.id.beginSearchButton)).check(matches(isClickable())).perform(click());
        onView(withId(R.id.searchView)).check(matches(not(hasFocus())));
        verify(mockPresenter).fetchSearchResults(anyString());
    }

    @Test
    public void testReloadMainOdds() throws Throwable {
        mainOddsFragmentRule.runOnUiThread(() -> mainOddsFragment.showBackButtonOnSearchResults());
        onView(withId(R.id.backToMainButton)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.searchView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        verify(mockPresenter).fetchOdds();
    }

    @Test
    public void testOnItemClicked() throws Throwable {
        mainOddsFragmentRule.runOnUiThread(() -> mainOddsFragment.onItemClicked(singleOddList.get(0)));
        verify(mockPresenter).fetchSingleOdd(singleOddList.get(0).getPostId());
    }

    @Test
    public void testLaunchSingleOdd() throws Throwable {
        mainOddsFragmentRule.runOnUiThread(() -> mainOddsFragment.launchSingleOdd(singleOddList.get(0)));
        onView(withId(R.id.fragmentSingleOdd)).check(matches(isDisplayed()));
        onView(withId(R.id.descriptionSingleOdd)).check(matches(withText(singleOddList.get(0).getDescription())));
        onView(withId(R.id.oddsAgainstSingleOdd)).check(matches(withText(String.valueOf(singleOddList.get(0).getOddsAgainst()))));
        onView(withId(R.id.oddsForSingleOdd)).check(matches(withText(String.valueOf(singleOddList.get(0).getOddsFor()))));
        onView(withId(R.id.singleOddDueDateTextview)).check(matches(withText(
                targetContext.getResources().getString(R.string.due_date_answer, singleOddList.get(0).getDueDate()))));
        onView(withId(R.id.percentageSingleOdd)).check(matches(withText(
                targetContext.getResources().getString(R.string.percentage_text, singleOddList.get(0).getPercentage()))));
    }

    @Test
    public void testOnError() throws Throwable {
        mainOddsFragmentRule.runOnUiThread(() -> mainOddsFragment.onError());
        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.bats_data_error))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testOnNoSearchResults() throws Throwable {
        mainOddsFragmentRule.runOnUiThread(() -> mainOddsFragment.onNoSearchResults());
        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.no_search_results_msg))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testShowBackButtonOnSearchResults() throws Throwable {
        mainOddsFragmentRule.runOnUiThread(() -> mainOddsFragment.showBackButtonOnSearchResults());
        onView(withId(R.id.closeSearchButton)).check(matches(not(isDisplayed())));
        onView(withId(R.id.backToMainButton)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() {
        mainOddsFragmentRule.finishActivity();
    }

    public List<SingleOdd> createTestOdd() {
        List<SingleOdd> oddList = new ArrayList<>();
        SingleOdd singleOdd = new SingleOdd();
        singleOdd.setPercentage(50);
        singleOdd.setOddsAgainst(25);
        singleOdd.setDueDate("12/27/1978");
        singleOdd.setOddsFor(25);
        singleOdd.setDateSubmitted("01/01/2018");
        singleOdd.setImageUrl("https://images.pexels.com/photos/937465/pexels-photo-937465.jpeg?auto=compress&cs=tinysrgb&h=350");
        singleOdd.setDescription("The Buddha");
        singleOdd.setPostId("12345");
        oddList.add(singleOdd);
        return oddList;
    }
}
