package com.lilarcor.popularmovies.features.moviescollection.logic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.features.home.logic.events.ShowTabletMovieDetailsEvent;
import com.lilarcor.popularmovies.features.moviedetails.logic.MovieDetailsController;
import com.lilarcor.popularmovies.features.moviedetails.ui.MovieDetailsActivity;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.common.BaseMoviesRequestEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.popularmovies.PopularMoviesRequestCompleteEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.popularmovies.PopularMoviesRequestFailedEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.popularmovies.PopularMoviesRequestMoreDataEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.popularmovies.PopularMoviesSwipeToRefreshEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.topratedmovies.TopRatedMoviesRequestCompleteEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.topratedmovies.TopRatedMoviesRequestFailedEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.topratedmovies.TopRatedMoviesRequestMoreDataEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.topratedmovies.TopRatedMoviesSwipeToRefreshEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.models.MoviesCollectionFilter;
import com.lilarcor.popularmovies.framework.foundation.appstrings.contracts.AppStringsProvider;
import com.lilarcor.popularmovies.framework.foundation.device.contracts.DeviceInfoProvider;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusProvider;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusSubscriber;
import com.lilarcor.popularmovies.framework.movies.models.Movie;
import com.lilarcor.popularmovies.framework.movies.provider.contracts.MoviesProvider;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by Marcel Braghetto on 15/07/15.
 *
 * Controller for the movies collection fragment which can
 * run in the different movie filter modes:
 *
 * 1. Popular
 * 2. Top Rated
 * 3. Favourites
 *
 */
public final class MoviesCollectionController implements EventBusSubscriber {
    //region Public fields
    public static final String ARG_MOVIES_FILTER = "MCCMMF";
    //endregion

    //region Private fields
    private final Context mApplicationContext;
    private final MoviesProvider mMoviesProvider;
    private final EventBusProvider mEventBusProvider;
    private final DeviceInfoProvider mDeviceInfoProvider;
    private final AppStringsProvider mAppStringsProvider;

    private MoviesCollectionFilter mFilter;
    private ControllerDelegate mDelegate;
    //endregion

    //region Public methods
    @Inject
    public MoviesCollectionController(@NonNull Context applicationContext,
                                      @NonNull MoviesProvider moviesProvider,
                                      @NonNull EventBusProvider eventBusProvider,
                                      @NonNull DeviceInfoProvider deviceInfoProvider,
                                      @NonNull AppStringsProvider appStringsProvider) {

        mMoviesProvider = moviesProvider;
        mApplicationContext = applicationContext;
        mEventBusProvider = eventBusProvider;
        mDeviceInfoProvider = deviceInfoProvider;
        mAppStringsProvider = appStringsProvider;
    }

    /**
     * Initialise and connect the controller to the host environment. Also expect to
     * receive a bundle of arguments to indicate what 'filter' to apply.
     *
     * @param delegate to send commands to.
     * @param arguments to parse for configuration.
     * @param collectionViewWidth the measured width of the collection view.
     */
    public void initController(@NonNull ControllerDelegate delegate, int collectionViewWidth, @Nullable Bundle arguments) {
        mDelegate = delegate;
        parseArguments(arguments);
        configureScreen(collectionViewWidth);
    }

    /**
     * The lifecycle onStarted should call this method.
     */
    public void screenStarted() {
        subscribeToEventBus();
    }

    /**
     * The lifecycle onStopped should call this method.
     */
    public void screenStopped() {
        unsubscribeFromEventBus();
    }

    /**
     * User initiates a swipe to refresh interaction.
     *
     * Note that the favourites fragment should not have
     * the swipe to refresh mechanism available so we shouldn't
     * see a user initiated swipe to refresh on the favourites
     * filter (what would be the point?)
     */
    public void swipeToRefreshInitiated() {
        switch (mFilter) {
            case Popular:
                mEventBusProvider.postEvent(new PopularMoviesSwipeToRefreshEvent());
                break;
            case TopRated:
                mEventBusProvider.postEvent(new TopRatedMoviesSwipeToRefreshEvent());
                break;
        }
    }

