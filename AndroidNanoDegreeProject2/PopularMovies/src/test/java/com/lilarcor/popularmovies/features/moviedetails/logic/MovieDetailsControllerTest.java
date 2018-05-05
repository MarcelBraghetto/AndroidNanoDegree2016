package com.lilarcor.popularmovies.features.moviedetails.logic;

import android.content.Context;
import android.os.Bundle;

import com.google.gson.Gson;
import com.lilarcor.popularmovies.BuildConfig;
import com.lilarcor.popularmovies.features.application.MainApp;
import com.lilarcor.popularmovies.framework.foundation.appstrings.contracts.AppStringsProvider;
import com.lilarcor.popularmovies.framework.foundation.network.contracts.NetworkRequestProvider;
import com.lilarcor.popularmovies.framework.foundation.threadutils.contracts.ThreadUtilsProvider;
import com.lilarcor.popularmovies.framework.movies.models.Movie;
import com.lilarcor.popularmovies.framework.movies.provider.contracts.MoviesProvider;
import com.lilarcor.popularmovies.testutils.RobolectricProperties;
import com.lilarcor.popularmovies.testutils.UnitTestFileLoader;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by Marcel Braghetto on 26/07/15.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class MovieDetailsControllerTest {
    @Mock Context mApplicationContext;
    @Mock MoviesProvider mMoviesProvider;
    @Mock NetworkRequestProvider mNetworkRequestProvider;
    @Mock ThreadUtilsProvider mThreadUtilsProvider;
    @Mock MovieDetailsController.ControllerDelegate mDelegate;

    private AppStringsProvider mAppStringsProvider;
    private MovieDetailsController mController;

    @Rule public ExpectedException mExpectedException = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mAppStringsProvider = MainApp.getDagger().getAppStringsProvider();

        mController = new MovieDetailsController(mApplicationContext, mMoviesProvider, mAppStringsProvider, mNetworkRequestProvider, mThreadUtilsProvider);
    }

    @Test
    public void testInitControllerNullIntent() {
        // Setup
        mExpectedException.expect(IllegalArgumentException.class);

        Bundle bundle = null;

        // Run
        mController.initController(mDelegate, bundle);
    }

    @Test
    public void testInitControllerIntentMissingData() {
        // Setup
        mExpectedException.expect(IllegalArgumentException.class);

        Bundle bundle = new Bundle();

        // Run
        mController.initController(mDelegate, bundle);
    }

    @Test
    public void testInitControllerWithNotFoundMovieId() {
        // Setup
        when(mMoviesProvider.getMovieWithId(1)).thenReturn(null);

        Bundle bundle = new Bundle();
        bundle.putInt("MDCMI", 1);

        // Run
        mController.initController(mDelegate, bundle);

        // Verify
        verify(mDelegate).finishActivity();
        verifyNoMoreInteractions(mDelegate);
    }

    @Test
    public void testInitControllerWithValidMovieId() {
        // Setup
        String jsonData = UnitTestFileLoader.loadTextFile(getClass(), "movie_details_data_ok.txt");
        Movie movie = new Gson().fromJson(jsonData, Movie.class);
        when(mMoviesProvider.getMovieWithId(1)).thenReturn(movie);

        Bundle bundle = new Bundle();
        bundle.putInt("MDCMI", 1);

        // Run
        mController.initController(mDelegate, bundle);

        // Verify
        verify(mDelegate, never()).finishActivity();
        verify(mDelegate).hideHeaderImageFailedIndicator();
        verify(mDelegate).showHeaderImageProgressIndicator();
        verify(mDelegate).setActivityTitle("Jurassic World");
        verify(mDelegate).refreshFloatingActionButton(false);

        verify(mDelegate, never()).hideHeaderImageProgressIndicator();
        verify(mDelegate, never()).showHeaderImageFailedIndicator();
        verify(mDelegate).loadHeaderImage("http://image.tmdb.org/t/p/w500/dkMD5qlogeRMiEixC4YNPUvax2T.jpg");

        verify(mDelegate).setVoteAverageText("70%");
        verify(mDelegate).setVoteTotalText("1369 votes");
//        verify(mDelegate).setReleaseDateText("Friday 12 June 2015");
//        verify(mDelegate).setOverviewText("Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.");
    }

    @Test
    public void testInitControllerWithValidMovieIdMissingBackdropPath() {
        // Setup
        String jsonData = UnitTestFileLoader.loadTextFile(getClass(), "movie_details_data_missing_backdrop_path.txt");
        Movie movie = new Gson().fromJson(jsonData, Movie.class);
        when(mMoviesProvider.getMovieWithId(1)).thenReturn(movie);

        Bundle bundle = new Bundle();
        bundle.putInt("MDCMI", 1);

        // Run
        mController.initController(mDelegate, bundle);

        // Verify
        verify(mDelegate, never()).finishActivity();
        verify(mDelegate).hideHeaderImageFailedIndicator();
        verify(mDelegate).showHeaderImageProgressIndicator();
        verify(mDelegate).setActivityTitle("Jurassic World");
        verify(mDelegate).refreshFloatingActionButton(false);

        verify(mDelegate).hideHeaderImageProgressIndicator();
        verify(mDelegate).showHeaderImageFailedIndicator();
        verify(mDelegate, never()).loadHeaderImage(anyString());

        verify(mDelegate).setVoteAverageText("70%");
        verify(mDelegate).setVoteTotalText("1369 votes");
//        verify(mDelegate).setReleaseDateText("Friday 12 June 2015");
//        verify(mDelegate).setOverviewText("Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.");
    }

    @Test
    public void testInitControllerWithValidMovieIdMissingReleaseDate() {
        // Setup
        String jsonData = UnitTestFileLoader.loadTextFile(getClass(), "movie_details_data_missing_release_date.txt");
        Movie movie = new Gson().fromJson(jsonData, Movie.class);
        when(mMoviesProvider.getMovieWithId(1)).thenReturn(movie);

        Bundle bundle = new Bundle();
        bundle.putInt("MDCMI", 1);

        // Run
        mController.initController(mDelegate, bundle);

        // Verify
        verify(mDelegate, never()).finishActivity();
        verify(mDelegate).hideHeaderImageFailedIndicator();
        verify(mDelegate).showHeaderImageProgressIndicator();
        verify(mDelegate).setActivityTitle("Jurassic World");
        verify(mDelegate).refreshFloatingActionButton(false);

        verify(mDelegate, never()).hideHeaderImageProgressIndicator();
        verify(mDelegate, never()).showHeaderImageFailedIndicator();
        verify(mDelegate).loadHeaderImage("http://image.tmdb.org/t/p/w500/dkMD5qlogeRMiEixC4YNPUvax2T.jpg");

        verify(mDelegate).setVoteAverageText("70%");
        verify(mDelegate).setVoteTotalText("1369 votes");
//        verify(mDelegate).setReleaseDateText("Unknown");
//        verify(mDelegate).setOverviewText("Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.");
    }

    @Test
    public void testInitControllerWithValidMovieIdMalformedReleaseDate() {
        // Setup
        String jsonData = UnitTestFileLoader.loadTextFile(getClass(), "movie_details_data_malformed_release_date.txt");
        Movie movie = new Gson().fromJson(jsonData, Movie.class);
        when(mMoviesProvider.getMovieWithId(1)).thenReturn(movie);

        Bundle bundle = new Bundle();
        bundle.putInt("MDCMI", 1);

        // Run
        mController.initController(mDelegate, bundle);

        // Verify
        verify(mDelegate, never()).finishActivity();
        verify(mDelegate).hideHeaderImageFailedIndicator();
        verify(mDelegate).showHeaderImageProgressIndicator();
        verify(mDelegate).setActivityTitle("Jurassic World");
        verify(mDelegate).refreshFloatingActionButton(false);

        verify(mDelegate, never()).hideHeaderImageProgressIndicator();
        verify(mDelegate, never()).showHeaderImageFailedIndicator();
        verify(mDelegate).loadHeaderImage("http://image.tmdb.org/t/p/w500/dkMD5qlogeRMiEixC4YNPUvax2T.jpg");

        verify(mDelegate).setVoteAverageText("70%");
        verify(mDelegate).setVoteTotalText("1369 votes");
//        verify(mDelegate).setReleaseDateText("Unknown");
//        verify(mDelegate).setOverviewText("Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.");
    }

    @Test
    public void testToggleFavouriteSelectedIsAlreadyFavourite() {
        // Setup
        int textDataMovieId = 135397; // This is the movie id from the text data.

        String jsonData = UnitTestFileLoader.loadTextFile(getClass(), "movie_details_data_ok.txt");
        Movie movie = spy(new Gson().fromJson(jsonData, Movie.class));
        movie.setIsFavourite(true);
        when(mMoviesProvider.getMovieWithId(textDataMovieId)).thenReturn(movie);

        Bundle bundle = new Bundle();
        bundle.putInt("MDCMI", textDataMovieId);

        mController.initController(mDelegate, bundle);

        reset(mDelegate);
        reset(mMoviesProvider);
        reset(movie);

        // Run
        mController.toggleFavouriteSelected();

        // Verify
        verify(movie, times(2)).isFavourite();
        verify(movie).setIsFavourite(false);
        verify(mMoviesProvider).removeMovieFromFavourites(textDataMovieId);
        verify(mDelegate).showSnackbar("Jurassic World removed from your favourites.");

        verify(movie, never()).setIsFavourite(true);
        verify(mMoviesProvider, never()).addMovieToFavourites(anyInt());
        verify(mDelegate, never()).showSnackbar("Jurassic World added to your favourites.");

        verify(mDelegate).refreshFloatingActionButton(false);
    }

    @Test
    public void testToggleFavouriteSelectedIsNotAlreadyFavourite() {
        // Setup
        int textDataMovieId = 135397; // This is the movie id from the text data.

        String jsonData = UnitTestFileLoader.loadTextFile(getClass(), "movie_details_data_ok.txt");
        Movie movie = spy(new Gson().fromJson(jsonData, Movie.class));
        movie.setIsFavourite(false);
        when(mMoviesProvider.getMovieWithId(textDataMovieId)).thenReturn(movie);

        Bundle bundle = new Bundle();
        bundle.putInt("MDCMI", textDataMovieId);

        mController.initController(mDelegate, bundle);

        reset(mDelegate);
        reset(mMoviesProvider);
        reset(movie);

        // Run
        mController.toggleFavouriteSelected();

        // Verify
        verify(movie, times(2)).isFavourite();
        verify(movie, never()).setIsFavourite(false);
        verify(mMoviesProvider, never()).removeMovieFromFavourites(anyInt());
        verify(mDelegate, never()).showSnackbar("Jurassic World removed from your favourites.");

        verify(movie).setIsFavourite(true);
        verify(mMoviesProvider).addMovieToFavourites(textDataMovieId);
        verify(mDelegate).showSnackbar("Jurassic World added to your favourites.");

        verify(mDelegate).refreshFloatingActionButton(true);
    }
}