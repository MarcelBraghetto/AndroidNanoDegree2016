package com.lilarcor.popularmovies.framework.foundation.network.dagger;

import android.content.Context;
import android.support.annotation.NonNull;

import com.lilarcor.popularmovies.framework.foundation.appstrings.contracts.AppStringsProvider;
import com.lilarcor.popularmovies.framework.foundation.network.DefaultNetworkRequestProvider;
import com.lilarcor.popularmovies.framework.foundation.network.contracts.NetworkRequestProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Marcel Braghetto on 30/07/15.
 *
 * This is the main network request provider
 * Dagger mapping module to construct a network
 * request provider when needed.
 *
 * Other build variants can choose a different
 * module implementation to effectively swap
 * out the default network request provider
 * implementation. For example, in the
 * Espresso automation tests, we want canned
 * network responses to be delivered when
 * running tests, so the network request
 * provider has a completely different
 * implementation while running the suite of
 * automated Espresso tests.
 */
@Module
public class NetworkRequestProviderModule {
    @Provides
    @Singleton
    @NonNull
    public NetworkRequestProvider provideNetworkRequestProvider(@NonNull Context applicationContext, @NonNull AppStringsProvider appStringsProvider) {
        return new DefaultNetworkRequestProvider(applicationContext, appStringsProvider);
    }
}
