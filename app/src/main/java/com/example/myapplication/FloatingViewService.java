package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;

public class FloatingViewService extends Service implements View.OnClickListener {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("ServiceDemo", "On Bind");
        return mBinder;
    }

    class MyServiceBinder extends Binder {
        public FloatingViewService getService() {
            return FloatingViewService.this;
        }
    }

    private IBinder mBinder = new MyServiceBinder();

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("ServiceDemo", "On Unbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i("ServiceDemo", "On Rebind");
        super.onRebind(intent);
    }

    private WindowManager mWindowManager;
    protected View mFloatingView;
    protected View collapsedView;
    protected View expandedView;

    private PackageManager packageManager;
    protected WindowManager.LayoutParams params;

    private static final int CLICK_THRESHOLD = 150;
    private static final int LONG_CLICK_THRESHOLD = 1500;

    public FloatingViewService() {

    }

    private static final int REGION_1 = -1;
    private static final int REGION_2 = -2;
    private static final int REGION_3 = -3;
    private static final int REGION_4 = 1;
    private static final int REGION_5 = 2;
    private static final int REGION_6 = 3;


                                //one,   two, three, four, five, six
    private String[] newAppList = {null, null, null, null, null, null};
    /**
     * Check this method later because of arithmetic errors
     * Determine region for app launching
     *
     * Annotations and rules: x left to right, y up to down
     * |-------------|
     * |-x, -y|+x, -y|
     * |------|------|
     * |-x, +y|+x, +y|
     * |------|------|
     *
     * @param x
     * @param y
     * @return
     */
    protected float maxX;
    protected float minX;
    protected float maxY;
    protected float minY;

    int checkRegion(int x, int y) {
        if (y < 0) {
            if (x <= minX/3) {
                Log.i("MIN X", minX/3 + "");
                return REGION_1;
            } else if (x > minX/3 && x < maxX/3) {
                 return REGION_2;
            } else {
                Log.i("MAX X", maxX/3 + "");
                return REGION_3;
            }
        } else {
            if (x <= minX/3) {
                return REGION_4;
            } else if (x > minX/3 && x < maxX/3) {
                return REGION_5;
            } else {
                return REGION_6;
            }
        }
    }

    private boolean checkIfMove(float dx, float dy, long t1, long t2) {
        float dist = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        return (dist <= 5) && (Math.abs(t1 - t2) <= CLICK_THRESHOLD);
    }

    public void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("Launch", "Launch", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(getApplicationContext(), FloatingViewService.class);
        intent.putExtra("region 1", newAppList[0]);
        intent.putExtra("region 2", newAppList[1]);
        intent.putExtra("region 3", newAppList[2]);
        intent.putExtra("region 4", newAppList[3]);
        intent.putExtra("region 5", newAppList[4]);
        intent.putExtra("region 6", newAppList[5]);

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
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = params.x;
                    initialY = params.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    int xDiff = Math.round(event.getRawX() - initialTouchX);
                    int yDiff = Math.round(event.getRawY() - initialTouchY);
                    params.x = initialX + (int) xDiff;
                    params.y = initialY + (int) yDiff;
                    mWindowManager.updateViewLayout(mFloatingView, params);
                    return true;
                case MotionEvent.ACTION_UP:
                    if (checkIfMove(params.x - initialX, params.y - initialY, event.getEventTime(), event.getDownTime())) {
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setExpandedView();
                            }
                        });
                        v.performClick();
                        return true;
                    }
                    float closestWall = params.x >= 0 ? maxX : minX;
                    params.x = (int) closestWall;
                    mWindowManager.updateViewLayout(mFloatingView, params);
                    return true;
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
                    return true;
                case MotionEvent.ACTION_MOVE:
                    Log.i("", params.x + " " + params.y);
                    int xDiff = Math.round(event.getRawX() - initialTouchX);
                    int yDiff = Math.round(event.getRawY() - initialTouchY);
                    params.x = initialX + (int) xDiff;
                    params.y = initialY + (int) yDiff;
                    switch (checkRegion(params.x, params.y)) {
                        case REGION_1:
                            mFloatingView.findViewById(R.id.collapsed_iv).
                                    setBackground(AppInfo.of(newAppList[0]).getIcon(getApplicationContext()));
                            break;
                        case REGION_2:
                            mFloatingView.findViewById(R.id.collapsed_iv).
                                    setBackground(AppInfo.of(newAppList[1]).getIcon(getApplicationContext()));
                            break;
                        case REGION_3:
                            mFloatingView.findViewById(R.id.collapsed_iv).
                                    setBackground(AppInfo.of(newAppList[2]).getIcon(getApplicationContext()));
                            break;
                        case REGION_4:
                            mFloatingView.findViewById(R.id.collapsed_iv).
                                    setBackground(AppInfo.of(newAppList[3]).getIcon(getApplicationContext()));
                            break;
                        case REGION_5:
                            mFloatingView.findViewById(R.id.collapsed_iv).
                                    setBackground(AppInfo.of(newAppList[4]).getIcon(getApplicationContext()));
                            break;
                        case REGION_6:
                            mFloatingView.findViewById(R.id.collapsed_iv).
                                    setBackground(AppInfo.of(newAppList[5]).getIcon(getApplicationContext()));
                            break;
                        default:
                            break;
                    }
                    mWindowManager.updateViewLayout(mFloatingView, params);
                    return true;
                case MotionEvent.ACTION_UP:
                    if (checkIfMove(params.x - initialX, params.y - initialY, event.getEventTime(), event.getDownTime())) {
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                params.x = 0;
                                mFloatingView.findViewById(R.id.relativeLayoutParent).
                                        setOnTouchListener(new NormalMovement());
                            }
                        });
                        v.performClick();
                        return true;
                    }
                    switch (checkRegion(params.x, params.y)) {
                        case REGION_1:
                            launchApp(newAppList[0]);
                            Toast.makeText(getApplicationContext(), "Launch " +
                                    AppInfo.of(newAppList[0]).getLabel(getApplicationContext()), Toast.LENGTH_SHORT).show();
                            break;
                        case REGION_2:
                            launchApp(newAppList[1]);
                            Toast.makeText(getApplicationContext(), "Launch " +
                                    AppInfo.of(newAppList[1]).getLabel(getApplicationContext()), Toast.LENGTH_SHORT).show();
                            break;
                        case REGION_3:
                            launchApp(newAppList[2]);
                            Toast.makeText(getApplicationContext(), "Launch " +
                                    AppInfo.of(newAppList[2]).getLabel(getApplicationContext()), Toast.LENGTH_SHORT).show();
                            break;
                        case REGION_4:
                            launchApp(newAppList[3]);
                            Toast.makeText(getApplicationContext(), "Launch " +
                                    AppInfo.of(newAppList[3]).getLabel(getApplicationContext()), Toast.LENGTH_SHORT).show();
                            break;
                        case REGION_5:
                            launchApp(newAppList[4]);
                            Toast.makeText(getApplicationContext(), "Launch " +
                                    AppInfo.of(newAppList[4]).getLabel(getApplicationContext()), Toast.LENGTH_SHORT).show();
                            break;
                        case REGION_6:
                            launchApp(newAppList[5]);
                            Toast.makeText(getApplicationContext(), "Launch " +
                                    AppInfo.of(newAppList[5]).getLabel(getApplicationContext()), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                    mWindowManager.removeView(mFloatingView);
                    mFloatingView = null;
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
        this.newAppList[0] = intent.getStringExtra("region 1");
        this.newAppList[1] = intent.getStringExtra("region 2");
        this.newAppList[2] = intent.getStringExtra("region 3");
        this.newAppList[3] = intent.getStringExtra("region 4");
        this.newAppList[4] = intent.getStringExtra("region 5");
        this.newAppList[5] = intent.getStringExtra("region 6");

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);
        this.params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        mFloatingView.findViewById(R.id.collapsed_iv).setBackgroundResource(R.drawable.concat);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        Display display = mWindowManager.getDefaultDisplay();
        this.maxX = (float) 0.5 * display.getWidth();
        this.minX = -maxX;
        this.maxY = (float) 0.5 * display.getHeight();
        this.minY = -maxY;
        Log.i("", ""+  minX/3);

        // getting the collapsed and expanded view
        this.collapsedView = mFloatingView.findViewById(R.id.layoutCollapsed);
        this.expandedView = mFloatingView.findViewById(R.id.layoutExpanded);

        //adding click listener to close button and expanded view
        mFloatingView.findViewById(R.id.buttonClose).setOnClickListener(this);
        mFloatingView.findViewById(R.id.quickLaunch).setOnClickListener(this);
        mFloatingView.findViewById(R.id.returnToApp).setOnClickListener(this);

        Button returnToApp = (Button) mFloatingView.findViewById(R.id.returnToApp);
        returnToApp.setText("Go to App");
        expandedView.setOnClickListener(this);

        mFloatingView.findViewById(R.id.relativeLayoutParent).setOnTouchListener(new NormalMovement());
        return super.onStartCommand(intent, flags, startId);
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
            return;
        }
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
            case R.id.quickLaunch:
                params.x = 0;
                params.y = 0;
                setCollapsedView();
                mWindowManager.updateViewLayout(mFloatingView, params);
                mFloatingView.findViewById(R.id.relativeLayoutParent).
                        setOnTouchListener(new LauncherMovement());
                Toast.makeText(getApplicationContext(), "App launcher state entered",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.returnToApp:
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                stopSelf();
                break;
            case R.id.layoutExpanded:
                setCollapsedView();
                break;
            case R.id.buttonClose:
                stopSelf();
                createNotification();
                break;
            default:
        }
    }

    public void setCollapsedView() {
        mWindowManager.updateViewLayout(mFloatingView, this.params);
        collapsedView.setVisibility(View.VISIBLE);
        expandedView.setVisibility(View.GONE);
    }

    public void setExpandedView() {
        WindowManager.LayoutParams expandedParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        mWindowManager.updateViewLayout(mFloatingView, expandedParams);
        collapsedView.setVisibility(View.GONE);
        expandedView.setVisibility(View.VISIBLE);
    }

    public void onDestroy(){
        super.onDestroy();
        Log.i("ServiceDemo", "Service Destroyed");
        if (mFloatingView != null) {
            mWindowManager.removeView(mFloatingView);
        }
    }
}
