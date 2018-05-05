package io.github.marcelbraghetto.dailydeviations.framework.foundation.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.Toast;

/**
 * Created by Marcel Braghetto on 16/06/16.
 *
 * Utility to help with displaying toast messages.
 */
public class ToastUtils {
    private ToastUtils() { }

    private static Toast sToast;

    /**
     * Display a toast with the given message. Use this method to ensure the Toast is
     * displayed from the UI thread (can be called from any thread).
     * @param context to use.
     * @param message to display.
     * @param toastLength Toast.LENGTH_SHORT or Toast.LENGTH_LONG
     */
    public static void showToast(@NonNull final Context context, @NonNull final String message, final int toastLength) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(sToast != null) {
                    sToast.cancel();
                }

                // Make sure we only choose from valid toast lengths.
                int length = Toast.LENGTH_SHORT;
                if(toastLength == Toast.LENGTH_LONG) {
                    length = Toast.LENGTH_LONG;
                }

                sToast = Toast.makeText(context.getApplicationContext(), message, length);
                sToast.show();
            }
        });
    }
}
