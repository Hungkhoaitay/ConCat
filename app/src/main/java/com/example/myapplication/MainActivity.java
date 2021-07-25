package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity
                                            implements View.OnClickListener {

    final static int[] buttonIDs = new int[] {
            R.id.firstButton,
            R.id.secondButton,
            R.id.thirdButton,
            R.id.fourthButton,
            R.id.fifthButton,
            R.id.sixthButton
    };

    final static int NUMBER_OF_BUTTONS = 6;

    private static final int WINDOW_PERMISSION = 123;
    private static boolean PERMISSION_GRANTED = true;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
         Log.d(TAG, "onCreate");

        loadData();

        Button conCat = findViewById(R.id.ConCat);
        conCat.setOnClickListener(this);

         Button customizeBtn = findViewById(R.id.customizeBtn);
         customizeBtn.setOnClickListener(this);

         FloatingActionButton accountBtn = findViewById(R.id.accountBtn);
         accountBtn.setOnClickListener(this);

         Button systemBtn = findViewById(R.id.systemBtn);
         systemBtn.setOnClickListener(this);

         ExtendedFloatingActionButton searchAppBtn = findViewById(R.id.searchAppBtn);
         searchAppBtn.setOnClickListener(this);

         Button MOSABtn = findViewById(R.id.MOSABtn);
         MOSABtn.setOnClickListener(this);

         NestedScrollView scrollView = findViewById(R.id.scrollView);
         scrollView.getViewTreeObserver()
                 .addOnScrollChangedListener(() -> {
                     if (scrollView.getChildAt(0).getBottom()
                             <= (scrollView.getHeight() + scrollView.getScrollY())) {
                         searchAppBtn.setVisibility(View.INVISIBLE);
                     } else {
                          searchAppBtn.setVisibility(View.VISIBLE);
                     }
                 });
    }

     @Override
     protected void onStart() {
         super.onStart();
         // UserData.USERDATA.set(this);
         Log.d(TAG, "OnStart");
     }

     private void askForSystemOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, WINDOW_PERMISSION);
        if (Settings.canDrawOverlays(this.getApplicationContext())) {
            this.PERMISSION_GRANTED = true;
        }
    }

     ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
             new ActivityResultContracts.StartActivityForResult(),
             result -> {
                 if (result.getResultCode() == Activity.RESULT_OK) {
                     Intent i = result.getData();
                     // handle the code here
                 }
             });


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ConCat:
                Log.i("", "Launched");
                if (PERMISSION_GRANTED == false) {
                    askForSystemOverlayPermission();
                    break;
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(MainActivity.this, FloatingViewService.class);
                    startService(intent);
                } else {
                    askForSystemOverlayPermission();
                }
                break;
            case R.id.customizeBtn:
                Intent intent = new Intent(this, ChooseIcon.class);
                activityResultLauncher.launch(intent);
                break;
            case R.id.accountBtn:
                Intent intentAccount = new Intent(MainActivity.this, AccountActivity.class);
                MainActivity.this.startActivity(intentAccount);
                break;
            case R.id.MOSABtn:
                Intent intentMOSA = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                MainActivity.this.startActivity(intentMOSA);
                break;
            case R.id.searchAppBtn:
                Intent intentSearchApp = new Intent(MainActivity.this, ScrollingActivity.class);
                MainActivity.this.startActivity(intentSearchApp);
                break;
            case R.id.systemBtn:
//                Intent intentSystem = new Intent(Settings.ACTION_APP_SEARCH_SETTINGS);
//                MainActivity.this.startActivity(intentSystem);
                break;
            default:
                break;
        }
    }

     public void loadData() {
        UserData.USERDATA.update(GoogleSignIn.getLastSignedInAccount(this).getId(), this);
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