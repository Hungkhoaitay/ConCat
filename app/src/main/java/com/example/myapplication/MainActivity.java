package com.example.myapplication;

import android.app.ActivityManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.content.ContentValues.TAG;

import static com.example.myapplication.FloatingViewValues.CANCEL_SELECTION;
import static com.example.myapplication.FloatingViewValues.DEFAULT_VALUE;
import static com.example.myapplication.FloatingViewValues.IMAGE_SELECTION;

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
    private String widgetIcon = DEFAULT_VALUE;

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
         if (isMyServiceRunning(FloatingViewService.class)) {
             this.stopService(new Intent(this, FloatingViewService.class));
             Toast.makeText(getApplicationContext(),
                     "Widget is removed. Please restart!", Toast.LENGTH_SHORT).show();
         }
         PERMISSION_GRANTED = true;
         Log.d(TAG, "OnStart");
     }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
             new ActivityResultCallback<ActivityResult>() {
                 @Override
                 public void onActivityResult(ActivityResult result) {
                     if (result.getResultCode() == 420) {
                         Intent i = result.getData();
                         // handle the code here
                         /*
                         String path = Optional.of(i.getStringExtra("Image"))
                                 .orElse("DEFAULT");

                          */
                         // widgetIcon = i.getStringExtra("Image");
                         // Log.i(TAG, widgetIcon);
                         Toast.makeText(getApplicationContext(),
                                 CANCEL_SELECTION, Toast.LENGTH_SHORT).show();
                     } else if (result.getResultCode() == 69) {
                         Intent i = result.getData();
                         String path = i.getStringExtra("Image Path");
                         widgetIcon = path;
                         Toast.makeText(getApplicationContext(),
                                 widgetIcon, Toast.LENGTH_SHORT).show();
                     } else if (result.getResultCode() == 42069) {
                         Intent i = result.getData();
                         widgetIcon = DEFAULT_VALUE;
                     }
                 }
             });


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ConCat:
                if (PERMISSION_GRANTED == false) {
                    askForSystemOverlayPermission();
                    break;
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(MainActivity.this, FloatingViewService.class);
                    intent.putExtra(IMAGE_SELECTION, widgetIcon);
                    startService(intent);
                } else {
                    askForSystemOverlayPermission();
                }
                break;
            case R.id.customizeBtn:
                this.stopService(new Intent(this, FloatingViewService.class));
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