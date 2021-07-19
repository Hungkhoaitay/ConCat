package com.example.myapplication;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseIcon extends Activity {

    RecyclerView recyclerView;
    GalleryImage adapter;
    String[] name = {
            "ConCat",
            "Apple",
            "Communism",
            "Rick Ashley-sama"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_icon);
        findViewById(R.id.button_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("", 0);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        recyclerView = findViewById(R.id.choose_icon_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GalleryImage(this, name);
        recyclerView.setAdapter(adapter);
    }
}
