package io.github.marcelbraghetto.deviantartreader.framework.foundation.dagger;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.marcelbraghetto.deviantartreader.features.collection.logic.providers.DefaultCollectionProvider;
import io.github.marcelbraghetto.deviantartreader.features.collection.logic.providers.contracts.CollectionProvider;
import io.github.marcelbraghetto.deviantartreader.features.application.MainApp;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.DefaultArtworksProvider;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.contracts.ArtworksProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.dates.DefaultDateProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.dates.contracts.DateProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.device.DefaultDeviceProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.device.contracts.DeviceProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.DefaultEventBusProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.network.DefaultNetworkRequestProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.network.contracts.NetworkRequestProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.notifications.DefaultLocalNotificationProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.notifications.contracts.LocalNotificationsProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.sharedpreferences.DefaultSharedPreferencesProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.sharedpreferences.contracts.SharedPreferencesProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.strings.DefaultStringsProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.strings.contracts.StringsProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.threading.DefaultThreadUtilsProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.threading.contracts.ThreadUtilsProvider;

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
    public ThreadUtilsProvider provideThreadUtilsProvider(@NonNull Context applicationContext) {
        return new DefaultThreadUtilsProvider(applicationContext);
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
                                                                        @NonNull DeviceProvider deviceProvider) {
        return new DefaultLocalNotificationProvider(applicationContext, deviceProvider);
    }

    @Provides
    @NonNull
    public ArtworksProvider provideArtworksProvider(@NonNull Context applicationContext,
                                                    @NonNull SharedPreferencesProvider sharedPreferencesProvider) {
        return new DefaultArtworksProvider(applicationContext, sharedPreferencesProvider);
    }
}
