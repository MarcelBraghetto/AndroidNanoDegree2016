package com.lilarcor.popularmovies.testhelpers.framework.foundation.network.dagger;

import android.content.Context;
import android.support.annotation.NonNull;

import com.lilarcor.popularmovies.framework.foundation.appstrings.contracts.AppStringsProvider;
import com.lilarcor.popularmovies.framework.foundation.network.contracts.NetworkRequestProvider;
import com.lilarcor.popularmovies.framework.foundation.network.dagger.NetworkRequestProviderModule;
import com.lilarcor.popularmovies.testhelpers.framework.foundation.network.EspressoNetworkRequestProvider;

import dagger.Module;

/**
 * Created by Marcel Braghetto on 30/07/15.
 *
 * Custom Dagger module to represent the network
 * request provider for use in the Espresso test
 * suites. This is then used when mapping the
 * network request provider contract to the concrete
 * implementation in the application start up.
 */
@Module
public class EspressoNetworkRequestModule extends NetworkRequestProviderModule {
    @NonNull
    @Override
    public NetworkRequestProvider provideNetworkRequestProvider(@NonNull Context applicationContext, @NonNull AppStringsProvider appStringsProvider) {
        return new EspressoNetworkRequestProvider();
    }
}