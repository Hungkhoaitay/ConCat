package com.example.myapplication;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FloatingViewService extends Service
        implements View.OnClickListener, View.OnDragListener {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * mWindowManager: System service responsible for managing what is displayed
     * and organized on the screen of the user.
     * mFloatingView: Design, action, and algorithms relating to the floating widget
     * collapsedView:
     * expandedView:
     */

    private WindowManager mWindowManager;
    private View mFloatingView;
    private View collapsedView;
    private View expandedView;

    private PackageManager packageManager;
    private WindowManager.LayoutParams params;

    private static final int CLICK_THRESHOLD = 100;

    private String[] appList = {null, null, null, null};

    @Override
    public boolean onDrag(View v, DragEvent event) {
        return false;
    }

    public FloatingViewService() {

    }

    private static final int NORTH = 1;
    private static final int WEST = 2;
    private static final int SOUTH = 3;
    private static final int EAST = 4;

    private int checkRegion(int x, int y) {
        float gradient = Math.abs(y/x);
        if (gradient >= 1) {
            if (y > 0) {
                return NORTH;
            } else {
                return SOUTH;
            }
        } else {
            if (x > 0) {
                return EAST;
            } else {
                return WEST;
            }
        }
    }

    public void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Launch", "Launch", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(getApplicationContext(), FloatingViewService.class);
        intent.putExtra("North", appList[0]);
        // Log.i("TAG", hashMapNames.get(R.id.firstButton));
        intent.putExtra("South", appList[1]);
        intent.putExtra("East", appList[2]);
        intent.putExtra("West", appList[3]);

        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Launch")
                .setSmallIcon(R.drawable.concat)
                .setContentTitle("To relaunch application")
                .setContentText("Click here to relaunch ConCat!")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(true);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(12, builder.build());
    }

    public class NormalMovement extends Binder implements View.OnTouchListener {
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Display display = mWindowManager.getDefaultDisplay();
            float maxX = (float) 0.5 * display.getWidth();
            float minX = -maxX;
            float maxY = (float) 0.5 * display.getHeight();
            float minY = -maxY;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = params.x;
                    initialY = params.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    Log.i("", "Started action");
                    Log.i("", initialX + "" + initialY);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    int xDiff = Math.round(event.getRawX() - initialTouchX);
                    int yDiff = Math.round(event.getRawY() - initialTouchY);
                    params.x = initialX + (int) xDiff;
                    params.y = initialY + (int) yDiff;
                    Log.i("", params.x + " and " + params.y);
                    Log.i("", xDiff + " and " + yDiff);
                    mWindowManager.updateViewLayout(mFloatingView, params);
                    return true;
                case MotionEvent.ACTION_UP:
                    float closestWall = params.x >= 0 ? maxX : minX;
                    params.x = (int) closestWall;
                    mWindowManager.updateViewLayout(mFloatingView, params);
                    if (event.getEventTime() - event.getDownTime() <= CLICK_THRESHOLD) {
                        // specify click
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                params.x = 0;
                                params.y = 0;
                                mWindowManager.updateViewLayout(mFloatingView, params);
                            }
                        });
                        v.performClick();
                        v.setOnTouchListener(new LauncherMovement());
                    }
                default:
                    return false;
            }
        }
    }

    public class LauncherMovement extends Binder implements View.OnTouchListener {
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = params.x;
                    initialY = params.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    Log.i("", "Started action");
                    Log.i("", initialX + "" + initialY);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    int xDiff = Math.round(event.getRawX() - initialTouchX);
                    int yDiff = Math.round(event.getRawY() - initialTouchY);
                    params.x = initialX + (int) xDiff;
                    params.y = initialY + (int) yDiff;
                    Log.i("", params.x + " and " + params.y);
                    Log.i("", xDiff + " and " + yDiff);
                    mWindowManager.updateViewLayout(mFloatingView, params);
                    return true;
                case MotionEvent.ACTION_UP:
                    Toast.makeText(getApplicationContext(), params.x + " " + params.y, Toast.LENGTH_SHORT).show();
                    if (event.getEventTime() - event.getDownTime() <= CLICK_THRESHOLD) {
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        stopSelf();
                        return true;
                    }
                    switch (checkRegion(params.x, params.y)) {
                        case NORTH:
                            launchApp(appList[0]);
                            Log.i("", "App 1 launched");
                            Toast.makeText(getApplicationContext(), "Launch app 1", Toast.LENGTH_SHORT).show();
                            break;
                        case SOUTH:
                            launchApp(appList[1]);
                            Toast.makeText(getApplicationContext(), "Launch app 2", Toast.LENGTH_SHORT).show();
                            break;
                        case EAST:
                            launchApp(appList[2]);
                            Toast.makeText(getApplicationContext(), "Launch app 3", Toast.LENGTH_SHORT).show();
                            break;
                        case WEST:
                            launchApp(appList[3]);
                            Toast.makeText(getApplicationContext(), "Launch app 4", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    stopSelf();
                    createNotification();
                    return true;
                default:
                    return false;
            }
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mFloatingView != null) {
            Toast.makeText(getApplicationContext(), "Widget is already created", Toast.LENGTH_SHORT).show();
            return super.onStartCommand(intent, flags, startId);
        }

        this.packageManager = getApplicationContext().getPackageManager();
        this.appList[0] = intent.getStringExtra("North");
        this.appList[1] = intent.getStringExtra("South");
        this.appList[2] = intent.getStringExtra("East");
        this.appList[3] = intent.getStringExtra("West");

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        this.params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        // getting the collapsed and expanded view
        collapsedView = mFloatingView.findViewById(R.id.layoutCollapsed);
        expandedView = mFloatingView.findViewById(R.id.layoutExpanded);

        //adding click listener to close button and expanded view
        mFloatingView.findViewById(R.id.buttonClose).setOnClickListener(this);
        expandedView.setOnClickListener(this);

        WidgetMovement widgetMovement = new WidgetMovement();
        widgetMovement.setParams(mWindowManager, mFloatingView, params);
        widgetMovement.setViews(collapsedView, expandedView);
        mFloatingView.findViewById(R.id.relativeLayoutParent).setOnTouchListener(new NormalMovement());
        return super.onStartCommand(intent, flags, startId);
    }

    private boolean clickCondition() {
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void launchApp(String appInfo) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager packageManager = getApplicationContext().getPackageManager();
        if (packageManager == null) {
            Log.i("", "null package");
            return;
        }
        Log.i("", "package is not null");
        List<ResolveInfo> temp = packageManager.queryIntentActivities(intent, 0);
        for (ResolveInfo info : temp) {
            if (info.activityInfo.packageName.equalsIgnoreCase(appInfo)) {
                try {
                    startActivity(packageManager.getLaunchIntentForPackage(info.activityInfo.packageName));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "Activity not found", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layoutExpanded:
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
                Log.i("", "Button layout closed");
                break;
            case R.id.buttonClose:
                stopSelf();
                break;
            default:
        }
    }

    public void onDestroy(){
        super.onDestroy();
        if (mFloatingView != null) {
            mWindowManager.removeView(mFloatingView);
        }
    }

}
