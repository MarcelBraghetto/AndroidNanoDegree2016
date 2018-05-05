package io.github.marcelbraghetto.dailydeviations.features.wallpaper.providers;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.github.marcelbraghetto.dailydeviations.features.wallpaper.providers.contracts.WallpaperProvider;
import io.github.marcelbraghetto.dailydeviations.features.wallpaper.services.SetWallpaperIntentService;

/**
 * Created by Marcel Braghetto on 25/05/16.
 *
 * Wallpaper provider.
 */
public class DefaultWallpaperProvider implements WallpaperProvider {
    private final Context mApplicationContext;

    @Inject
    public DefaultWallpaperProvider(@NonNull Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    @Override
    public void setPhoneWallpaper(@NonNull String imageUrl, boolean triggeredFromWidget) {
        mApplicationContext.startService(SetWallpaperIntentService.createIntent(mApplicationContext, imageUrl, triggeredFromWidget));
    }
}
