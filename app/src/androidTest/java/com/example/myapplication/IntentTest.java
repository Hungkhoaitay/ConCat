package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.web.internal.deps.guava.collect.Iterables;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static androidx.test.ext.truth.content.IntentSubject.assertThat;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertTrue;

/**
 * Test 1: Asserting that click intent will launch user to another package. Asserting that
 * application intent is launch-able, user can use ConCat to launch to another package on
 * the phone. Use example with Youtube, Telegram, Whatsapp, Reddit, Netflix...
 * Test case passes.
 *
 * Test 2: Complete Widget movement test: 6 different apps are test with full activity scenario
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class IntentTest {

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule(MainActivity.class);

    UiDevice device;

    @Before
    public void setup() {
        this.device = UiDevice.getInstance(getInstrumentation());
    }

    @Ignore
    @Test
    public void test1() {
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
        onView(withId(R.id.firstButton)).perform(click());
        intended(allOf(toPackage("com.google.android.youtube")));
        intended(not(allOf(toPackage("sg.ndi.sp"))));
        onView(withId(R.id.secondButton)).perform(click());
        intended(allOf(toPackage("org.telegram.messenger")));
        onView(withId(R.id.thirdButton)).perform(click());
        intended(allOf(toPackage("com.whatsapp")));
        onView(withId(R.id.fourthButton)).perform(click());
        intended(allOf(toPackage("com.reddit.frontpage")));
        onView(withId(R.id.fifthButton)).perform(click());
        intended(allOf(toPackage("fr.playsoft.vnexpress")));
        onView(withId(R.id.sixthButton)).perform(click());
        intended(allOf(toPackage("com.android.settings")));
    }


    @Test
    public void launcherTest1() {
        onView(withId(R.id.ConCat)).perform(click());
        UiObject2 floatingWidget = device.findObject(By.desc("Floating Widget"));
        floatingWidget.click();
        UiObject2 launch_apps = device.findObject(By.desc("Launch Apps"));
        launch_apps.click();
        UiObject2 widget = device.findObject(By.desc("Floating Widget"));
        widget.drag(new Point(-250, -350));
        device.waitForIdle(2000);
        intended(toPackage("com.google.android.youtube"));
        device.pressHome();
        device.openNotification();
        device.wait(Until.hasObject(By.text("To relaunch application")), 3000);
        UiObject2 notification = device.findObject(By.text("To relaunch application"));
        notification.click();
        // device.waitForIdle(10000);
        int width = device.getDisplayWidth();
        int height = device.getDisplayHeight();
        device.findObject(By.desc("Floating Widget")).click();
        device.findObject(By.desc("Launch Apps")).click();
        device.findObject(By.desc("Floating Widget"))
                .drag(new Point(width/2, 100));
        intended(toPackage("com.whatsapp"));
        device.pressHome();
        device.openNotification();
        device.wait(Until.hasObject(By.text("To relaunch application")), 3000);
        device.findObject(By.text("To relaunch application")).click();

        device.findObject(By.desc("Floating Widget")).click();
        device.findObject(By.desc("Launch Apps")).click();
        device.findObject(By.desc("Floating Widget"))
                .drag(new Point(width, 150));
        intended(toPackage("com.android.vending"));
        device.pressHome();
        device.openNotification();
        device.wait(Until.hasObject(By.text("To relaunch application")), 3000);
        device.findObject(By.text("To relaunch application")).click();

        device.findObject(By.desc("Floating Widget")).click();
        device.findObject(By.desc("Launch Apps")).click();
        device.findObject(By.desc("Floating Widget"))
                .drag(new Point(100, height));
        intended(toPackage("com.instagram.android"));
        device.pressHome();
        device.openNotification();
        device.wait(Until.hasObject(By.text("To relaunch application")), 3000);
        device.findObject(By.text("To relaunch application")).click();

        device.findObject(By.desc("Floating Widget")).click();
        device.findObject(By.desc("Launch Apps")).click();
        device.findObject(By.desc("Floating Widget"))
                .drag(new Point(width/2, height*2/3));
        intended(toPackage("com.google.android.apps.maps"));
        device.pressHome();
        device.openNotification();
        device.wait(Until.hasObject(By.text("To relaunch application")), 3000);
        device.findObject(By.text("To relaunch application")).click();

        device.findObject(By.desc("Floating Widget")).click();
        device.findObject(By.desc("Launch Apps")).click();
        device.findObject(By.desc("Floating Widget"))
                .drag(new Point(width, height));
        intended(toPackage("com.netflix.mediaclient"));
        device.pressHome();
        device.openNotification();
        device.wait(Until.hasObject(By.text("To relaunch application")), 3000);
        device.findObject(By.text("To relaunch application")).click();
    }
}