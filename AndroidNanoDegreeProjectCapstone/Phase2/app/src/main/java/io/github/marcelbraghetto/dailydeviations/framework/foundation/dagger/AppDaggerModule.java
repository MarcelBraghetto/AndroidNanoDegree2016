package io.github.marcelbraghetto.dailydeviations.framework.foundation.dagger;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.features.collection.logic.providers.DefaultCollectionProvider;
import io.github.marcelbraghetto.dailydeviations.features.collection.logic.providers.contracts.CollectionProvider;
import io.github.marcelbraghetto.dailydeviations.features.settings.logic.providers.DefaultSettingsProvider;
import io.github.marcelbraghetto.dailydeviations.features.settings.logic.providers.contracts.SettingsProvider;
import io.github.marcelbraghetto.dailydeviations.features.wallpaper.providers.DefaultWallpaperProvider;
import io.github.marcelbraghetto.dailydeviations.features.wallpaper.providers.contracts.WallpaperProvider;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.DefaultArtworksProvider;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.contracts.ArtworksProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.DefaultAnalyticsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.AnalyticsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.dates.DefaultDateProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.dates.contracts.DateProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.device.DefaultDeviceProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.device.contracts.DeviceProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.DefaultEventBusProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.network.DefaultNetworkRequestProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.network.contracts.NetworkRequestProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.notifications.DefaultLocalNotificationProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.notifications.contracts.LocalNotificationsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.sharedpreferences.DefaultSharedPreferencesProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.sharedpreferences.contracts.SharedPreferencesProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.strings.DefaultStringsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.strings.contracts.StringsProvider;

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
    public DeviceProvider provideDeviceUtilsProvider(@NonNull Context applicationContext) {
        return new DefaultDeviceProvider(applicationContext);
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
    public StringsProvider provideAppStringsProvider(@NonNull Context applicationContext) {
        return new DefaultStringsProvider(applicationContext);
    }

    @Provides
    @Singleton
    @NonNull
    public NetworkRequestProvider provideNetworkRequestProvider(@NonNull Context applicationContext) {
        return new DefaultNetworkRequestProvider(applicationContext);
    }

    @Singleton
    @Provides
    @NonNull
    public DateProvider provideDateProvider(@NonNull StringsProvider stringsProvider) {
        return new DefaultDateProvider(stringsProvider);
    }

    @Singleton
    @Provides
    @NonNull
    public CollectionProvider provideCollectionProvider(@NonNull SharedPreferencesProvider sharedPreferencesProvider) {
        return new DefaultCollectionProvider(sharedPreferencesProvider);
    }

    @Singleton
    @Provides
    @NonNull
    public LocalNotificationsProvider provideLocalNotificationsProvider(@NonNull Context applicationContext,
                                                                        @NonNull DeviceProvider deviceProvider,
                                                                        @NonNull SettingsProvider settingsProvider) {
        return new DefaultLocalNotificationProvider(applicationContext, deviceProvider, settingsProvider);
    }

    @Singleton
    @Provides
    @NonNull
    public WallpaperProvider provideWallpaperProvider(@NonNull Context applicationContext) {
        return new DefaultWallpaperProvider(applicationContext);
    }

    @Singleton
    @Provides
    @NonNull
    public SettingsProvider provideSettingsProvider(@NonNull SharedPreferencesProvider sharedPreferencesProvider,
                                                    @NonNull AnalyticsProvider analyticsProvider,
                                                    @NonNull EventBusProvider eventBusProvider) {
        return new DefaultSettingsProvider(sharedPreferencesProvider, analyticsProvider, eventBusProvider);
    }

    @Singleton
    @Provides
    @NonNull
    public AnalyticsProvider provideAnalyticsProvider(@NonNull Context applicationContext) {
        return new DefaultAnalyticsProvider(applicationContext);
    }

    @Provides
    @NonNull
    public ArtworksProvider provideArtworksProvider(@NonNull Context applicationContext,
                                                    @NonNull SharedPreferencesProvider sharedPreferencesProvider) {
        return new DefaultArtworksProvider(applicationContext, sharedPreferencesProvider);
    }
}
