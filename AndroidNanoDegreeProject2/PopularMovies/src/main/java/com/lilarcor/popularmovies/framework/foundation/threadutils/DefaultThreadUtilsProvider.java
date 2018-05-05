package com.lilarcor.popularmovies.framework.foundation.threadutils;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.lilarcor.popularmovies.framework.foundation.threadutils.contracts.ThreadUtilsProvider;

import javax.inject.Singleton;

/**
 * Created by Marcel Braghetto on 15/07/15.
 *
 * Convenience implementation to run code on the main looper thread,
 * regardless of where the calling code is scoped (ie, doesn't have
 * to be inside an Android component etc).
 */
@Singleton
public final class DefaultThreadUtilsProvider implements ThreadUtilsProvider {
    private final Context mApplicationContext;

    public DefaultThreadUtilsProvider(@NonNull Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    @Override
    public void runOnMainThread(final @NonNull Runnable runnable) {
        new Handler(mApplicationContext.getMainLooper()).post(runnable);
    }
}
