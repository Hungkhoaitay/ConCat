 package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tooleap.sdk.TooleapPopOutMiniApp;

import java.util.HashMap;
import java.util.Set;

import static android.content.ContentValues.TAG;

 public class MainActivity extends AppCompatActivity
     implements View.OnClickListener {

    final int[] buttonIDs = new int[] { R.id.firstButton, R.id.secondButton, R.id.thirdButton, R.id.fourthButton };
    public static final String SHARED_PREFS = "sharedPrefs";
    private HashMap<Integer, String> buttonID2appName = new HashMap<>();

    private static final int WINDOW_PERMISSION = 123;
    private static boolean PERMISSION_GRANTED = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadData();

        AppInfo appInfo = (AppInfo) getIntent().getSerializableExtra("app");
        int buttonID = getIntent().getIntExtra("Button ID", 0);
        if ((appInfo != null) && (buttonID2appName.containsKey(buttonID))) {
            buttonID2appName.remove(buttonID);
            buttonID2appName.put(buttonID, appInfo.getName());
        }

        Button conCat = findViewById(R.id.ConCat);
        conCat.setOnClickListener(this);

        for (int btnID: buttonIDs) {
            AppInfo.of(buttonID2appName.get(btnID)).setButton(this, findViewById(btnID));
        }

        saveData();
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
                if (this.hashMapNames.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "At least an app must be chosen to launch ConCat!",
                            Toast.LENGTH_SHORT).show();
                    break;
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(MainActivity.this, FloatingViewService.class);
                    intent.putExtra("North", hashMapNames.get(buttonIDs[0]));
                    intent.putExtra("South", hashMapNames.get(buttonIDs[1]));
                    intent.putExtra("East", hashMapNames.get(buttonIDs[2]));
                    intent.putExtra("West", hashMapNames.get(buttonIDs[3]));
                    startService(intent);
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
     public void saveData() {
         SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
         SharedPreferences.Editor editor = sharedPreferences.edit();

         for (int btnID: buttonIDs) {
             editor.putString(Integer.toString(btnID), buttonID2appName.get(btnID));
         }
         editor.apply();
     }

     public void loadData() {
         SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
         buttonID2appName.clear();

         for (int btnID: buttonIDs) {
             buttonID2appName.put(btnID, sharedPreferences.getString(Integer.toString(btnID), AppInfo.EMPTY));
         }
     }

     public void resetData() {
         buttonID2appName.clear();
         for (int btnID: buttonIDs) {
             buttonID2appName.put(btnID, AppInfo.EMPTY);
         }
     }
 }