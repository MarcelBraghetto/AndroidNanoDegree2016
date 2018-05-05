package com.lilarcor.popularmovies.features.application.logic;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.common.BaseMoviesRequestEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.popularmovies.PopularMoviesRequestCompleteEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.popularmovies.PopularMoviesRequestFailedEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.popularmovies.PopularMoviesRequestMoreDataEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.popularmovies.PopularMoviesSwipeToRefreshEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.topratedmovies.TopRatedMoviesRequestCompleteEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.topratedmovies.TopRatedMoviesRequestFailedEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.topratedmovies.TopRatedMoviesRequestMoreDataEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.topratedmovies.TopRatedMoviesSwipeToRefreshEvent;
import com.lilarcor.popularmovies.framework.foundation.appstrings.contracts.AppStringsProvider;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusProvider;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusSubscriber;
import com.lilarcor.popularmovies.framework.foundation.network.contracts.NetworkRequestProvider;
import com.lilarcor.popularmovies.framework.movies.models.Movie;
import com.lilarcor.popularmovies.framework.movies.provider.contracts.MoviesProvider;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.PopularMovies;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.TopRatedMovies;

/**
 * Created by Marcel Braghetto on 15/07/15.
 *
 * Main application logic controller that is responsible for calling
 * network resources and updating data stores locally. Is also a
 * receiver of certain event bus events to trigger application actions.
 */
public final class MainAppController implements EventBusSubscriber {
    //region Private fields
    private static final int MAX_REQUEST_AGE_HOURS = 12;
    private static final int MAX_REQUEST_AGE_INSTANT = 0;

    private static final String REQUEST_TAG_POPULAR_MOVIES = "PopularMoviesRequest";
    private static final String REQUEST_TAG_TOP_RATED_MOVIES = "TopRatedMoviesRequest";

