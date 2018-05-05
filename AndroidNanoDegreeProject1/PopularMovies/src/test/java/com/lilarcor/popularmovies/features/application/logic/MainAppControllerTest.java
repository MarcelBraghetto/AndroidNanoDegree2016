package com.lilarcor.popularmovies.features.application.logic;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.lilarcor.popularmovies.BuildConfig;
import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.features.application.MainApp;
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
import com.lilarcor.popularmovies.framework.foundation.network.contracts.NetworkRequestProvider;
import com.lilarcor.popularmovies.framework.movies.models.Movie;
import com.lilarcor.popularmovies.framework.movies.provider.contracts.MoviesProvider;
import com.lilarcor.popularmovies.testutils.RobolectricProperties;
import com.lilarcor.popularmovies.testutils.UnitTestFileLoader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.PopularMovies;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.TopRatedMovies;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Created by Marcel Braghetto on 27/07/15.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class MainAppControllerTest {
    @Mock MoviesProvider mMoviesProvider;
    @Mock EventBusProvider mEventBusProvider;
    @Spy FakeNetworkRequestProvider mNetworkRequestProvider;

    private AppStringsProvider mAppStringsProvider;
    private MainAppController mController;
    private String mMoviesAPIKey;

    @Captor ArgumentCaptor<String> mCaptorRequestTag;
    @Captor ArgumentCaptor<String> mCaptorRequestUrl;
    @Captor ArgumentCaptor<Integer> mCaptorRequestCacheAge;
    @Captor ArgumentCaptor<NetworkRequestProvider.RequestDelegate> mCaptorRequestDelegate;
    @Captor ArgumentCaptor<Movie[]> mCaptorMovies;
    @Captor ArgumentCaptor<ContentValues[]> mCaptorPopularMovies;
    @Captor ArgumentCaptor<ContentValues[]> mCaptorTopRatedMovies;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mAppStringsProvider = MainApp.getDagger().getAppStringsProvider();
        mMoviesAPIKey = mAppStringsProvider.getString(R.string.movies_db_api_key);
    }

    @Test
    public void testBasicInstantiation() {
        // Setup
        mNetworkRequestProvider.configure("PopularMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.NoAction, null, null);

        // Run
        mController = new MainAppController(mNetworkRequestProvider, mMoviesProvider, mEventBusProvider, mAppStringsProvider);

        // Verify
        verify(mEventBusProvider).subscribe(mController);
        verify(mNetworkRequestProvider, times(2)).startGetRequest(
                mCaptorRequestTag.capture(),
                mCaptorRequestUrl.capture(),
                mCaptorRequestCacheAge.capture(),
                mCaptorRequestDelegate.capture());

        List<String> capturedRequestTags = mCaptorRequestTag.getAllValues();
        assertThat(capturedRequestTags.get(0), is("PopularMoviesRequest"));
        assertThat(capturedRequestTags.get(1), is("TopRatedMoviesRequest"));

        List<String> capturedRequestUrls = mCaptorRequestUrl.getAllValues();
        assertThat(capturedRequestUrls.get(0), is("http://api.themoviedb.org/3/discover/movie?api_key=" + mMoviesAPIKey + "&page=1&sort_by=popularity.desc"));
        assertThat(capturedRequestUrls.get(1), is("http://api.themoviedb.org/3/discover/movie?api_key=" + mMoviesAPIKey + "&page=1&sort_by=vote_average.desc&vote_count.gte=1000"));

        List<Integer> capturedRequestCacheAge = mCaptorRequestCacheAge.getAllValues();
        assertThat(capturedRequestCacheAge.get(0), is(12));
        assertThat(capturedRequestCacheAge.get(1), is(12));
    }

    //region Popular Movies
    @Test
    public void testPopularMoviesRequestConnectionFailed() {
        // Setup
        mNetworkRequestProvider.configure("PopularMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Failed, null, null);

        // Run
        mController = new MainAppController(mNetworkRequestProvider, mMoviesProvider, mEventBusProvider, mAppStringsProvider);

        // Verify
        verify(mEventBusProvider).postEvent(isA(PopularMoviesRequestFailedEvent.class));
        verifyZeroInteractions(mMoviesProvider);
        verify(mEventBusProvider, never()).postEvent(isA(PopularMoviesRequestCompleteEvent.class));
    }

    @Test
    public void testPopularMoviesRequestBadDataFormat() {
        // Setup
        mNetworkRequestProvider.configure("PopularMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "movies_bad_data.txt");

        // Run
        mController = new MainAppController(mNetworkRequestProvider, mMoviesProvider, mEventBusProvider, mAppStringsProvider);

        // Verify
        verify(mEventBusProvider).postEvent(isA(PopularMoviesRequestFailedEvent.class));
        verifyZeroInteractions(mMoviesProvider);
        verify(mEventBusProvider, never()).postEvent(isA(PopularMoviesRequestCompleteEvent.class));
    }

    @Test
    public void testPopularMoviesRequestInvalidDataStructure() {
        // Setup
        mNetworkRequestProvider.configure("PopularMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "movies_invalid_data_structure.txt");

        // Run
        mController = new MainAppController(mNetworkRequestProvider, mMoviesProvider, mEventBusProvider, mAppStringsProvider);

        // Verify
        verify(mEventBusProvider).postEvent(isA(PopularMoviesRequestFailedEvent.class));
        verifyZeroInteractions(mMoviesProvider);
        verify(mEventBusProvider, never()).postEvent(isA(PopularMoviesRequestCompleteEvent.class));
    }

    @Test
    public void testPopularMoviesRequestValidDataRequestPage1() {
        // Setup
        mNetworkRequestProvider.configure("PopularMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "popular_movies_data_ok_request_page1.txt");

        // Run
        mController = new MainAppController(mNetworkRequestProvider, mMoviesProvider, mEventBusProvider, mAppStringsProvider);

        // Verify
        verify(mNetworkRequestProvider, times(2)).startGetRequest(
                mCaptorRequestTag.capture(),
                mCaptorRequestUrl.capture(),
                mCaptorRequestCacheAge.capture(),
                mCaptorRequestDelegate.capture());

        assertThat(mCaptorRequestTag.getAllValues().get(0), is("PopularMoviesRequest"));
        assertThat(mCaptorRequestUrl.getAllValues().get(0), is("http://api.themoviedb.org/3/discover/movie?api_key=" + mMoviesAPIKey + "&page=1&sort_by=popularity.desc"));
        assertThat(mCaptorRequestCacheAge.getAllValues().get(0), is(12));

        verify(mEventBusProvider, never()).postEvent(isA(PopularMoviesRequestFailedEvent.class));
        verify(mMoviesProvider).deleteAllPopularMovies();
        verify(mMoviesProvider).saveBulkMovies(mCaptorMovies.capture());

        Movie[] movies = mCaptorMovies.getValue();

        Movie movie = movies[0];
        assertThat(movie.getMovieId(), is(135397));
        assertThat(movie.getTitle(), is("Jurassic World"));
        assertThat(movie.getOverview(), is("Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond."));
        assertThat(movie.getReleaseDate(), is("2015-06-12"));
        assertThat(movie.getPosterImageUrl(), is("http://image.tmdb.org/t/p/w185/uXZYawqUsChGSj54wcuBtEdUJbh.jpg"));
        assertThat(movie.getBackdropImageUrl(), is("http://image.tmdb.org/t/p/w500/dkMD5qlogeRMiEixC4YNPUvax2T.jpg"));
        assertThat(movie.getVoteAverage(), is(7f));
        assertThat(movie.getVoteCount(), is(1369));

        verify(mMoviesProvider).saveBulkPopularMovies(mCaptorPopularMovies.capture());

        ContentValues[] contentValues = mCaptorPopularMovies.getValue();
        ContentValues firstValues = contentValues[0];
        assertThat(firstValues.getAsInteger(PopularMovies.COLUMN_MOVIE_ID), is(135397));
        assertThat(firstValues.getAsInteger(PopularMovies.COLUMN_RESULT_PAGE), is(1));

        verify(mEventBusProvider).postEvent(isA(PopularMoviesRequestCompleteEvent.class));
    }

    @Test
    public void testPopularMoviesRequestValidDataRequestPage2() {
        // Setup - first run with the network request to advance to request page 2
        mNetworkRequestProvider.configure("PopularMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "popular_movies_data_ok_request_page1.txt");
        mController = new MainAppController(mNetworkRequestProvider, mMoviesProvider, mEventBusProvider, mAppStringsProvider);

        reset(mEventBusProvider);
        reset(mMoviesProvider);
        reset(mNetworkRequestProvider);

        // Run
        mNetworkRequestProvider.configure("PopularMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "popular_movies_data_ok_request_page2.txt");
        mController.onPopularMoviesRequestMoreDataEvent(new PopularMoviesRequestMoreDataEvent(2));

        // Verify
        verify(mNetworkRequestProvider).startGetRequest(
                mCaptorRequestTag.capture(),
                mCaptorRequestUrl.capture(),
                mCaptorRequestCacheAge.capture(),
                mCaptorRequestDelegate.capture());

        assertThat(mCaptorRequestTag.getValue(), is("PopularMoviesRequest"));
        assertThat(mCaptorRequestUrl.getValue(), is("http://api.themoviedb.org/3/discover/movie?api_key=" + mMoviesAPIKey + "&page=2&sort_by=popularity.desc"));
        assertThat(mCaptorRequestCacheAge.getValue(), is(12));

        verify(mEventBusProvider, never()).postEvent(isA(PopularMoviesRequestFailedEvent.class));
        verify(mMoviesProvider, never()).deleteAllPopularMovies();
        verify(mMoviesProvider).saveBulkMovies(mCaptorMovies.capture());

        Movie[] movies = mCaptorMovies.getValue();

        Movie movie = movies[0];
        assertThat(movie.getMovieId(), is(87101));
        assertThat(movie.getTitle(), is("Terminator Genisys"));
        assertThat(movie.getOverview(), is("The year is 2029. John Connor, leader of the resistance continues the war against the machines. At the Los Angeles offensive, John's fears of the unknown future begin to emerge when TECOM spies reveal a new plot by SkyNet that will attack him from both fronts; past and future, and will ultimately change warfare forever."));
        assertThat(movie.getReleaseDate(), is("2015-07-01"));
        assertThat(movie.getPosterImageUrl(), is("http://image.tmdb.org/t/p/w185/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg"));
        assertThat(movie.getBackdropImageUrl(), is("http://image.tmdb.org/t/p/w500/o4I5sHdjzs29hBWzHtS2MKD3JsM.jpg"));
        assertThat(movie.getVoteAverage(), is(6.4f));
        assertThat(movie.getVoteCount(), is(428));

        verify(mMoviesProvider).saveBulkPopularMovies(mCaptorPopularMovies.capture());

        ContentValues[] contentValues = mCaptorPopularMovies.getValue();
        ContentValues firstValues = contentValues[0];
        assertThat(firstValues.getAsInteger(PopularMovies.COLUMN_MOVIE_ID), is(87101));
        assertThat(firstValues.getAsInteger(PopularMovies.COLUMN_RESULT_PAGE), is(2));

        verify(mEventBusProvider).postEvent(isA(PopularMoviesRequestCompleteEvent.class));
    }

    @Test
    public void testPopularMoviesMoreDataRequestWhileAlreadyInProgress() {
        // Setup - Allow the first request to simulate being started but not returning before the next request is made
        mNetworkRequestProvider.configure("PopularMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.NoAction, null, null);
        mController = new MainAppController(mNetworkRequestProvider, mMoviesProvider, mEventBusProvider, mAppStringsProvider);

        reset(mEventBusProvider);
        reset(mMoviesProvider);

        // Run
        mController.onPopularMoviesRequestMoreDataEvent(new PopularMoviesRequestMoreDataEvent(2));

        // Verify
        verifyZeroInteractions(mEventBusProvider);
        verifyZeroInteractions(mMoviesProvider);
    }

    /**
     * Scenario: Request page 1 and 2 have already been loaded before and an
     * event is received to load request page 1, which should be
     * ignored because if falls under the boundary of the pages that have
     * already been loaded.
     */
    @Test
    public void testPopularMoviesMoreDataRequestForAlreadyLoadedPage() {
        // Setup
        mNetworkRequestProvider.configure("PopularMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "popular_movies_data_ok_request_page1.txt");
        mController = new MainAppController(mNetworkRequestProvider, mMoviesProvider, mEventBusProvider, mAppStringsProvider);
        mNetworkRequestProvider.configure("PopularMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "popular_movies_data_ok_request_page2.txt");
        mController.onPopularMoviesRequestMoreDataEvent(new PopularMoviesRequestMoreDataEvent(2));

        reset(mEventBusProvider);
        reset(mMoviesProvider);

        // Run
        mController.onPopularMoviesRequestMoreDataEvent(new PopularMoviesRequestMoreDataEvent(1));

        // Verify
        verifyZeroInteractions(mEventBusProvider);
        verifyZeroInteractions(mMoviesProvider);
    }

    /**
     * Scenario: User has already loaded data but performs a swipe
     * to refresh. The request should start again at request page 1
     * and do a force load ignoring cache.
     */
    @Test
    public void testPopularMoviesSwipeToRefreshEvent() {
        // Setup - preload the first two request pages to simulate some existing data before refreshing
        mNetworkRequestProvider.configure("PopularMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "popular_movies_data_ok_request_page1.txt");
        mController = new MainAppController(mNetworkRequestProvider, mMoviesProvider, mEventBusProvider, mAppStringsProvider);
        mNetworkRequestProvider.configure("PopularMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "popular_movies_data_ok_request_page2.txt");
        mController.onTopRatedRequestMoreDataEvent(new TopRatedMoviesRequestMoreDataEvent(2));

        reset(mEventBusProvider);
        reset(mMoviesProvider);
        reset(mNetworkRequestProvider);

        // Run
        mNetworkRequestProvider.configure("PopularMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "popular_movies_data_ok_request_page1.txt");
        mController.onPopularMoviesSwipeToRefreshEvent(new PopularMoviesSwipeToRefreshEvent());

        verify(mNetworkRequestProvider).clearResponseCache();

        // Verify - the network request for top rated movies should start again but with cache age of 0
        verify(mNetworkRequestProvider).startGetRequest(
                mCaptorRequestTag.capture(),
                mCaptorRequestUrl.capture(),
                mCaptorRequestCacheAge.capture(),
                mCaptorRequestDelegate.capture());

        assertThat(mCaptorRequestTag.getValue(), is("PopularMoviesRequest"));
        assertThat(mCaptorRequestUrl.getValue(), is("http://api.themoviedb.org/3/discover/movie?api_key=" + mMoviesAPIKey + "&page=1&sort_by=popularity.desc"));
        assertThat(mCaptorRequestCacheAge.getValue(), is(0));

        verify(mEventBusProvider, never()).postEvent(isA(PopularMoviesRequestFailedEvent.class));
        verify(mMoviesProvider).deleteAllPopularMovies();
        verify(mMoviesProvider).saveBulkMovies(mCaptorMovies.capture());

        Movie[] movies = mCaptorMovies.getValue();

        Movie movie = movies[0];
        assertThat(movie.getMovieId(), is(135397));
        assertThat(movie.getTitle(), is("Jurassic World"));
        assertThat(movie.getOverview(), is("Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond."));
        assertThat(movie.getReleaseDate(), is("2015-06-12"));
        assertThat(movie.getPosterImageUrl(), is("http://image.tmdb.org/t/p/w185/uXZYawqUsChGSj54wcuBtEdUJbh.jpg"));
        assertThat(movie.getBackdropImageUrl(), is("http://image.tmdb.org/t/p/w500/dkMD5qlogeRMiEixC4YNPUvax2T.jpg"));
        assertThat(movie.getVoteAverage(), is(7f));
        assertThat(movie.getVoteCount(), is(1369));

        verify(mMoviesProvider).saveBulkPopularMovies(mCaptorPopularMovies.capture());

        ContentValues[] contentValues = mCaptorPopularMovies.getValue();
        ContentValues firstValues = contentValues[0];
        assertThat(firstValues.getAsInteger(PopularMovies.COLUMN_MOVIE_ID), is(135397));
        assertThat(firstValues.getAsInteger(PopularMovies.COLUMN_RESULT_PAGE), is(1));

        verify(mEventBusProvider).postEvent(isA(PopularMoviesRequestCompleteEvent.class));
    }
    //endregion

    //region Top Rated Movies
    @Test
    public void testTopRatedMoviesRequestConnectionFailed() {
        // Setup
        mNetworkRequestProvider.configure("TopRatedMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Failed, null, null);

        // Run
        mController = new MainAppController(mNetworkRequestProvider, mMoviesProvider, mEventBusProvider, mAppStringsProvider);

        // Verify
        verify(mEventBusProvider).postEvent(isA(TopRatedMoviesRequestFailedEvent.class));
        verifyZeroInteractions(mMoviesProvider);
        verify(mEventBusProvider, never()).postEvent(isA(TopRatedMoviesRequestCompleteEvent.class));
    }

    @Test
    public void testTopRatedMoviesRequestBadDataFormat() {
        // Setup
        mNetworkRequestProvider.configure("TopRatedMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "movies_bad_data.txt");

        // Run
        mController = new MainAppController(mNetworkRequestProvider, mMoviesProvider, mEventBusProvider, mAppStringsProvider);

        // Verify
        verify(mEventBusProvider).postEvent(isA(TopRatedMoviesRequestFailedEvent.class));
        verifyZeroInteractions(mMoviesProvider);
        verify(mEventBusProvider, never()).postEvent(isA(TopRatedMoviesRequestCompleteEvent.class));
    }

    @Test
    public void testTopRatedMoviesRequestInvalidDataStructure() {
        // Setup
        mNetworkRequestProvider.configure("TopRatedMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "movies_invalid_data_structure.txt");

        // Run
        mController = new MainAppController(mNetworkRequestProvider, mMoviesProvider, mEventBusProvider, mAppStringsProvider);

        // Verify
        verify(mEventBusProvider).postEvent(isA(TopRatedMoviesRequestFailedEvent.class));
        verifyZeroInteractions(mMoviesProvider);
        verify(mEventBusProvider, never()).postEvent(isA(TopRatedMoviesRequestCompleteEvent.class));
    }

    @Test
    public void testTopRatedMoviesRequestValidDataRequestPage1() {
        // Setup
        mNetworkRequestProvider.configure("TopRatedMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "top_rated_movies_data_ok_request_page1.txt");

        // Run
        mController = new MainAppController(mNetworkRequestProvider, mMoviesProvider, mEventBusProvider, mAppStringsProvider);

        // Verify
        verify(mNetworkRequestProvider, times(2)).startGetRequest(
                mCaptorRequestTag.capture(),
                mCaptorRequestUrl.capture(),
                mCaptorRequestCacheAge.capture(),
                mCaptorRequestDelegate.capture());

        assertThat(mCaptorRequestTag.getAllValues().get(1), is("TopRatedMoviesRequest"));
        assertThat(mCaptorRequestUrl.getAllValues().get(1), is("http://api.themoviedb.org/3/discover/movie?api_key=" + mMoviesAPIKey + "&page=1&sort_by=vote_average.desc&vote_count.gte=1000"));
        assertThat(mCaptorRequestCacheAge.getAllValues().get(1), is(12));

        verify(mEventBusProvider, never()).postEvent(isA(TopRatedMoviesRequestFailedEvent.class));
        verify(mMoviesProvider).deleteAllTopRatedMovies();
        verify(mMoviesProvider).saveBulkMovies(mCaptorMovies.capture());

        Movie[] movies = mCaptorMovies.getValue();

        Movie movie = movies[0];
        assertThat(movie.getMovieId(), is(157336));
        assertThat(movie.getTitle(), is("Interstellar"));
        assertThat(movie.getOverview(), is("Interstellar chronicles the adventures of a group of explorers who make use of a newly discovered wormhole to surpass the limitations on human space travel and conquer the vast distances involved in an interstellar voyage."));
        assertThat(movie.getReleaseDate(), is("2014-11-05"));
        assertThat(movie.getPosterImageUrl(), is("http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"));
        assertThat(movie.getBackdropImageUrl(), is("http://image.tmdb.org/t/p/w500/xu9zaAevzQ5nnrsXN6JcahLnG4i.jpg"));
        assertThat(movie.getVoteAverage(), is(8.4f));
        assertThat(movie.getVoteCount(), is(2828));

        verify(mMoviesProvider).saveBulkTopRatedMovies(mCaptorTopRatedMovies.capture());

        ContentValues[] contentValues = mCaptorTopRatedMovies.getValue();
        ContentValues firstValues = contentValues[0];
        assertThat(firstValues.getAsInteger(TopRatedMovies.COLUMN_MOVIE_ID), is(157336));
        assertThat(firstValues.getAsInteger(TopRatedMovies.COLUMN_RESULT_PAGE), is(1));

        verify(mEventBusProvider).postEvent(isA(TopRatedMoviesRequestCompleteEvent.class));
    }

    @Test
    public void testTopRatedMoviesRequestValidDataRequestPage2() {
        // Setup - first run with the network request to advance to request page 2
        mNetworkRequestProvider.configure("TopRatedMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "top_rated_movies_data_ok_request_page1.txt");
        mController = new MainAppController(mNetworkRequestProvider, mMoviesProvider, mEventBusProvider, mAppStringsProvider);

        reset(mEventBusProvider);
        reset(mMoviesProvider);
        reset(mNetworkRequestProvider);

        // Run
        mNetworkRequestProvider.configure("TopRatedMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "top_rated_movies_data_ok_request_page2.txt");
        mController.onTopRatedRequestMoreDataEvent(new TopRatedMoviesRequestMoreDataEvent(2));

        // Verify
        verify(mNetworkRequestProvider).startGetRequest(
                mCaptorRequestTag.capture(),
                mCaptorRequestUrl.capture(),
                mCaptorRequestCacheAge.capture(),
                mCaptorRequestDelegate.capture());

        assertThat(mCaptorRequestTag.getValue(), is("TopRatedMoviesRequest"));
        assertThat(mCaptorRequestUrl.getValue(), is("http://api.themoviedb.org/3/discover/movie?api_key=" + mMoviesAPIKey + "&page=2&sort_by=vote_average.desc&vote_count.gte=1000"));
        assertThat(mCaptorRequestCacheAge.getValue(), is(12));

        verify(mEventBusProvider, never()).postEvent(isA(TopRatedMoviesRequestFailedEvent.class));
        verify(mMoviesProvider, never()).deleteAllTopRatedMovies();
        verify(mMoviesProvider).saveBulkMovies(mCaptorMovies.capture());

        Movie[] movies = mCaptorMovies.getValue();

        Movie movie = movies[0];
        assertThat(movie.getMovieId(), is(278));
        assertThat(movie.getTitle(), is("The Shawshank Redemption"));
        assertThat(movie.getOverview(), is("Framed in the 1940s for the double murder of his wife and her lover, upstanding banker Andy Dufresne begins a new life at the Shawshank prison, where he puts his accounting skills to work for an amoral warden. During his long stretch in prison, Dufresne comes to be admired by the other inmates -- including an older prisoner named Red -- for his integrity and unquenchable sense of hope."));
        assertThat(movie.getReleaseDate(), is("1994-09-14"));
        assertThat(movie.getPosterImageUrl(), is("http://image.tmdb.org/t/p/w185/9O7gLzmreU0nGkIB6K3BsJbzvNv.jpg"));
        assertThat(movie.getBackdropImageUrl(), is("http://image.tmdb.org/t/p/w500/xBKGJQsAIeweesB79KC89FpBrVr.jpg"));
        assertThat(movie.getVoteAverage(), is(8.2f));
        assertThat(movie.getVoteCount(), is(3934));

        verify(mMoviesProvider).saveBulkTopRatedMovies(mCaptorTopRatedMovies.capture());

        ContentValues[] contentValues = mCaptorTopRatedMovies.getValue();
        ContentValues firstValues = contentValues[0];
        assertThat(firstValues.getAsInteger(TopRatedMovies.COLUMN_MOVIE_ID), is(278));
        assertThat(firstValues.getAsInteger(TopRatedMovies.COLUMN_RESULT_PAGE), is(2));

        verify(mEventBusProvider).postEvent(isA(TopRatedMoviesRequestCompleteEvent.class));
    }

    @Test
    public void testTopRatedMoviesMoreDataRequestWhileAlreadyInProgress() {
        // Setup - Allow the first request to simulate being started but not returning before the next request is made
        mNetworkRequestProvider.configure("TopRatedMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.NoAction, null, null);
        mController = new MainAppController(mNetworkRequestProvider, mMoviesProvider, mEventBusProvider, mAppStringsProvider);

        reset(mEventBusProvider);
        reset(mMoviesProvider);

        // Run
        mController.onTopRatedRequestMoreDataEvent(new TopRatedMoviesRequestMoreDataEvent(2));

        // Verify
        verifyZeroInteractions(mEventBusProvider);
        verifyZeroInteractions(mMoviesProvider);
    }

    /**
     * Scenario: Request page 1 and 2 have already been loaded before and an
     * event is received to load request page 1, which should be
     * ignored because if falls under the boundary of the pages that have
     * already been loaded.
     */
    @Test
    public void testTopRatedMoviesMoreDataRequestForAlreadyLoadedPage() {
        // Setup
        mNetworkRequestProvider.configure("TopRatedMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "top_rated_movies_data_ok_request_page1.txt");
        mController = new MainAppController(mNetworkRequestProvider, mMoviesProvider, mEventBusProvider, mAppStringsProvider);
        mNetworkRequestProvider.configure("TopRatedMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "top_rated_movies_data_ok_request_page2.txt");
        mController.onTopRatedRequestMoreDataEvent(new TopRatedMoviesRequestMoreDataEvent(2));

        reset(mEventBusProvider);
        reset(mMoviesProvider);

        // Run
        mController.onTopRatedRequestMoreDataEvent(new TopRatedMoviesRequestMoreDataEvent(1));

        // Verify
        verifyZeroInteractions(mEventBusProvider);
        verifyZeroInteractions(mMoviesProvider);
    }

    /**
     * Scenario: User has already loaded data but performs a swipe
     * to refresh. The request should start again at request page 1
     * and do a force load ignoring cache.
     */
    @Test
    public void testTopRatedMoviesSwipeToRefreshEvent() {
        // Setup - preload the first two request pages to simulate some existing data before refreshing
        mNetworkRequestProvider.configure("TopRatedMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "top_rated_movies_data_ok_request_page1.txt");
        mController = new MainAppController(mNetworkRequestProvider, mMoviesProvider, mEventBusProvider, mAppStringsProvider);
        mNetworkRequestProvider.configure("TopRatedMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "top_rated_movies_data_ok_request_page2.txt");
        mController.onTopRatedRequestMoreDataEvent(new TopRatedMoviesRequestMoreDataEvent(2));

        reset(mEventBusProvider);
        reset(mMoviesProvider);
        reset(mNetworkRequestProvider);

        // Run
        mNetworkRequestProvider.configure("TopRatedMoviesRequest", FakeNetworkRequestProvider.SimulatedResponseMode.Success, getClass(), "top_rated_movies_data_ok_request_page1.txt");
        mController.onTopRatedMoviesSwipeToRefreshEvent(new TopRatedMoviesSwipeToRefreshEvent());

        verify(mNetworkRequestProvider).clearResponseCache();

        // Verify - the network request for top rated movies should start again but with cache age of 0
        verify(mNetworkRequestProvider).startGetRequest(
                mCaptorRequestTag.capture(),
                mCaptorRequestUrl.capture(),
                mCaptorRequestCacheAge.capture(),
                mCaptorRequestDelegate.capture());

        assertThat(mCaptorRequestTag.getValue(), is("TopRatedMoviesRequest"));
        assertThat(mCaptorRequestUrl.getValue(), is("http://api.themoviedb.org/3/discover/movie?api_key=" + mMoviesAPIKey + "&page=1&sort_by=vote_average.desc&vote_count.gte=1000"));
        assertThat(mCaptorRequestCacheAge.getValue(), is(0));

        verify(mEventBusProvider, never()).postEvent(isA(TopRatedMoviesRequestFailedEvent.class));
        verify(mMoviesProvider).deleteAllTopRatedMovies();
        verify(mMoviesProvider).saveBulkMovies(mCaptorMovies.capture());

        Movie[] movies = mCaptorMovies.getValue();

        Movie movie = movies[0];
        assertThat(movie.getMovieId(), is(157336));
        assertThat(movie.getTitle(), is("Interstellar"));
        assertThat(movie.getOverview(), is("Interstellar chronicles the adventures of a group of explorers who make use of a newly discovered wormhole to surpass the limitations on human space travel and conquer the vast distances involved in an interstellar voyage."));
        assertThat(movie.getReleaseDate(), is("2014-11-05"));
        assertThat(movie.getPosterImageUrl(), is("http://image.tmdb.org/t/p/w185/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg"));
        assertThat(movie.getBackdropImageUrl(), is("http://image.tmdb.org/t/p/w500/xu9zaAevzQ5nnrsXN6JcahLnG4i.jpg"));
        assertThat(movie.getVoteAverage(), is(8.4f));
        assertThat(movie.getVoteCount(), is(2828));

        verify(mMoviesProvider).saveBulkTopRatedMovies(mCaptorTopRatedMovies.capture());

        ContentValues[] contentValues = mCaptorTopRatedMovies.getValue();
        ContentValues firstValues = contentValues[0];
        assertThat(firstValues.getAsInteger(TopRatedMovies.COLUMN_MOVIE_ID), is(157336));
        assertThat(firstValues.getAsInteger(TopRatedMovies.COLUMN_RESULT_PAGE), is(1));

        verify(mEventBusProvider).postEvent(isA(TopRatedMoviesRequestCompleteEvent.class));
    }
    //endregion

    /**
     * Fake network request provider that can be controlled for unit test simulations.
     */
    private static class FakeNetworkRequestProvider implements NetworkRequestProvider {
        public enum SimulatedResponseMode {
            NoAction,
            Success,
            Failed
        }

        private Map<String, Pair<SimulatedResponseMode, String>> mResponseConfig;
        private Class mClazz;

        public FakeNetworkRequestProvider() {
            mResponseConfig = new HashMap<>();
        }

        public void configure(@NonNull String requestTag, @NonNull SimulatedResponseMode simulatedResponseMode, @Nullable Class clazz, @Nullable String textFileToLoad) {
            mClazz = clazz;
            mResponseConfig.put(requestTag, new Pair<>(simulatedResponseMode, textFileToLoad));
        }

        @Override
        public void startGetRequest(@NonNull String requestTag, @NonNull String url, int maxCacheAgeInHours, @NonNull RequestDelegate callbackDelegate) {
            Pair<SimulatedResponseMode, String> responseConfig = mResponseConfig.get(requestTag);

            if(responseConfig == null) {
                return;
            }

            switch (responseConfig.first) {
                case Failed:
                    callbackDelegate.onRequestFailed();
                    break;
                case Success:
                    callbackDelegate.onRequestComplete(0, UnitTestFileLoader.loadTextFile(mClazz, responseConfig.second));
                    break;
            }
        }

        @Override
        public boolean clearResponseCache() {
            return true;
        }
    }
}

