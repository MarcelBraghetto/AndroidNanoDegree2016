package io.github.marcelbraghetto.sunshinewatch.framework.core.dagger;

import android.content.Context;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.marcelbraghetto.sunshinewatch.framework.network.DefaultNetworkRequestProvider;
import io.github.marcelbraghetto.sunshinewatch.framework.network.contracts.NetworkRequestProvider;
import io.github.marcelbraghetto.sunshinewatch.framework.strings.DefaultStringsProvider;
import io.github.marcelbraghetto.sunshinewatch.framework.strings.contracts.StringsProvider;
import io.github.marcelbraghetto.sunshinewatch.weather.DefaultWeatherProvider;
import io.github.marcelbraghetto.sunshinewatch.weather.contracts.WeatherProvider;

/**
 * Created by Marcel Braghetto on 23/04/16.
 *
 * Dagger module for the app.
 */
@Module
public final class InjectorDaggerModule {
    private final Context mApplicationContext;

    public InjectorDaggerModule(@NonNull Context applicationContext) {
        mApplicationContext = applicationContext;
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
    public StringsProvider provideAppStringsProvider(@NonNull Context applicationContext) {
        return new DefaultStringsProvider(applicationContext);
    }

    @Provides
    @Singleton
    @NonNull
    public NetworkRequestProvider provideNetworkRequestProvider() {
        return new DefaultNetworkRequestProvider();
    }

    @Provides
    @NonNull
    public WeatherProvider provideWeatherProvider(@NonNull Context applicationContext,
                                                  @NonNull StringsProvider stringsProvider) {

        return new DefaultWeatherProvider(applicationContext, stringsProvider);
    }
}
