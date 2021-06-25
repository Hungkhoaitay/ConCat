package com.example.myapplication;

import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.Point;
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

import java.util.Collections;
import java.util.List;

public class FloatingViewService extends Service
        implements View.OnClickListener, View.OnDragListener {

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

    private int mWidth = 0;

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

    @Override
    public void onCreate() {
        super.onCreate();
        if (mFloatingView != null) {
            return;
        }
        
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
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
        mFloatingView.findViewById(R.id.relativeLayoutParent).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private int CLICK_THRESHOLD = 200;

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
                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                            /*
                            collapsedView.setVisibility(View.GONE);
                            expandedView.setVisibility(View.VISIBLE);

                             */
                                    params.x = 0;
                                    params.y = 0;
                                    mWindowManager.updateViewLayout(mFloatingView, params);
                                    v.setOnTouchListener(new View.OnTouchListener() {
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
                                                    float gradient = params.y / params.x;
                                                    switch (checkRegion(params.x, params.y)) {
                                                        case NORTH:
                                                            Log.i("", "North");
                                                            launchApp("com.android.bluetooth");
                                                            Toast.makeText(getApplicationContext(), "Launch app 1", Toast.LENGTH_SHORT).show();
                                                            break;
                                                        case SOUTH:
                                                            Log.i("", "South");
                                                            Toast.makeText(getApplicationContext(), "Launch app 2", Toast.LENGTH_SHORT).show();
                                                            break;
                                                        case EAST:
                                                            Log.i("", "East");
                                                            Toast.makeText(getApplicationContext(), "Launch app 3", Toast.LENGTH_SHORT).show();
                                                            break;
                                                        case WEST:
                                                            Log.i("", "West");
                                                            Toast.makeText(getApplicationContext(), "Launch app 4", Toast.LENGTH_SHORT).show();
                                                            break;
                                                    }
                                                    stopSelf();
                                                    return true;
                                                default:
                                                    return false;
                                            }
                                        }
                                    });
                                }
                            });
                            v.performClick();
                            Log.i("", "Button clicked");
                            return false;
                        } else {
                            Log.i("", "Button moved");
                            return true;
                        }
                    default:
                        return false;
                }
            }
        });
    }

    public void launchApp(String appInfo) {
        /*
        List<ApplicationInfo> temp = getPackageManager().getInstalledApplications(0);
        Intent intent = getPackageManager().getLaunchIntentForPackage(temp.get(5).packageName);
        if (intent == null) {
            Log.i("", "Activity does not exist");
            Log.i("", ""+ temp.size());
            Log.i("", temp.get(5).packageName);
            return;
        }
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Activity not found!", Toast.LENGTH_SHORT).show();
        }
         */
        Intent intent = new Intent();
        intent.setPackage(appInfo);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));

        if (resolveInfos.size() > 0) {
            ResolveInfo launchable = resolveInfos.get(0);
            ActivityInfo activity = launchable.activityInfo;
            ComponentName name = new ComponentName(activity.applicationInfo.packageName,
                    activity.name);
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            i.setComponent(name);

            startActivity(i);
        }
    }

    public void stopService() {
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
