package io.github.marcelbraghetto.dailydeviations.framework.foundation.utils;

import android.support.annotation.NonNull;

/**
 * Created by Marcel Braghetto on 18/06/16.
 *
 * Utility methods for fetching files from the assets folder.
 */
public class AssetUtils {
    private AssetUtils() { }

    @NonNull
    public static String getLocalAssetPath(@NonNull String fileName) {
        return "file:///android_asset/" + fileName;
    }
}
