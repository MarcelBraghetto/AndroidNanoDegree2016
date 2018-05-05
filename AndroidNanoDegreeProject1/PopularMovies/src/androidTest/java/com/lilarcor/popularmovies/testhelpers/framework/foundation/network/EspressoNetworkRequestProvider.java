package com.lilarcor.popularmovies.testhelpers.framework.foundation.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.lilarcor.popularmovies.framework.foundation.network.DefaultNetworkRequestProvider;
import com.lilarcor.popularmovies.framework.foundation.network.contracts.NetworkRequestProvider;

import javax.inject.Singleton;

/**
 * Created by Marcel Braghetto on 30/07/15.
 *
 * Custom network request provider implementation that
 * extends the normal implementation and alters its behaviour.
 *
 * The idea is to stub out and mock any network requests
 * that occur during the Espresso tests with pre determined
 * responses based on what url etc is requested.
 */
@Singleton
public class EspressoNetworkRequestProvider extends DefaultNetworkRequestProvider implements NetworkRequestProvider {
    public EspressoNetworkRequestProvider(@NonNull Context applicationContext) {
        super(applicationContext);
    }

    @Override
    public void startGetRequest(@NonNull String requestTag, @NonNull String url, int maxCacheAgeInHours, @NonNull RequestDelegate callbackDelegate) {
        if(requestTag.equals("PopularMoviesRequest")) {
            callbackDelegate.onRequestComplete(0, FakeResponsePopularMovies.CONTENT);
            return;
        }

        if(requestTag.equals("TopRatedMoviesRequest")) {
            callbackDelegate.onRequestComplete(0, FakeResponseTopRatedMovies.CONTENT);
            return;
        }

        callbackDelegate.onRequestFailed();
    }
}