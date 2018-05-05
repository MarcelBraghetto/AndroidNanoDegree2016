package com.lilarcor.popularmovies.framework.foundation.dagger;

import android.content.Context;
import android.support.annotation.NonNull;

import com.lilarcor.popularmovies.features.application.MainApp;
import com.lilarcor.popularmovies.framework.foundation.appstrings.DefaultAppStringsProvider;
import com.lilarcor.popularmovies.framework.foundation.appstrings.contracts.AppStringsProvider;
import com.lilarcor.popularmovies.framework.foundation.device.DefaultDeviceInfoProvider;
import com.lilarcor.popularmovies.framework.foundation.device.contracts.DeviceInfoProvider;
import com.lilarcor.popularmovies.framework.foundation.eventbus.DefaultEventBusProvider;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusProvider;
import com.lilarcor.popularmovies.framework.foundation.threadutils.DefaultThreadUtilsProvider;
import com.lilarcor.popularmovies.framework.foundation.threadutils.contracts.ThreadUtilsProvider;
import com.lilarcor.popularmovies.framework.movies.provider.DefaultMoviesProvider;
import com.lilarcor.popularmovies.framework.movies.provider.contracts.MoviesProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Marcel Braghetto on 12/07/15.
 *
 * Core Dagger module to cover the framework level injection mapping.
 */
@Module
public final class AppDaggerModule {
    private final Context mApplicationContext;

    public AppDaggerModule(@NonNull MainApp mainApplication) {
        mApplicationContext = mainApplication.getApplicationContext();
    }

    //region Singleton providers
    @Provides
    @Singleton
    @NonNull
    public Context provideApplicationContext() {
        return mApplicationContext;
    }

    @Provides
    @Singleton
    @NonNull
    public EventBusProvider provideEventBusProvider(@NonNull ThreadUtilsProvider threadUtilsProvider) {
        return new DefaultEventBusProvider(threadUtilsProvider);
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
    public DeviceInfoProvider provideDeviceInfoProvider(@NonNull Context applicationContext) {
        return new DefaultDeviceInfoProvider(applicationContext);
    }

    @Provides
    @Singleton
    @NonNull
    public AppStringsProvider provideAppStringsProvider(@NonNull Context applicationContext) {
        return new DefaultAppStringsProvider(applicationContext);
    }
    //endregion

    //region
    @Provides
    @NonNull
    public MoviesProvider provideMoviesProvider(@NonNull Context applicationContext) {
        return new DefaultMoviesProvider(applicationContext);
    }
    //endregion
}