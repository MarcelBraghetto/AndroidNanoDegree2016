package io.github.marcelbraghetto.deviantartreader.framework.foundation.device;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import io.github.marcelbraghetto.deviantartreader.R;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.device.contracts.DeviceProvider;

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
    public boolean isLargeDevice() {
        return mApplicationContext.getResources().getBoolean(R.bool.is_large_device);
    }

    @Override
    public boolean isAtLeastIceCreamSandwich() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    @Override
    public boolean isAtLeastLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @Override
    public boolean isPortrait() {
        int orientation = mApplicationContext.getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT || !(orientation == Configuration.ORIENTATION_LANDSCAPE);
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
