package io.github.marcelbraghetto.sunshinewatch.features.application;

import android.app.Application;
import android.support.annotation.NonNull;

import io.github.marcelbraghetto.sunshinewatch.framework.core.dagger.InjectorComponent;
import io.github.marcelbraghetto.sunshinewatch.framework.core.dagger.Injector;
import io.github.marcelbraghetto.sunshinewatch.weather.sync.SunshineSyncAdapter;

/**
 * Created by Marcel Braghetto on 16/04/16.
 *
 * Main application to bootstrap.
 */
public class MainApp extends Application {
    private static MainApp sSelf;

    @Override
    public void onCreate() {
        super.onCreate();
        sSelf = this;

        SunshineSyncAdapter.initializeSyncAdapter();
        SunshineSyncAdapter.syncImmediately();
    }

    @NonNull
    public static InjectorComponent getInjector() {
        return Injector.get(sSelf);
    }
}
