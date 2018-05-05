package io.github.marcelbraghetto.deviantartreader.framework.foundation.notifications.contracts;

import android.app.Notification;
import android.app.PendingIntent;
import android.support.annotation.NonNull;

/**
 * Created by Marcel Braghetto on 20/03/16.
 *
 * Provide to assist with showing local notifications.
 */
public interface LocalNotificationsProvider {
    /**
     * Display the given local notification with the given notification id.
     * @param notificationId of the notification to show.
     * @param notification describing the content of the notification.
     * @param badgeCount for certain device models an app icon badge can be displayed with the given count.
     */
    void showNotification(int notificationId, Notification notification, int badgeCount);

    /**
     * Cancel and remove any local notification with the given id.
     * @param notificationId of the notification to cancel.
     */
    void cancelNotification(int notificationId);

    /**
     * Build a notification object with the given properties.
     * @param title to display.
     * @param message to display.
     * @param actionIntent to perform when the notification is tapped.
     * @return a notification object that can be presented to the user.
     */
    @NonNull
    Notification createNotification(@NonNull String title, @NonNull String message, @NonNull PendingIntent actionIntent);
}
