package com.example.myapplication;


import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.example.myapplication.DrawableMatchers.withBackground;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
public class TestChangeIcon {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    UiDevice device;
    @Before
    public void setup() {
        this.device = UiDevice.getInstance(getInstrumentation());
    }
    @Test
    public void test1() {
        onView(withId(R.id.customizeBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.customizeBtn)).perform(click());
        onView(withId(R.id.cancel_selection)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel_selection)).perform(click());
        onView(withId(R.id.customizeBtn)).perform(click());
        onView(withId(R.id.choose_icon_recycler_view)).check(matches(isDisplayed()));
        onView(withId(R.id.default_view)).perform(click());
        onView(withId(R.id.customizeBtn)).check(matches(isDisplayed()));
        onView(withId(R.id.ConCat)).perform(click());
        onView(withContentDescription("Floating Widget"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView())))).check(matches(isDisplayed()));
        onView(withBackground(R.drawable.concat))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                .getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        onView(withBackground(R.drawable.concat))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                .getWindow().getDecorView()))))
                .perform(click());
        onView(withContentDescription("Return to App"))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                .getWindow().getDecorView()))))
                .perform(click());
        onView(withId(R.id.customizeBtn)).perform(click());
        onView(withId(R.id.choose_icon_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(6, click()));
        onView(withId(R.id.ConCat)).perform(click());
        onView(withBackground(R.drawable.communism))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView()))))
                .perform(click());
        onView(withId(R.id.returnToApp))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView()))))
                .perform(click());
        onView(withId(R.id.customizeBtn)).perform(click());
        onView(withId(R.id.choose_icon_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.ConCat)).perform(click());
        onView(withBackground(R.drawable.shiba))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView()))))
                .check(doesNotExist());
        onView(withBackground(R.drawable.rick_sama))
                .inRoot(withDecorView(not(is(activityTestRule.getActivity()
                        .getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }
}
