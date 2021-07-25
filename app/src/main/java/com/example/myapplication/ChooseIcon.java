package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChooseIcon extends Activity
        implements GalleryImageAdapter.onItemClickListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private GalleryImageAdapter adapter;
    String[] name = {
            "ConCat",
            "Apple",
            "Communism",
            "Rick Ashley-sama"
    };

    Button cancelSelection;
    static int CANCEL_SELECTION = 420;
    static int CONFIRM_SELECTION = 69;
    static int DEFAULT_SELECTION = 42069;

    private String selectedImagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.choose_icon);
        Log.d("", "First");
        askPermission();
        recyclerView = findViewById(R.id.choose_icon_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setHasFixedSize(true);
        Log.d("", "Second");
        List<String> imageFiles = getMediaFiles(this);
        Collections.reverse(imageFiles);
        adapter = new GalleryImageAdapter(this, imageFiles, this);
        recyclerView.setAdapter(adapter);


        this.cancelSelection = findViewById(R.id.cancel_selection);
        Log.d("", "Third");
        cancelSelection.setText("Cancel Selection");
        cancelSelection.setOnClickListener(this);

        findViewById(R.id.default_view).setOnClickListener(this);
    }
//
//        this.confirmSelection = findViewById(R.id.confirm_selection);
//        confirmSelection.setText("Confirm Selection");
//        confirmSelection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("Image Path", selectedImagePath);
//                setResult(CONFIRM_SELECTION, resultIntent);
//                finish();
//            }
//        });
//    }

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
        Log.d("", "Fourth");
        return fileList;
    }

    @Override
    public void onItemClick(String filePath) {
        File imageFile = new File(filePath);
        if (imageFile.exists()) {
            this.selectedImagePath = filePath;
            Log.d("", filePath);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("Image Path", selectedImagePath);
            setResult(CONFIRM_SELECTION, resultIntent);
            Log.d("","FINISH");
            finish();
            Log.d("", "Finish activity");
        }
    }

    @Override
    public void onClick(View v) {
        Intent resultIntent = new Intent();
        switch (v.getId()) {
            case R.id.cancel_selection:
                setResult(CANCEL_SELECTION, resultIntent);
                finish();
            case R.id.default_view:
                setResult(DEFAULT_SELECTION, resultIntent);
                finish();
            default:
        }
    }
}
