package io.github.marcelbraghetto.football.framework.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Marcel Braghetto on 3/12/15.
 *
 * Collection of view helper methods.
 */
public class ViewUtils {
    private ViewUtils() { }

    /**
     * Dismiss the keyboard for the given activity.
     * @param activity to dismiss associated keyboard for.
     */
    public static void hideKeyboard(@NonNull Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View focus = activity.getCurrentFocus();
        if (focus != null) {
            inputManager.hideSoftInputFromWindow(focus.getWindowToken(), 0);
        }
    }

    /**
     * Given a view and an existing listener, remove the listener from that
     * view's view tree layout observer.
     *
     * @param view to remove the listener from.
     * @param listener to remove.
     */
    public static void removeOnGlobalLayoutListener(@NonNull View view, @NonNull ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
        } else {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        }
    }
}
