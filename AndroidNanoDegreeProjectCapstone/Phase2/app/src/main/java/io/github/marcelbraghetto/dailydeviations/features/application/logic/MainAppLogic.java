package io.github.marcelbraghetto.dailydeviations.features.application.logic;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import io.github.marcelbraghetto.dailydeviations.R;
import io.github.marcelbraghetto.dailydeviations.features.home.ui.HomeActivity;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.contracts.ArtworksProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusSubscriber;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.events.DataLoadEvent;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.notifications.contracts.LocalNotificationsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.strings.contracts.StringsProvider;

/**
 * Created by Marcel Braghetto on 20/03/16.
 *
 * Logic to drive the main app component.
 */
public class MainAppLogic implements EventBusSubscriber {
    private static final int NOTIFICATION_ID = 1;

    private final Context mApplicationContext;
    private final EventBusProvider mEventBusProvider;
    private final StringsProvider mStringsProvider;
    private final LocalNotificationsProvider mLocalNotificationsProvider;
    private final ArtworksProvider mArtworksProvider;
    private int mActivityStartedCount;

    @Inject
    public MainAppLogic(@NonNull Context applicationContext,
                        @NonNull EventBusProvider eventBusProvider,
                        @NonNull StringsProvider stringsProvider,
                        @NonNull LocalNotificationsProvider localNotificationsProvider,
                        @NonNull ArtworksProvider artworksProvider) {

        mApplicationContext = applicationContext;
        mStringsProvider = stringsProvider;
        mLocalNotificationsProvider = localNotificationsProvider;
        mEventBusProvider = eventBusProvider;
        mArtworksProvider = artworksProvider;
    }

    public void begin() {
        subscribeToEventBus();
    }

    /**
     * The application lifecycle has detected that an activity has started.
     */
    public void activityStarted() {
        mActivityStartedCount++;

        // If we have detected an activity start then the app must be in the
        // foreground - which means we should try to cancel any existing local notifications
        // that might have been displayed to the user.
        mLocalNotificationsProvider.cancelNotification(NOTIFICATION_ID);
        mArtworksProvider.resetNumUnseenArtworks();
    }

    /**
     * The application lifecycle has detected that an activity has stopped.
     */
    public void activityStopped() {
        mActivityStartedCount--;
    }

    /**
     * A broadcast received to indicate a data loading event has occurred. We are interested only
     * in a 'loaded' event to determine whether to display a local notification and app icon
     * badge count for any new items that were loaded since the user last saw the app.
     * @param event that was broadcast.
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull DataLoadEvent event) {
        // If the event was from a data refresh and there were new artworks and there are NO
        // activities in the started phase (meaning we are in the background) then display a
        // local notification hinting to the user that there are new artworks to see.
        if(event.getEventType() == DataLoadEvent.EventType.Loaded) {
            if(mActivityStartedCount <= 0) {
                int numUnseenArtworks = mArtworksProvider.getNumUnseenArtworks();

                // Only proceed if there are actually any new artworks not previously seen.
                if (numUnseenArtworks > 0) {
                    triggerNotification(numUnseenArtworks);
                }
            } else {
                mArtworksProvider.resetNumUnseenArtworks();
            }
        }
    }

    @Override
    public void subscribeToEventBus() {
        mEventBusProvider.subscribe(this);
    }

    @Override
    public void unsubscribeFromEventBus() {
        mEventBusProvider.unsubscribe(this);
    }

    /**
     * Construct and trigger a local notification to display, along with applying an app icon
     * badge number (only works on certain Android models).
     * @param numNewArtworks that haven't been seen by the user.
     */
    private void triggerNotification(int numNewArtworks) {
        String title = mStringsProvider.getString(R.string.app_notification_title);
        String message = mStringsProvider.getString(R.string.app_notification_message, numNewArtworks);

        Intent intent = new Intent(mApplicationContext, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mApplicationContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = mLocalNotificationsProvider.createNotification(title, message, pendingIntent);
        mLocalNotificationsProvider.showNotification(NOTIFICATION_ID, notification, numNewArtworks);
    }
}
