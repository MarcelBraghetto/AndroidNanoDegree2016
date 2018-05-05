package com.lilarcor.popularmovies.framework.ui;

import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;

import com.lilarcor.popularmovies.features.application.MainApp;

/**
 * Created by Marcel Braghetto on 19/07/15.
 *
 * Collection of helper methods for common tasks that
 * are performed on Android view objects.
 */
public final class ViewUtils {
    private ViewUtils() { }

    /**
     * Override the colour of an indeterminate progress bar.
     *
     * @param progressBar instance to apply the colour to.
     * @param colourId of the colour resource id to apply.
     */
    public static void setProgressBarColour(@NonNull ProgressBar progressBar, @ColorRes int colourId) {
        progressBar.getIndeterminateDrawable().setColorFilter(MainApp.getDagger().getApplicationContext().getResources().getColor(colourId), PorterDuff.Mode.SRC_IN);
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
