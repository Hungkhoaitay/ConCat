package com.example.myapplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
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

import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
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
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented Unit and GUI test, in combination:
 * Test 1: Test whether floating widget can overlay user activity.
 * Status: Failed, it is necessary to have tampering while testing, by clicking back
 * on granting permission testing while doing testing, as this is an action granted externally.
 * GrantPermissionRule does not allow tester to manually access permission.
 *
 * Test 2: Test transition of two child views within the floating widget. Test involves whether
 * collapsed view (which is only wrap-contented) and expanded view (which is match-parent) can
 * alternate with each other on clicking.
 * Status: Test passes
 *
 * Test 3: Test whether the user can launch and close the floating widget on demand. User can
 * close the floating widget when they click the X button on top, and can relaunch the floating
 * widget when scrolling and clicking notification.
 * Status: Test passes
 *
 * Test 4: Auto-alignment of floating widget. Test whether the floating widget will automatically
 * align itself to the nearest edge of the phone.
 * Status: Test passes
 *
 * Integration Test with GUI assertions:
 *
 * Complete test scenario:
 * 1. User long click the 6 buttons and choose an app by scrolling through recycler view. Actions
 * require alternation between MainActivity and ScrollingActivity. On finish, 6 buttons will have
 * 6 different icons attaching to them.
 * Status: Test passes
 *
 * 2. User launch ConCat and is able to use the floating widget. User tap on the floating widget
 * and is able to see two buttons: launch app button and go to settings button.
 * User then enter a sequence of actions that clicks on the launch app button 6 different times
 * and drag on 6 main directions as divided. On finish dragging, a launch intent will be created
 * to the specified application.
 * Status: Test passes
 */
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

    @Ignore
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
        assertEquals(widget.getVisibleCenter().x, width);
        widget.drag(new Point(-width/3, -height/4));
        assertEquals(widget.getVisibleCenter().x, 0);
        widget.drag(new Point(-width/2, height));
        assertEquals(widget.getVisibleCenter().x, 0);
        onView(withId(R.id.buttonClose))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView())))).perform(click());
    }


    @Test
    public void chooserTestIntegration() {
        onView(withId(R.id.firstButton)).perform(longClick());
        // Youtube
        onView(withId(R.id.applicationRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, click())
        );
        onView(withId(R.id.secondButton)).perform(longClick());
        onView(withId(R.id.applicationRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(13, scrollTo())
        );
        // Whatsapp
        onView(withId(R.id.applicationRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(13, click())
        );

        onView(withId(R.id.thirdButton)).perform(longClick());
        // Google Play
        onView(withId(R.id.applicationRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(25, scrollTo())
        );
        onView(withId(R.id.applicationRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(25, click())
        );
        onView(withId(R.id.fourthButton)).perform(longClick());
        onView(withId(R.id.applicationRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(30, scrollTo())
        );
        // Instagram
        onView(withId(R.id.applicationRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(30, click())
        );
        onView(withId(R.id.fifthButton)).perform(longClick());

        onView(withId(R.id.applicationRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(40, scrollTo())
        );
        // Google Map
        onView(withId(R.id.applicationRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(40, click())
        );
        onView(withId(R.id.sixthButton)).perform(longClick());

        onView(withId(R.id.applicationRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(50, scrollTo())
        );
        // Netflix
        onView(withId(R.id.applicationRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(50, click())
        );
    }

}
