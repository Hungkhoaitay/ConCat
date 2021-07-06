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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.stream.Collectors;

import static android.content.ContentValues.TAG;

/**
 * Class contains the necessary information about the app (icon, intent, label,...)
 */
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
        public Drawable getIcon(Context context) {
            try {
                return context.getPackageManager().getApplicationIcon(this.name);
            } catch (PackageManager.NameNotFoundException e) {
                return null;
            }

        }

        @Override
        public String getLabel(Context context) {
            PackageManager pm = context.getPackageManager();
            try {
                return (String) pm.getApplicationLabel(pm.getApplicationInfo(this.name, 0));
            } catch (PackageManager.NameNotFoundException e) {
                return null;
            }
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
        public void setButton(Context context, ImageView btn) {
            btn.setBackground(this.getIcon(context));

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
        public void setButton(Context context, ImageView btn) {
            btn.setOnClickListener(v -> {
                Toast.makeText(context, "Empty Button! Hold to choose app", Toast.LENGTH_LONG).show();
            });

            btn.setOnLongClickListener(v -> {
                Intent intent = new Intent(context, ScrollingActivity.class);
                intent.putExtra("Button ID", btn.getId());
                context.startActivity(intent);
                return false;
            });

            btn.setBackgroundResource(R.drawable.concat);
        }

        @Override
        public String getName() {
            return EMPTY;
        }
    }

    private static final AppInfo NONE = new EmptyApp();

    /**
     *
     * @return EMPTY_APP
     */
    public static AppInfo none() {
        return NONE;
    }

    /**
     *
     * @param applicationInfo
     * @return SomeAppInfo
     */
    public static AppInfo some(ApplicationInfo applicationInfo) {
        return new SomeAppInfo(applicationInfo);
    }

    /**
     *
     * @param name
     * @return SomeAppInfo if the name is not EMPTY, EMPTY_APP otherwise
     */
    public static AppInfo of(String name) {
        if (name.equals(EMPTY)) {
            return none();
        } else {
            return new SomeAppInfo(name);
        }
    }

    /**
     *
     * @param context
     * @return the icon of the app
     * @throws PackageManager.NameNotFoundException
     */
    public abstract Drawable getIcon(Context context);

    /**
     *
     * @param context
     * @return the label of the app
     * @throws PackageManager.NameNotFoundException
     */
    public abstract String getLabel(Context context);

    /**
     * method set the icon, name, onClickListener, onLongClickListener of the button
     *
     * @param context
     * @param btn
     */
    public abstract void setButton(Context context, ImageView btn);

    /**
     *
     * @return the name of the app
     */
    public abstract String getName();
}
