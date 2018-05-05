package com.lilarcor.popularmovies.framework.movies.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lilarcor.popularmovies.features.moviescollection.logic.models.MoviesCollectionFilter;
import com.lilarcor.popularmovies.framework.movies.models.Movie;
import com.lilarcor.popularmovies.framework.movies.provider.contracts.MoviesProvider;

import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.Movies;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.PopularMovies;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.TopRatedMovies;

/**
 * Created by Marcel Braghetto on 21/07/15.
 *
 * Implementation of the movies provider contract, primarily responsible for
 * being the proxy between the movies content provider and the consumer of
 * this contract through the Dagger injection framework.
 */
public final class DefaultMoviesProvider implements MoviesProvider {
    private final Context mApplicationContext;

    public DefaultMoviesProvider(@NonNull Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    @Override
    public void addMovieToFavourites(int movieId) {
        ContentValues values = new ContentValues();
        values.put(Movies.COLUMN_MOVIE_IS_FAVOURITE, 1);
        mApplicationContext.getContentResolver().update(Movies.getContentUriSpecificMovie(movieId), values, null, null);

        mApplicationContext.getContentResolver().notifyChange(PopularMovies.getContentUriAllPopularMovies(), null);
        mApplicationContext.getContentResolver().notifyChange(TopRatedMovies.getContentUriAllTopRatedMovies(), null);
        mApplicationContext.getContentResolver().notifyChange(Movies.getContentUriAllFavouriteMovies(), null);
    }

    @Override
    public void removeMovieFromFavourites(int movieId) {
        ContentValues values = new ContentValues();
        values.put(Movies.COLUMN_MOVIE_IS_FAVOURITE, 0);
        mApplicationContext.getContentResolver().update(Movies.getContentUriSpecificMovie(movieId), values, null, null);

        mApplicationContext.getContentResolver().notifyChange(PopularMovies.getContentUriAllPopularMovies(), null);
        mApplicationContext.getContentResolver().notifyChange(TopRatedMovies.getContentUriAllTopRatedMovies(), null);
        mApplicationContext.getContentResolver().notifyChange(Movies.getContentUriAllFavouriteMovies(), null);
    }

    @Override
    public void deleteAllPopularMovies() {
        mApplicationContext.getContentResolver().delete(PopularMovies.getContentUriAllPopularMovies(), null, null);
    }

    @Override
    public void deleteAllTopRatedMovies() {
        mApplicationContext.getContentResolver().delete(TopRatedMovies.getContentUriAllTopRatedMovies(), null, null);
    }

    @Override
    public void deleteAllMovies() {
        mApplicationContext.getContentResolver().delete(Movies.getContentUriAllMovies(), null, null);
    }

    @Override
    public void saveMovie(@NonNull Movie movie) {
        mApplicationContext.getContentResolver().insert(Movies.getContentUriAllMovies(), movie.getContentValues());
    }

    @Override
    public void saveBulkMovies(@NonNull Movie[] movies) {
        ContentValues[] contentValues = new ContentValues[movies.length];

        for(int i = 0; i < movies.length; i++) {
            contentValues[i] = movies[i].getContentValues();
        }

        mApplicationContext.getContentResolver().bulkInsert(Movies.getContentUriAllMovies(), contentValues);
    }

    @Override
    public void savePopularMovie(int movieId, int resultPage) {
        ContentValues values = new ContentValues();

        values.put(PopularMovies.COLUMN_MOVIE_ID, movieId);
        values.put(PopularMovies.COLUMN_RESULT_PAGE, resultPage);

        mApplicationContext.getContentResolver().insert(PopularMovies.getContentUriAllPopularMovies(), values);
    }

    @Override
    public void saveBulkPopularMovies(@NonNull ContentValues[] popularMoviesContentValues) {
        mApplicationContext.getContentResolver().bulkInsert(PopularMovies.getContentUriAllPopularMovies(), popularMoviesContentValues);
    }

    @Override
    public void saveTopRatedMovie(int movieId, int resultPage) {
        ContentValues values = new ContentValues();
        values.put(TopRatedMovies.COLUMN_MOVIE_ID, movieId);
        values.put(TopRatedMovies.COLUMN_RESULT_PAGE, resultPage);

        mApplicationContext.getContentResolver().insert(TopRatedMovies.getContentUriAllTopRatedMovies(), values);
    }

    @Override
    public void saveBulkTopRatedMovies(@NonNull ContentValues[] topRatedMoviesContentValues) {
        mApplicationContext.getContentResolver().bulkInsert(TopRatedMovies.getContentUriAllTopRatedMovies(), topRatedMoviesContentValues);
    }

    @Override
    @NonNull
    public Uri getMovieCollectionUri(@NonNull MoviesCollectionFilter filter) {
        switch (filter) {
            case Popular:
                return PopularMovies.getContentUriAllPopularMovies();

            case TopRated:
                return TopRatedMovies.getContentUriAllTopRatedMovies();

            case Favourites:
                return Movies.getContentUriAllFavouriteMovies();

            default:
                throw new IllegalArgumentException("Unable to get content URI - invalid movies collection filter provided.");
        }
    }

    @Override
    @Nullable
    public Movie getMovieWithId(int movieId) {
        Cursor cursor = mApplicationContext.getContentResolver().query(Movies.getContentUriSpecificMovie(movieId), null, null, null, null);

        if(cursor != null && cursor.moveToFirst()) {
            Movie movie = new Movie();
            movie.populateFromCursor(cursor, Movies.getAllColumnsIndexMap());

            cursor.close();

            return movie;
        }

        return null;
    }
}
