package io.github.marcelbraghetto.dailydeviations.features.settings.logic.providers.contracts;

/**
 * Created by Marcel Braghetto on 6/06/16.
 *
 * Settings provider to give a single route to checking and manipulating the app's configuration.
 */
public interface SettingsProvider {
    /**
     * Enable the background data option to fetch daily deviations periodically.
     */
    void enableBackgroundData();

    /**
     * Disable the background data option to fetch daily deviations periodically.
     */
    void disableBackgroundDate();

    /**
     * Determine whether background data is enabled.
     * @return true if background data is enabled.
     */
    boolean isBackgroundDataEnabled();

    /**
     * Enable the local notification system used to notify users when there is new data.
     */
    void enableNotifications();

    /**
     * Disable the local notification system used to notify users when there is new data.
     */
    void disableNotifications();

    /**
     * Determine whether local notifications are enabled.
     * @return true if local notifications are enabled - note this will be overridden by
     * the Android system settings regarding notifications for the app.
     */
    boolean isNotificationsEnabled();

    /**
     * Enable the automatic header image selection when the app starts up - where an
     * image is chosen at random from the content provider and displayed in the nav menu.
     */
    void enableAutomaticHeaderImage();

    /**
     * Disable the automatic header image selection on app start.
     */
    void disableAutomaticHeaderImage();

    /**
     * Determine whether the automatic header image is enabled.
     * @return true if automatic header images is enabled.
     */
    boolean isAutomaticHeaderImageEnabled();
}
