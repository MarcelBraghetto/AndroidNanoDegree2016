package io.github.marcelbraghetto.deviantartreader.framework.foundation.device.contracts;

/**
 * Created by Marcel Braghetto on 02/12/15.
 *
 * Collection of helper methods to obtain information about the current device.
 */
public interface DeviceProvider {
    /**
     * Determine if this device is considered 'large' based on the overridden resource properties
     * - in particular the 'is_large_device' boolean value.
     * @return whether this is a 'large device'.
     */
    boolean isLargeDevice();

    /**
     * Determine if the device is running at least ICS.
     * @return true if the device is >= ICS.
     */
    boolean isAtLeastIceCreamSandwich();

    /**
     * Determine if the device is running at least Lollipop
     * @return true if the device >= Lollipop
     */
    boolean isAtLeastLollipop();

    /**
     * Determine whether the device is currently being displayed in portrait orientation.
     * @return true if device is in portrait.
     */
    boolean isPortrait();

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