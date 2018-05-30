package com.rosebay.odds;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.rosebay.odds.ui.mainOdds.MainActivity;
import com.rosebay.odds.util.SharedPreferencesClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Mock
    SharedPreferencesClient mockSharedPrefsClient;

    private MainActivity activity;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        mockSharedPrefsClient = mock(SharedPreferencesClient.class);
        when(mockSharedPrefsClient.getUsername(eq("username"))).thenReturn("username");
        activity = mainActivityActivityTestRule.getActivity();
    }

    @Test
    public void testLoadInitialHasUsernameShowMain() throws Throwable {
        mainActivityActivityTestRule.runOnUiThread(() -> activity.loadInitialView(InstrumentationRegistry.getTargetContext().getResources().getString(R.string.username)));
        onView(withId(R.id.adView)).check(matches(isDisplayed()));
        onView(withId(R.id.home_menu)).check(matches(isDisplayed()));
        onView(withId(R.id.fragmentMainOdds)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoadInitialNullShowDisclaimer() throws Throwable {
        mainActivityActivityTestRule.runOnUiThread(() -> activity.loadInitialView(null));
        onView(withId(R.id.adView)).check(matches(isDisplayed()));
        onView(withId(R.id.home_menu)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
        onView(withId(R.id.fragmentDisclaimer)).check(matches(isDisplayed()));
    }

    @Test
    public void testShowMyOddsFragment() throws Throwable {
        mainActivityActivityTestRule.runOnUiThread(() -> activity.getMyOddsFragment());
        onView(withId(R.id.fragmentMyOdds)).check(matches(isDisplayed()));
    }

    @Test
    public void testShowCreateOddsFragment() throws Throwable {
        mainActivityActivityTestRule.runOnUiThread(() -> activity.getCreateOddsFragment());
        onView(withId(R.id.fragmentCreateOdds)).check(matches(isDisplayed()));
    }

    @Test
    public void testShowFavoriteOddsFragment() throws Throwable {
        mainActivityActivityTestRule.runOnUiThread(() -> activity.getFavoriteOddsFragment());
        onView(withId(R.id.fragmentFavoriteOdds)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnUserNameSaved() throws Throwable {
        mainActivityActivityTestRule.runOnUiThread(() -> activity.onUsernameSaved());
        onView(withId(R.id.adView)).check(matches(isDisplayed()));
        onView(withId(R.id.home_menu)).check(matches(isDisplayed()));
        onView(withId(R.id.fragmentMainOdds)).check(matches(isDisplayed()));
    }

    @After
    public void tearDown() {
        mainActivityActivityTestRule.finishActivity();
    }
}
