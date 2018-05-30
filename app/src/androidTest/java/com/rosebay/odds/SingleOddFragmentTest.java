package com.rosebay.odds;

import android.content.Context;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.rosebay.odds.model.SingleOdd;
import com.rosebay.odds.ui.mainOdds.MainActivity;
import com.rosebay.odds.ui.singleOdd.SingleOddFragment;
import com.rosebay.odds.ui.singleOdd.SingleOddPresenterImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class SingleOddFragmentTest {

    @Mock
    SingleOddPresenterImpl mockPresenter;
    @InjectMocks
    SingleOddFragment singleOddFragment;
    private SingleOdd singleOdd;
    private Context targetContext;

    @Rule
    public ActivityTestRule<MainActivity> singleOddFragmentRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup() throws Throwable {
        MockitoAnnotations.initMocks(this);
        targetContext = InstrumentationRegistry.getTargetContext();
        singleOddFragment.setArguments(createTestSingleOdd());
        singleOdd = (SingleOdd) singleOddFragment.getArguments().getSerializable(Constants.SINGLE_ODD_KEY);
        singleOddFragmentRule.getActivity().createFragment(singleOddFragment);
        singleOddFragmentRule.runOnUiThread(() -> singleOddFragment.attach(mockPresenter));
        when(mockPresenter.getView()).thenReturn(singleOddFragment);
        Espresso.closeSoftKeyboard();
    }

    @Test
    public void testInitialPresenterCalls() throws Throwable {
        singleOddFragmentRule.runOnUiThread(() -> singleOddFragment.onResume());
        verify(mockPresenter).checkForFavorite(anyString());
        verify(mockPresenter).checkIfVoted(anyString());
        verify(mockPresenter).loadOddsData(any(SingleOdd.class));
    }

    @Test
    public void testDisableFavoritesButton() throws Throwable {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        singleOddFragmentRule.runOnUiThread(() -> singleOddFragment.disableFavoritesButton());
        onView(withId(R.id.addToFavoritesButton)).check(matches(not(isEnabled())));
    }

    @Test
    public void testEnableFavoritesButton() throws Throwable {
        singleOddFragmentRule.runOnUiThread(() -> singleOddFragment.enableFavoritesButton());
        onView(withId(R.id.addToFavoritesButton)).check(matches(isEnabled()));
    }

    @Test
    public void testSetPercentage() throws Throwable {
        singleOddFragmentRule.runOnUiThread(() -> singleOddFragment.setPercentage(singleOdd.getPercentage()));
        onView(withId(R.id.percentageSingleOdd)).check(matches(withText(
                targetContext.getResources().getString(R.string.percentage_text, singleOdd.getPercentage()))));
    }

    @Test
    public void testSetOddsFor() throws Throwable {
        singleOddFragmentRule.runOnUiThread(() -> singleOddFragment.setOddsFor(singleOdd.getOddsFor()));
        onView(withId(R.id.oddsForSingleOdd)).check(matches(withText(String.valueOf(singleOdd.getOddsFor()))));
    }

    @Test
    public void testSetOddsAgainst() throws Throwable {
        singleOddFragmentRule.runOnUiThread(() -> singleOddFragment.setOddsAgainst(singleOdd.getOddsAgainst()));
        onView(withId(R.id.oddsAgainstSingleOdd)).check(matches(withText(String.valueOf(singleOdd.getOddsAgainst()))));
    }

    @Test
    public void testSetHasDueDate() throws Throwable {
        singleOddFragmentRule.runOnUiThread(() -> singleOddFragment.setDueDate(singleOdd.getDueDate()));
        onView(withId(R.id.singleOddDueDateTextview)).check(matches(withText(
                targetContext.getResources().getString(R.string.due_date_answer, singleOdd.getDueDate()))));
    }

    @Test
    public void testSetNullDueDate() throws Throwable {
        singleOddFragmentRule.runOnUiThread(() -> singleOddFragment.setDueDate(null));
        onView(withId(R.id.singleOddDueDateTextview)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void testSetDescription() throws Throwable {
        singleOddFragmentRule.runOnUiThread(() -> singleOddFragment.setDescription(singleOdd.getDescription()));
        onView(withId(R.id.descriptionSingleOdd)).check(matches(withText(singleOdd.getDescription())));
    }

    @Test
    public void testOnAddedToFavorites() throws Throwable {
        singleOddFragmentRule.runOnUiThread(() -> singleOddFragment.onAddedToFavorites());
        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.added_to_favorites_msg))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testOnVoteYes() throws Throwable {
        onView(withId(R.id.voteYesButton)).check(matches(isEnabled()));
        onView(withId(R.id.voteNoButton)).check(matches(isEnabled()));
        onView(withId(R.id.voteYesButton)).perform(click());
        verify(mockPresenter).voteYes(anyString());
    }

    @Test
    public void testOnNoVote() throws Throwable {
        onView(withId(R.id.voteYesButton)).check(matches(isEnabled()));
        onView(withId(R.id.voteNoButton)).check(matches(isEnabled()));
        onView(withId(R.id.voteNoButton)).perform(click());
        verify(mockPresenter).voteNo(anyString());
    }

    @Test
    public void testAddToFavorites() throws Throwable {
        singleOddFragmentRule.runOnUiThread(() -> singleOddFragment.enableFavoritesButton());
        onView(withId(R.id.addToFavoritesButton)).perform(click());
        verify(mockPresenter).addToFavorites(any(), anyString());
    }

    @Test
    public void testOnVoteSuccess() throws Throwable {
        singleOddFragmentRule.runOnUiThread(() -> singleOddFragment.onVoteSuccess());
        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.vote_has_been_saved_msg))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testDisableVoteButtons() throws Throwable {
        onView(withId(R.id.voteYesButton)).check(matches(isEnabled()));
        onView(withId(R.id.voteNoButton)).check(matches(isEnabled()));
        singleOddFragmentRule.runOnUiThread(() -> singleOddFragment.disableVoteButtons());
        onView(withId(R.id.voteYesButton)).check(matches(not(isEnabled())));
        onView(withId(R.id.voteNoButton)).check(matches(not(isEnabled())));
    }

    @Test
    public void testEnableVoteButtons() throws Throwable {
        singleOddFragmentRule.runOnUiThread(() -> singleOddFragment.enableVoteButtons());
        onView(withId(R.id.voteYesButton)).check(matches(isEnabled()));
        onView(withId(R.id.voteNoButton)).check(matches(isEnabled()));
    }

    @Test
    public void testOnError() throws Throwable {
        singleOddFragmentRule.runOnUiThread(() -> singleOddFragment.onError());
        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.bats_data_error))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @After
    public void tearDown() {
        singleOddFragmentRule.finishActivity();
    }

    private Bundle createTestSingleOdd() {
        SingleOdd singleOdd = new SingleOdd();
        Bundle args = new Bundle();
        singleOdd.setPercentage(50);
        singleOdd.setOddsAgainst(25);
        singleOdd.setDueDate("12/27/1978");
        singleOdd.setOddsFor(25);
        singleOdd.setDateSubmitted("01/01/2018");
        singleOdd.setImageUrl("https://images.pexels.com/photos/937465/pexels-photo-937465.jpeg?auto=compress&cs=tinysrgb&h=350");
        singleOdd.setDescription("The Buddha");
        singleOdd.setPostId("12345");
        args.putSerializable(Constants.SINGLE_ODD_KEY, singleOdd);
        return args;
    }

}
