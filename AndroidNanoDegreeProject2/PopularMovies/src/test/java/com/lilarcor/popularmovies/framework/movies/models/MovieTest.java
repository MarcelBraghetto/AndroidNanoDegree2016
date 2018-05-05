package com.lilarcor.popularmovies.framework.movies.models;

import android.content.ContentValues;
import android.database.Cursor;

import com.lilarcor.popularmovies.BuildConfig;
import com.lilarcor.popularmovies.testutils.RobolectricProperties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.Movies;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by Marcel Braghetto on 25/07/15.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class MovieTest {
    @Mock Cursor mMockCursor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBasicInstance() {
        // Setup
        Movie movie = new Movie();

        // Verify
        assertThat(movie.getMovieId(), is(0));
        assertThat(movie.getTitle(), is(""));
        assertThat(movie.getOverview(), is(""));
        assertThat(movie.getBackdropImageUrl(), is(""));
        assertThat(movie.getPosterImageUrl(), is(""));
        assertThat(movie.getReleaseDate(), is(""));
        assertThat(movie.getVoteAverage(), is(0f));
        assertThat(movie.getVoteCount(), is(0));
        assertThat(movie.isFavourite(), is(false));

        ContentValues contentValues = movie.getContentValues();
        assertThat(contentValues.size(), is(9));
        assertThat(contentValues.getAsInteger(Movies._ID), is(0));
        assertThat(contentValues.getAsString(Movies.COLUMN_MOVIE_TITLE), is(""));
        assertThat(contentValues.getAsString(Movies.COLUMN_MOVIE_OVERVIEW), is(""));
        assertThat(contentValues.getAsString(Movies.COLUMN_MOVIE_BACKDROP_PATH), is(""));
        assertThat(contentValues.getAsString(Movies.COLUMN_MOVIE_POSTER_PATH), is(""));
        assertThat(contentValues.getAsString(Movies.COLUMN_MOVIE_RELEASE_DATE), is(""));
        assertThat(contentValues.getAsFloat(Movies.COLUMN_MOVIE_VOTE_AVERAGE), is(0f));
        assertThat(contentValues.getAsInteger(Movies.COLUMN_MOVIE_VOTE_COUNT), is(0));
        assertThat(contentValues.getAsInteger(Movies.COLUMN_MOVIE_IS_FAVOURITE), is(0));
    }

    @Test
    public void testPopulateFromCursorNullCursor() {
        // Setup
        Movie movie = new Movie();

        // Run
        movie.populateFromCursor(null, Movies.getAllColumnsIndexMap());

        // Verify
        assertThat(movie.getMovieId(), is(0));
        assertThat(movie.getTitle(), is(""));
        assertThat(movie.getOverview(), is(""));
        assertThat(movie.getBackdropImageUrl(), is(""));
        assertThat(movie.getPosterImageUrl(), is(""));
        assertThat(movie.getReleaseDate(), is(""));
        assertThat(movie.getVoteAverage(), is(0f));
        assertThat(movie.getVoteCount(), is(0));
        assertThat(movie.isFavourite(), is(false));

        ContentValues contentValues = movie.getContentValues();
        assertThat(contentValues.size(), is(9));
        assertThat(contentValues.getAsInteger(Movies._ID), is(0));
        assertThat(contentValues.getAsString(Movies.COLUMN_MOVIE_TITLE), is(""));
        assertThat(contentValues.getAsString(Movies.COLUMN_MOVIE_OVERVIEW), is(""));
        assertThat(contentValues.getAsString(Movies.COLUMN_MOVIE_BACKDROP_PATH), is(""));
        assertThat(contentValues.getAsString(Movies.COLUMN_MOVIE_POSTER_PATH), is(""));
        assertThat(contentValues.getAsString(Movies.COLUMN_MOVIE_RELEASE_DATE), is(""));
        assertThat(contentValues.getAsFloat(Movies.COLUMN_MOVIE_VOTE_AVERAGE), is(0f));
        assertThat(contentValues.getAsInteger(Movies.COLUMN_MOVIE_VOTE_COUNT), is(0));
        assertThat(contentValues.getAsInteger(Movies.COLUMN_MOVIE_IS_FAVOURITE), is(0));
    }

    @Test
    public void testPopulateFromCursorWithData() {
        // Setup
        Movie movie = new Movie();

        when(mMockCursor.getInt(0)).thenReturn(99);
        when(mMockCursor.getString(1)).thenReturn("movie title");
        when(mMockCursor.getString(2)).thenReturn("movie overview");
        when(mMockCursor.getString(3)).thenReturn("/movie backdrop path");
        when(mMockCursor.getString(4)).thenReturn("/movie poster path");
        when(mMockCursor.getString(5)).thenReturn("movie release date");
        when(mMockCursor.getFloat(6)).thenReturn(99f);
        when(mMockCursor.getInt(7)).thenReturn(11);
        when(mMockCursor.getInt(8)).thenReturn(1);

        // Run
        movie.populateFromCursor(mMockCursor, Movies.getAllColumnsIndexMap());

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

        ContentValues contentValues = movie.getContentValues();
        assertThat(contentValues.size(), is(9));
        assertThat(contentValues.getAsInteger(Movies._ID), is(99));
        assertThat(contentValues.getAsString(Movies.COLUMN_MOVIE_TITLE), is("movie title"));
        assertThat(contentValues.getAsString(Movies.COLUMN_MOVIE_OVERVIEW), is("movie overview"));
        assertThat(contentValues.getAsString(Movies.COLUMN_MOVIE_BACKDROP_PATH), is("http://image.tmdb.org/t/p/w500/movie backdrop path"));
        assertThat(contentValues.getAsString(Movies.COLUMN_MOVIE_POSTER_PATH), is("http://image.tmdb.org/t/p/w185/movie poster path"));
        assertThat(contentValues.getAsString(Movies.COLUMN_MOVIE_RELEASE_DATE), is("movie release date"));
        assertThat(contentValues.getAsFloat(Movies.COLUMN_MOVIE_VOTE_AVERAGE), is(99f));
        assertThat(contentValues.getAsInteger(Movies.COLUMN_MOVIE_VOTE_COUNT), is(11));
        assertThat(contentValues.getAsInteger(Movies.COLUMN_MOVIE_IS_FAVOURITE), is(1));
    }
}