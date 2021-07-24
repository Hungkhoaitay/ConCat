package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivityScrollingBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

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

    public static final String SHARED_PREFS = "sharedPrefs";

    private static final int WINDOW_PERMISSION = 123;
    private static boolean PERMISSION_GRANTED = true;

    private SharedPreferences sharedPreferences;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//         this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

         // Create a new user with a first and last name
         Map<String, Object> user = new HashMap<>();
         user.put("first", "Ada");
         user.put("last", "Lovelace");
         user.put("born", 1815);

// Add a new document with a generated ID
         db.collection("users")
                 .add(user)
                 .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                     @Override
                     public void onSuccess(DocumentReference documentReference) {
                         Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                     }
                 })
                 .addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Log.w(TAG, "Error adding document", e);
                     }
                 });

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
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
         
//         mConditionRef.addValueEventListener(new ValueEventListener() {
//             @Override
//             public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                 String text = snapshot.getValue(String.class);
//                 Toast.makeText(MainActivity.this, "It is work", Toast.LENGTH_SHORT).show();
//             }
//
//             @Override
//             public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//             }
//         });
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
                     if (result.getResultCode() == Activity.RESULT_OK) {
                         Intent i = result.getData();
                         // handle the code here
                     }
                 }
             });


    @Override
    public void onClick(View v) {
        TextView display = findViewById(R.id.display);
        switch (v.getId()) {
            case R.id.ConCat:
                Log.i("", "Launched");
                if (PERMISSION_GRANTED == false) {
                    askForSystemOverlayPermission();
                    break;
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(MainActivity.this, FloatingViewService.class);
                    intent.putExtra("region 1", sharedPreferences.getString(Integer.toString(buttonIDs[0]), AppInfo.EMPTY));
                    intent.putExtra("region 2", sharedPreferences.getString(Integer.toString(buttonIDs[1]), AppInfo.EMPTY));
                    intent.putExtra("region 3", sharedPreferences.getString(Integer.toString(buttonIDs[2]), AppInfo.EMPTY));
                    intent.putExtra("region 4", sharedPreferences.getString(Integer.toString(buttonIDs[3]), AppInfo.EMPTY));
                    intent.putExtra("region 5", sharedPreferences.getString(Integer.toString(buttonIDs[4]), AppInfo.EMPTY));
                    intent.putExtra("region 6", sharedPreferences.getString(Integer.toString(buttonIDs[5]), AppInfo.EMPTY));
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
//                Intent intentMOSA = new Intent(MainActivity.this, MostOnScreenApps.class);
//                MainActivity.this.startActivity(intentMOSA);
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
 }