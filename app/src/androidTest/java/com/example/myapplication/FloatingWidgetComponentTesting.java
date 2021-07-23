package com.example.myapplication;

import android.content.Intent;
import android.Manifest;
import android.app.Service;
import android.graphics.Point;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.filters.SdkSuppress;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.rule.ServiceTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiSelector;

import org.apiguardian.api.API;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.MethodSorter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        if (!Settings.canDrawOverlays(activityTestRule.getActivity().getApplicationContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package: " + activityTestRule.getActivity().getPackageName()));
            activityTestRule.getActivity().startActivityForResult(intent, 100);
        }
    }
    // Widget alternate between layout expanded and collapsed, is overlaying, and close on clicking X
    @Ignore
    @Test
    public void aStateTransitionTest() {
        // Launching ConCat
        onView(withId(R.id.ConCat)).perform(click());

        //  Check for widget visibility
        onView(withContentDescription("Floating Widget"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView())))).check(matches(isDisplayed()));
        onView(withContentDescription("Collapsed Layout"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView())))).check(matches(isDisplayed()));
        onView(withId(R.id.collapsed_iv)).
                inRoot(withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.buttonClose))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView()))))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
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
        onView(withId(R.id.quickLaunch))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView()))))
                .check(matches(isClickable()));
        onView(withId(R.id.returnToApp))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView()))))
                .check(matches(isClickable()));

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
    }

    // Widget can overlay user screen
    // Need one tampering action by exiting Settings draw overlay request manually while testing
    // Later can explain in documentation that the settings must be explicitly granted by users,
    // that the nature of our application require users' explicit permission to allow the floating
    // widget to come on top, and that no testing or mocking framework (so far) can replicate this
    @Ignore
    @Test
    public void overlayTest() {
        onView(withId(R.id.ConCat)).perform(click());
        onView(withContentDescription("Floating Widget"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView())))).check(matches(isDisplayed()));
        activityTestRule.finishActivity();
        device.pressHome();
        UiObject2 object = device.findObject(By.desc("Floating Widget"));
        object.click();
        object.click();
        assertNotNull(object);
        UiObject2 close = device.findObject(By.desc("Button Closed"));
        close.click();
    }

    @Test
    public void movementTest() {
        int width = device.getDisplayWidth();
        int height = device.getDisplayHeight();

        int maxX = width/2;
        int minX = -maxX;

        onView(withId(R.id.ConCat)).perform(click());
        onView(withContentDescription("Floating Widget"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView())))).check(matches(isDisplayed()));
        UiObject2 widget = device.findObject(By.desc("Floating Widget"));
        widget.drag(new Point(width/2, height/3));
        // assertEquals(widget.getVisibleCenter().x, width);
        widget.drag(new Point(-width/3, -height/4));
        // assertEquals(widget.getVisibleCenter().x, 0);
        widget.drag(new Point(-width/2, height));
        // assertEquals(widget.getVisibleCenter().x, 0);
        onView(withId(R.id.buttonClose))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView())))).perform(click());
    }

    @Test
    public void launcherTest() {
        onView(withId(R.id.firstButton)).perform(longClick());
        onView(withId(R.id.applicationRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click())
        );
        onView(withId(R.id.secondButton)).perform(longClick());
        onView(withId(R.id.applicationRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(10, click())
        );
        onView(withId(R.id.thirdButton)).perform(longClick());
        onView(withId(R.id.applicationRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(20, click())
        );
        onView(withId(R.id.fourthButton)).perform(longClick());
        onView(withId(R.id.applicationRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(30, click())
        );
        onView(withId(R.id.fifthButton)).perform(longClick());
        onView(withId(R.id.applicationRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(40, click())
        );
        onView(withId(R.id.sixthButton)).perform(longClick());
        onView(withId(R.id.applicationRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(50, click())
        );
    }
}
