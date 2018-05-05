package io.github.marcelbraghetto.jokemachine.lib.android.utils;

import android.graphics.Typeface;
import android.support.annotation.NonNull;

import io.github.marcelbraghetto.jokemachine.lib.android.MainApplication;

/**
 * Created by Marcel Braghetto on 19/01/16.
 *
 * Font utility to provide access to the custom app font typeface via a memory cache.
 */
public enum FontUtils {
    Instance;

    private Typeface mAppTypeface;

    /**
     * Create a single instance of the custom font which can be applied to UI widget text views.
     */
    FontUtils() {
        mAppTypeface = Typeface.createFromAsset(MainApplication.getContext().getAssets(), "custom_font.ttf");
    }

    /**
     * Get the application's custom font typeface that can be applied to text views etc.
     * @return app font typeface.
     */
    @NonNull
    public Typeface getAppTypeface() {
        return mAppTypeface;
    }
}
