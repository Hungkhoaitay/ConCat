package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.app.Service;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;

import android.os.Build;
import android.os.IBinder;

import android.support.test.InstrumentationRegistry;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.ext.truth.content.IntentSubject.assertThat;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.ServiceTestRule;

import org.junit.runner.RunWith;

import org.junit.*;
import org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

/*
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
 */

import java.util.concurrent.TimeoutException;

import static android.os.Looper.getMainLooper;
import static androidx.test.espresso.action.ViewActions.click;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import androidx.test.filters.SdkSuppress;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@Ignore
@LargeTest
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 26)
public class ApplicationSetUpTest {

    private static final String SAMPLE_PACKAGE =
            "com.example.myapplication";

    private static final int LAUNCH_TIMEOUT = 5000;
    private UiDevice device;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void startMainActivity() {
        this.device = UiDevice.getInstance(getInstrumentation());
        device.pressHome();

        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        PackageManager packageManager = getApplicationContext().getPackageManager();
        ResolveInfo resolveInfo = packageManager.resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        String launcherPackage = resolveInfo.activityInfo.packageName;
        Log.i("Package name: ", launcherPackage);
        assertThat(launcherPackage).isNotNull();
        // Launch the blueprint app
        Context context = getApplicationContext();
        final Intent intent1 = context.getPackageManager()
                .getLaunchIntentForPackage(SAMPLE_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
        context.startActivity(intent1);

        device.wait(Until.hasObject(By.pkg(SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }

    // Confirm that application is launched on call
    @Test
    public void checkPreCondition() {
        assertThat(device).isNotNull();
    }
}
