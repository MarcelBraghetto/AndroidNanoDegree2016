package com.lilarcor.portfolio.framework.foundation;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Marcel Braghetto on 11/07/15.
 */
public class MainApplication extends Application {
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();
    }

    /**
     * Convenience method to retrieve the application context.
     *
     * @return application context.
     */
    public static @NonNull Context getContext() {
        return sContext;
    }
}
