package com.example.myapplication;

import android.content.Context;
import android.test.ServiceTestCase;
import android.view.View;
import android.view.WindowManager;

import androidx.test.core.app.ApplicationProvider;
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
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.annotation.Config;

import java.util.concurrent.TimeoutException;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

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

@Config(sdk = 28)
@RunWith(AndroidJUnit4.class)
public class FloatingWidgetTest {

    private FloatingViewService fvs = Mockito.mock(FloatingViewService.class);
    private Context context = ApplicationProvider.getApplicationContext();

    @Rule
    public final ServiceTestRule serviceTestRule = new ServiceTestRule();


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


    @Test
    public void testDirection() throws TimeoutException {
        /**
         * Test checkRegion method for Launcher Movement
         */
        serviceTestRule.startService(new Intent(context, FloatingViewService.class));
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        int height = windowManager.getDefaultDisplay().getHeight();
        System.out.println(width + " " + height);
        Assert.assertEquals(fvs.checkRegion(300, 400), NORTH);
    }

    @Test
    public void testClickAndMove() {
        /**
         * Test checkIfMove and others
         */
    }

    @Test
    public void testViewState() {
        /**
         * Test expanded and collapsed view
         */
    }
}
