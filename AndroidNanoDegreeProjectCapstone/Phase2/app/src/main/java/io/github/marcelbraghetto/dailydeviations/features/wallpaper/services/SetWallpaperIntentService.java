package io.github.marcelbraghetto.dailydeviations.features.wallpaper.services;

import android.app.IntentService;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import io.github.marcelbraghetto.dailydeviations.R;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.features.wallpaper.events.WallpaperEvent;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.strings.contracts.StringsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.utils.StringUtils;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.utils.ToastUtils;

/**
 * Created by Marcel Braghetto on 16/06/16.
 *
 * Intent service to apply a given image url as the device wallpaper.
 */
public class SetWallpaperIntentService extends IntentService {
    private static final String EXTRA_IMAGE_URL = "ImageUrl";
    private static final String EXTRA_TRIGGERED_FROM_WIDGET = "TriggeredFromWidget";

    /**
     * Create a new intent that can be started to run a wallpaper intent service.
     * @param context to use.
     * @param imageUrl of the image to load and set as the wallpaper.
     * @return intent that can be launched to set the wallpaper.
     */
    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull String imageUrl, boolean triggeredFromWidget) {
        Intent intent = new Intent(context, SetWallpaperIntentService.class);
        intent.putExtra(EXTRA_IMAGE_URL, imageUrl);
        intent.putExtra(EXTRA_TRIGGERED_FROM_WIDGET, triggeredFromWidget);
        return intent;
    }

    public SetWallpaperIntentService() {
        super("SetWallpaper");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL);

        if(StringUtils.isEmpty(imageUrl)) {
            return;
        }

        boolean triggeredFromWidget = intent.getBooleanExtra(EXTRA_TRIGGERED_FROM_WIDGET, false);

        EventBusProvider eventBusProvider = MainApp.getDagger().getEventBusProvider();
        StringsProvider stringsProvider = MainApp.getDagger().getAppStringsProvider();
        Context context = getApplicationContext();
        boolean success;

        if(triggeredFromWidget) {
            ToastUtils.showToast(context, stringsProvider.getString(R.string.set_phone_wallpaper_widget_started), Toast.LENGTH_LONG);
        }

        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);

            int maximumImageSize = context.getResources().getInteger(R.integer.maximum_image_dimension);

            Bitmap bitmap = Glide.with(context).
                    load(imageUrl).
                    asBitmap().
                    into(maximumImageSize, maximumImageSize).
                    get();

            wallpaperManager.setBitmap(bitmap);
            bitmap.recycle();
            success = true;
        } catch (InterruptedException | ExecutionException | IOException e) {
            success = false;
        }

        if(success) {
            if(triggeredFromWidget) {
                ToastUtils.showToast(context, stringsProvider.getString(R.string.set_phone_wallpaper_success), Toast.LENGTH_SHORT);
            } else {
                // Notify any listeners that the wallpaper action succeeded
                eventBusProvider.postEvent(new WallpaperEvent(WallpaperEvent.Reason.PhoneWallpaperSuccess));
            }
            return;
        }

        if(triggeredFromWidget) {
            ToastUtils.showToast(context, stringsProvider.getString(R.string.set_phone_wallpaper_failed), Toast.LENGTH_SHORT);
        } else {
            // Notify any listeners that the wallpaper action failed
            eventBusProvider.postEvent(new WallpaperEvent(WallpaperEvent.Reason.PhoneWallpaperFailed));
        }
    }
}
