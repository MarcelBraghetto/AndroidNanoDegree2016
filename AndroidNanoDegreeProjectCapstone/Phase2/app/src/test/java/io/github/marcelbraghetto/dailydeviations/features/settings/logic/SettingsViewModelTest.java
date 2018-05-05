package io.github.marcelbraghetto.dailydeviations.features.settings.logic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import io.github.marcelbraghetto.dailydeviations.BuildConfig;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.features.settings.logic.providers.contracts.SettingsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.AnalyticsProvider;
import io.github.marcelbraghetto.dailydeviations.testconfig.RobolectricProperties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Marcel Braghetto on 14/06/16.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class SettingsViewModelTest {
    @Mock SettingsProvider mSettingsProvider;
    @Mock AnalyticsProvider mAnalyticsProvider;
    @Mock SettingsViewModel.Actions mDelegate;
    private SettingsViewModel mViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mViewModel = new SettingsViewModel(
                mSettingsProvider,
                MainApp.getDagger().getAppStringsProvider(),
                mAnalyticsProvider);
    }

    @Test
    public void verifyDefaultDataBinding() {
        // Verify
        assertThat(mViewModel.glue.backgroundDataEnabled.get(), is(true));
        assertThat(mViewModel.glue.notificationsEnabled.get(), is(true));
        assertThat(mViewModel.glue.automaticHeaderImageEnabled.get(), is(true));
    }

    @Test
    public void beginAllSettingsOff() {
        // Setup
        when(mSettingsProvider.isBackgroundDataEnabled()).thenReturn(false);
        when(mSettingsProvider.isNotificationsEnabled()).thenReturn(false);
        when(mSettingsProvider.isAutomaticHeaderImageEnabled()).thenReturn(false);

        // Run
        mViewModel.begin(mDelegate);

        // Verify
        assertThat(mViewModel.glue.backgroundDataEnabled.get(), is(false));
        assertThat(mViewModel.glue.notificationsEnabled.get(), is(false));
        assertThat(mViewModel.glue.automaticHeaderImageEnabled.get(), is(false));
    }

    @Test
    public void screenStarted() {
        // Run
        mViewModel.screenStarted();

        // Verify
        verify(mAnalyticsProvider).trackScreenView("SettingsScreen");
    }

    @Test
    public void toggleBackgroundDataWasOff() {
        // Setup
        when(mSettingsProvider.isBackgroundDataEnabled()).thenReturn(false);
        mViewModel.begin(mDelegate);
        reset(mDelegate);

        // Run
        mViewModel.toggleBackgroundData();

        // Verify
        verify(mSettingsProvider, times(2)).isBackgroundDataEnabled();
        verify(mSettingsProvider, never()).disableBackgroundDate();
        verify(mSettingsProvider).enableBackgroundData();
        assertThat(mViewModel.glue.backgroundDataEnabled.get(), is(true));
        verify(mDelegate).showSnackbar("Daily deviations will be fetched in the background");
    }

    @Test
    public void toggleBackgroundDataWasOn() {
        // Setup
        when(mSettingsProvider.isBackgroundDataEnabled()).thenReturn(true);
        mViewModel.begin(mDelegate);
        reset(mDelegate);

        // Run
        mViewModel.toggleBackgroundData();

        // Verify
        verify(mSettingsProvider, times(2)).isBackgroundDataEnabled();
        verify(mSettingsProvider).disableBackgroundDate();
        verify(mSettingsProvider, never()).enableBackgroundData();
        assertThat(mViewModel.glue.backgroundDataEnabled.get(), is(false));
        verify(mDelegate).showSnackbar("Daily deviations will not be fetched in the background");
    }

    @Test
    public void toggleNotificationsWasOff() {
        // Setup
        when(mSettingsProvider.isNotificationsEnabled()).thenReturn(false);
        mViewModel.begin(mDelegate);
        reset(mDelegate);

        // Run
        mViewModel.toggleNotifications();

        // Verify
        verify(mSettingsProvider, times(2)).isNotificationsEnabled();
        verify(mSettingsProvider, never()).disableNotifications();
        verify(mSettingsProvider).enableNotifications();
        assertThat(mViewModel.glue.notificationsEnabled.get(), is(true));
        verify(mDelegate).showSnackbar("Notifications will display for new daily deviations");
    }

    @Test
    public void toggleNotificationsWasOn() {
        // Setup
        when(mSettingsProvider.isNotificationsEnabled()).thenReturn(true);
        mViewModel.begin(mDelegate);
        reset(mDelegate);

        // Run
        mViewModel.toggleNotifications();

        // Verify
        verify(mSettingsProvider, times(2)).isNotificationsEnabled();
        verify(mSettingsProvider).disableNotifications();
        verify(mSettingsProvider, never()).enableNotifications();
        assertThat(mViewModel.glue.notificationsEnabled.get(), is(false));
        verify(mDelegate).showSnackbar("Notifications will not display for new daily deviations");
    }

    @Test
    public void toggleAutomaticHeaderImageWasOff() {
        // Setup
        when(mSettingsProvider.isAutomaticHeaderImageEnabled()).thenReturn(false);
        mViewModel.begin(mDelegate);
        reset(mDelegate);

        // Run
        mViewModel.toggleAutomaticHeaderImage();

        // Verify
        verify(mSettingsProvider, times(2)).isAutomaticHeaderImageEnabled();
        verify(mSettingsProvider, never()).disableAutomaticHeaderImage();
        verify(mSettingsProvider).enableAutomaticHeaderImage();
        assertThat(mViewModel.glue.automaticHeaderImageEnabled.get(), is(true));
        verify(mDelegate).showSnackbar("A random image will display in the menu on app start");
    }

    @Test
    public void toggleAutomaticHeaderImageWasOn() {
        // Setup
        when(mSettingsProvider.isAutomaticHeaderImageEnabled()).thenReturn(true);
        mViewModel.begin(mDelegate);
        reset(mDelegate);

        // Run
        mViewModel.toggleAutomaticHeaderImage();

        // Verify
        verify(mSettingsProvider, times(2)).isAutomaticHeaderImageEnabled();
        verify(mSettingsProvider).disableAutomaticHeaderImage();
        verify(mSettingsProvider, never()).enableAutomaticHeaderImage();
        assertThat(mViewModel.glue.automaticHeaderImageEnabled.get(), is(false));
        verify(mDelegate).showSnackbar("The image in the menu will not change automatically");
    }
}
