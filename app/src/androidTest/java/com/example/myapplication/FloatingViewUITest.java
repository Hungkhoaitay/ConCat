package com.example.myapplication;

import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.support.test.InstrumentationRegistry;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ServiceTestRule;

import org.junit.runner.RunWith;

import android.app.Service;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;

import org.junit.*;
import org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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

/**
 * List of things needed to do an instrumented unit test on:
 * 1. Direction of floating widget determined by checkRegion
 * 2. Click, long-click, and move condition determined by checkIfMove
 * 3. Visibility state of floating widget
 * 4. Application intent and package name received is launch-able
 * 5. Application is launched as specified
 * 6. Notification testing
 * 7. The specified direction receives the correct launch intent
 * 8. If String app package is null or launch intent is null, it is handled
 *
 * https://androidx.de/androidx/test/rule/ServiceTestRule.html
 */
@Ignore
@RunWith(MockitoJUnitRunner.class)
public class FloatingViewUITest {


    /*
    @Rule
    public ServiceTestRule serviceTestRule = new ServiceTestRule();


    @Before
    public void setUp() throws TimeoutException {
        this.floatingViewService = Robolectric.setupService(FloatingViewService.class);
        MockitoAnnotations.initMocks(this);
    }
     */

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    /**
     * Assert that all specified component are visible and working
     * @throws TimeoutException
     */
    @Test
    public void onCreateTest() throws TimeoutException {
        FloatingViewService floatingViewService = Mockito.mock(FloatingViewService.class);

        assertThat(floatingViewService).isNotNull();
        assertThat(floatingViewService.mFloatingView.getVisibility()).isEqualTo(View.VISIBLE);
    }
}