package com.lilarcor.popularmovies.testhelpers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.lilarcor.popularmovies.framework.foundation.network.contracts.NetworkRequestProvider;
import com.lilarcor.popularmovies.framework.movies.provider.contracts.MoviesProvider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Marcel Braghetto on 11/07/15.
 *
 * Colletion of utility methods to assist with calling common Espresso commands.
 */
public class EspressoHelper {
    private EspressoHelper() { }

    public static void resetApplicationData() {
        MoviesProvider moviesProvider = EspressoMainApp.getDagger().getMoviesProvider();

        moviesProvider.deleteAllPopularMovies();
        moviesProvider.deleteAllTopRatedMovies();
        moviesProvider.deleteAllMovies();

        NetworkRequestProvider networkRequestProvider = EspressoMainApp.getDagger().getNetworkRequestProvider();
        networkRequestProvider.clearResponseCache();
    }

    public static void verifyActionBarTitle(@NonNull Activity activity, @NonNull String title) throws NullPointerException {
        String actionBarTitle = "";

        if(activity instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity)activity).getSupportActionBar();

            if(actionBar != null && actionBar.getTitle() != null) {
                actionBarTitle = actionBar.getTitle().toString();
            }
        }

        assertThat(actionBarTitle, is(title));
    }
}