    /**
     * User selects a movie from the collection view, which will trigger
     * the movie details screen to be presented for that movie.
     *
     * If the user is on a large device, we will broadcast an event with
     * the movie details fragment to display, instead of starting a new
     * activity intent.
     *
     * @param movieId of the movie to display details for.
     */
    public void movieIdSelected(int movieId) {
        if(mDeviceInfoProvider.isLargeDevice()) {
            mEventBusProvider.postEvent(new ShowTabletMovieDetailsEvent(movieId));
            return;
        }

        Intent intent = new Intent(mApplicationContext, MovieDetailsActivity.class);
        intent.putExtra(MovieDetailsController.ARG_MOVIE_ID, movieId);
        mDelegate.startActivityIntent(intent);
    }

    /**
     * User selects the toggle favourite button for a given movie id,
     * which will toggle whether or not that movie is in the users
     * 'favourites' collection.
     *
     * @param movieId to toggle as a favourite movie.
     */
    public void toggleFavouriteSelected(int movieId) {
        Movie movie = mMoviesProvider.getMovieWithId(movieId);

        if(movie != null) {
            String wordFavourites = mAppStringsProvider.getString(R.string.word_favourites);

            if(movie.isFavourite()) {
                mMoviesProvider.removeMovieFromFavourites(movie.getMovieId());
                mDelegate.showSnackbar(mAppStringsProvider.getString(R.string.favourite_removed_message, movie.getTitle(), wordFavourites));
            } else {
                mMoviesProvider.addMovieToFavourites(movie.getMovieId());
                mDelegate.showSnackbar(mAppStringsProvider.getString(R.string.favourite_added_message, movie.getTitle(), wordFavourites));
            }
        }
    }

    /**
     * The adapter will signal a request for more data when the
     * user scrolls the collection to particular intervals through
     * the content.
     *
     * @param fromRequestPage the 'request page' of the collection item
     *                        that initiated the request for more data,
     *                        which is important for determining whether
     *                        or not a network operation is required to
     *                        fulfill the pre loading of the next
     *                        page of request data.
     */
    public void requestMoreData(int fromRequestPage) {
        switch(mFilter) {
            case Popular:
                mEventBusProvider.postEvent(new PopularMoviesRequestMoreDataEvent(fromRequestPage));
                break;
            case TopRated:
                mEventBusProvider.postEvent(new TopRatedMoviesRequestMoreDataEvent(fromRequestPage));
                break;
        }
    }
    //endregion

    //region Private methods
    /**
     * A bundle should have been passed in via the fragments arguments and should
     * contain the relevant key/value pair(s).
     *
     * @param arguments containing the properties of this controller instance.
     */
    private void parseArguments(Bundle arguments) {
        // If the bundle arguments were null or don't contain the filter key, then the developer did something dumb, so punish them!
        if(arguments == null || !arguments.containsKey(ARG_MOVIES_FILTER)) {
            throw new IllegalArgumentException("Invalid use of MoviesCollectionController - you MUST pass a valid filter in via argument bundle.");
        }

        // Retrieve the filter from the given bundle arguments.
        mFilter = (MoviesCollectionFilter) arguments.getSerializable(ARG_MOVIES_FILTER);
    }

    /**
     * Configure the screen for use.
     */
    private void configureScreen(int collectionViewWidth) {
        // The 'favourites' filter has no need for swipe to refresh behaviour.
        if(mFilter == MoviesCollectionFilter.Favourites) {
            mDelegate.disableSwipeRefresh();
        }

        // We need to calculate the desired number of grid columns to populate the grid with, as well
        // as work out what the desired height of each image should be so it respects its aspect ratio
        // for the width it will be allocated based on the number of columns and the current window
        // width. This will allow the movie images to scale their size in a uniform way, without being
        // distorted on the X or Y axis. Note: the 'movies_collection_item_poster_***' dimensions are
        // overridden for large devices, so the movie images are overall larger on tablets (and therefore
        // there will be less columns on tablets).
        int baseLinePosterWidth = mApplicationContext.getResources().getDimensionPixelSize(R.dimen.movies_collection_item_poster_width);
        int baseLinePosterHeight = mApplicationContext.getResources().getDimensionPixelSize(R.dimen.movies_collection_item_poster_height);
        int columns = collectionViewWidth / baseLinePosterWidth;
        float spanWidth = baseLinePosterWidth + ((collectionViewWidth - columns * baseLinePosterWidth) / columns);
        float ratio = spanWidth / baseLinePosterWidth;
        int desiredMovieImageHeight = (int) (ratio * baseLinePosterHeight);

        mDelegate.initScreen(mMoviesProvider.getMovieCollectionUri(mFilter), mFilter, columns, desiredMovieImageHeight);
    }
    //endregion

