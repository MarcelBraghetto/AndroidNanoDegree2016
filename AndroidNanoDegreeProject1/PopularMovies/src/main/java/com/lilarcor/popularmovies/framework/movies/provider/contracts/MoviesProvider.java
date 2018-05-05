package com.lilarcor.popularmovies.framework.movies.provider.contracts;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lilarcor.popularmovies.features.moviescollection.logic.models.MoviesCollectionFilter;
import com.lilarcor.popularmovies.framework.movies.models.Movie;

/**
 * Created by Marcel Braghetto on 18/07/15.
 *
 * Movies provider to encapsulate the various tasks related to managing
 * movie data via dependency injection. In particular this assist with
 * maintaing separation of concerns and allows consumers of movie feature
 * functionality to not be aware of any implementation behind it.
 *
 * It also greatly helps with unit testing!!
 */
public interface MoviesProvider {
    /**
     * Given a movie id, add it to the current user's
     * favourite movies collection.
     *
     * @param movieId to add as a favourite.
     */
    void addMovieToFavourites(int movieId);

    /**
     * Given a movie id, remove if from the current user's
     * favourite movies collection.
     *
     * @param movieId to remove from favourites.
     */
    void removeMovieFromFavourites(int movieId);

    /**
     * Delete all the currently stored 'popular movies'
     * records. Highly destructive - use with caution!
     */
    void deleteAllPopularMovies();

    /**
     * Delete all the currently stored 'top rated movies'
     * records. Highly destructive - use with caution!
     */
    void deleteAllTopRatedMovies();

    /**
     * Delete all the currently stored movie records.
     * Highly destructive - use with caution!
     */
    void deleteAllMovies();

    /**
     * Given a movie collection 'filter' (eg, popular, top
     * rated, favourites), return the content URI that can
     * be used to access the collection via the movies
     * content provider.
     *
     * If a movies collection filter is passed in that is
     * not recognised, an illegal argument exception will
     * be thrown.
     *
     * @param filter to obtain the URI for.
     *
     * @return content URI for the given movies collection filter type.
     */
    @NonNull Uri getMovieCollectionUri(@NonNull MoviesCollectionFilter filter);

    /**
     * Given a movie id, return the content for that movie from the
     * movies provider. If the movie cannot be found, null will be
     * returend.
     *
     * @param movieId to obtain the movie for.
     *
     * @return movie with the given movie id or null.
     */
    @Nullable Movie getMovieWithId(int movieId);

    /**
     * Given a movie instance, save it to the persistent storage via
     * the movies content provider. Saving a movie will either insert
     * it or update it if it already exists.
     *
     * @param movie to save.
     */
    void saveMovie(@NonNull Movie movie);

    /**
     * Save the array of movies via the movies content provider. Using
     * the bulk saving mechanism is faster than doing one movie at a
     * time.
     *
     * @param movies to save.
     */
    void saveBulkMovies(@NonNull Movie[] movies);

    /**
     * Given a movie id and a result page, save them as a new
     * 'popular movie' record in the persistent storage via the
     * movies content provider.
     *
     * Saving these records will update the data source used for
     * the 'popular movies' collection store.
     *
     * @param movieId to associate this 'popular movie' record with.
     * @param resultPage the request response 'page' for which this
     *                   popular movie record was added.
     */
    void savePopularMovie(int movieId, int resultPage);

    /**
     * Given a collection of content values that represent popular
     * movie records, save them using the bulk save mechanism.
     *
     * @param popularMoviesContentValues collection of content values representing
     *                                   popular movie objects.
     */
    void saveBulkPopularMovies(@NonNull ContentValues[] popularMoviesContentValues);

    /**
     * Given a movie id and a result page, save them as a new
     * 'top rated movie' record in the persistent storage via the
     * movies content provider.
     *
     * Saving these records will update the data source used for
     * the 'top rated movies' collection store.
     *
     * @param movieId to associate this 'top rated movie' record with.
     * @param resultPage the request response 'page' for which this
     *                   top rated movie record was added.
     */
    void saveTopRatedMovie(int movieId, int resultPage);

    /**
     * Given a collection of content values that represent top rated
     * movie records, save them using the bulk save mechanism.
     *
     * @param topRatedMoviesContentValues collection of content values representing
     *                                    top rated movie objects.
     */
    void saveBulkTopRatedMovies(@NonNull ContentValues[] topRatedMoviesContentValues);
}
