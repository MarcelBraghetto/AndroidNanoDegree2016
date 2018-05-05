package io.github.marcelbraghetto.dailydeviations.features.wallpaper.events;

import android.support.annotation.NonNull;

import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusEvent;

/**
 * Created by Marcel Braghetto on 4/06/16.
 *
 * Event bus event to notify listeners about the result of performing a set wallpaper action.
 */
public final class WallpaperEvent implements EventBusEvent {
    public enum Reason {
        PhoneWallpaperSuccess,
        PhoneWallpaperFailed,
        WatchWallpaperSuccess,
        WatchWallpaperFailed
    }

    private final Reason mReason;

    public WallpaperEvent(@NonNull Reason reason) {
        mReason = reason;
    }

    @NonNull
    public Reason getReason() {
        return mReason;
    }
}
