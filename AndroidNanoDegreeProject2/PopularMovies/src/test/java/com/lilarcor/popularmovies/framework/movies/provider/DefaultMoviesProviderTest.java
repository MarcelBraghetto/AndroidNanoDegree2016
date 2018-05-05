package com.lilarcor.popularmovies.framework.movies.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.lilarcor.popularmovies.BuildConfig;
import com.lilarcor.popularmovies.features.moviescollection.logic.models.MoviesCollectionFilter;
import com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract;
import com.lilarcor.popularmovies.framework.movies.models.Movie;
import com.lilarcor.popularmovies.testutils.RobolectricProperties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Marcel Braghetto on 26/07/15.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class DefaultMoviesProviderTest {
    @Mock Context mApplicationContext;
    @Mock ContentResolver mContentResolver;
    @Mock Cursor mMockCursor;

    private DefaultMoviesProvider mProvider;

    @Captor ArgumentCaptor<ContentValues> mCaptorContentValues;
    @Captor ArgumentCaptor<Uri> mCaptorUri1;
    @Captor ArgumentCaptor<Uri> mCaptorUri2;
    @Captor ArgumentCaptor<Uri> mCaptorUri3;
    @Captor ArgumentCaptor<Uri> mCaptorUri4;

    @Captor ArgumentCaptor<String> mCaptorWhereClause;
    @Captor ArgumentCaptor<String[]> mCaptorSelectionArgs;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(mApplicationContext.getContentResolver()).thenReturn(mContentResolver);

        mProvider = new DefaultMoviesProvider(mApplicationContext);
    }

    @Test
    public void testAddMovieToFavourites() {
        // Run
        mProvider.addMovieToFavourites(11);

        // Verify
        verify(mApplicationContext.getContentResolver()).update(
                mCaptorUri1.capture(),
                mCaptorContentValues.capture(),
                mCaptorWhereClause.capture(),
                mCaptorSelectionArgs.capture());

        assertThat(mCaptorUri1.getValue(), is(MoviesContentContract.Movies.getContentUriSpecificMovie(11)));
        ContentValues contentValues = mCaptorContentValues.getValue();
        assertThat(contentValues.size(), is(1));
        assertThat(contentValues.getAsInteger(MoviesContentContract.Movies.COLUMN_MOVIE_IS_FAVOURITE), is(1));
        assertThat(mCaptorWhereClause.getValue(), nullValue());
        assertThat(mCaptorSelectionArgs.getValue(), nullValue());

        verify(mApplicationContext.getContentResolver()).notifyChange(
                MoviesContentContract.PopularMovies.getContentUriAllPopularMovies(),
                null);

        verify(mApplicationContext.getContentResolver()).notifyChange(
                MoviesContentContract.TopRatedMovies.getContentUriAllTopRatedMovies(),
                null);

        verify(mApplicationContext.getContentResolver()).notifyChange(
                MoviesContentContract.Movies.getContentUriAllFavouriteMovies(),
                null);
    }

    @Test
    public void testRemoveMovieFromFavourites() {
        // Run
        mProvider.removeMovieFromFavourites(11);

        // Verify
        verify(mApplicationContext.getContentResolver()).update(
                mCaptorUri1.capture(),
                mCaptorContentValues.capture(),
                mCaptorWhereClause.capture(),
                mCaptorSelectionArgs.capture());

        assertThat(mCaptorUri1.getValue().toString(), is("content://com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentProvider/movies/11"));
        ContentValues contentValues = mCaptorContentValues.getValue();
        assertThat(contentValues.size(), is(1));
        assertThat(contentValues.getAsInteger(MoviesContentContract.Movies.COLUMN_MOVIE_IS_FAVOURITE), is(0));
        assertThat(mCaptorWhereClause.getValue(), nullValue());
        assertThat(mCaptorSelectionArgs.getValue(), nullValue());

        verify(mApplicationContext.getContentResolver()).notifyChange(
                MoviesContentContract.PopularMovies.getContentUriAllPopularMovies(),
                null);

        verify(mApplicationContext.getContentResolver()).notifyChange(
                MoviesContentContract.TopRatedMovies.getContentUriAllTopRatedMovies(),
                null);

        verify(mApplicationContext.getContentResolver()).notifyChange(
                MoviesContentContract.Movies.getContentUriAllFavouriteMovies(),
                null);
    }

    @Test
    public void testDeleteAllPopularMovies() {
        // Run
        mProvider.deleteAllPopularMovies();

        // Verify
        verify(mApplicationContext.getContentResolver()).delete(
                MoviesContentContract.PopularMovies.getContentUriAllPopularMovies(),
                null,
                null);
    }

    @Test
    public void testDeleteAllTopRatedMovies() {
        // Run
        mProvider.deleteAllTopRatedMovies();

        // Verify
        verify(mApplicationContext.getContentResolver()).delete(
                MoviesContentContract.TopRatedMovies.getContentUriAllTopRatedMovies(),
                null,
                null);
    }

    @Test
    public void testSaveMovie() {
        // Setup
        Movie movie = new Movie();

        // Run
        mProvider.saveMovie(movie);

        // Verify
        // Verify
        verify(mApplicationContext.getContentResolver()).insert(
                MoviesContentContract.Movies.getContentUriAllMovies(), movie.getContentValues());
    }

    @Test
    public void testSavePopularMovie() {
        // Run
        mProvider.savePopularMovie(11, 1);

        // Verify
        verify(mApplicationContext.getContentResolver()).insert(
                mCaptorUri1.capture(),
                mCaptorContentValues.capture()
        );

        assertThat(mCaptorUri1.getValue(), is(MoviesContentContract.PopularMovies.getContentUriAllPopularMovies()));

        ContentValues contentValues = mCaptorContentValues.getValue();
        assertThat(contentValues.size(), is(2));
        assertThat(contentValues.getAsInteger(MoviesContentContract.PopularMovies.COLUMN_MOVIE_ID), is(11));
        assertThat(contentValues.getAsInteger(MoviesContentContract.PopularMovies.COLUMN_RESULT_PAGE), is(1));
    }

    @Test
    public void testSaveTopRatedMovie() {
        // Run
        mProvider.saveTopRatedMovie(11, 1);

        // Verify
        verify(mApplicationContext.getContentResolver()).insert(
                mCaptorUri1.capture(),
                mCaptorContentValues.capture()
        );

        assertThat(mCaptorUri1.getValue(), is(MoviesContentContract.TopRatedMovies.getContentUriAllTopRatedMovies()));

        ContentValues contentValues = mCaptorContentValues.getValue();
        assertThat(contentValues.size(), is(2));
        assertThat(contentValues.getAsInteger(MoviesContentContract.PopularMovies.COLUMN_MOVIE_ID), is(11));
        assertThat(contentValues.getAsInteger(MoviesContentContract.PopularMovies.COLUMN_RESULT_PAGE), is(1));
    }

    @Test
    public void testGetMovieCollectionUriPopular() {
        // Run
        Uri result = mProvider.getMovieCollectionUri(MoviesCollectionFilter.Popular);

        // Verify
        assertThat(result, is(MoviesContentContract.PopularMovies.getContentUriAllPopularMovies()));
    }

    @Test
    public void testGetMovieCollectionUriTopRated() {
        // Run
        Uri result = mProvider.getMovieCollectionUri(MoviesCollectionFilter.TopRated);

        // Verify
        assertThat(result, is(MoviesContentContract.TopRatedMovies.getContentUriAllTopRatedMovies()));
    }

    @Test
    public void testGetMovieCollectionUriFavourites() {
        // Run
        Uri result = mProvider.getMovieCollectionUri(MoviesCollectionFilter.Favourites);

        // Verify
        assertThat(result, is(MoviesContentContract.Movies.getContentUriAllFavouriteMovies()));
    }

    @Test
    public void testGetMovieWithIdMovieDoesNotExist() {
        // Setup
        when(mApplicationContext
                .getContentResolver()
                .query(MoviesContentContract.Movies.getContentUriSpecificMovie(11),
                null,
                null,
                null,
                null)).thenReturn(null);

        // Run
        Movie result = mProvider.getMovieWithId(11);

        // Verify
        assertThat(result, nullValue());
    }

    @Test
    public void testGetMovieWithIdMovieExists() {
        // Setup
        when(mMockCursor.getInt(0)).thenReturn(99);
        when(mMockCursor.getString(1)).thenReturn("movie title");
        when(mMockCursor.getString(2)).thenReturn("movie overview");
        when(mMockCursor.getString(3)).thenReturn("/movie backdrop path");
        when(mMockCursor.getString(4)).thenReturn("/movie poster path");
        when(mMockCursor.getString(5)).thenReturn("movie release date");
        when(mMockCursor.getFloat(6)).thenReturn(99f);
        when(mMockCursor.getInt(7)).thenReturn(11);
        when(mMockCursor.getInt(8)).thenReturn(1);

        when(mApplicationContext
                .getContentResolver()
                .query(MoviesContentContract.Movies.getContentUriSpecificMovie(11),
                        null,
                        null,
                        null,
                        null)).thenReturn(mMockCursor);

        when(mMockCursor.moveToFirst()).thenReturn(true);

        // Run
        Movie movie = mProvider.getMovieWithId(11);

        // Verify
        assertThat(movie.getMovieId(), is(99));
        assertThat(movie.getTitle(), is("movie title"));
        assertThat(movie.getOverview(), is("movie overview"));
        assertThat(movie.getBackdropImageUrl(), is("http://image.tmdb.org/t/p/w500/movie backdrop path"));
        assertThat(movie.getPosterImageUrl(), is("http://image.tmdb.org/t/p/w185/movie poster path"));
        assertThat(movie.getReleaseDate(), is("movie release date"));
        assertThat(movie.getVoteAverage(), is(99f));
        assertThat(movie.getVoteCount(), is(11));
        assertThat(movie.isFavourite(), is(true));
    }
}