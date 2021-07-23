package com.example.myapplication;


import android.Manifest;
import android.app.Service;
import android.provider.Settings;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.rule.ServiceTestRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FloatingWidgetComponentTesting {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public ServiceTestRule serviceTestRule = new ServiceTestRule();

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
            Manifest.permission.SYSTEM_ALERT_WINDOW
    );

    UiDevice device;
    @Before
    public void setup() {
        this.device = UiDevice.getInstance(getInstrumentation());
    }
    // Widget alternate between layout expanded and collapsed, is overlaying, and close on clicking X
    @Test
    public void test1() {
        // Launching ConCat
        onView(withId(R.id.ConCat)).perform(click());

        //  Check for widget visibility
        onView(withContentDescription("Floating Widget"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView())))).check(matches(isDisplayed()));
        onView(withContentDescription("Collapsed Layout"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView())))).check(matches(isDisplayed()));
        onView(withContentDescription("Expanded Layout"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView())))).
                check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        // Clicking on widget
        onView(withContentDescription("Floating Widget"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView())))).perform(click());
        onView(withContentDescription("Collapsed Layout"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView()))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withContentDescription("Expanded Layout"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView()))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // click on expanded view
        onView(withContentDescription("Expanded Layout"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView())))).perform(click());
        onView(withContentDescription("Collapsed Layout"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView()))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withContentDescription("Expanded Layout"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView())))).
                check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        // click on the floating widget again
        onView(withContentDescription("Floating Widget"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView())))).perform(click());
        onView(withContentDescription("Collapsed Layout"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView()))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withContentDescription("Expanded Layout"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView()))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // click on expanded layout again
        onView(withContentDescription("Expanded Layout"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView()))))
                .perform(click());

        // check and click on the X button
        onView(withId(R.id.buttonClose))
                .inRoot(withDecorView(not(is(activityTestRule.
                        getActivity().getWindow().getDecorView()))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.buttonClose))
                .inRoot(withDecorView(not(is(activityTestRule.
                        getActivity().getWindow().getDecorView()))))
                .perform(click());
        onView(withContentDescription("Floating Widget")).check(doesNotExist());
        device.pressHome();
    }

    // Emulate a Touch Event
    @Ignore
    @Test
    public void test2() {
        onView(withId(R.id.ConCat)).perform(click());
        this.device.openQuickSettings();
        onView(withContentDescription("Floating Widget"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView())))).check(matches(isDisplayed()));
        activityTestRule.finishActivity();
        onView(withId(R.id.relativeLayoutParent))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void test3() {
        this.device.openQuickSettings();
    }
}
