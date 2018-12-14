package com.kieranwaugh.coinz.coinz;


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class IncorrectLoginTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void incorrectLoginTest() {
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
                                                allOf(withId(R.id.action_bar_root),
                                                        childAtPosition(
                                                                childAtPosition(
                                                                        withClassName(is("android.widget.LinearLayout")),
                                                                        1),
                                                                0)),
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
                                        allOf(withId(R.id.textInputLayout),
                                                childAtPosition(
                                                        childAtPosition(
                                                                allOf(withId(android.R.id.content),
                                                                        childAtPosition(
                                                                                allOf(withId(R.id.action_bar_root),
                                                                                        childAtPosition(
                                                                                                childAtPosition(
                                                                                                        withClassName(is("android.widget.LinearLayout")),
                                                                                                        1),
                                                                                                0)),
                                                                                1)),
                                                                0),
                                                        1)),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("1"), closeSoftKeyboard());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.email), withText("1"),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(R.id.textInputLayout),
                                                childAtPosition(
                                                        childAtPosition(
                                                                allOf(withId(android.R.id.content),
                                                                        childAtPosition(
                                                                                allOf(withId(R.id.action_bar_root),
                                                                                        childAtPosition(
                                                                                                childAtPosition(
                                                                                                        withClassName(is("android.widget.LinearLayout")),
                                                                                                        1),
                                                                                                0)),
                                                                                1)),
                                                                0),
                                                        1)),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("1@2.1"));

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.email), withText("1@2.1"),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(R.id.textInputLayout),
                                                childAtPosition(
                                                        childAtPosition(
                                                                allOf(withId(android.R.id.content),
                                                                        childAtPosition(
                                                                                allOf(withId(R.id.action_bar_root),
                                                                                        childAtPosition(
                                                                                                childAtPosition(
                                                                                                        withClassName(is("android.widget.LinearLayout")),
                                                                                                        1),
                                                                                                0)),
                                                                                1)),
                                                                0),
                                                        1)),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText3.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.password),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(R.id.textInputLayout2),
                                                childAtPosition(
                                                        childAtPosition(
                                                                allOf(withId(android.R.id.content),
                                                                        childAtPosition(
                                                                                allOf(withId(R.id.action_bar_root),
                                                                                        childAtPosition(
                                                                                                childAtPosition(
                                                                                                        withClassName(is("android.widget.LinearLayout")),
                                                                                                        1),
                                                                                                0)),
                                                                                1)),
                                                                0),
                                                        2)),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("123456"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_login), withText("LOGIN"),
                        childAtPosition(
                                childAtPosition(
                                        allOf(withId(android.R.id.content),
                                                childAtPosition(
                                                        allOf(withId(R.id.action_bar_root),
                                                                childAtPosition(
                                                                        childAtPosition(
                                                                                withClassName(is("android.widget.LinearLayout")),
                                                                                1),
                                                                        0)),
                                                        1)),
                                        0),
                                3),
                        isDisplayed()));
        appCompatButton.perform(click());

        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(allOf(withId(android.support.design.R.id.snackbar_text), withText("Incorrect email or password, try again or sign up below.")))
                .check(matches(isDisplayed()));

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
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