    //region Event bus registration
    @Override
    public void subscribeToEventBus() {
        mEventBusProvider.subscribe(this);
    }

    @Override
    public void unsubscribeFromEventBus() {
        mEventBusProvider.unsubscribe(this);
    }
    //endregion

    //region Popular movies events
    /**
     * Received a broadcast event that the popular movies request has failed.
     *
     * @param event that was broadcast.
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onPopularMoviesRequestFailedEvent(@NonNull PopularMoviesRequestFailedEvent event) {
        // We only care about this if our filter is the same as the event's filter.
        if(event.getRequestPage() != BaseMoviesRequestEvent.REQUEST_PAGE_NONE && mFilter == MoviesCollectionFilter.Popular) {
            mDelegate.hideSwipeIndicator();
        }
    }

    /**
     * Received a broadcast event that the popular movies request has completed.
     *
     * @param event that was broadcast.
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onPopularMoviesRequestCompleteEvent(@NonNull PopularMoviesRequestCompleteEvent event) {
        // We only care about this if our filter is the same as the event's filter.
        if(mFilter == MoviesCollectionFilter.Popular) {
            mDelegate.hideSwipeIndicator();
        }
    }
    //endregion

    //region Top rated movies events
    /**
     * Received a broadcast event that the top rated movies request has failed.
     *
     * @param event that was broadcast.
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onTopRatedMoviesRequestFailedEvent(@NonNull TopRatedMoviesRequestFailedEvent event) {
        // We only care about this if our filter is the same as the event's filter.
        if(event.getRequestPage() != BaseMoviesRequestEvent.REQUEST_PAGE_NONE && mFilter == MoviesCollectionFilter.TopRated) {
            mDelegate.hideSwipeIndicator();
        }
    }

    /**
     * Received a broadcast event that the top rated movies request has completed.
     *
     * @param event that was broadcast.
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onTopRatedMoviesRequestCompleteEvent(@NonNull TopRatedMoviesRequestCompleteEvent event) {
        // We only care about this if our filter is the same as the event's filter.
        if(mFilter == MoviesCollectionFilter.TopRated) {
            mDelegate.hideSwipeIndicator();
        }
    }
    //endregion

    //region Controller delegate contract
    public interface ControllerDelegate {
        /**
         * Initialise the screen and start populating the collection
         * data into the adapter.
         *
         * @param dataSourceUri the URI to load the collection data from.
         * @param filter the 'filter' to apply to the collection data for
         *               when it is being rendered by the screen and the
         *               adapter.
         * @param numColumns the number of columns to render in the grid
         *                   layout view, which will be different depending
         *                   on the orientation of the device and the size
         *                   of the screen.
         * @param desiredMovieImageHeightPx the desired height in pixels to
         *                                  apply to each movie image in the
         *                                  grid view, calculated by the number
         *                                  of columns and the aspect ratio of
         *                                  the original image size.
         */
        void initScreen(@NonNull Uri dataSourceUri, @NonNull MoviesCollectionFilter filter, int numColumns, int desiredMovieImageHeightPx);

        /**
         * Hide the swipe to refresh indicator from the screen.
         */
        void hideSwipeIndicator();

        /**
         * Completely disable the swipe to refresh functionality - for
         * example the 'favourites' filter mode should not be able to
         * perform any swipe to refresh actions.
         */
        void disableSwipeRefresh();

        /**
         * Initiate an activity with the given intent, typically for
         * opening the details of a movie, where the movie data is
         * packaged into the intent extras.
         *
         * @param intent to start.
         */
        void startActivityIntent(@NonNull Intent intent);

        /**
         * Display a Snackbar to the user with the given text.
         *
         * @param text to display in the Snackbar.
         */
        void showSnackbar(@NonNull String text);
    }
    //endregion
}
