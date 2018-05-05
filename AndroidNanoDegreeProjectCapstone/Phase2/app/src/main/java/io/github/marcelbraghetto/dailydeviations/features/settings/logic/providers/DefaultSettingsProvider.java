package io.github.marcelbraghetto.dailydeviations.features.settings.logic.providers;

import android.support.annotation.NonNull;

import io.github.marcelbraghetto.dailydeviations.features.settings.logic.SettingsChangeEvent;
import io.github.marcelbraghetto.dailydeviations.features.settings.logic.providers.contracts.SettingsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.AnalyticsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.sharedpreferences.contracts.SharedPreferencesProvider;

/**
 * Created by Marcel Braghetto on 6/06/16.
 *
 * Default implementation of the settings provider to give access to configuring the app.
 *
 * The shared preference values will return sensible defaults when absent.
 */
public class DefaultSettingsProvider implements SettingsProvider {
    private static final String SETTING_BACKGROUND_DATA_ENABLED = "Settings.BackgroundData.Enabled";
    private static final String SETTING_NOTIFICATIONS_ENABLED = "Settings.Notifications.Enabled";
    private static final String SETTING_AUTOMATIC_HEADER_IMAGE = "Settings.AutomaticHeaderImage.Enabled";

    private static final String ANALYTICS_SETTING_ENABLED = "AppSettingEnabled";
    private static final String ANALYTICS_SETTING_DISABLED = "AppSettingDisabled";

    private final SharedPreferencesProvider mSharedPreferencesProvider;
    private final AnalyticsProvider mAnalyticsProvider;
    private final EventBusProvider mEventBusProvider;

    public DefaultSettingsProvider(@NonNull SharedPreferencesProvider sharedPreferencesProvider,
                                   @NonNull AnalyticsProvider analyticsProvider,
                                   @NonNull EventBusProvider eventBusProvider) {

        mSharedPreferencesProvider = sharedPreferencesProvider;
        mAnalyticsProvider = analyticsProvider;
        mEventBusProvider = eventBusProvider;
    }

    @Override
    public void enableBackgroundData() {
        mAnalyticsProvider.trackEvent(ANALYTICS_SETTING_ENABLED, "", "BackgroundData");
        mSharedPreferencesProvider.saveBoolean(SETTING_BACKGROUND_DATA_ENABLED, true);
        mEventBusProvider.postEvent(new SettingsChangeEvent());
    }

    @Override
    public void disableBackgroundDate() {
        mAnalyticsProvider.trackEvent(ANALYTICS_SETTING_DISABLED, "", "BackgroundData");
        mSharedPreferencesProvider.saveBoolean(SETTING_BACKGROUND_DATA_ENABLED, false);
        mEventBusProvider.postEvent(new SettingsChangeEvent());
    }

    @Override
    public boolean isBackgroundDataEnabled() {
        return mSharedPreferencesProvider.getBoolean(SETTING_BACKGROUND_DATA_ENABLED, true);
    }

    @Override
    public void enableNotifications() {
        mAnalyticsProvider.trackEvent(ANALYTICS_SETTING_ENABLED, "", "Notifications");
        mSharedPreferencesProvider.saveBoolean(SETTING_NOTIFICATIONS_ENABLED, true);
        mEventBusProvider.postEvent(new SettingsChangeEvent());
    }

    @Override
    public void disableNotifications() {
        mAnalyticsProvider.trackEvent(ANALYTICS_SETTING_DISABLED, "", "Notifications");
        mSharedPreferencesProvider.saveBoolean(SETTING_NOTIFICATIONS_ENABLED, false);
        mEventBusProvider.postEvent(new SettingsChangeEvent());
    }

    @Override
    public boolean isNotificationsEnabled() {
        return mSharedPreferencesProvider.getBoolean(SETTING_NOTIFICATIONS_ENABLED, true);
    }

    @Override
    public void enableAutomaticHeaderImage() {
        mAnalyticsProvider.trackEvent(ANALYTICS_SETTING_ENABLED, "", "AutomaticHeaderImage");
        mSharedPreferencesProvider.saveBoolean(SETTING_AUTOMATIC_HEADER_IMAGE, true);
        mEventBusProvider.postEvent(new SettingsChangeEvent());
    }

    @Override
    public void disableAutomaticHeaderImage() {
        mAnalyticsProvider.trackEvent(ANALYTICS_SETTING_DISABLED, "", "AutomaticHeaderImage");
        mSharedPreferencesProvider.saveBoolean(SETTING_AUTOMATIC_HEADER_IMAGE, false);
        mEventBusProvider.postEvent(new SettingsChangeEvent());
    }

    @Override
    public boolean isAutomaticHeaderImageEnabled() {
        return mSharedPreferencesProvider.getBoolean(SETTING_AUTOMATIC_HEADER_IMAGE, true);
    }
}
