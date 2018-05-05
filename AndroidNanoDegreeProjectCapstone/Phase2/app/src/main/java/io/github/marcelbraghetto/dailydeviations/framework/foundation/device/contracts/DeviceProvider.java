package io.github.marcelbraghetto.dailydeviations.framework.foundation.device.contracts;

/**
 * Created by Marcel Braghetto on 02/12/15.
 *
 * Collection of helper methods to obtain information about the current device.
 */
public interface DeviceProvider {
    /**
     * Determine if the device is running at least Lollipop
     * @return true if the device >= Lollipop
     */
    boolean isAtLeastLollipop();

    /**
     * Return the current width of the active
     * window.
     *
     * @return the width in pixels of the current window.
     */
    int getCurrentWindowWidth();

    /**
     * Return the current height of the active window.
     *
     * @return the height in pixels of the current window.
     */
    int getCurrentWindowHeight();
}