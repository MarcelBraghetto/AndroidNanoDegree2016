package com.lilarcor.popularmovies.framework.ui;

import android.graphics.PorterDuff;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
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
}
