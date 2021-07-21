package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static com.example.myapplication.MainActivity.buttonIDs;

public class UserData {
    private SharedPreferences sharedPreferences;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    private Buttons buttons;

    private class Buttons {
        private HashMap<Integer, String> data;

        private Buttons(HashMap<Integer, String> buttons) {
            this.data = buttons;
        }
    }

    public UserData(Context c) {
        sharedPreferences = c.getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        for (int btnID: buttonIDs) {
            HashMap<Integer, String> buttons = new HashMap<>();
            buttons.put(btnID, sharedPreferences.getString(Integer.toString(btnID), AppInfo.EMPTY));
            this.buttons = new Buttons(buttons);
        }
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users").child(FirebaseAuth.getInstance().getUid());
    }

    public void sendData() {
        this.reference.setValue(this.buttons);
        Log.d(TAG, "Send Data Successfully!");
    }

    public void updateData() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                buttons = dataSnapshot.getValue(Buttons.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        for (int btnID: buttonIDs) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Integer.toString(btnID), this.buttons.data.get(btnID));
        }
    }
}
