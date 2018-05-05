package com.lilarcor.popularmovies.features.moviedetails.logic;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.framework.foundation.appstrings.contracts.AppStringsProvider;
import com.lilarcor.popularmovies.framework.movies.models.Movie;
import com.lilarcor.popularmovies.framework.movies.provider.contracts.MoviesProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by Marcel Braghetto on 19/07/15.
 *
 * Controller logic for parsing and rendering a movie
 * and its details (if possible).
 */
public class MovieDetailsController {
    //region Public fields
    public static final String ARG_MOVIE_ID = "MDCMI";
    //endregion

    //region Private fields
    private final MoviesProvider mMoviesProvider;
    private final AppStringsProvider mAppStringsProvider;

    private ControllerDelegate mDelegate;
    private Movie mMovie;
    //endregion

    //region Public methods
    @Inject
    public MovieDetailsController(@NonNull MoviesProvider moviesProvider,
                                  @NonNull AppStringsProvider appStringsProvider) {
        mMoviesProvider = moviesProvider;
        mAppStringsProvider = appStringsProvider;

        mMovie = new Movie();
    }

    /**
     * Initialise and connect the controller to the host environment. Also expect to
     * receive a bundle of arguments to indicate what 'filter' to apply.
     *
     * @param delegate to send commands to.
     * @param intent to parse for configuration.
     */
    public void initController(@NonNull ControllerDelegate delegate, @Nullable Intent intent) {
        mDelegate = delegate;

        initScreen(intent);
    }

    public void headerImageFailedToLoad() {
        mDelegate.hideHeaderImageProgressIndicator();
        mDelegate.showHeaderImageFailedIndicator();
    }

    /**
     * User selects the toggle favourite button, to
     * add or remove this movie to their favourite
     * movies collection.
     */
    public void toggleFavouriteSelected() {
        String wordFavourites = mAppStringsProvider.getString(R.string.word_favourites);

        if(mMovie.isFavourite()) {
            mMovie.setIsFavourite(false);
            mMoviesProvider.removeMovieFromFavourites(mMovie.getMovieId());
            mDelegate.showSnackbar(mAppStringsProvider.getString(R.string.favourite_removed_message, mMovie.getTitle(), wordFavourites));
        } else {
            mMovie.setIsFavourite(true);
            mMoviesProvider.addMovieToFavourites(mMovie.getMovieId());
            mDelegate.showSnackbar(mAppStringsProvider.getString(R.string.favourite_added_message, mMovie.getTitle(), wordFavourites));
        }

        mDelegate.refreshFloatingActionButton(mMovie.isFavourite());
    }
    //endregion

    //region Private methods
    /**
     * A bundle should have been passed in via the fragments arguments and should
     * contain the relevant key/value pair(s).
     *
     * @param intent containing the properties of this controller instance.
     */
    private void initScreen(Intent intent) {
        // If the bundle arguments were null or don't contain the filter key, then the developer did something dumb, so punish them!
        if(intent == null || !intent.hasExtra(ARG_MOVIE_ID)) {
            throw new IllegalArgumentException("Invalid use of MovieDetailsController - you MUST pass a valid movie id in via argument bundle.");
        }

        int movieId = intent.getIntExtra(ARG_MOVIE_ID, -1);
        mMovie = mMoviesProvider.getMovieWithId(movieId);

        // Movie couldn't be found in the database - this is
        // extremely unlikely be programmatically possible.
        if(mMovie == null) {
            mDelegate.finishActivity();
            return;
        }

        mDelegate.hideHeaderImageFailedIndicator();
        mDelegate.showHeaderImageProgressIndicator();
        mDelegate.setActivityTitle(mMovie.getTitle());
        mDelegate.refreshFloatingActionButton(mMovie.isFavourite());

        if(TextUtils.isEmpty(mMovie.getBackdropImageUrl())) {
            headerImageFailedToLoad();
        } else {
            mDelegate.loadHeaderImage(mMovie.getBackdropImageUrl());
        }

        int voteAverage = Math.round((mMovie.getVoteAverage() / 10f) * 100f);
        mDelegate.setVoteAverageText(mAppStringsProvider.getString(R.string.movie_details_vote_average, voteAverage));
        mDelegate.setVoteTotalText(mAppStringsProvider.getString(R.string.movie_details_vote_total, mMovie.getVoteCount()));

        Date releaseDate;

        try {
            releaseDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(mMovie.getReleaseDate());
        } catch (ParseException e) {
            releaseDate = null;
        }

        if(releaseDate == null) {
            mDelegate.setReleaseDateText(mAppStringsProvider.getString(R.string.movie_details_release_date_unknown));
        } else {
            mDelegate.setReleaseDateText(new SimpleDateFormat("EEEE d MMMM yyyy", Locale.ENGLISH).format(releaseDate));
        }

        mDelegate.setOverviewText(mMovie.getOverview());
    }
    //endregion

    //region Controller delegate contract
    public interface ControllerDelegate {
        /**
         * Assign the given title to the activity.
         *
         * @param title to display.
         */
        void setActivityTitle(@NonNull String title);

        /**
         * Initiate the loading of the large 'header'
         * image for the movie.
         *
         * @param imageUrl to load and display.
         */
        void loadHeaderImage(@NonNull String imageUrl);

        /**
         * Show the progress indicator while the header
         * image is loading.
         */
        void showHeaderImageProgressIndicator();

        /**
         * Hide the progress indicator for the header
         * image.
         */
        void hideHeaderImageProgressIndicator();

        /**
         * If there was a problem while loading the
         * header image, show the failure indicator.
         */
        void showHeaderImageFailedIndicator();

        /**
         * Hide the header image failure indicator.
         */
        void hideHeaderImageFailedIndicator();

        /**
         * Exit out of the activity.
         */
        void finishActivity();

        /**
         * Display a Snackbar to the user with the given text.
         *
         * @param text to display in the Snackbar.
         */
        void showSnackbar(@NonNull String text);

        /**
         * If the movie is a favourite, the floating action button
         * should show an icon to reflect this.
         *
         * @param isMovieFavourite if the movie is a user favourite.
         */
        void refreshFloatingActionButton(boolean isMovieFavourite);

        /**
         * Display the given vote average text.
         *
         * @param text to display.
         */
        void setVoteAverageText(@NonNull String text);

        /**
         * Display the given vote total text.
         *
         * @param text to display.
         */
        void setVoteTotalText(@NonNull String text);

        /**
         * Display the given release date text.
         *
         * @param text to display.
         */
        void setReleaseDateText(@NonNull String text);

        /**
         * Display the given overview text.
         *
         * @param text to display.
         */
        void setOverviewText(@NonNull String text);
    }
    //endregion
}
