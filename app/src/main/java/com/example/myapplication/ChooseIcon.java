package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChooseIcon extends Activity {

    private RecyclerView recyclerView;
    private GalleryImageAdapter adapter;
    String[] name = {
            "ConCat",
            "Apple",
            "Communism",
            "Rick Ashley-sama"
    };

    private Drawable selectedImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.choose_icon);

        askPermission();

        // getActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.choose_icon_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setHasFixedSize(true);

        List<String> imageFiles = getMediaFiles(this);
        Collections.reverse(imageFiles);
        adapter = new GalleryImageAdapter(this, imageFiles);
        recyclerView.setAdapter(adapter);
        Button button = findViewById(R.id.button_fragment);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("", 0);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
        button.setText("Cancel Selection");
    }

    private void askPermission() {
        String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permission, 101);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public List<String> getMediaFiles(Context context) {
        List<String> fileList = new ArrayList<>();
        final String[] column = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID
        };
        final String orderBy = MediaStore.Images.Media._ID;

        Cursor cursor = context.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column,
                        null,
                        null,
                        orderBy);

        if (cursor != null) {
            int count = cursor.getCount();
            String[] arrPath = new String[count];
            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);
                int dataColumnIndex = cursor.getColumnIndex(
                        MediaStore.Images.Media.DATA
                );
                arrPath[i] = cursor.getString(dataColumnIndex);
                fileList.add(arrPath[i]);
            }
            cursor.close();
        }
        return fileList;
    }
}
