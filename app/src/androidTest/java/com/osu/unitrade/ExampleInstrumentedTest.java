package com.osu.unitrade;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.os.IBinder;
import android.view.WindowManager;

import androidx.test.espresso.Root;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.assertion.ViewAssertions.*;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

import com.osu.unitrade.activity.MainActivity;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void registerWithWrongEmail() {
        onView(withId(R.id.register)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.register)).perform(click());
        onView(withId(R.id.register_editTextEmailAddress)).perform(typeText("test@gmail.com"));
        onView(withId(R.id.register_editTextPassword)).perform(typeText("123456"));
        onView(withId(R.id.register_editTextNickname)).perform(typeText("Test"));
        onView(withId(R.id.registerSubmit)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.registerSubmit)).perform(click());
        onView(withId(R.id.register_editTextEmailAddress)).check(ViewAssertions.matches(hasErrorText("Please enter a valid OSU email.")));
    }

    @Test
    public void loginSuccess() throws InterruptedException {
        onView(withId(R.id.login)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.login_editTextEmailAddress)).perform(typeText("chen.8095@osu.edu"));
        onView(withId(R.id.login_editTextPassword)).perform(typeText("123456"));
        onView(withId(R.id.login_loginButton)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.login_loginButton)).perform(click());
        Thread.sleep(1500);
        onView(withId(R.id.allListing)).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void loginFail() {
        onView(withId(R.id.login)).check(ViewAssertions.matches(isDisplayed()));
        onView(withId(R.id.login)).perform(click());
        onView(withId(R.id.login_editTextEmailAddress)).perform(typeText("chen.8095@osu.edu"));
        onView(withId(R.id.login_editTextPassword)).perform(typeText("1234567"));
        onView(withId(R.id.login_loginButton)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.login_loginButton)).perform(click());
        onView(withId(R.id.login_loginButton)).check(ViewAssertions.matches(isDisplayed()));
    }


}