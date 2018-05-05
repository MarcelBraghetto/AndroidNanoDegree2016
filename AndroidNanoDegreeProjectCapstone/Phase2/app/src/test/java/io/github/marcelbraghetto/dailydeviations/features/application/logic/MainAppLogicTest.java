package io.github.marcelbraghetto.dailydeviations.features.application.logic;

import android.app.Notification;
import android.app.PendingIntent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import io.github.marcelbraghetto.dailydeviations.BuildConfig;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.contracts.ArtworksProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.events.DataLoadEvent;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.notifications.contracts.LocalNotificationsProvider;
import io.github.marcelbraghetto.dailydeviations.testconfig.RobolectricProperties;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by Marcel Braghetto on 12/06/16.
 *
 * Main app logic test suite.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class MainAppLogicTest {
    @Mock EventBusProvider mEventBusProvider;
    @Mock LocalNotificationsProvider mLocalNotificationsProvider;
    @Mock ArtworksProvider mArtworksProvider;
    private MainAppLogic mLogic;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mLogic = new MainAppLogic(
                MainApp.getDagger().getApplicationContext(),
                mEventBusProvider,
                MainApp.getDagger().getAppStringsProvider(),
                mLocalNotificationsProvider,
                mArtworksProvider);
    }

    @Test
    public void begin() {
        // Run
        mLogic.begin();

        // Verify
        verify(mEventBusProvider).subscribe(mLogic);
    }

    @Test
    public void activityStarted() {
        // Run
        mLogic.activityStarted();

        // Verify
        verify(mLocalNotificationsProvider).cancelNotification(1);
        verify(mArtworksProvider).resetNumUnseenArtworks();
    }

    @Test
    public void onDataLoadedEventFailedEvent() {
        // Setup
        DataLoadEvent event = DataLoadEvent.createFailedEvent();

        // Run
        mLogic.onEvent(event);

        // Verify
        verifyZeroInteractions(mArtworksProvider);
    }

    @Test
    public void onDataLoadedEventActivityInForeground() {
        // Setup
        DataLoadEvent event = DataLoadEvent.createLoadedEvent();
        mLogic.activityStarted();
        reset(mArtworksProvider);
        reset(mLocalNotificationsProvider);

        // Run
        mLogic.onEvent(event);

        // Verify
        verify(mArtworksProvider).resetNumUnseenArtworks();
        verifyNoMoreInteractions(mArtworksProvider);
        verifyZeroInteractions(mLocalNotificationsProvider);
    }

    @Test
    public void onDataLoadedActivityNotInForegroundZeroNewArtworks() {
        // Setup
        when(mArtworksProvider.getNumUnseenArtworks()).thenReturn(0);
        DataLoadEvent event = DataLoadEvent.createLoadedEvent();

        // Run
        mLogic.onEvent(event);

        // Verify
        verify(mArtworksProvider).getNumUnseenArtworks();
        verifyNoMoreInteractions(mArtworksProvider);
        verifyZeroInteractions(mLocalNotificationsProvider);
    }

    @Test
    public void onDataLoadedActivityNotInForegroundNewArtworks() {
        // Setup
        when(mArtworksProvider.getNumUnseenArtworks()).thenReturn(10);
        DataLoadEvent event = DataLoadEvent.createLoadedEvent();

        // Run
        mLogic.onEvent(event);

        // Verify
        verify(mArtworksProvider).getNumUnseenArtworks();
        verifyNoMoreInteractions(mArtworksProvider);
        verify(mLocalNotificationsProvider).createNotification(eq("New deviations"), eq("10 new deviations to see! Tap to view."), any(PendingIntent.class));
        verify(mLocalNotificationsProvider).showNotification(eq(1), any(Notification.class), eq(10));
    }

    @Test
    public void unsubscribeToEventBus() {
        // Run
        mLogic.unsubscribeFromEventBus();

        // Verify
        verify(mEventBusProvider).unsubscribe(mLogic);

    }
}