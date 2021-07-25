package com.example.myapplication;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

@RunWith(AndroidJUnit4.class)
public class AuthenticationTest {

    UiDevice device;

    @Rule
    public ActivityTestRule<LoginActivity> activityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void setup() {
        this.device = UiDevice.getInstance(getInstrumentation());
    }

    // Users are able to sign in with their Google Account, check their information, and then sign out
    @Test
    public void test1() throws UiObjectNotFoundException {
        onView(withId(R.id.sign_in_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_in_btn)).perform(click());
        UiObject name = device.findObject(new UiSelector().text("Manh Duc Hoang"));
        name.click();
        onView(withId(R.id.ConCat)).check(matches(isDisplayed()));
        onView(withId(R.id.accountBtn)).perform(click());
        onView(withId(R.id.profile_image)).check(matches(isDisplayed()));
        onView(withId(R.id.email_txt)).check(matches(isDisplayed()));
        onView(withId(R.id.name_txt)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_out_btn)).perform(click());
        onView(withId(R.id.sign_in_btn)).check(matches(isDisplayed()));
        onView(withId(R.id.sign_in_btn)).perform(click());
        UiObject name1 = device.findObject(new UiSelector().text("Manh Duc Hoang"));
        name1.click();
        onView(withId(R.id.ConCat)).check(matches(isDisplayed()));
    }
}
