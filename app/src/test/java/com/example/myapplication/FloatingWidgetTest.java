package com.example.myapplication;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.test.ServiceTestCase;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ServiceTestRule;
import androidx.test.runner.AndroidJUnit4;

import android.app.Service;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;

import org.junit.*;
import org.junit.Assert.*;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import java.util.concurrent.TimeoutException;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.robolectric.Shadows.shadowOf;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadow.api.Shadow;

/**
 * List of things needed to do a local unit test on:
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

@Config(sdk = {Build.VERSION_CODES.O_MR1})
@RunWith(AndroidJUnit4.class)
public class FloatingWidgetTest {

    private FloatingViewService floatingViewService;

    @Before
    public void setup() {
        floatingViewService = Robolectric.setupService(FloatingViewService.class);
    }

    @Test
    public void onCreateTest() {
        Application application = ApplicationProvider.getApplicationContext();
        // check that service is started on calling it
        assertThat(floatingViewService).isNotNull();
    }

    @Ignore
    @Test
    public void testCheckRegion() {
        assertAll("check Region method",
            () -> assertEquals(floatingViewService.checkRegion(100, 300), NORTH),
            () -> assertEquals(floatingViewService.checkRegion(250, 150), EAST),
            () -> assertEquals(floatingViewService.checkRegion(0, 200), EAST),
            () -> assertEquals(floatingViewService.checkRegion(-50, 240), NORTH),
            () -> assertEquals(floatingViewService.checkRegion(-300, 150), WEST),
            () -> assertEquals(floatingViewService.checkRegion(-200, 0), EAST),
            () -> assertEquals(floatingViewService.checkRegion(-300, -100), EAST),
            () -> assertEquals(floatingViewService.checkRegion(-100, -400), SOUTH),
            () -> assertEquals(floatingViewService.checkRegion(0, -250), SOUTH),
            () -> assertEquals(floatingViewService.checkRegion(100, -300), SOUTH),
            () -> assertEquals(floatingViewService.checkRegion(400, -50), WEST),
            () -> assertEquals(floatingViewService.checkRegion(0, 0), WEST)
        );
    }

    @Test
    public void testClickCondition() {
        MotionEvent motionEvent;
    }
    /*
    @Test
    public void testDirection() {

        serviceTestRule.startService(new Intent(context, FloatingViewService.class));

        assertAll("check Region method",
            () -> assertEquals(fvs.checkRegion(100, 300), NORTH),
            () -> assertEquals(fvs.checkRegion(250, 150), EAST),
            () -> assertEquals(fvs.checkRegion(0, 200), EAST),
            () -> assertEquals(fvs.checkRegion(-50, 240), NORTH),
            () -> assertEquals(fvs.checkRegion(-300, 150), WEST)
        );
    }

     */
    // Checking state of Floating Widget View
    private static final int VIEW_GONE = View.GONE;
    private static final int VIEW_VISIBLE = View.VISIBLE;
    // Checking position of Floating Widget relative to the phone
    private static final int NORTH = 1;
    private static final int WEST = 2;
    private static final int SOUTH = 3;
    private static final int EAST = 4;
    // Checking
    private static final int CLICK_THRESHOLD = 150;
    private static final int LONG_CLICK_THRESHOLD = 1500;
    private static final int MOVE_THRESHOLD = 5;
    /*
    @Test
    public void testClickAndMove() {
        /**
         * Test checkIfMove and others

    }
    /*
    @Test
    public void testViewState() {
        /**
         * Test expanded and collapsed view

    }
    */
}
