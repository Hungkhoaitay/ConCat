package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.test.ServiceTestCase;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.filters.SdkSuppress;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.ServiceTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeoutException;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import static com.google.common.truth.Truth.assertThat;

@Ignore
@SdkSuppress(minSdkVersion = 26)
@RunWith(AndroidJUnit4.class)
public class FloatingViewServiceTest {

    static class ObjectsInWidget {
        static UiObject widget = new UiObject(
                new UiSelector().className("android.widget.RelativeLayout").
                        resourceId("id:/relativeLayoutParent"));
    }
    private UiDevice device;

    @Before
    public void setup() {
        this.device = UiDevice.getInstance(getInstrumentation());
        Context context = getApplicationContext();
        Intent intent = new Intent(context, FloatingViewService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent);
    }

    @Test
    public void test1() {
        // device.findObject(By.)
        // assertThat(w)
        UiObject widget = new UiObject(
                new UiSelector().className("android.widget.RelativeLayout").
                        resourceId("android:id/relativeLayoutParent"));
        UiObject widget1 = device.findObject(new UiSelector()
                .resourceId("com.android.application:id/relativeLayoutParent"));
        widget1.waitForExists(2000);
        assertThat(widget1.exists()).isTrue();
    }
}
