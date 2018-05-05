package com.lilarcor.popularmovies.features.home.logic;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.features.home.logic.events.ShowTabletMovieDetailsEvent;
import com.lilarcor.popularmovies.features.moviedetails.ui.MovieDetailsFragment;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.common.BaseMoviesRequestEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.popularmovies.PopularMoviesRequestCompleteEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.popularmovies.PopularMoviesRequestFailedEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.topratedmovies.TopRatedMoviesRequestFailedEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.models.MoviesCollectionFilter;
import com.lilarcor.popularmovies.features.moviescollection.ui.MoviesCollectionFragment;
import com.lilarcor.popularmovies.framework.foundation.appstrings.contracts.AppStringsProvider;
import com.lilarcor.popularmovies.framework.foundation.device.contracts.DeviceInfoProvider;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusProvider;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusSubscriber;
import com.lilarcor.popularmovies.framework.movies.provider.contracts.MoviesProvider;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by Marcel Braghetto on 11/07/15.
 *
 * Logic controller for home screen.
 */
public class HomeController implements EventBusSubscriber {
    //region Private fields
    private static final int INVALID_MOVIE_ID = -1;

    private static int sCurrentlyDisplayedMovieId = INVALID_MOVIE_ID;

    private ControllerDelegate mDelegate;

    private final AppStringsProvider mAppStringsProvider;
    private final EventBusProvider mEventBusProvider;
    private final DeviceInfoProvider mDeviceInfoProvider;
    private final MoviesProvider mMoviesProvider;
    //endregion

    //region Public methods.
    @Inject
    public HomeController(@NonNull AppStringsProvider appStringsProvider,
                          @NonNull EventBusProvider eventBusProvider,
                          @NonNull DeviceInfoProvider deviceInfoProvider,
                          @NonNull MoviesProvider moviesProvider) {

        mAppStringsProvider = appStringsProvider;
        mEventBusProvider = eventBusProvider;
        mDeviceInfoProvider = deviceInfoProvider;
        mMoviesProvider = moviesProvider;
    }

    /**
     * Initialise and connect the controller to the host environment.
     *
     * @param delegate to send commands to.
     */
    public void initController(@NonNull ControllerDelegate delegate) {
        mDelegate = delegate;
        populateScreen();
    }
    //endregion

    //region Private methods
    private void populateScreen() {
        if(mDeviceInfoProvider.isLargeDevice()) {
            mDelegate.setActionBarTitle("");
        } else {
            mDelegate.setActionBarTitle(mAppStringsProvider.getString(R.string.app_name));
        }

        Fragment[] fragments = new Fragment[] {
                MoviesCollectionFragment.newInstance(MoviesCollectionFilter.Popular),
                MoviesCollectionFragment.newInstance(MoviesCollectionFilter.TopRated),
                MoviesCollectionFragment.newInstance(MoviesCollectionFilter.Favourites)
        };

        String[] fragmentTitles = new String[] {
                mAppStringsProvider.getString(R.string.home_view_pager_title_popular),
                mAppStringsProvider.getString(R.string.home_view_pager_title_top_rated),
                mAppStringsProvider.getString(R.string.home_view_pager_title_favourites)
        };

        mDelegate.initialiseViewPager(fragments, fragmentTitles);
    }

    public void screenStarted() {
        subscribeToEventBus();
    }

    public void screenStopped() {
        unsubscribeFromEventBus();
    }

    @Override
    public void subscribeToEventBus() {
        mEventBusProvider.subscribe(this);
    }

    @Override
    public void unsubscribeFromEventBus() {
        mEventBusProvider.unsubscribe(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onShowTabletMovieDetailsEvent(@NonNull ShowTabletMovieDetailsEvent event) {
        mDelegate.loadTabletMovieDetailsFragment(event.createFragment());
    }

    /**
     * If we are on a large device, and receive the popular movies request completion
     * event, AND we have never automatically populated the details pane, then attempt
     * to find the movie id of the first popular movie from the content provider, and
     * automatically populate it into the content pane.
     *
     * The movie id is then stored in a static member variable, so subsequent broadcasts
     * of the event don't cause the content pane to continually reload.
     *
     * @param event that was broadcast.
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void onPopularMoviesRequestCompleteEvent(@NonNull PopularMoviesRequestCompleteEvent event) {
        if(mDeviceInfoProvider.isLargeDevice() && sCurrentlyDisplayedMovieId == INVALID_MOVIE_ID) {
            int movieId = mMoviesProvider.getFirstSavedPopularMovieId();

            if(movieId != INVALID_MOVIE_ID) {
                sCurrentlyDisplayedMovieId = movieId;
                mDelegate.loadTabletMovieDetailsFragment(MovieDetailsFragment.newInstance(movieId));
            }
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onPopularMoviesRequestFailedEvent(@NonNull PopularMoviesRequestFailedEvent event) {
        if(event.getRequestPage() != BaseMoviesRequestEvent.REQUEST_PAGE_NONE) {
            mDelegate.showSnackbar(mAppStringsProvider.getString(R.string.movies_collection_request_failed_message));
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onTopRatedMoviesRequestFailedEvent(@NonNull TopRatedMoviesRequestFailedEvent event) {
        if(event.getRequestPage() != BaseMoviesRequestEvent.REQUEST_PAGE_NONE) {
            mDelegate.showSnackbar(mAppStringsProvider.getString(R.string.movies_collection_request_failed_message));
        }
    }
    //endregion

    //region Controller delegate contract
    /**
     * Controller delegate contract for the home feature.
     */
    public interface ControllerDelegate {
        /**
         * Set the given string as current action bar
         * title.
         *
         * @param title to display.
         */
        void setActionBarTitle(@NonNull String title);

        /**
         * Given the array of fragments and fragment titles,
         * populate the view pager with them.
         *
         * @param fragments to display in the view pager.
         * @param fragmentTitles to associate with each fragment.
         */
        void initialiseViewPager(@NonNull Fragment[] fragments, @NonNull String[] fragmentTitles);

        /**
         * Replace the fragment in the tablet layout with the given
         * fragment.
         *
         * @param fragment to replace in the tablet layout.
         */
        void loadTabletMovieDetailsFragment(@NonNull Fragment fragment);

        void showSnackbar(@NonNull String text);
    }
    //endregion
}
