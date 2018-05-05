package com.lilarcor.popularmovies.features.application.logic;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.lilarcor.popularmovies.features.moviescollection.logic.events.common.BaseMoviesRequestEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.popularmovies.PopularMoviesRequestCompleteEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.popularmovies.PopularMoviesRequestFailedEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.popularmovies.PopularMoviesRequestMoreDataEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.popularmovies.PopularMoviesSwipeToRefreshEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.topratedmovies.TopRatedMoviesRequestCompleteEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.topratedmovies.TopRatedMoviesRequestFailedEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.topratedmovies.TopRatedMoviesRequestMoreDataEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.topratedmovies.TopRatedMoviesSwipeToRefreshEvent;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusProvider;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusSubscriber;
import com.lilarcor.popularmovies.framework.foundation.network.contracts.NetworkRequestProvider;
import com.lilarcor.popularmovies.framework.foundation.network.contracts.NetworkRequestProvider.CacheRequestPolicy;
import com.lilarcor.popularmovies.framework.foundation.network.models.MoviesDTO;
import com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract;
import com.lilarcor.popularmovies.framework.movies.models.Movie;
import com.lilarcor.popularmovies.framework.movies.provider.contracts.MoviesProvider;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.lilarcor.popularmovies.framework.foundation.network.contracts.NetworkRequestProvider.CacheRequestPolicy.Instant;
import static com.lilarcor.popularmovies.framework.foundation.network.contracts.NetworkRequestProvider.CacheRequestPolicy.Normal;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.PopularMovies.COLUMN_MOVIE_ID;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.PopularMovies.COLUMN_RESULT_PAGE;

/**
 * Created by Marcel Braghetto on 15/07/15.
 *
 * Main application logic controller that is responsible for calling
 * network resources and updating data stores locally. Is also a
 * receiver of certain event bus events to trigger application actions.
 */
public final class MainAppController implements EventBusSubscriber {
    //region Private fields
    private final NetworkRequestProvider mNetworkRequestProvider;
    private final MoviesProvider mMoviesProvider;
    private final EventBusProvider mEventBusProvider;

    private boolean mPopularMoviesRequestInProgress;
    private boolean mTopRatedMoviesRequestInProgress;

    private int mCurrentPopularMoviesRequestPage;
    private int mLastFailedPopularMoviesRequestPage;

    private int mCurrentTopRatedMoviesRequestPage;
    private int mLastFailedTopRatedMoviesRequestPage;
    //endregion

