package com.example.myapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.Resource;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ImageMatcher extends TypeSafeMatcher<View> {

    private final Drawable expectedImage;

    public ImageMatcher(Drawable expectedImage) {
        super(View.class);
        this.expectedImage = expectedImage;
    }

    @Override
    protected boolean matchesSafely(View item) {
        if (!(item instanceof ImageView) || expectedImage == null) {
            return false;
        }
        ImageView imageView = (ImageView) item;
        Drawable temp = imageView.getDrawable();

        Bitmap bitmap = ((BitmapDrawable) temp).getBitmap();
        Bitmap otherBitmap = ((BitmapDrawable) expectedImage).getBitmap();
        return bitmap.sameAs(otherBitmap);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("with drawable file: ");
        if (expectedImage == null) {
            return;
        }
        description.appendText(expectedImage.toString());
    }
}
