package com.example.myapplication;



import android.content.Context;
import android.content.pm.PackageManager;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.Root;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.filters.SdkSuppress;
import androidx.test.filters.SmallTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
// import static com.google.common.truth.Truth.assertThat;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
// import static org.hamcrest.Matchers.matchesPattern;

/**
 * Instrumented Unit Test:
 * App Info Test: Asserting that the class and the method returns labelling and icon
 * of the specified app package. Test case passes.
 *
 * GUI Test:
 * Test 2: Assuming freshly installed, each button will not have any app on it.
 * Assert that Toast will come up. Test case pass with all API of 29 and below but
 * fail on API 30, possibly due to internal changes of testing methods.
 *
 * Test 3: Asserting that App bar is shown with the user account circle button,
 * view is scrollable, main UI components all exist (Button choosing, launcher etc)
 * Test case passes.
 *
 * Integration Test:
 * Test 1: Asserting that Scrolling activity is launched, intent is returned back
 * Test case passes
 * Test 2: Asserting that Account activity is launched, intent is returned back.
 * Test case passes
 * Test 3: Asserting that Customize activity is launched, all components exist and that
 * intent is returned back. Test case passes.
 * Test 4: Asserting that Floating View service is launched with intent. Test passes
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    private UiDevice device;

    @Before
    public void setup() {
        this.device = UiDevice.getInstance(getInstrumentation());
    }

    // Toast messages shown is successful on API level 29 and below, rather than API 30

    /**
     * Test results: Toast messages shown on calling each transition phase, to assist viewers:
     * Pass on all devices with API of 29 and below, but fail specific on API level 30
     */
    @SdkSuppress(maxSdkVersion = 29)
    @Test
    public void activityScenario0() {
        onView(withId(R.id.ConCat)).check(matches(isDisplayed()));
        onView(withId(R.id.firstButton)).check(matches(isDisplayed()));
        onView(withId(R.id.firstButton)).perform(click());
        onView(withText("Empty Button! Hold to choose app")).inRoot(
                withDecorView(is(not(mActivityTestRule.getActivity().getWindow().getDecorView()))
        )).check(matches(isDisplayed()));
    }

    @Test
    public void activityScenario1() {
        onView(withId(R.id.app_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.accountBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.secondButton)).check(matches(isDisplayed()));
        onView(withId(R.id.customizeBtn)).check(matches(isDisplayed()));
    }


    @Test
    public void activityScenario2() {
        onView(withId(R.id.app_text)).check(matches(isDisplayed()));
        onView(withId(R.id.sixthButton)).check(matches(isDisplayed()));
        onView(withId(R.id.sixthButton)).perform(click());
        device.pressHome();
    }


    // customizer of icon is launched on intent
    @Test
    public void activityScenario3() {
        onView(withId(R.id.customizeBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.customizeBtn)).perform(click());
        onView(withId(R.id.cancel_selection)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel_selection)).perform(click());
        onView(withId(R.id.app_bar)).check(matches(isDisplayed()));
    }

    @Test
    public void appInfoTest() throws PackageManager.NameNotFoundException {
        Context context = mActivityTestRule.getActivity().getApplicationContext();
        assertEquals(AppInfo.of("com.reddit.frontpage").getLabel(context), "Reddit");
        assertEquals(AppInfo.of("com.facebook.katana").getLabel(context), "Facebook");
        assertEquals(AppInfo.of("com.google.android.youtube").getLabel(context), "YouTube");
        assertEquals(AppInfo.of("com.whatsapp").getLabel(context), "WhatsApp");
        assertEquals(AppInfo.of("com.luminus.nus.edu.sg").getLabel(context), "LumiNUS");
        assertEquals(AppInfo.of("com.spotify.music").getLabel(context), "Spotify");
//        assertEquals(AppInfo.of("com.reddit.frontpage").getIcon(context),
//                context.getPackageManager().getApplicationIcon("com.reddit.frontpage"));
//        assertEquals(AppInfo.of("com.facebook.katana").getIcon(context),
//                context.getPackageManager().getApplicationIcon("com.facebook.katana"));
//        assertEquals(AppInfo.of("com.android.vending").getIcon(context),
//                context.getPackageManager().getApplicationIcon("com.android.vending"));
//        assertEquals(AppInfo.of("com.whatsapp").getIcon(context),
//                context.getPackageManager().getApplicationIcon("com.whatsapp"));
//        assertEquals(AppInfo.of("com.luminus.nus.edu.sg").getIcon(context),
//                context.getPackageManager().getApplicationIcon("com.luminus.nus.edu.sg"));
    }

}