package com.lilarcor.popularmovies.framework.foundation.dagger;

import android.content.Context;

import com.lilarcor.popularmovies.features.application.MainApp;
import com.lilarcor.popularmovies.features.home.ui.HomeActivity;
import com.lilarcor.popularmovies.features.moviedetails.ui.MovieDetailsFragment;
import com.lilarcor.popularmovies.features.moviereviews.ui.MovieReviewsFragment;
import com.lilarcor.popularmovies.features.moviescollection.ui.MoviesCollectionFragment;
import com.lilarcor.popularmovies.framework.foundation.appstrings.contracts.AppStringsProvider;
import com.lilarcor.popularmovies.framework.foundation.device.contracts.DeviceInfoProvider;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusProvider;
import com.lilarcor.popularmovies.framework.foundation.network.contracts.NetworkRequestProvider;
import com.lilarcor.popularmovies.framework.foundation.network.dagger.NetworkRequestProviderModule;
import com.lilarcor.popularmovies.framework.foundation.threadutils.contracts.ThreadUtilsProvider;
import com.lilarcor.popularmovies.framework.movies.provider.contracts.MoviesProvider;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Marcel Braghetto on 12/07/15.
 *
 * Framework level registration of all Dagger classes
 * that need to be part of the dependency graph.
 */
@Singleton
@Component(modules = {
        AppDaggerModule.class,
        NetworkRequestProviderModule.class
})
public interface AppComponent {
    /**
     * Inject the given application object.
     *
     * @param mainApplication to inject.
     */
    void inject(MainApp mainApplication);

    /**
     * Inject the given HomeActivity instance.
     *
     * @param homeActivity instance to inject.
     */
    void inject(HomeActivity homeActivity);

    /**
     * Inject a MoviesCollectionFragment instance.
     *
     * @param moviesCollectionFragment instance to inject.
     */
    void inject(MoviesCollectionFragment moviesCollectionFragment);

    /**
     * Inject a MovieDetailsActivity instance.
     *
     * @param movieDetailsActivity instance to inject.
     */
    void inject(MovieDetailsFragment movieDetailsActivity);

    /**
     * Inject a MovieReviewsFragment instance.
     *
     * @param movieReviewsFragment instance to inject.
     */
    void inject(MovieReviewsFragment movieReviewsFragment);

    /**
     * Resolve the application context.
     *
     * @return application context.
     */
    Context getApplicationContext();

    /**
     * Resolve the network request provider.
     *
     * @return resolved network request provider.
     */
    NetworkRequestProvider getNetworkRequestProvider();

    /**
     * Resolve the event bus provider.
     *
     * @return resolved event bus provider.
     */
    EventBusProvider getEventBusProvider();

    /**
     * Resolve the thread utils provider.
     *
     * @return resolved thread utils provider.
     */
    ThreadUtilsProvider getThreadUtilsProvider();

    /**
     * Resolve the movies provider.
     *
     * @return resolved movies provider.
     */
    MoviesProvider getMoviesProvider();

    /**
     * Resolve the device info provider.
     *
     * @return resolved device info provider.
     */
    DeviceInfoProvider getDeviceInfoProvider();

    /**
     * Resolve the app strings provider.
     *
     * @return resolved app strings provider.
     */
    AppStringsProvider getAppStringsProvider();
}