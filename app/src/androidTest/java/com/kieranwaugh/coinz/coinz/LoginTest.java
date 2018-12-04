package com.kieranwaugh.coinz.coinz;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
//import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import com.kieranwaugh.coinz.coinz.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

//@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void loginTest() {
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
try {
 Thread.sleep(500);
 } catch (InterruptedException e) {
 e.printStackTrace();
 }

        ViewInteraction constraintLayout = onView(
allOf(withId(R.id.contentSpace),
childAtPosition(
allOf(withId(android.R.id.content),
childAtPosition(
withId(R.id.action_bar_root),
1)),
0),
isDisplayed()));
        constraintLayout.perform(click());

         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
try {
 Thread.sleep(500);
 } catch (InterruptedException e) {
 e.printStackTrace();
 }

        ViewInteraction appCompatEditText = onView(
allOf(withId(R.id.email),
childAtPosition(
childAtPosition(
withId(R.id.textInputLayout),
0),
0),
isDisplayed()));
        appCompatEditText.perform(replaceText("kie"), closeSoftKeyboard());

         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
try {
 Thread.sleep(500);
 } catch (InterruptedException e) {
 e.printStackTrace();
 }

        ViewInteraction appCompatEditText2 = onView(
allOf(withId(R.id.email), withText("kie"),
childAtPosition(
childAtPosition(
withId(R.id.textInputLayout),
0),
0),
isDisplayed()));
        appCompatEditText2.perform(replaceText("kieran"));

        ViewInteraction appCompatEditText3 = onView(
allOf(withId(R.id.email), withText("kieran"),
childAtPosition(
childAtPosition(
withId(R.id.textInputLayout),
0),
0),
isDisplayed()));
        appCompatEditText3.perform(closeSoftKeyboard());

         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
try {
 Thread.sleep(500);
 } catch (InterruptedException e) {
 e.printStackTrace();
 }

        ViewInteraction appCompatEditText4 = onView(
allOf(withId(R.id.email), withText("kieran"),
childAtPosition(
childAtPosition(
withId(R.id.textInputLayout),
0),
0),
isDisplayed()));
        appCompatEditText4.perform(replaceText("kieran.waugh@sky.com"));

        ViewInteraction appCompatEditText5 = onView(
allOf(withId(R.id.email), withText("kieran.waugh@sky.com"),
childAtPosition(
childAtPosition(
withId(R.id.textInputLayout),
0),
0),
isDisplayed()));
        appCompatEditText5.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText6 = onView(
allOf(withId(R.id.password),
childAtPosition(
childAtPosition(
withId(R.id.textInputLayout2),
0),
0),
isDisplayed()));
        appCompatEditText6.perform(replaceText("12345678"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
allOf(withId(R.id.btn_login), withText("LOGIN"),
childAtPosition(
childAtPosition(
withId(android.R.id.content),
0),
3),
isDisplayed()));
        appCompatButton.perform(click());

         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
try {
 Thread.sleep(500);
 } catch (InterruptedException e) {
 e.printStackTrace();
 }

        ViewInteraction frameLayout = onView(
allOf(withId(R.id.mapboxMapView), withContentDescription("Showing a Map created with Mapbox. Scroll by dragging two fingers. Zoom by pinching two fingers."),
childAtPosition(
allOf(withId(R.id.ConstraintLayout),
childAtPosition(
withId(android.R.id.content),
0)),
0),
isDisplayed()));
        frameLayout.check(matches(isDisplayed()));

        ViewInteraction bottomNavigationItemView = onView(
allOf(withId(R.id.navigation_stats),
childAtPosition(
childAtPosition(
withId(R.id.navigation),
0),
0),
isDisplayed()));
        bottomNavigationItemView.perform(click());

         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
try {
 Thread.sleep(500);
 } catch (InterruptedException e) {
 e.printStackTrace();
 }

        ViewInteraction textView = onView(
allOf(withId(R.id.nameView), withText("Kieran Waugh"),
childAtPosition(
childAtPosition(
withId(android.R.id.content),
0),
2),
isDisplayed()));
        textView.check(matches(withText("Kieran Waugh")));

        }

        private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup)parent).getChildAt(position));
            }
        };
    }
    }
