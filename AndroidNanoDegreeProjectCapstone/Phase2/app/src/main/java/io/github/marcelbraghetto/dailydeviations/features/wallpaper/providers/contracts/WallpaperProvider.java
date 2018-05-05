package io.github.marcelbraghetto.dailydeviations.features.wallpaper.providers.contracts;

import android.support.annotation.NonNull;

/**
 * Created by Marcel Braghetto on 25/05/16.
 *
 * The wallpaper provider allows for the changing of the device or watch face background images.
 */
public interface WallpaperProvider {
    /**
     * Set the wallpaper of the phone to the image at the given url.
     *
     * @param imageUrl of the image to load and set as the wallpaper.
     * @param triggeredFromWidget set to true if called from the home screen widget.
     */
    void setPhoneWallpaper(@NonNull String imageUrl, boolean triggeredFromWidget);
}
