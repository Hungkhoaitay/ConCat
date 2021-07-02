package com.example.myapplication;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

import static android.content.ContentValues.TAG;

public abstract class AppInfo implements Serializable {

    public static final String EMPTY = "empty";

    public static final class SomeAppInfo extends AppInfo implements Serializable {
        private String name;

        private SomeAppInfo(ApplicationInfo applicationInfo) {
            this.name = applicationInfo.packageName;
        }

        private SomeAppInfo(String name) {
            this.name = name;
        }

        @Override
        public Drawable getIcon(Context context) throws PackageManager.NameNotFoundException {
            return context.getPackageManager().getApplicationIcon(this.name);
        }

        @Override
        public String getLabel(Context context) throws PackageManager.NameNotFoundException {
            PackageManager pm = context.getPackageManager();
            return (String) pm.getApplicationLabel(pm.getApplicationInfo(this.name, 0));
        }

        private void launch(Context context) {
            try {
                context.startActivity(context.getPackageManager().getLaunchIntentForPackage(this.name));
            } catch (ActivityNotFoundException err) {
                Toast t = Toast.makeText(context,
                        R.string.app_not_found, Toast.LENGTH_SHORT);
                t.show();
            }
        }

        @Override
        public void setButton(Context context, Button btn) {
            try {
                btn.setText(this.getLabel(context));
            } catch (PackageManager.NameNotFoundException e) {
                Log.d(TAG, e.toString());
            }

            btn.setOnClickListener(v -> this.launch(context));

            btn.setOnLongClickListener(v -> {
                Intent intent = new Intent(context, ScrollingActivity.class);
                intent.putExtra("Button ID", btn.getId());
                context.startActivity(intent);
                return false;
            });
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    public static class EmptyApp extends AppInfo implements Serializable {
        private EmptyApp() { }

        @Override
        public Drawable getIcon(Context context) {
            return null;
        }

        @Override
        public String getLabel(Context context) {
            return null;
        }

        @Override
        public void setButton(Context context, Button btn) {
            btn.setOnClickListener(v -> {
                Toast.makeText(context, "Empty Button! Hold to choose app", Toast.LENGTH_SHORT).show();
            });

            btn.setOnLongClickListener(v -> {
                Intent intent = new Intent(context, ScrollingActivity.class);
                intent.putExtra("Button ID", btn.getId());
                context.startActivity(intent);
                return false;
            });

            btn.setText("empty button");
        }

        @Override
        public String getName() {
            return EMPTY;
        }
    }

    private static final AppInfo NONE = new EmptyApp();

    public static AppInfo none() {
        return NONE;
    }

    public static AppInfo some(ApplicationInfo applicationInfo) {
        return new SomeAppInfo(applicationInfo);
    }

    public static AppInfo of(String name) {
        if (name.equals(EMPTY)) {
            return none();
        } else {
            return new SomeAppInfo(name);
        }
    }

    public abstract Drawable getIcon(Context context) throws PackageManager.NameNotFoundException;

    public abstract String getLabel(Context context) throws PackageManager.NameNotFoundException;

    public abstract void setButton(Context context, Button btn);

    public abstract String getName();
}
