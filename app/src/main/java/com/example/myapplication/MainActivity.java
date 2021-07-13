 package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Set;

import static android.content.ContentValues.TAG;

 public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final static int[] buttonIDs = new int[] { R.id.firstButton, R.id.secondButton, R.id.thirdButton, R.id.fourthButton };
    public static final String SHARED_PREFS = "sharedPrefs";

    private static final int WINDOW_PERMISSION = 123;
    private static boolean PERMISSION_GRANTED = true;

    private SharedPreferences sharedPreferences;


     private FloatingViewService fvs;
     private boolean isBound;
     private ServiceConnection serviceConnection;
     private Intent intent;

     private void bindService() {
         if (fvs == null) {
             this.serviceConnection = new ServiceConnection() {
                 @Override
                 public void onServiceConnected(ComponentName name, IBinder service) {
                     Log.i("", "Service connected");
                     FloatingViewService.MyServiceBinder myServiceBinder =
                             (FloatingViewService.MyServiceBinder) service;
                     fvs = myServiceBinder.getService();
                     intent = new Intent(MainActivity.this, FloatingViewService.class);
                     intent.putExtra("North", sharedPreferences.getString(Integer.toString(buttonIDs[0]), AppInfo.EMPTY));
                     intent.putExtra("South", sharedPreferences.getString(Integer.toString(buttonIDs[1]), AppInfo.EMPTY));
                     intent.putExtra("East", sharedPreferences.getString(Integer.toString(buttonIDs[2]), AppInfo.EMPTY));
                     intent.putExtra("West", sharedPreferences.getString(Integer.toString(buttonIDs[3]), AppInfo.EMPTY));
                     isBound = true;
                 }

                 @Override
                 public void onServiceDisconnected(ComponentName name) {
                     Log.i("", "Service disconnected");
                     fvs = null;
                     isBound = false;
                 }
             };
         }
         bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
     }

     private void unBindService() {
         if (isBound) {
             unbindService(serviceConnection);
             isBound = false;
         }
     }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        loadData();

        Button conCat = findViewById(R.id.ConCat);
        conCat.setOnClickListener(this);
    }

    private void askForSystemOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, WINDOW_PERMISSION);
        if (Settings.canDrawOverlays(this.getApplicationContext())) {
            this.PERMISSION_GRANTED = true;
        }
    }


    @Override
    public void onClick(View v) {
        TextView display = findViewById(R.id.display);
        switch (v.getId()) {
            case R.id.ConCat:
                if (PERMISSION_GRANTED == false) {
                    askForSystemOverlayPermission();
                    break;
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) {
                    bindService();
                } else {
                    askForSystemOverlayPermission();
                }
                break;
            default:
                break;
        }
    }

     /**
      * save the states of the buttons
      */
//     public void saveData() {
//         SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
//         SharedPreferences.Editor editor = sharedPreferences.edit();
//
//         for (int btnID: buttonIDs) {
//             editor.putString(Integer.toString(btnID), buttonID2appName.get(btnID));
//         }
//         editor.apply();
//     }

     public void loadData() {
         for (int btnID: buttonIDs) {
             AppInfo.of(sharedPreferences.getString(Integer.toString(btnID), AppInfo.EMPTY))
                     .setButton(this, findViewById(btnID));
         }
     }

     public void resetData() {
         SharedPreferences.Editor editor = sharedPreferences.edit();
         editor.clear();
         editor.apply();
     }

     @Override
     protected void onStop() {
         super.onStop();
     }

     @Override
     protected void onResume() {
         Log.i("", "On Resume");
         if (this.fvs != null) {
             unBindService();
             this.fvs = null;
         }
         super.onResume();
     }

     @Override
     protected void onRestart() {
         Log.i("", "On Restart");
         if (this.fvs != null) {
             unBindService();
             this.fvs = null;
         }
         super.onRestart();
     }

     @Override
     protected void onDestroy() {
         super.onDestroy();
         unBindService();
         PERMISSION_GRANTED = false;
     }
 }