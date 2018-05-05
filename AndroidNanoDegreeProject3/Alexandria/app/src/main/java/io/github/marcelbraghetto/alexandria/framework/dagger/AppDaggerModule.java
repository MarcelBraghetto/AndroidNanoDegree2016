package io.github.marcelbraghetto.alexandria.framework.dagger;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.marcelbraghetto.alexandria.framework.application.MainApp;
import io.github.marcelbraghetto.alexandria.framework.providers.books.DefaultBooksProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.books.contracts.BooksProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.device.DefaultDeviceUtilsProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.device.contracts.DeviceUtilsProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.eventbus.DefaultEventBusProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.network.DefaultNetworkRequestProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.network.contracts.NetworkRequestProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.sharedpreferences.DefaultSharedPreferencesProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.sharedpreferences.contracts.SharedPreferencesProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.strings.DefaultAppStringsProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.strings.contracts.AppStringsProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.threading.DefaultThreadUtilsProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.threading.contracts.ThreadUtilsProvider;

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
    public NetworkRequestProvider provideNetworkRequestProvider(@NonNull Context applicationContext) {
        return new DefaultNetworkRequestProvider(applicationContext);
    }

    @Provides
    @NonNull
    public BooksProvider provideBooksProvider(@NonNull Context applicationContext,
                                              @NonNull NetworkRequestProvider networkRequestProvider,
                                              @NonNull AppStringsProvider appStringsProvider,
                                              @NonNull ThreadUtilsProvider threadUtilsProvider) {

        return new DefaultBooksProvider(applicationContext, networkRequestProvider, appStringsProvider, threadUtilsProvider);
    }
}
