package com.example.myapplication;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import com.example.myapplication.databinding.ActivityScrollingBinding;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import static android.content.ContentValues.TAG;

public class ScrollingActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener {

    private ActivityScrollingBinding binding;
    private ActionBar actionBar;

    RecyclerView applicationRecyclerView;
    List<ApplicationInfo> packages;
    List<AppInfo> appInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityScrollingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(getTitle());
        actionBar = getSupportActionBar();
        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(this);

        // =========================================================================================

        final PackageManager pm = getPackageManager();

        packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        appInfos = packages.stream()
                .filter(packageInfo -> pm.getLaunchIntentForPackage(packageInfo.packageName) != null)
                .map(packageInfo -> AppInfo.some(packageInfo))
                .collect(Collectors.toList());

        // =========================================================================================

        applicationRecyclerView = findViewById(R.id.applicationRecyclerView);

        MyAdapter myAdapter = new MyAdapter(this, appInfos);

        applicationRecyclerView.setAdapter(myAdapter);
        applicationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        if (offset == 0) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        else {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }
}