package com.example.myapplication;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.filters.SmallTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.uiautomator.UiDevice;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Instrumented Unit Test in combination with GUI test for Scrolling Activity:
 * Test 1: Asserting that all required views are there, recycler view and app bar search
 * Test case passes
 * Test 2: Asserting that user can scroll to different view and element in recycler view
 * list elements, with examples, and can click on them. Test case passes
 * Test 3: Asserting that ConCat can access all launch-able package on any user phone, and
 * can convert all of them into recycler view for scrolling purpose. To do this, we manually
 * convert all application to a list externally, and test whether our application can create
 * the same list that matches the externally-created list, and perform scrollTo() action on
 * each one of them. Test case passes.
 * Test 4: Search engine and filter test, asserting whether user can access the search function
 * and filter out only necessary apps. We use 3 cases: with prefix "You", "Hjk" and "Ga", and
 * asserting whether all applications with those prefix are there, and that recycler view only
 * show the number of applications.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ScrollingActivityTest {

    /**
     * Test cases:
     * Inclusive of Integration Testing
     */
    @Rule
    public ActivityTestRule<ScrollingActivity> activityTestRule = new ActivityTestRule<>(ScrollingActivity.class);

    UiDevice device;
    @BeforeAll
    public void setup() {
        this.device = UiDevice.getInstance(getInstrumentation());
        File root = InstrumentationRegistry.getTargetContext().getFilesDir().getParentFile();
        String[] sharedPreferencesFileNames = new File(root, "shared_prefs").list();
        for (String fileName : sharedPreferencesFileNames) {
            InstrumentationRegistry.getTargetContext().
                    getSharedPreferences(fileName.replace(".xml", ""), Context.MODE_PRIVATE)
                    .edit().clear().commit();
        }
        activityTestRule.launchActivity(null);
    }



    // Test integration with MainActivity
    @Test
    public void test1() {
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
        onView(withId(R.id.app_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.applicationRecyclerView)).check(matches(isDisplayed()));
        onView(withId(R.id.applicationRecyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.ConCat)).check(matches(isDisplayed()));
    }

    // Test item finding and clicking
    @Test
    public void test2() throws PackageManager.NameNotFoundException {
        PackageManager packageManager = activityTestRule.getActivity().getPackageManager();
        onView(withId(R.id.applicationRecyclerView)).check(matches(isDisplayed()));
        onView(withId(R.id.applicationRecyclerView))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(
                        withText("Reddit")
                )));
        onView(withId(R.id.applicationRecyclerView))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(
                        withText("VnExpress")
                )));
        onView(withId(R.id.applicationRecyclerView))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(
                        withText("YouTube")
                ), click()));
        onView(withId(R.id.ConCat)).check(matches(isDisplayed()));
        onView(withId(R.id.firstButton)).perform(longClick());
        onView(withId(R.id.applicationRecyclerView)).check(matches(isDisplayed()));
        onView(withId(R.id.applicationRecyclerView))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(
                        withText("Google")
                ), click()));

    }


    // Test search engine and filter service
    @Test
    public void test3() {
        onView(withId(R.id.applicationRecyclerView)).check(matches(isDisplayed()));
        onView(withId(R.id.action_settings)).check(matches(isDisplayed()));
        onView(withId(R.id.action_settings)).perform(click());

        RecyclerView recyclerView = activityTestRule.getActivity().findViewById(R.id.applicationRecyclerView);

        onView(withContentDescription("Search")).perform(click());
        onView(withId(Resources.getSystem()
                .getIdentifier("search_src_text", "id", "android")))
                .perform(typeText("You"), closeSoftKeyboard());
        onView(withId(R.id.applicationRecyclerView)).check(matches(isDisplayed()));
        assertEquals(2, recyclerView.getAdapter().getItemCount());
        onView(withText("YouTube")).check(matches(isDisplayed()));
        onView(withText("YouTube Music")).check(matches(isDisplayed()));
        onView(withText("Facebook")).check(doesNotExist());
        onView(withText("VnExpress")).check(doesNotExist());
        onView(withId(Resources.getSystem()
                .getIdentifier("search_src_text", "id", "android")))
                .perform(clearText(), typeText("Hjk"), closeSoftKeyboard());
        assertEquals(0, recyclerView.getAdapter().getItemCount());
        onView(withId(Resources.getSystem()
                .getIdentifier("search_src_text", "id", "android")))
                .perform(clearText(), typeText("Ga"), closeSoftKeyboard());
        assertEquals(5, recyclerView.getAdapter().getItemCount());
        assertNotEquals(69, recyclerView.getAdapter().getItemCount());
        onView(withText("Galaxy Store")).check(matches(isDisplayed()));
        onView(withText("Game Launcher")).check(matches(isDisplayed()));
        onView(withText("Galaxy Wearable")).check(matches(isDisplayed()));
        onView(withText("Gallery")).check(matches(isDisplayed()));
        onView(withText("Galaxy Shop")).check(matches(isDisplayed()));
        onView(withText("Youtube")).check(doesNotExist());
        onView(withText("Reddit")).check(doesNotExist());
        onView(withText("Telegram")).check(doesNotExist());
        onView(withText("Google")).check(doesNotExist());
        onView(withText("Gallery")).perform(click());
        onView(withId(R.id.ConCat)).check(matches(isDisplayed()));
    }

    // System is able to access all packages on user phone

    @Test
    public void test4() throws NoMatchingViewException {
        onView(withId(R.id.applicationRecyclerView)).check(matches(isDisplayed()));
        PackageManager packageManager = activityTestRule.getActivity().getPackageManager();

        List<AppInfo> appLists = packageManager
                .getInstalledApplications(PackageManager.GET_META_DATA)
                .stream()
                .filter(packageInfo -> packageManager.getLaunchIntentForPackage(packageInfo.packageName) != null)
                .map(packageInfo -> AppInfo.some(packageInfo))
                .collect(Collectors.toList());

        onView(withId(R.id.applicationRecyclerView)).check(matches(isDisplayed()));
        RecyclerView recyclerView = activityTestRule.getActivity().findViewById(R.id.applicationRecyclerView);
        assertEquals(appLists.size(), recyclerView.getAdapter().getItemCount());
        for (AppInfo appInfo : appLists) {
            onView(withId(R.id.applicationRecyclerView))
                    .perform(RecyclerViewActions.scrollTo((
                            withText(appInfo.getLabel(activityTestRule.getActivity()))
                    )));
        }
    }


    @After
    public void afterTest() {
        activityTestRule.finishActivity();

    }

    public static ViewAction typeSearchViewText(final String text){
        return new ViewAction(){
            @Override
            public Matcher<View> getConstraints() {
                //Ensure that only apply if it is a SearchView and if it is visible.
                return allOf(isDisplayed(), isAssignableFrom(SearchView.class));
            }

            @Override
            public String getDescription() {
                return "Change view text";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((SearchView) view).setQuery(text,false);
            }
        };
    }
}
