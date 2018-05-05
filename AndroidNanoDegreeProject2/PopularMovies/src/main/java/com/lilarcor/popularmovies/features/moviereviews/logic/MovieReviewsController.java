package com.lilarcor.popularmovies.features.moviereviews.logic;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.framework.foundation.appstrings.contracts.AppStringsProvider;
import com.lilarcor.popularmovies.framework.foundation.device.contracts.DeviceInfoProvider;
import com.lilarcor.popularmovies.framework.movies.provider.contracts.MoviesProvider;

import javax.inject.Inject;

/**
 * Created by Marcel Braghetto on 9/08/15.
 *
 * Controller logic for the movie reviews screen.
 */
public class MovieReviewsController {
    //region Public fields
    public static final String ARG_MOVIE_ID = "MRCMI";
    //endregion

    private final AppStringsProvider mAppStringsProvider;
    private final MoviesProvider mMoviesProvider;
    private final DeviceInfoProvider mDeviceInfoProvider;

    private ControllerDelegate mDelegate;

    @Inject
    public MovieReviewsController(@NonNull AppStringsProvider appStringsProvider,
                                  @NonNull MoviesProvider moviesProvider,
                                  @NonNull DeviceInfoProvider deviceInfoProvider) {

        mAppStringsProvider = appStringsProvider;
        mMoviesProvider = moviesProvider;
        mDeviceInfoProvider = deviceInfoProvider;
    }

    public void initController(@NonNull ControllerDelegate delegate, @Nullable Bundle arguments) {
        mDelegate = delegate;
        mDelegate.setActionBarTitle(mAppStringsProvider.getString(R.string.movie_reviews_title));

        initScreen(arguments);
    }

    private void initScreen(Bundle arguments) {
        // If the bundle arguments were null or don't contain the filter key, then the developer did something dumb, so punish them!
        if (arguments == null || !arguments.containsKey(ARG_MOVIE_ID)) {
            throw new IllegalArgumentException("Invalid use of MovieReviewsController - you MUST pass a valid movie id in via argument bundle.");
        }

        if(mDeviceInfoProvider.isLargeDevice()) {
            mDelegate.showCloseButton();
        }

        mDelegate.populateReviews(mMoviesProvider.getMovieReviewsUri(arguments.getInt(ARG_MOVIE_ID)));
    }

    //region Controller delegate contract
    /**
     * Controller delegate for the movie reviews screen.
     */
    public interface ControllerDelegate {
        /**
         * Set the given title on the action bar.
         *
         * @param title to display.
         */
        void setActionBarTitle(@NonNull String title);

        /**
         * On large devices a close button is displayed
         * in the action bar.
         */
        void showCloseButton();

        /**
         * Populate the screen with the given content Uri
         * which should represent the collection of user
         * reviews for the given movie id.
         *
         * @param dataSourceUri containing user reviews.
         */
        void populateReviews(@NonNull Uri dataSourceUri);
    }
    //endregion
}
