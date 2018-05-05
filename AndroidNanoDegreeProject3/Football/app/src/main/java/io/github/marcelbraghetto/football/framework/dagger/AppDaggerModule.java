package io.github.marcelbraghetto.football.framework.dagger;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.marcelbraghetto.football.framework.application.MainApp;
import io.github.marcelbraghetto.football.framework.providers.dates.DefaultDateProvider;
import io.github.marcelbraghetto.football.framework.providers.dates.contracts.DateProvider;
import io.github.marcelbraghetto.football.framework.providers.device.DefaultDeviceUtilsProvider;
import io.github.marcelbraghetto.football.framework.providers.device.contracts.DeviceUtilsProvider;
import io.github.marcelbraghetto.football.framework.providers.eventbus.DefaultEventBusProvider;
import io.github.marcelbraghetto.football.framework.providers.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.football.framework.providers.football.DefaultFootballProvider;
import io.github.marcelbraghetto.football.framework.providers.football.contracts.FootballProvider;
import io.github.marcelbraghetto.football.framework.providers.network.DefaultNetworkRequestProvider;
import io.github.marcelbraghetto.football.framework.providers.network.contracts.NetworkRequestProvider;
import io.github.marcelbraghetto.football.framework.providers.sharedpreferences.DefaultSharedPreferencesProvider;
import io.github.marcelbraghetto.football.framework.providers.sharedpreferences.contracts.SharedPreferencesProvider;
import io.github.marcelbraghetto.football.framework.providers.strings.DefaultAppStringsProvider;
import io.github.marcelbraghetto.football.framework.providers.strings.contracts.AppStringsProvider;
import io.github.marcelbraghetto.football.framework.providers.threading.DefaultThreadUtilsProvider;
import io.github.marcelbraghetto.football.framework.providers.threading.contracts.ThreadUtilsProvider;

/**
 * Created by Marcel Braghetto on 1/12/15.
 *
 * Dagger module for the app.
 */
@Module
public final class AppDaggerModule {
    private final Context mApplicationContext;

    public AppDaggerModule(@NonNull MainApp mainApplication) {
        mApplicationContext = mainApplication.getApplicationContext();
    }

    @Provides
    @Singleton
    @NonNull
    public Context provideApplicationContext() {
        return mApplicationContext;
    }

    @Provides
    @Singleton
    @NonNull
    public DeviceUtilsProvider provideDeviceUtilsProvider(@NonNull Context applicationContext) {
        return new DefaultDeviceUtilsProvider(applicationContext);
    }

    @Provides
    @Singleton
    @NonNull
    public EventBusProvider provideEventBusProvider() {
        return new DefaultEventBusProvider();
    }

    @Provides
    @Singleton
    @NonNull
    public SharedPreferencesProvider provideSharedPreferencesProvider(@NonNull Context applicationContext) {
        return new DefaultSharedPreferencesProvider(applicationContext);
    }

    @Provides
    @Singleton
    @NonNull
    public AppStringsProvider provideAppStringsProvider(@NonNull Context applicationContext) {
        return new DefaultAppStringsProvider(applicationContext);
    }

    @Provides
    @Singleton
    @NonNull
    public ThreadUtilsProvider provideThreadUtilsProvider(@NonNull Context applicationContext) {
        return new DefaultThreadUtilsProvider(applicationContext);
    }

    @Provides
    @Singleton
    @NonNull
    public NetworkRequestProvider provideNetworkRequestProvider(@NonNull Context applicationContext, @NonNull AppStringsProvider appStringsProvider) {
        return new DefaultNetworkRequestProvider(applicationContext, appStringsProvider);
    }

    @Singleton
    @Provides
    @NonNull
    public DateProvider provideDateProvider(@NonNull AppStringsProvider appStringsProvider) {
        return new DefaultDateProvider(appStringsProvider);
    }

    @Provides
    @NonNull
    public FootballProvider provideFootballProvider(@NonNull Context applicationContext) {
        return new DefaultFootballProvider(applicationContext);
    }
}
