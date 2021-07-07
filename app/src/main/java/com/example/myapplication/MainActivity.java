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

import java.util.HashMap;
import java.util.Set;

import static android.content.ContentValues.TAG;

 public class MainActivity extends AppCompatActivity
     implements View.OnClickListener, View.OnLongClickListener {

    int[] buttonIDs;

    private static final int WINDOW_PERMISSION = 123;
    private static boolean PERMISSION_GRANTED = true;

    public static final String SHARED_PREFS = "sharedPrefs";

    private HashMap<Integer, String> hashMapNames = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonIDs = new int[] { R.id.firstButton, R.id.secondButton, R.id.thirdButton, R.id.fourthButton };

        loadData();

        AppInfo appInfo = (AppInfo) getIntent().getSerializableExtra("app");
        int buttonID = getIntent().getIntExtra("Button ID", 0);
        if ((appInfo != null) && (hashMapNames.containsKey(buttonID))) {
            hashMapNames.remove(buttonID);
            hashMapNames.put(buttonID, appInfo.getName());
        }

        Button conCat = findViewById(R.id.ConCat);
        conCat.setOnClickListener(this);

        for (int btnID: buttonIDs) {
            AppInfo.of(hashMapNames.get(btnID)).setButton(this, findViewById(btnID));
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

     @Override
     public boolean onLongClick(View v) {
         return false;
     }

     public void saveData() {
         SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
         SharedPreferences.Editor editor = sharedPreferences.edit();

         for (int btnID: buttonIDs) {
             editor.putString(Integer.toString(btnID), hashMapNames.get(btnID));
             Log.i("", hashMapNames.get(btnID));
         }
         editor.apply();
     }

     public void loadData() {
         SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
         hashMapNames.clear();

         for (int btnID: buttonIDs) {
             hashMapNames.put(btnID, sharedPreferences.getString(Integer.toString(btnID), AppInfo.EMPTY));
         }
     }

     @Override
     protected void onStop() {
         super.onStop();
     }

     @Override
     protected void onDestroy() {
         super.onDestroy();
         PERMISSION_GRANTED = false;
     }
 }