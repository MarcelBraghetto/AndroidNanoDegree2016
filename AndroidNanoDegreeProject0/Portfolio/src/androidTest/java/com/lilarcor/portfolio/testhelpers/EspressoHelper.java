package com.lilarcor.portfolio.testhelpers;

import android.os.IBinder;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.test.espresso.Root;
import android.view.WindowManager;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Marcel Braghetto on 11/07/15.
 *
 * Colletion of utility methods to assist with calling common Espresso commands.
 */
public class EspressoHelper {
    private EspressoHelper() { }

    public static void verifyTextWithId(@IdRes int resourceId, @NonNull String text) {
        onView(withId(resourceId)).check(matches(withText(text)));
    }

    public static void clickViewWithId(@IdRes int resourceId) {
        onView(withId(resourceId)).perform(click());
    }

    public static void verifyToastMessage(@NonNull String message) {
        onView(withText(message)).inRoot(isToast());
    }

    /**
     * Toast matcher code sourced from blog post at:
     *
     * http://baroqueworksdev.blogspot.co.nz/
     *
     * @return whether the matcher is the toast window.
     */
    private static Matcher<Root> isToast() {
        return new TypeSafeMatcher<Root>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("is toast");
            }

            @Override
            public boolean matchesSafely(Root root) {
                int type = root.getWindowLayoutParams().get().type;
                if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
                    IBinder windowToken = root.getDecorView().getWindowToken();
                    IBinder appToken = root.getDecorView().getApplicationWindowToken();
                    if(windowToken == appToken) {
                        // windowToken == appToken means this window isn't contained by any other windows.
                        // if it was a window for an activity, it would have TYPE_BASE_APPLICATION.
                        return true;
                    }
                }
                return false;
            }
        };
    }
}
