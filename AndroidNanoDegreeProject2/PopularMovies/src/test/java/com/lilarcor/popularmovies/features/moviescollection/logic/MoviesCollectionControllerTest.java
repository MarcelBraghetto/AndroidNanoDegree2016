package com.lilarcor.popularmovies.features.moviescollection.logic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.gson.Gson;
import com.lilarcor.popularmovies.BuildConfig;
import com.lilarcor.popularmovies.features.application.MainApp;
import com.lilarcor.popularmovies.features.home.logic.events.ShowTabletMovieDetailsEvent;
import com.lilarcor.popularmovies.features.moviedetails.ui.MovieDetailsFragment;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.common.BaseMoviesRequestEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.popularmovies.PopularMoviesRequestFailedEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.popularmovies.PopularMoviesRequestMoreDataEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.popularmovies.PopularMoviesSwipeToRefreshEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.topratedmovies.TopRatedMoviesRequestFailedEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.topratedmovies.TopRatedMoviesRequestMoreDataEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.events.topratedmovies.TopRatedMoviesSwipeToRefreshEvent;
import com.lilarcor.popularmovies.features.moviescollection.logic.models.MoviesCollectionFilter;
import com.lilarcor.popularmovies.framework.foundation.appstrings.contracts.AppStringsProvider;
import com.lilarcor.popularmovies.framework.foundation.device.contracts.DeviceInfoProvider;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusEvent;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusProvider;
import com.lilarcor.popularmovies.framework.movies.models.Movie;
import com.lilarcor.popularmovies.framework.movies.provider.contracts.MoviesProvider;
import com.lilarcor.popularmovies.testutils.RobolectricProperties;
import com.lilarcor.popularmovies.testutils.UnitTestFileLoader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by Marcel Braghetto on 27/07/15.
 *
 * Controller logic validation for the movies collection controller.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class MoviesCollectionControllerTest {
    @Mock MoviesProvider mMoviesProvider;
    @Mock EventBusProvider mEventBusProvider;
    @Mock DeviceInfoProvider mDeviceInfoProvider;
    @Mock MoviesCollectionController.ControllerDelegate mDelegate;
    @Mock Uri mFakeUri;

    @Rule public ExpectedException mExpectedException = ExpectedException.none();

    @Captor ArgumentCaptor<Intent> mCaptorIntent;
    @Captor ArgumentCaptor<EventBusEvent> mCaptorEventBusEvent;

    private AppStringsProvider mAppStringsProvider;
    private MoviesCollectionController mController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mAppStringsProvider = MainApp.getDagger().getAppStringsProvider();

        when(mMoviesProvider.getMovieCollectionUri(any(MoviesCollectionFilter.class))).thenReturn(mFakeUri);

        mController = new MoviesCollectionController(RuntimeEnvironment.application.getApplicationContext(), mMoviesProvider, mEventBusProvider, mDeviceInfoProvider, mAppStringsProvider);
    }

    @Test
    public void testInitControllerNullArgumentsBundle() {
        // Setup
        Bundle arguments = null;

        mExpectedException.expect(IllegalArgumentException.class);

        // Run
        mController.initController(mDelegate, 1000, arguments);
    }

    @Test
    public void testInitControllerArgumentsBundleMissingData() {
        // Setup
        Bundle arguments = new Bundle();

        mExpectedException.expect(IllegalArgumentException.class);

        // Run
        mController.initController(mDelegate, 1000, arguments);
    }

    @Test
    public void testInitControllerPopularMovies() {
        // Setup
        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.Popular);

        // Run
        mController.initController(mDelegate, 1000, arguments);

        // Verify
        verify(mDelegate, never()).disableSwipeRefresh();
        verify(mMoviesProvider).getMovieCollectionUri(MoviesCollectionFilter.Popular);
        verify(mDelegate).initScreen(mFakeUri, MoviesCollectionFilter.Popular, 7, 213);
    }

    @Test
    public void testInitControllerTopRatedMovies() {
        // Setup
        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.TopRated);

        // Run
        mController.initController(mDelegate, 1000, arguments);

        // Verify
        verify(mDelegate, never()).disableSwipeRefresh();
        verify(mMoviesProvider).getMovieCollectionUri(MoviesCollectionFilter.TopRated);
        verify(mDelegate).initScreen(mFakeUri, MoviesCollectionFilter.TopRated, 7, 213);
    }

    @Test
    public void testInitControllerFavouriteMovies() {
        // Setup
        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.Favourites);

        // Run
        mController.initController(mDelegate, 1000, arguments);

        // Verify
        verify(mDelegate).disableSwipeRefresh();
        verify(mMoviesProvider).getMovieCollectionUri(MoviesCollectionFilter.Favourites);
        verify(mDelegate).initScreen(mFakeUri, MoviesCollectionFilter.Favourites, 7, 213);
    }

    @Test
    public void testScreenStarted() {
        // Setup
        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.Popular);
        mController.initController(mDelegate, 1000, arguments);

        // Run
        mController.screenStarted();

        // Verify
        verify(mEventBusProvider).subscribe(mController);
    }

    @Test
    public void testScreenStopped() {
        // Setup
        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.Popular);
        mController.initController(mDelegate, 1000, arguments);

        // Run
        mController.screenStopped();

        // Verify
        verify(mEventBusProvider).unsubscribe(mController);
    }

    @Test
    public void testSwipeToRefreshInitiatedPopularMovies() {
        // Setup
        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.Popular);
        mController.initController(mDelegate, 1000, arguments);

        // Run
        mController.swipeToRefreshInitiated();

        // Verify
        verify(mEventBusProvider).postEvent(isA(PopularMoviesSwipeToRefreshEvent.class));
        verify(mEventBusProvider, never()).postEvent(isA(TopRatedMoviesSwipeToRefreshEvent.class));
    }

    @Test
    public void testSwipeToRefreshInitiatedTopRatedMovies() {
        // Setup
        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.TopRated);
        mController.initController(mDelegate, 1000, arguments);

        // Run
        mController.swipeToRefreshInitiated();

        // Verify
        verify(mEventBusProvider, never()).postEvent(isA(PopularMoviesSwipeToRefreshEvent.class));
        verify(mEventBusProvider).postEvent(isA(TopRatedMoviesSwipeToRefreshEvent.class));
    }

    @Test
    public void testSwipeToRefreshInitiatedFavouriteMovies() {
        // Setup
        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.Favourites);
        mController.initController(mDelegate, 1000, arguments);

        // Run
        mController.swipeToRefreshInitiated();

        // Verify
        verify(mEventBusProvider, never()).postEvent(isA(PopularMoviesSwipeToRefreshEvent.class));
        verify(mEventBusProvider, never()).postEvent(isA(TopRatedMoviesSwipeToRefreshEvent.class));
    }

    @Test
    public void testMovieIdSelectedNotLargeDevice() {
        // Setup
        when(mDeviceInfoProvider.isLargeDevice()).thenReturn(false);

        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.Popular);
        mController.initController(mDelegate, 1000, arguments);

        // Run
        mController.movieIdSelected(1);

        // Verify
        verify(mDeviceInfoProvider).isLargeDevice();

        verify(mEventBusProvider, never()).postEvent(any(ShowTabletMovieDetailsEvent.class));

        verify(mDelegate).startActivityIntent(mCaptorIntent.capture());

        Intent intent = mCaptorIntent.getValue();

        assertThat(intent.getComponent().getClassName(), is("com.lilarcor.popularmovies.features.moviedetails.ui.MovieDetailsActivity"));
        assertThat(intent.getIntExtra("MDCMI", -1), is(1));
    }

    @Test
    public void testMovieIdSelectedIsLargeDevice() {
        // Setup
        when(mDeviceInfoProvider.isLargeDevice()).thenReturn(true);

        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.Popular);
        mController.initController(mDelegate, 1000, arguments);

        // Run
        mController.movieIdSelected(1);

        // Verify
        verify(mDeviceInfoProvider).isLargeDevice();

        verify(mEventBusProvider).postEvent(mCaptorEventBusEvent.capture());

        ShowTabletMovieDetailsEvent event = (ShowTabletMovieDetailsEvent) mCaptorEventBusEvent.getValue();
        assertThat(event, instanceOf(ShowTabletMovieDetailsEvent.class));

        MovieDetailsFragment fragment = (MovieDetailsFragment) event.createFragment();
        assertThat(fragment, instanceOf(MovieDetailsFragment.class));
        assertThat(fragment.getArguments().getInt("MDCMI"), is(1));

        verify(mDelegate, never()).startActivityIntent(mCaptorIntent.capture());
    }

    @Test
    public void testToggleFavouriteSelectedNullMovie() {
        // Setup
        when(mMoviesProvider.getMovieWithId(1)).thenReturn(null);
        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.Popular);
        mController.initController(mDelegate, 1000, arguments);

        reset(mDelegate);
        reset(mMoviesProvider);

        // Run
        mController.toggleFavouriteSelected(1);

        // Verify
        verify(mMoviesProvider).getMovieWithId(1);
        verifyNoMoreInteractions(mMoviesProvider);
        verifyZeroInteractions(mDelegate);
    }

    @Test
    public void testToggleFavouriteSelectedMovieAlreadyFavourite() {
        // Setup
        int textDataMovieId = 135397; // This is the movie id from the text data.
        String jsonData = UnitTestFileLoader.loadTextFile(getClass(), "movie_details_data_ok.txt");
        Movie movie = spy(new Gson().fromJson(jsonData, Movie.class));
        movie.setIsFavourite(true);

        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.Popular);
        mController.initController(mDelegate, 1000, arguments);

        reset(mDelegate);
        reset(mMoviesProvider);
        when(mMoviesProvider.getMovieWithId(textDataMovieId)).thenReturn(movie);

        // Run
        mController.toggleFavouriteSelected(textDataMovieId);

        // Verify
        verify(mMoviesProvider).getMovieWithId(textDataMovieId);

        verify(mMoviesProvider).removeMovieFromFavourites(textDataMovieId);
        verify(mDelegate).showSnackbar("Jurassic World removed from your favourites.");

        verify(mMoviesProvider, never()).addMovieToFavourites(anyInt());
        verify(mDelegate, never()).showSnackbar("Jurassic World added to your favourites.");
    }

    @Test
    public void testToggleFavouriteSelectedMovieNotAlreadyFavourite() {
        // Setup
        int textDataMovieId = 135397; // This is the movie id from the text data.
        String jsonData = UnitTestFileLoader.loadTextFile(getClass(), "movie_details_data_ok.txt");
        Movie movie = spy(new Gson().fromJson(jsonData, Movie.class));
        movie.setIsFavourite(false);

        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.Popular);
        mController.initController(mDelegate, 1000, arguments);

        reset(mDelegate);
        reset(mMoviesProvider);
        when(mMoviesProvider.getMovieWithId(textDataMovieId)).thenReturn(movie);

        // Run
        mController.toggleFavouriteSelected(textDataMovieId);

        // Verify
        verify(mMoviesProvider).getMovieWithId(textDataMovieId);

        verify(mMoviesProvider, never()).removeMovieFromFavourites(textDataMovieId);
        verify(mDelegate, never()).showSnackbar("Jurassic World removed from your favourites.");

        verify(mMoviesProvider).addMovieToFavourites(anyInt());
        verify(mDelegate).showSnackbar("Jurassic World added to your favourites.");
    }

    @Test
    public void testRequestMoreDataPopularMovies() {
        // Setup
        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.Popular);
        mController.initController(mDelegate, 1000, arguments);

        // Run
        mController.requestMoreData(1);

        // Verify
        verify(mEventBusProvider).postEvent(mCaptorEventBusEvent.capture());

        EventBusEvent event = mCaptorEventBusEvent.getValue();
        assertThat(event, is(PopularMoviesRequestMoreDataEvent.class));
        assertThat(((PopularMoviesRequestMoreDataEvent) event).getFromRequestPage(), is(1));

        verify(mEventBusProvider, never()).postEvent(isA(TopRatedMoviesRequestMoreDataEvent.class));
    }

    @Test
    public void testRequestMoreDataTopRatedMovies() {
        // Setup
        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.TopRated);
        mController.initController(mDelegate, 1000, arguments);

        // Run
        mController.requestMoreData(1);

        // Verify
        verify(mEventBusProvider, never()).postEvent(isA(PopularMoviesRequestMoreDataEvent.class));

        verify(mEventBusProvider).postEvent(mCaptorEventBusEvent.capture());

        EventBusEvent event = mCaptorEventBusEvent.getValue();
        assertThat(event, is(TopRatedMoviesRequestMoreDataEvent.class));
        assertThat(((TopRatedMoviesRequestMoreDataEvent) event).getFromRequestPage(), is(1));
    }

    @Test
    public void testRequestMoreDataFavouriteMovies() {
        // Setup
        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.Favourites);
        mController.initController(mDelegate, 1000, arguments);

        // Run
        mController.requestMoreData(1);

        // Verify
        verify(mEventBusProvider, never()).postEvent(isA(PopularMoviesRequestMoreDataEvent.class));
        verify(mEventBusProvider, never()).postEvent(isA(TopRatedMoviesRequestMoreDataEvent.class));
    }

    //region Event bus events - popular movies
    @Test
    public void testOnPopularMoviesRequestFailedEventRequestPageNone() {
        // Setup
        PopularMoviesRequestFailedEvent event = new PopularMoviesRequestFailedEvent(BaseMoviesRequestEvent.REQUEST_PAGE_NONE);

        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.Popular);
        mController.initController(mDelegate, 1000, arguments);
        reset(mDelegate);

        // Run
        mController.onPopularMoviesRequestFailedEvent(event);

        // Verify
        verify(mDelegate, never()).hideSwipeIndicator();
        verify(mDelegate, never()).showSnackbar(anyString());
    }

    @Test
    public void testOnPopularMoviesRequestFailedEventRequestPageNot1() {
        // Setup
        PopularMoviesRequestFailedEvent event = new PopularMoviesRequestFailedEvent(2);

        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.Popular);
        mController.initController(mDelegate, 1000, arguments);
        reset(mDelegate);

        // Run
        mController.onPopularMoviesRequestFailedEvent(event);

        // Verify
        verify(mDelegate).hideSwipeIndicator();
        verify(mDelegate, never()).showSnackbar(anyString());
    }

    @Test
    public void testOnPopularMoviesRequestFailedEventRequestPageIs1() {
        // Setup
        PopularMoviesRequestFailedEvent event = new PopularMoviesRequestFailedEvent(1);

        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.Popular);
        mController.initController(mDelegate, 1000, arguments);
        reset(mDelegate);

        // Run
        mController.onPopularMoviesRequestFailedEvent(event);

        // Verify
        verify(mDelegate).hideSwipeIndicator();
    }

    @Test
    public void testOnPopularMoviesEventRequestCompletePopularMovies() {
        // Setup
        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.Popular);
        mController.initController(mDelegate, 1000, arguments);
        reset(mDelegate);

        // Run
        mController.onPopularMoviesRequestCompleteEvent(null);

        // Verify
        verify(mDelegate).hideSwipeIndicator();
    }
    //endregion

    //region Event bus events - top rated movies
    @Test
    public void testOnTopRatedMoviesRequestFailedEventRequestPageNone() {
        // Setup
        TopRatedMoviesRequestFailedEvent event = new TopRatedMoviesRequestFailedEvent(BaseMoviesRequestEvent.REQUEST_PAGE_NONE);

        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.TopRated);
        mController.initController(mDelegate, 1000, arguments);
        reset(mDelegate);

        // Run
        mController.onTopRatedMoviesRequestFailedEvent(event);

        // Verify
        verify(mDelegate, never()).hideSwipeIndicator();
        verify(mDelegate, never()).showSnackbar(anyString());
    }

    @Test
    public void testOnTopRatedMoviesRequestFailedEventRequestPageNot1() {
        // Setup
        TopRatedMoviesRequestFailedEvent event = new TopRatedMoviesRequestFailedEvent(2);

        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.TopRated);
        mController.initController(mDelegate, 1000, arguments);
        reset(mDelegate);

        // Run
        mController.onTopRatedMoviesRequestFailedEvent(event);

        // Verify
        verify(mDelegate).hideSwipeIndicator();
        verify(mDelegate, never()).showSnackbar(anyString());
    }

    @Test
    public void testOnTopRatedMoviesRequestFailedEventRequestPageIs1() {
        // Setup
        TopRatedMoviesRequestFailedEvent event = new TopRatedMoviesRequestFailedEvent(1);

        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.TopRated);
        mController.initController(mDelegate, 1000, arguments);
        reset(mDelegate);

        // Run
        mController.onTopRatedMoviesRequestFailedEvent(event);

        // Verify
        verify(mDelegate).hideSwipeIndicator();
    }

    @Test
    public void testOnTopRatedMoviesEventRequestCompleteTopRatedMovies() {
        // Setup
        Bundle arguments = new Bundle();
        arguments.putSerializable("MCCMMF", MoviesCollectionFilter.TopRated);
        mController.initController(mDelegate, 1000, arguments);
        reset(mDelegate);

        // Run
        mController.onTopRatedMoviesRequestCompleteEvent(null);

        // Verify
        verify(mDelegate).hideSwipeIndicator();
    }
    //endregion
}