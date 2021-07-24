package com.example.myapplication;

import android.app.Application;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.test.runner.AndroidJUnit4;
import android.test.ServiceTestCase;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.junit.runner.RunWith;

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

import java.util.concurrent.TimeoutException;

import static android.os.Looper.getMainLooper;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * List of things needed to do a local unit test on:
 * 1. Direction of floating widget determined by checkRegion - local
 * 2. Click, long-click, and move condition determined by checkIfMove - local
 * 3. Visibility state of floating widget - instrumented
 * 4. Application intent and package name received is launch-able - instrumented
 * 5. Application is launched as specified - instrumented
 * 6. Notification testing - instrumented
 * 7. The specified direction receives the correct launch intent
 * 8. If String app package is null or launch intent is null, it is handled
 *
 * https://androidx.de/androidx/test/rule/ServiceTestRule.html
 */

@RunWith(AndroidJUnit4.class)
public class FloatingViewServiceTest {

    private FloatingViewService floatingViewService;

    @Before
    public void setUp() {
        LayoutInflater inflater = (LayoutInflater) floatingViewService.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        floatingViewService.mFloatingView = inflater.inflate(R.layout.layout_floating_widget, null);
        floatingViewService.collapsedView = floatingViewService.mFloatingView.findViewById(R.id.layoutCollapsed);
        floatingViewService.expandedView = floatingViewService.mFloatingView.findViewById(R.id.layoutExpanded);
    }

    @Test
    public void onCreateTest() throws TimeoutException {
        /*
        shadowOf(getMainLooper()).idle();
        Application application = ApplicationProvider.getApplicationContext();
        Intent newIntent = new Intent(InstrumentationRegistry.getTargetContext(), FloatingViewService.class);
        serviceTestRule.startService(newIntent);
        // check that service is started on calling it

         */
    }

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
        floatingViewService.params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        WindowManager windowManager = (WindowManager) floatingViewService.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(floatingViewService.mFloatingView, floatingViewService.params);
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
