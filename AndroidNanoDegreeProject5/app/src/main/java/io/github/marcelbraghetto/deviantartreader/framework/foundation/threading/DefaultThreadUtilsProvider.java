package io.github.marcelbraghetto.deviantartreader.framework.foundation.threading;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import io.github.marcelbraghetto.deviantartreader.framework.foundation.threading.contracts.ThreadUtilsProvider;


/**
 * Created by Marcel Braghetto on 02/12/15.
 *
 * Convenience implementation to run code on the main looper thread, regardless of where the
 * calling code is scoped (ie, doesn't have to be inside an Android component etc).
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