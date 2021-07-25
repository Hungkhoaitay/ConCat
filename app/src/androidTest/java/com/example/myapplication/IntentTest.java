package com.example.myapplication;

import android.app.Activity;
import android.app.Instrumentation;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;


@RunWith(AndroidJUnit4.class)
public class IntentTest {

    @Rule
    public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class);
    @Ignore
    @Test
    public void test1() {
        intending(not(isInternal())).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
        onView(withId(R.id.firstButton)).perform(click());
        intended(allOf(toPackage("com.google.android.youtube")));
        intended(not(allOf(toPackage("sg.ndi.sp"))));
        onView(withId(R.id.secondButton)).perform(click());
        intended(allOf(toPackage("org.telegram.messenger")));
        onView(withId(R.id.thirdButton)).perform(click());
        intended(allOf(toPackage("com.whatsapp")));
        onView(withId(R.id.fourthButton)).perform(click());
        intended(allOf(toPackage("com.reddit.frontpage")));
        onView(withId(R.id.fifthButton)).perform(click());
        intended(allOf(toPackage("fr.playsoft.vnexpress")));
        onView(withId(R.id.sixthButton)).perform(click());
        intended(allOf(toPackage("com.android.settings")));
    }
}
