package com.lilarcor.popularmovies.framework.foundation.device;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.framework.foundation.device.contracts.DeviceInfoProvider;

/**
 * Created by Marcel Braghetto on 27/07/15.
 *
 * Implementation of the device info provider contract.
 */
public class DefaultDeviceInfoProvider implements DeviceInfoProvider {
    private Context mApplicationContext;

    public DefaultDeviceInfoProvider(@NonNull Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    @Override
    public int getCurrentWindowWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) mApplicationContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.widthPixels;
    }

    @Override
    public boolean isLargeDevice() {
        return mApplicationContext.getResources().getBoolean(R.bool.is_large_device);
    }
}
