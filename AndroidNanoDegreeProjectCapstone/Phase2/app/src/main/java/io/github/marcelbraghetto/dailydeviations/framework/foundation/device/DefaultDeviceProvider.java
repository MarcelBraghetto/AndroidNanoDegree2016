package io.github.marcelbraghetto.dailydeviations.framework.foundation.device;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import io.github.marcelbraghetto.dailydeviations.framework.foundation.device.contracts.DeviceProvider;

/**
 * Created by Marcel Braghetto on 02/12/15.
 *
 * Implementation of the device utils provider contract.
 */
public class DefaultDeviceProvider implements DeviceProvider {
    private Context mApplicationContext;

    public DefaultDeviceProvider(@NonNull Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    @Override
    public boolean isAtLeastLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @Override
    public int getCurrentWindowWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) mApplicationContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    @Override
    public int getCurrentWindowHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) mApplicationContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }
}
