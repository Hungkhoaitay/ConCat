package com.example.myapplication;

import androidx.annotation.NonNull;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

 public class MainActivity extends AppCompatActivity {

    final static int[] buttonIDs = new int[] { R.id.firstButton, R.id.secondButton, R.id.thirdButton, R.id.fourthButton };
    public static final String SHARED_PREFS = "sharedPrefs";

    private static final int WINDOW_PERMISSION = 123;
    private static boolean PERMISSION_GRANTED = true;

    private SharedPreferences sharedPreferences;

    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 19;
    private FirebaseAuth mAuth;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private FirebaseUser user;

     @Override
     protected void onStart() {
         super.onStart();

         user = mAuth.getCurrentUser();
     }

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_main);

//        rootNode = FirebaseDatabase.getInstance();
//        reference = rootNode.getReference("user");
//        reference.child(user.getUid());

        createRequest();
//        findViewById(R.id.ConCat).setOnClickListener(v -> signIn());
//        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        loadData();

//        Button conCat = findViewById(R.id.ConCat);
//        conCat.setOnClickListener(this);
    }

     private void createRequest() {
         // Configure Google Sign In
         GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                 .requestIdToken(getString(R.string.default_web_client_id))
                 .requestEmail()
                 .build();

         // Build a GoogleSignInClient with the options specified by gso.
         mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
     }

     private void signIn() {
         Intent signInIntent = mGoogleSignInClient.getSignInIntent();
         startActivityForResult(signInIntent, RC_SIGN_IN);
     }

     @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
         if (requestCode == RC_SIGN_IN) {
             Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
             try {
                 // Google Sign In was successful, authenticate with Firebase
                 GoogleSignInAccount account = task.getResult(ApiException.class);
                 Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                 firebaseAuthWithGoogle(account.getIdToken());
             } catch (ApiException e) {
                 // Google Sign In failed, update UI appropriately
                 Log.w(TAG, "Google sign in failed", e);
             }
         }
     }

     private void firebaseAuthWithGoogle(String idToken) {
         AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
         mAuth.signInWithCredential(credential)
                 .addOnCompleteListener(this, task -> {
                     if (task.isSuccessful()) {
                         // Sign in success, update UI with the signed-in user's information
                         Log.d(TAG, "signInWithCredential:success");
                         user = mAuth.getCurrentUser();
                         updateUI(user);
                     } else {
                         // If sign in fails, display a message to the user.
                         Log.w(TAG, "signInWithCredential:failure", task.getException());
                         updateUI(null);
                     }
                 });
     }

     private void updateUI(FirebaseUser user) {
         if (user == null) {

         } else {
             Toast.makeText(this, "Hi " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
         }
     }

     private void askForSystemOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, WINDOW_PERMISSION);
        if (Settings.canDrawOverlays(this.getApplicationContext())) {
            this.PERMISSION_GRANTED = true;
        }
    }


//    @Override
//    public void onClick(View v) {
//        TextView display = findViewById(R.id.display);
//        switch (v.getId()) {
//            case R.id.ConCat:
//                if (PERMISSION_GRANTED == false) {
//                    askForSystemOverlayPermission();
//                    break;
//                }
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) {
//                    Intent intent = new Intent(MainActivity.this, FloatingViewService.class);
//                    intent.putExtra("North", sharedPreferences.getString(Integer.toString(buttonIDs[0]), AppInfo.EMPTY));
//                    intent.putExtra("South", sharedPreferences.getString(Integer.toString(buttonIDs[1]), AppInfo.EMPTY));
//                    intent.putExtra("East", sharedPreferences.getString(Integer.toString(buttonIDs[2]), AppInfo.EMPTY));
//                    intent.putExtra("West", sharedPreferences.getString(Integer.toString(buttonIDs[3]), AppInfo.EMPTY));
//                    startService(intent);
//                } else {
//                    askForSystemOverlayPermission();
//                }
//                break;
//            default:
//                break;
//        }
//    }

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
     protected void onDestroy() {
         super.onDestroy();
         PERMISSION_GRANTED = false;
     }
 }