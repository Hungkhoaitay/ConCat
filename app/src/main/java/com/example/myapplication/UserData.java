package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.example.myapplication.MainActivity.buttonIDs;

public class UserData {
    private Buttons button;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userID;

    private class Buttons {
        private String[] buttons;

        private Buttons() {
            String[] buttons = new String[6];
            Arrays.fill(buttons, AppInfo.EMPTY);
            this.buttons = buttons;
        }

        private void updateButton(int pos, String appName) {
            this.buttons[pos] = appName;
        }

        private void set(AppCompatActivity ac) {
            for (int i = 0; i < MainActivity.NUMBER_OF_BUTTONS; i++) {
                AppInfo.of(buttons[i]).setButton(ac, ac.findViewById(buttonIDs[i]), i);
            }
        }

        private HashMap<String, String> toHashMap() {
            HashMap<String, String> has = new HashMap<>();
            for (int i = 0; i < MainActivity.NUMBER_OF_BUTTONS; i++) {
                has.put(Integer.toString(i), buttons[i]);
            }
            return has;
        }

        private void sendData() {
            db.collection(userID)
                    .document("buttons")
                    .set(toHashMap());
        }

        private void update() {
            DocumentReference docRef = db.collection(userID).document("buttons");
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                Map<String, Object> has = documentSnapshot.getData();
                for (int i = 0; i < MainActivity.NUMBER_OF_BUTTONS; i++) {
                    this.buttons[i] = Optional.of((String)
                            has.get(Integer.toString(i))).orElse(AppInfo.EMPTY);
                    Log.d(TAG, "heelo" + buttons[i]);
                }
            });
        }
    }

    public UserData() {
        this.button = new Buttons();
    }

    public static UserData USERDATA = new UserData();

    public void updateButton(int pos, String appName) {
        this.button.updateButton(pos, appName);
    }

    public void set(AppCompatActivity ac) {
        this.button.set(ac);
    }

    public void sendData() {
        this.button.sendData();
    }

    public void clean() {
        USERDATA = new UserData();
    }

    public void update(String userID) {
        this.userID = userID;
        this.button.update();
    }
}