    //region Public methods
    @Inject
    public MainAppController(@NonNull NetworkRequestProvider networkRequestProvider,
                             @NonNull MoviesProvider moviesProvider,
                             @NonNull EventBusProvider eventBusProvider) {

        mNetworkRequestProvider = networkRequestProvider;
        mMoviesProvider = moviesProvider;
        mEventBusProvider = eventBusProvider;

        mLastFailedPopularMoviesRequestPage = BaseMoviesRequestEvent.REQUEST_PAGE_NONE;
        mLastFailedTopRatedMoviesRequestPage = BaseMoviesRequestEvent.REQUEST_PAGE_NONE;

        mCurrentPopularMoviesRequestPage = 1;
        mCurrentTopRatedMoviesRequestPage = 1;

        subscribeToEventBus();

        loadPopularMovies(Normal, 1);
        loadTopRatedMovies(Normal, 1);
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

    //region Home screen events
    /**
     * We need to 'produce' the popular movies request failed event,
     * because there is a possibility of the connection failure to occur
     * before the rest of the movie collections UI has initialised
     * and subscribed to the event bus.
     *
     * @return new instance of the popular movies request failed event
     * which is populated with the last failed 'request page', which
     * might be none to indicate there is no failure to report.
     */
    @Produce
    @SuppressWarnings("unused")
    public PopularMoviesRequestFailedEvent onProducePopularMoviesRequestFailedEvent() {
        return new PopularMoviesRequestFailedEvent(mLastFailedPopularMoviesRequestPage);
    }
    //endregion

    //region Popular movies event bus
    /**
     * Received an event indicating the user has initiated a
     * swipe to refresh operation in the Popular Movies feature.
     *
     * We should attempt to reload the popular movies from the
     * server.
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void onPopularMoviesSwipeToRefreshEvent(@NonNull PopularMoviesSwipeToRefreshEvent event) {
        mNetworkRequestProvider.clearResponseCache();
        mCurrentPopularMoviesRequestPage = 1;
        loadPopularMovies(Instant, 1);

        // If the user tried to do a swipe to refresh on the popular movies, but the last
        // 'top movies' request had also failed on request page 1, then trigger it to start
        // as well.
        if(mLastFailedTopRatedMoviesRequestPage == 1) {
            onTopRatedMoviesSwipeToRefreshEvent(new TopRatedMoviesSwipeToRefreshEvent());
        }
    }

    /**
     * Received an event indicating the user has triggered a request
     * for more popular movies data - possibly by scrolling through
     * the existing data set.
     *
     * @param event that was broadcast.
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void onPopularMoviesRequestMoreDataEvent(@NonNull PopularMoviesRequestMoreDataEvent event) {
        loadPopularMovies(Normal, event.getFromRequestPage());
    }
    //endregion

    //region Top rated event bus
    /**
     * Received an event indicating the user has initiated a
     * swipe to refresh operation in the Top Rated Movies feature.
     *
     * We should attempt to reload the top rated movies from the
     * server.
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void onTopRatedMoviesSwipeToRefreshEvent(@NonNull TopRatedMoviesSwipeToRefreshEvent event) {
        mNetworkRequestProvider.clearResponseCache();
        mCurrentTopRatedMoviesRequestPage = 1;
        loadTopRatedMovies(Instant, 1);

        // If the user tried to do a swipe to refresh on the top rated movies, but the last
        // 'popular movies' request had also failed on request page 1, then trigger it to start
        // as well.
        if(mLastFailedPopularMoviesRequestPage == 1) {
            onPopularMoviesSwipeToRefreshEvent(new PopularMoviesSwipeToRefreshEvent());
        }
    }

    /**
     * Received an event indicating the user has triggered a request
     * for more top rated movies data - possibly by scrolling through
     * the existing data set.
     *
     * @param event that was broadcast.
     */
    @SuppressWarnings("unused")
    @Subscribe
    public void onTopRatedRequestMoreDataEvent(@NonNull TopRatedMoviesRequestMoreDataEvent event) {
        loadTopRatedMovies(Normal, event.getFromRequestPage());
    }

    /**
     * We need to 'produce' the top rated movies request failed event,
     * because there is a possibility of the connection failure to occur
     * before the rest of the movie collections UI has initialised
     * and subscribed to the event bus.
     *
     * @return new instance of the top rated movies request failed event
     * which is populated with the last failed 'request page', which
     * might be none to indicate there is no failure to report.
     */
    @Produce
    @SuppressWarnings("unused")
    public TopRatedMoviesRequestFailedEvent onProduceTopRatedMoviesRequestFailedEvent() {
        return new TopRatedMoviesRequestFailedEvent(mLastFailedTopRatedMoviesRequestPage);
    }
    //endregion

    //region Private methods
    /**
     * Build a request url to retrieve the 'popular' movies feed from the
     * Movies DB API.
     *
     * @param cacheRequestPolicy for this request to determine whether the response
     *                           will be served directly from the local cache or
     *                           from the server.
     * @param fromRequestPage the request 'page' of the collection that triggered this
     *                        attempt to load more popular movies. This logic will
     *                        keep the networking such that only the next page of movie
     *                        data will be preloaded in advance as the user scrolls.
     */
    private void loadPopularMovies(CacheRequestPolicy cacheRequestPolicy, int fromRequestPage) {
        // If there is already a 'popular movies' request in progress, exit.
        if(mPopularMoviesRequestInProgress) {
            return;
        }

        // If the request page of the trigger content is less than the request page
        // immediately before the next unloaded page, then exit.
        if(fromRequestPage < (mCurrentPopularMoviesRequestPage - 1)) {
            return;
        }

        // Mark the popular movies request as being in progress now.
        mPopularMoviesRequestInProgress = true;
        mLastFailedPopularMoviesRequestPage = BaseMoviesRequestEvent.REQUEST_PAGE_NONE;

        mNetworkRequestProvider.getPopularMovies(mCurrentPopularMoviesRequestPage, cacheRequestPolicy, new Callback<MoviesDTO>() {
            @Override
            public void success(MoviesDTO moviesRequestDTO, Response response) {
                Movie[] movies = moviesRequestDTO.getMovies();

                if (movies == null) {
                    handlePopularMoviesRequestFailed();
                    return;
                }

                // Only delete all the saved popular movies if this is the first page of results, which would only
                // occur the first time the load happens, or if the user forced a refresh.
                if (mCurrentPopularMoviesRequestPage <= 1) {
                    mMoviesProvider.deleteAllPopularMovies();
                }

                // Iterate through all the movie results in the response to create new popular movie records.
                ContentValues[] popularMoviesContentValues = new ContentValues[movies.length];
                int resultPage = moviesRequestDTO.getResultPage();

                for (int i = 0; i < movies.length; i++) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(COLUMN_MOVIE_ID, movies[i].getMovieId());
                    contentValues.put(COLUMN_RESULT_PAGE, resultPage);

                    popularMoviesContentValues[i] = contentValues;
                }

                mMoviesProvider.saveMovies(movies);
                mMoviesProvider.savePopularMovies(popularMoviesContentValues);

                mEventBusProvider.postEvent(new PopularMoviesRequestCompleteEvent());

                // At this point we can increment the current 'request page' to get ready for
                // pre-loading the next page of popular movies.
                mCurrentPopularMoviesRequestPage = resultPage + 1;
                mPopularMoviesRequestInProgress = false;
            }

            @Override
            public void failure(RetrofitError error) {
                handlePopularMoviesRequestFailed();
            }
        });
    }

