package io.github.marcelbraghetto.alexandria.framework.providers.device;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import io.github.marcelbraghetto.alexandria.R;
import io.github.marcelbraghetto.alexandria.framework.providers.device.contracts.DeviceUtilsProvider;

/**
 * Created by Marcel Braghetto on 02/12/15.
 *
 * Implementation of the device utils provider contract.
 */
public class DefaultDeviceUtilsProvider implements DeviceUtilsProvider {
    private Context mApplicationContext;

    public DefaultDeviceUtilsProvider(@NonNull Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    @Override
    public boolean isLargeDevice() {
        return mApplicationContext.getResources().getBoolean(R.bool.is_large_device);
    }

    @Override
    public boolean hasBackCamera() {
        return mApplicationContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public boolean isAtLeastIceCreamSandwich() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    @NonNull
    @Override
    public Point getWindowSizePx() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) mApplicationContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);

        return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    @Override
    public boolean isPortrait() {
        int orientation = mApplicationContext.getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT || !(orientation == Configuration.ORIENTATION_LANDSCAPE);
    }
}
