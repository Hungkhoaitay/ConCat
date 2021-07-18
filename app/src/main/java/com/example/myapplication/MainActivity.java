 package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.GetChars;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Set;

import static android.content.ContentValues.TAG;

 public class MainActivity extends AppCompatActivity
         implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    final static int[] buttonIDs = new int[] { R.id.firstButton, R.id.secondButton, R.id.thirdButton, R.id.fourthButton };
    public static final String SHARED_PREFS = "sharedPrefs";

    private static final int WINDOW_PERMISSION = 123;
    private static boolean PERMISSION_GRANTED = true;

    private SharedPreferences sharedPreferences;


    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        loadData();
        setUpDrawer();

        Button conCat = findViewById(R.id.ConCat);
        conCat.setOnClickListener(this);
    }

    private void setUpDrawer() {
        this.drawerLayout = findViewById(R.id.drawerLayout);
        this.toolbar = findViewById(R.id.drawer_toolbar);
        setSupportActionBar(toolbar);
        this.navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        this.actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
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
                    Intent intent = new Intent(MainActivity.this, FloatingViewService.class);
                    intent.putExtra("North", sharedPreferences.getString(Integer.toString(buttonIDs[0]), AppInfo.EMPTY));
                    intent.putExtra("South", sharedPreferences.getString(Integer.toString(buttonIDs[1]), AppInfo.EMPTY));
                    intent.putExtra("East", sharedPreferences.getString(Integer.toString(buttonIDs[2]), AppInfo.EMPTY));
                    intent.putExtra("West", sharedPreferences.getString(Integer.toString(buttonIDs[3]), AppInfo.EMPTY));
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
     protected void onDestroy() {
         super.onDestroy();
         PERMISSION_GRANTED = false;
     }

     ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
             new ActivityResultContracts.StartActivityForResult(),
             new ActivityResultCallback<ActivityResult>() {
                 @Override
                 public void onActivityResult(ActivityResult result) {
                     if (result.getResultCode() == Activity.RESULT_OK) {
                         Intent i = result.getData();
                         // handle the code here
                     }
                 }
             });

     @Override
     public boolean onNavigationItemSelected(MenuItem item) {
         switch (item.getItemId()) {
             case R.id.profile:
                 return true;
             case R.id.main_activity_screen:
                 Toast.makeText(getApplicationContext(), "Main Activity", Toast.LENGTH_SHORT).show();
                 return true;
             case R.id.customize_icon:
                 Intent intent = new Intent(this, ChooseIcon.class);
                 activityResultLauncher.launch(intent);
                 return true;
             case R.id.settings:
                 Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
                 return true;
             default:
                 return false;
         }
     }
 }