    private final NetworkRequestProvider mNetworkRequestProvider;
    private final MoviesProvider mMoviesProvider;
    private final EventBusProvider mEventBusProvider;
    private final AppStringsProvider mAppStringsProvider;

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
                             @NonNull EventBusProvider eventBusProvider,
                             @NonNull AppStringsProvider appStringsProvider) {

        mNetworkRequestProvider = networkRequestProvider;
        mMoviesProvider = moviesProvider;
        mEventBusProvider = eventBusProvider;
        mAppStringsProvider = appStringsProvider;

        mLastFailedPopularMoviesRequestPage = BaseMoviesRequestEvent.REQUEST_PAGE_NONE;
        mLastFailedTopRatedMoviesRequestPage = BaseMoviesRequestEvent.REQUEST_PAGE_NONE;

        subscribeToEventBus();

        mCurrentPopularMoviesRequestPage = 1;
        mCurrentTopRatedMoviesRequestPage = 1;

        loadPopularMovies(MAX_REQUEST_AGE_HOURS, 1);
        loadTopRatedMovies(MAX_REQUEST_AGE_HOURS, 1);
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

    //region Popular movies event bus
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

    /**
     * Received an event indicating the user has initiated a
     * swipe to refresh operation in the Popular Movies feature.
     *
     * We should attempt to reload the popular movies from the
     * server.
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onPopularMoviesSwipeToRefreshEvent(@NonNull PopularMoviesSwipeToRefreshEvent event) {
        mNetworkRequestProvider.clearResponseCache();
        mCurrentPopularMoviesRequestPage = 1;
        loadPopularMovies(MAX_REQUEST_AGE_INSTANT, 1);

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
    @Subscribe
    @SuppressWarnings("unused")
    public void onPopularMoviesRequestMoreDataEvent(@NonNull PopularMoviesRequestMoreDataEvent event) {
        loadPopularMovies(MAX_REQUEST_AGE_HOURS, event.getFromRequestPage());
    }
    //endregion

    //region Top rated event bus
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

    /**
     * Received an event indicating the user has initiated a
     * swipe to refresh operation in the Top Rated Movies feature.
     *
     * We should attempt to reload the top rated movies from the
     * server.
     */
    @Subscribe
    @SuppressWarnings("unused")
    public void onTopRatedMoviesSwipeToRefreshEvent(@NonNull TopRatedMoviesSwipeToRefreshEvent event) {
        mNetworkRequestProvider.clearResponseCache();
        mCurrentTopRatedMoviesRequestPage = 1;
        loadTopRatedMovies(MAX_REQUEST_AGE_INSTANT, 1);

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
    @Subscribe
    @SuppressWarnings("unused")
    public void onTopRatedRequestMoreDataEvent(@NonNull TopRatedMoviesRequestMoreDataEvent event) {
        loadTopRatedMovies(MAX_REQUEST_AGE_HOURS, event.getFromRequestPage());
    }
    //endregion

    //region Private methods
    /**
     * Build a request url to retrieve the 'popular' movies feed from the
     * Movies DB API.
     *
     * @param maxRequestCacheAge for this request to determine whether the response
     *                           will be served directly from the local cache or
     *                           from the server.
     * @param fromRequestPage the request 'page' of the collection that triggered this
     *                        attempt to load more popular movies. This logic will
     *                        keep the networking such that only the next page of movie
     *                        data will be preloaded in advance as the user scrolls.
     */
    private void loadPopularMovies(int maxRequestCacheAge, int fromRequestPage) {
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

        // The popular movies uses the popularity API filter.
        String url = new Uri.Builder()
                        .scheme("http")
                        .authority("api.themoviedb.org")
                        .appendPath("3")
                        .appendPath("discover")
                        .appendPath("movie")
                        .appendQueryParameter("api_key", mAppStringsProvider.getString(R.string.movies_db_api_key))
                        .appendQueryParameter("page", String.valueOf(mCurrentPopularMoviesRequestPage))
                        .appendQueryParameter("sort_by", "popularity.desc")
                        .build()
                        .toString();

        mNetworkRequestProvider.startGetRequest(REQUEST_TAG_POPULAR_MOVIES, url, maxRequestCacheAge, new NetworkRequestProvider.RequestDelegate() {
            @Override
            public void onRequestComplete(int statusCode, @NonNull String response) {
                MovieResultsDTO dto;

                try {
                    // On a successful response, parse the response text into our data transformation object.
                    dto = new Gson().fromJson(response, MovieResultsDTO.class);

                    if(dto.getMovies() == null) {
                        throw new Exception("Couldn't parse popular movies");
                    }
                } catch (Exception e) {
                    // If something went wrong while parsing the response, treat it as a failure.
                    mPopularMoviesRequestInProgress = false;
                    mLastFailedPopularMoviesRequestPage = mCurrentPopularMoviesRequestPage;
                    mEventBusProvider.postEvent(new PopularMoviesRequestFailedEvent(mCurrentPopularMoviesRequestPage));
                    return;
                }

                // Only delete all the saved popular movies if this is the first page of results, which would only
                // occur the first time the load happens, or if the user forced a refresh.
                if (mCurrentPopularMoviesRequestPage <= 1) {
                    mMoviesProvider.deleteAllPopularMovies();
                }

                Movie[] movies = dto.getMovies();

                // Iterate through all the movie results in the response to create new popular movie records.
                ContentValues[] popularMoviesContentValues = new ContentValues[movies.length];

                for (int i = 0; i < movies.length; i++) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(PopularMovies.COLUMN_MOVIE_ID, movies[i].getMovieId());
                    contentValues.put(PopularMovies.COLUMN_RESULT_PAGE, dto.getResultPage());

                    popularMoviesContentValues[i] = contentValues;
                }

                mMoviesProvider.saveBulkMovies(movies);
                mMoviesProvider.saveBulkPopularMovies(popularMoviesContentValues);

                mEventBusProvider.postEvent(new PopularMoviesRequestCompleteEvent());

                // At this point we can increment the current 'request page' to get ready for
                // pre-loading the next page of popular movies.
                mCurrentPopularMoviesRequestPage++;
                mPopularMoviesRequestInProgress = false;
            }

            @Override
            public void onRequestFailed() {
                mLastFailedPopularMoviesRequestPage = mCurrentPopularMoviesRequestPage;
                mEventBusProvider.postEvent(new PopularMoviesRequestFailedEvent(mCurrentPopularMoviesRequestPage));
                mPopularMoviesRequestInProgress = false;
            }
        });
    }

    /**
     * Build a request url to retrieve the 'top rated' movies feed from the
     * Movies DB API.
     *
     * @param maxRequestCacheAge for this request to determine whether the response
     *                           will be served directly from the local cache or
     *                           from the server.
     * @param fromRequestPage the request 'page' of the collection that triggered this
     *                        attempt to load more popular movies. This logic will
     *                        keep the networking such that only the next page of movie
     *                        data will be preloaded in advance as the user scrolls.
     */
    private void loadTopRatedMovies(int maxRequestCacheAge, int fromRequestPage) {
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

        // The top rated request differs slightly from the popular movies request
        // because for top rated movies we want to sort by the vote average AND
        // we also don't want any results that have less than 1000 user votes.
        // This cleans up the majority of bad results where only a few people have
        // given obscure movies the highest ratings - they aren't really the top
        // rated movies in the context of the entire audience. 1000 votes is arbitrary
        // but seems to hold fairly accurate after some trial runs of sampling the
        // API responses with different vote count settings.
        String url = new Uri.Builder()
                .scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter("api_key", mAppStringsProvider.getString(R.string.movies_db_api_key))
                .appendQueryParameter("page", String.valueOf(mCurrentTopRatedMoviesRequestPage))
                .appendQueryParameter("sort_by", "vote_average.desc")
                .appendQueryParameter("vote_count.gte", "1000")
                .build()
                .toString();

        mNetworkRequestProvider.startGetRequest(REQUEST_TAG_TOP_RATED_MOVIES, url, maxRequestCacheAge, new NetworkRequestProvider.RequestDelegate() {
            @Override
            public void onRequestComplete(int statusCode, @NonNull String response) {
                MovieResultsDTO dto;

                try {
                    // On a successful response, parse the response text into our data transformation object.
                    dto = new Gson().fromJson(response, MovieResultsDTO.class);

                    if(dto.getMovies() == null) {
                        throw new Exception("Couldn't parse top rated movies");
                    }
                } catch (Exception e) {
                    // If something went wrong while parsing the response, treat it as a failure.
                    mTopRatedMoviesRequestInProgress = false;
                    mLastFailedTopRatedMoviesRequestPage = mCurrentTopRatedMoviesRequestPage;
                    mEventBusProvider.postEvent(new TopRatedMoviesRequestFailedEvent(mCurrentTopRatedMoviesRequestPage));
                    return;
                }

                // Only delete all the saved popular movies if this is the first page of results, which would only
                // occur the first time the load happens, or if the user forced a refresh.
                if(mCurrentTopRatedMoviesRequestPage <= 1) {
                    mMoviesProvider.deleteAllTopRatedMovies();
                }

                Movie[] movies = dto.getMovies();

                // Iterate through all the movie results in the response to create new top rated movie records.
                ContentValues[] topRatedMoviesContentValues = new ContentValues[movies.length];

                for (int i = 0; i < movies.length; i++) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(TopRatedMovies.COLUMN_MOVIE_ID, movies[i].getMovieId());
                    contentValues.put(TopRatedMovies.COLUMN_RESULT_PAGE, dto.getResultPage());

                    topRatedMoviesContentValues[i] = contentValues;
                }

                mMoviesProvider.saveBulkMovies(movies);
                mMoviesProvider.saveBulkTopRatedMovies(topRatedMoviesContentValues);

                mEventBusProvider.postEvent(new TopRatedMoviesRequestCompleteEvent());

                // At this point we can increment the current 'request page' to get ready for
                // pre-loading the next page of top rated movies.
                mCurrentTopRatedMoviesRequestPage++;
                mTopRatedMoviesRequestInProgress = false;
            }

            @Override
            public void onRequestFailed() {
                mLastFailedTopRatedMoviesRequestPage = mCurrentTopRatedMoviesRequestPage;
                mEventBusProvider.postEvent(new TopRatedMoviesRequestFailedEvent(mCurrentTopRatedMoviesRequestPage));
                mTopRatedMoviesRequestInProgress = false;
            }
        });
    }

    /**
     * This is a simple data transformation object class that represents
     * the API response from the Movies DB API. It is inflated by GSON
     * during the parsing of the response.
     */
    private static class MovieResultsDTO {
        @SuppressWarnings("unused")
        @SerializedName("page")
        private int mResultPage;

        @SuppressWarnings("unused")
        @SerializedName("results")
        private Movie[] mMovies;

        public Movie[] getMovies() {
            return mMovies;
        }

        public int getResultPage() {
            return mResultPage;
        }
    }
}
