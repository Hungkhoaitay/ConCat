package com.example.myapplication;

import android.app.Activity;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class WidgetMovement extends Service implements View.OnTouchListener {
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams params;
    private View mOverlayView;
    private View collapsedView;
    private View expandedView;
    private PackageManager packageManager;


    private int CLICK_THRESHOLD = 200;

    /**
     * Set initial params
     * @param mWindowManager
     * @param mOverlayView
     * @param params
     */
    public void setParams(WindowManager mWindowManager, View mOverlayView,
                          WindowManager.LayoutParams params){
        this.params = params;
        this.mWindowManager = mWindowManager;
        this.mOverlayView = mOverlayView;
    }

    public void setViews(View collapsedView, View expandedView){
        this.collapsedView = collapsedView;
        this.expandedView = expandedView;
    }

    public void setCoordinate() {

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

    /**
     * Specify movements of the widget on the home screen
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        FloatingViewService floatingViewService = new FloatingViewService();
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
                mWindowManager.updateViewLayout(mOverlayView, params);
                return true;
            case MotionEvent.ACTION_UP:
                float closestWall = params.x >= 0 ? maxX : minX;
                params.x = (int) closestWall;
                mWindowManager.updateViewLayout(mOverlayView, params);
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
                            mWindowManager.updateViewLayout(mOverlayView, params);
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
                                            mWindowManager.updateViewLayout(mOverlayView, params);
                                            return true;
                                        case MotionEvent.ACTION_UP:
                                            float gradient = params.y / params.x;
                                            switch (checkRegion(params.x, params.y)) {
                                                case NORTH:
                                                    Log.i("", "North");
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
                                            //stopSelf();
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}