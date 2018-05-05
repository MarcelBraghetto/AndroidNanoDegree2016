package io.github.marcelbraghetto.jokemachine.lib.android;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Marcel Braghetto on 19/01/16.
 *
 * Simple main application implementation.
 */
public class MainApplication extends Application {
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();
    }

    @NonNull
    public static Context getContext() {
        return sContext;
    }
}
