package com.rosebay.odds;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rosebay.odds.model.SingleOdd;
import com.rosebay.odds.ui.favoriteOdds.FavoriteOddsFragment;
import com.rosebay.odds.ui.favoriteOdds.FavoriteOddsPresenterImpl;
import com.rosebay.odds.ui.mainOdds.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.internal.util.Checks.checkNotNull;
import static org.hamcrest.CoreMatchers.allOf;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class FavoriteOddsFragmentTest {

    @Mock
    FavoriteOddsPresenterImpl mockPresenter;
    @InjectMocks
    FavoriteOddsFragment oddsFragment;
    private List<SingleOdd> testList;

    @Rule
    public ActivityTestRule<MainActivity> testRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Throwable {
        MockitoAnnotations.initMocks(this);
        testList = createTestList();
        testRule.getActivity().createFragment(oddsFragment);
        testRule.runOnUiThread(() -> oddsFragment.attach(mockPresenter));
        Espresso.closeSoftKeyboard();
    }

    @Test
    public void testInit() throws Throwable {
        onView(withId(R.id.favoritesProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.favoritesRecyclerView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.noFavoriteOddsTextView)).check(matches(isDisplayed()));
        testRule.runOnUiThread(() -> oddsFragment.onResume());
        verify(mockPresenter).getAllFavorites();
    }

    @Test
    public void testSetData() throws Throwable {
        testRule.runOnUiThread(() -> oddsFragment.setData(testList));
        onView(withId(R.id.favoritesRecyclerView)).check(matches(isDisplayed()));
        onView(withId(R.id.noFavoriteOddsTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.favoritesProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.favoritesRecyclerView))
                .check(matches(atPosition(0, hasDescendant(withText(testList.get(0).getDescription())))));
        onView(withId(R.id.favoritesRecyclerView))
                .check(matches(atPosition(0, hasDescendant(withText(String.valueOf(testList.get(0).getOddsAgainst()))))));
        onView(withId(R.id.favoritesRecyclerView))
                .check(matches(atPosition(0, hasDescendant(withText(String.valueOf(testList.get(0).getOddsFor()))))));
        onView(withId(R.id.favoritesRecyclerView))
                .check(matches(atPosition(0, hasDescendant(withText(InstrumentationRegistry.getTargetContext().getResources()
                        .getString(R.string.percentage_text, testList.get(0).getPercentage()))))));
    }

    @Test
    public void testNoFavorites() throws Throwable {
        testRule.runOnUiThread(() -> oddsFragment.noFavorites());
        onView(withId(R.id.favoritesRecyclerView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.favoritesProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.noFavoriteOddsTextView)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnLoading() throws Throwable {
        testRule.runOnUiThread(() -> oddsFragment.onLoading());
        onView(withId(R.id.favoritesRecyclerView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.noFavoriteOddsTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.favoritesProgressBar)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnError() throws Throwable {
        testRule.runOnUiThread(() -> oddsFragment.onError());
        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.bats_data_error))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @After
    public void tearDown() {
        testRule.finishActivity();
    }

    public List<SingleOdd> createTestList() {
        List<SingleOdd> oddList = new ArrayList<>();
        SingleOdd singleOdd = new SingleOdd();
        singleOdd.setPercentage(50);
        singleOdd.setOddsAgainst(30);
        singleOdd.setDueDate("12/27/1978");
        singleOdd.setOddsFor(25);
        singleOdd.setDateSubmitted("01/01/2018");
        singleOdd.setImageUrl("https://images.pexels.com/photos/937465/pexels-photo-937465.jpeg?auto=compress&cs=tinysrgb&h=350");
        singleOdd.setDescription("The Buddha");
        singleOdd.setPostId("12345");
        oddList.add(singleOdd);
        return oddList;
    }

    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                // has no item on such position
                return viewHolder != null && itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

}
