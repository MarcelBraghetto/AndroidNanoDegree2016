package io.github.marcelbraghetto.dailydeviations.features.settings.logic.providers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import io.github.marcelbraghetto.dailydeviations.BuildConfig;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.AnalyticsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.sharedpreferences.contracts.SharedPreferencesProvider;
import io.github.marcelbraghetto.dailydeviations.testconfig.RobolectricProperties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Marcel Braghetto on 14/06/16.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class DefaultSettingsProviderTest {
    @Mock SharedPreferencesProvider mSharedPreferencesProvider;
    @Mock AnalyticsProvider mAnalyticsProvider;
    @Mock EventBusProvider mEventBusProvider;
    private DefaultSettingsProvider mProvider;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mProvider = new DefaultSettingsProvider(mSharedPreferencesProvider, mAnalyticsProvider, mEventBusProvider);
    }

    @Test
    public void enableBackgroundData() {
        // Run
        mProvider.enableBackgroundData();

        // Verify
        verify(mAnalyticsProvider).trackEvent("AppSettingEnabled", "", "BackgroundData");
        verify(mSharedPreferencesProvider).saveBoolean("Settings.BackgroundData.Enabled", true);
    }

    @Test
    public void disableBackgroundData() {
        // Run
        mProvider.disableBackgroundDate();

        // Verify
        verify(mAnalyticsProvider).trackEvent("AppSettingDisabled", "", "BackgroundData");
        verify(mSharedPreferencesProvider).saveBoolean("Settings.BackgroundData.Enabled", false);
    }

    @Test
    public void isBackgroundDataEnabledTrue() {
        // Setup
        when(mSharedPreferencesProvider.getBoolean("Settings.BackgroundData.Enabled", true)).thenReturn(true);

        // Verify
        assertThat(mProvider.isBackgroundDataEnabled(), is(true));
    }

    @Test
    public void isBackgroundDataEnabledFalse() {
        // Setup
        when(mSharedPreferencesProvider.getBoolean("Settings.BackgroundData.Enabled", true)).thenReturn(false);

        // Verify
        assertThat(mProvider.isBackgroundDataEnabled(), is(false));
    }

    @Test
    public void enableNotifications() {
        // Run
        mProvider.enableNotifications();

        // Verify
        verify(mAnalyticsProvider).trackEvent("AppSettingEnabled", "", "Notifications");
        verify(mSharedPreferencesProvider).saveBoolean("Settings.Notifications.Enabled", true);
    }

    @Test
    public void disableNotifications() {
        // Run
        mProvider.disableNotifications();

        // Verify
        verify(mAnalyticsProvider).trackEvent("AppSettingDisabled", "", "Notifications");
        verify(mSharedPreferencesProvider).saveBoolean("Settings.Notifications.Enabled", false);
    }

    @Test
    public void isNotificationsEnabledTrue() {
        // Setup
        when(mSharedPreferencesProvider.getBoolean("Settings.Notifications.Enabled", true)).thenReturn(true);

        // Verify
        assertThat(mProvider.isNotificationsEnabled(), is(true));
    }

    @Test
    public void isNotificationsEnabledFalse() {
        // Setup
        when(mSharedPreferencesProvider.getBoolean("Settings.Notifications.Enabled", true)).thenReturn(false);

        // Verify
        assertThat(mProvider.isNotificationsEnabled(), is(false));
    }

    @Test
    public void enableAutomaticHeaderImage() {
        // Run
        mProvider.enableAutomaticHeaderImage();

        // Verify
        verify(mAnalyticsProvider).trackEvent("AppSettingEnabled", "", "AutomaticHeaderImage");
        verify(mSharedPreferencesProvider).saveBoolean("Settings.AutomaticHeaderImage.Enabled", true);
    }

    @Test
    public void disableAutomaticHeaderImage() {
        // Run
        mProvider.disableAutomaticHeaderImage();

        // Verify
        verify(mAnalyticsProvider).trackEvent("AppSettingDisabled", "", "AutomaticHeaderImage");
        verify(mSharedPreferencesProvider).saveBoolean("Settings.AutomaticHeaderImage.Enabled", false);
    }

    @Test
    public void isAutomaticHeaderImageEnabledTrue() {
        // Setup
        when(mSharedPreferencesProvider.getBoolean("Settings.AutomaticHeaderImage.Enabled", true)).thenReturn(true);

        // Verify
        assertThat(mProvider.isAutomaticHeaderImageEnabled(), is(true));
    }

    @Test
    public void isAutomaticHeaderImageEnabledFalse() {
        // Setup
        when(mSharedPreferencesProvider.getBoolean("Settings.AutomaticHeaderImage.Enabled", true)).thenReturn(false);

        // Verify
        assertThat(mProvider.isAutomaticHeaderImageEnabled(), is(false));
    }

}