    /**
     * If the popular movies request failed or there was a problem
     * parsing the response.
     */
    private void handlePopularMoviesRequestFailed() {
        mPopularMoviesRequestInProgress = false;
        mLastFailedPopularMoviesRequestPage = mCurrentPopularMoviesRequestPage;
        mEventBusProvider.postEvent(new PopularMoviesRequestFailedEvent(mCurrentPopularMoviesRequestPage));
    }

    /**
     * Build a request url to retrieve the 'top rated' movies feed from the
     * Movies DB API.
     *
     * @param cacheRequestPolicy for this request to determine whether the response
     *                           will be served directly from the local cache or
     *                           from the server.
     * @param fromRequestPage the request 'page' of the collection that triggered this
     *                        attempt to load more popular movies. This logic will
     *                        keep the networking such that only the next page of movie
     *                        data will be preloaded in advance as the user scrolls.
     */
    private void loadTopRatedMovies(CacheRequestPolicy cacheRequestPolicy, int fromRequestPage) {
        // If there is already a 'top rated movies' request in progress, exit.
        if(mTopRatedMoviesRequestInProgress) {
            return;
        }

        // If the request page of the trigger content is less than the request page
        // immediately before the next unloaded page, then exit.
        if(fromRequestPage < (mCurrentTopRatedMoviesRequestPage - 1)) {
            return;
        }

        // Mark the top rated movies request as being in progress now.
        mTopRatedMoviesRequestInProgress = true;
        mLastFailedTopRatedMoviesRequestPage = BaseMoviesRequestEvent.REQUEST_PAGE_NONE;

        mNetworkRequestProvider.getTopRatedMovies(mCurrentTopRatedMoviesRequestPage, cacheRequestPolicy, new Callback<MoviesDTO>() {
            @Override
            public void success(MoviesDTO moviesRequestDTO, Response response) {
                Movie[] movies = moviesRequestDTO.getMovies();

                if (movies == null) {
                    topRatedMoviesRequestFailed();
                    return;
                }

                // Only delete all the saved popular movies if this is the first page of results, which would only
                // occur the first time the load happens, or if the user forced a refresh.
                if (mCurrentTopRatedMoviesRequestPage <= 1) {
                    mMoviesProvider.deleteAllTopRatedMovies();
                }

                // Iterate through all the movie results in the response to create new top rated movie records.
                ContentValues[] topRatedMoviesContentValues = new ContentValues[movies.length];
                int resultPage = moviesRequestDTO.getResultPage();

                for (int i = 0; i < movies.length; i++) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MoviesContentContract.TopRatedMovies.COLUMN_MOVIE_ID, movies[i].getMovieId());
                    contentValues.put(MoviesContentContract.TopRatedMovies.COLUMN_RESULT_PAGE, resultPage);

                    topRatedMoviesContentValues[i] = contentValues;
                }

                mMoviesProvider.saveMovies(movies);
                mMoviesProvider.saveTopRatedMovies(topRatedMoviesContentValues);

                mEventBusProvider.postEvent(new TopRatedMoviesRequestCompleteEvent());

                // At this point we can increment the current 'request page' to get ready for
                // pre-loading the next page of top rated movies.
                mCurrentTopRatedMoviesRequestPage = resultPage + 1;
                mTopRatedMoviesRequestInProgress = false;
            }

            @Override
            public void failure(RetrofitError error) {
                topRatedMoviesRequestFailed();
            }
        });
    }

    /**
     * The top rated movies request failed or the response could
     * not be successfully parsed.
     */
    private void topRatedMoviesRequestFailed() {
        mTopRatedMoviesRequestInProgress = false;
        mLastFailedTopRatedMoviesRequestPage = mCurrentTopRatedMoviesRequestPage;
        mEventBusProvider.postEvent(new TopRatedMoviesRequestFailedEvent(mCurrentTopRatedMoviesRequestPage));
    }
}
