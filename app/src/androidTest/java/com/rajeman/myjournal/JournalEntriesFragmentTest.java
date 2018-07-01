package com.rajeman.myjournal;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import com.rajeman.myjournal.view.MainActivity;
import android.support.test.espresso.Espresso;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class JournalEntriesFragmentTest {

    @Rule public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    //The below tests will only run after the user has authenticated as the stated views are only available after sign-in

    @Test
    public  void clickFloatingActionButton_displaysAddEntryFragment(){
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.item_add_scroll_view)).check(matches(isDisplayed()));
    }

    @Test
  public  void clickEntryItem_displaysViewEntryFragment(){
          onView(withId(R.id.entries_recycler_view)).perform(click());
          onView(withId(R.id.item_view_scroll_view)).check(matches(isDisplayed()));
    }


}
