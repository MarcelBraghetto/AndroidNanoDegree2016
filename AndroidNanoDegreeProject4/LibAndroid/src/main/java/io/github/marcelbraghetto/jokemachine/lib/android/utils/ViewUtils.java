package io.github.marcelbraghetto.jokemachine.lib.android.utils;

import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Marcel Braghetto on 18/01/16.
 *
 * Collection of view utilities.
 */
public final class ViewUtils {
    private ViewUtils() { }
    /**
     * Given an activitie's window, apply a translucent status bar if the device is running
     * Lollipop or later.
     * @param window to apply the effect to.
     */
    public static void applyTranslucentStatusBar(@NonNull Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
