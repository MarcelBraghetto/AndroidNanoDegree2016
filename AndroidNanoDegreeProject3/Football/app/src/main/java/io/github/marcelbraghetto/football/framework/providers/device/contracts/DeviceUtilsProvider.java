package io.github.marcelbraghetto.football.framework.providers.device.contracts;

import android.graphics.Point;
import android.support.annotation.NonNull;

/**
 * Created by Marcel Braghetto on 02/12/15.
 *
 * Collection of helper methods to obtain information about
 * the current device.
 */
public interface DeviceUtilsProvider {
    /**
     * Determine if this device is considered 'large' based
     * on the overridden resource properties - in particular
     * the 'is_large_device' boolean value.
     *
     * @return whether this is a 'large device'.
     */
    boolean isLargeDevice();

    /**
     * Determine if the device has a back camera.
     * @return true if a back camera is available.
     */
    boolean hasBackCamera();

    /**
     * Determine if the device is running at
     * least ICS.
     * @return true if the device is >= ICS.
     */
    boolean isAtLeastIceCreamSandwich();

    /**
     * Get the current window size and return it
     * as a point, x = width, y = height.
     * @return point containing width (x)
     * and height (y). Result is expressed in
     * pixels.
     */
    @NonNull Point getWindowSizePx();

    /**
     * Determine whether the device is currently
     * being displayed in portrait orientation.
     * @return true if device is in portrait.
     */
    boolean isPortrait();
}