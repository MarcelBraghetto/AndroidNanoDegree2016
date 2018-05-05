package io.github.marcelbraghetto.deviantartreader.framework.foundation.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import javax.inject.Inject;

import io.github.marcelbraghetto.deviantartreader.R;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.device.contracts.DeviceProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.notifications.contracts.LocalNotificationsProvider;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Marcel Braghetto on 20/03/16.
 *
 * Implementation for showing and cancelling local notifications.
 */
public class DefaultLocalNotificationProvider implements LocalNotificationsProvider {
    private final Context mApplicationContext;
    private final DeviceProvider mDeviceProvider;

    @Inject
    public DefaultLocalNotificationProvider(@NonNull Context applicationContext,
                                            @NonNull DeviceProvider deviceProvider) {

        mApplicationContext = applicationContext;
        mDeviceProvider = deviceProvider;
    }

    @Override
    public void showNotification(int notificationId, Notification notification, int badgeCount) {
        ShortcutBadger.applyCount(mApplicationContext, badgeCount);
        NotificationManagerCompat.from(mApplicationContext).notify(notificationId, notification);
    }

    @Override
    public void cancelNotification(int notificationId) {
        ShortcutBadger.removeCount(mApplicationContext);
        NotificationManagerCompat.from(mApplicationContext).cancel(notificationId);
    }

    @NonNull
    @Override
    public Notification createNotification(@NonNull String title, @NonNull String message, @NonNull PendingIntent actionIntent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mApplicationContext)
                .setColor(mApplicationContext.getResources().getColor(R.color.primary))
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(BitmapFactory.decodeResource(mApplicationContext.getResources(), R.drawable.app_icon))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(actionIntent);

        return notificationBuilder.build();
    }

    @DrawableRes
    private int getNotificationIcon() {
        // Pre lollipop uses a different notification icon system.
        return mDeviceProvider.isAtLeastLollipop() ? R.drawable.notification_icon_lollipop : R.drawable.app_icon;
    }
}
