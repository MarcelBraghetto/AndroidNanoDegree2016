package com.lilarcor.popularmovies.framework.foundation.device.contracts;

/**
 * Created by Marcel Braghetto on 27/07/15.
 *
 * Collection of helper methods to obtain information about
 * the current device.
 */
public interface DeviceInfoProvider {
    /**
     * Return the current width of the active
     * window.
     *
     * @return the width in pixels of the current window.
     */
    int getCurrentWindowWidth();

    /**
     * Determine if this device is considered 'large' based
     * on the overridden resource properties - in particular
     * the 'is_large_device' boolean value.
     *
     * @return whether this is a 'large device'.
     */
    boolean isLargeDevice();
}
