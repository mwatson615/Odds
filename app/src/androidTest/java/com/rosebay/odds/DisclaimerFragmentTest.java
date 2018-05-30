package com.rosebay.odds;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.widget.AutoCompleteTextView;

import com.rosebay.odds.ui.disclaimer.DisclaimerFragment;
import com.rosebay.odds.ui.disclaimer.DisclaimerPresenterImpl;
import com.rosebay.odds.ui.mainOdds.MainActivity;

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
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class DisclaimerFragmentTest {

    @Mock
    DisclaimerPresenterImpl mockPresenter;
    @InjectMocks
    DisclaimerFragment fragment;
    private Context context;

    @Rule
    public ActivityTestRule<MainActivity> testRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Throwable {
        MockitoAnnotations.initMocks(this);
        context = InstrumentationRegistry.getTargetContext();
        testRule.getActivity().createFragment(fragment);
        testRule.runOnUiThread(() -> fragment.attach(mockPresenter));
    }

    @Test
    public void testInit() {
        verify(mockPresenter).onViewAttached(fragment);
    }

    @Test
    public void testOnStop() throws Throwable {
        testRule.runOnUiThread(() -> fragment.onStop());
        verify(mockPresenter).onViewDetached();
    }

    @Test
    public void testOnUsernameAvailable() throws Throwable {
        testRule.runOnUiThread(() -> fragment.onUsernameAvailable(anyString()));
        onView(withId(R.id.usernameSearchProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.saveUsernameButton)).check(matches(isDisplayed())).check(matches(isEnabled())).perform(click());
        verify(mockPresenter).saveUsername(anyString());
    }

    @Test
    public void testOnUsernameTaken() throws Throwable {
        testRule.runOnUiThread(() -> fragment.onUsernameTaken());
        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.username_taken_msg))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testOnError() throws Throwable {
        testRule.runOnUiThread(() -> fragment.onError());
        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.bats_data_error))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testShowSearchLayout() throws Throwable {
        testRule.runOnUiThread(() -> fragment.showSearchLayout());
        onView(withId(R.id.usernameSearchProgressBar)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.disclaimerSearchView)).check(matches(isDisplayed()));
        onView(isAssignableFrom(AutoCompleteTextView.class)).perform(typeText("S"));
        onView(withId(R.id.saveUsernameButton)).check(matches(not(isEnabled())));
    }

    @Test
    public void testShowProgressLayout() throws Throwable {
        testRule.runOnUiThread(() -> fragment.showProgressLayout());
        onView(withId(R.id.usernameSearchProgressBar)).check(matches(isDisplayed()));
        onView(withId(R.id.disclaimerSearchView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.saveUsernameButton)).check(matches(not(isEnabled())));
    }

    @Test
    public void testOnUserSaved() throws Throwable {
        testRule.runOnUiThread(() -> fragment.onUserSaved(context.getResources().getString(R.string.username)));
        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText(InstrumentationRegistry.getTargetContext().getResources().getString(R.string.username_saved_msg,
                        context.getResources().getString(R.string.username)))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testSearchForUsername() {
        onView(withId(R.id.disclaimerSearchView)).check(matches(isDisplayed()));
        onView(isAssignableFrom(AutoCompleteTextView.class)).perform(typeText("Search"));
        onView(withId(R.id.search_mag_icon)).perform(click());
        verify(mockPresenter).checkForUsername("Search");
    }

    @After
    public void tearDown() {
        testRule.finishActivity();
    }

}
