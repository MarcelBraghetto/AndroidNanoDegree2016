package com.lilarcor.popularmovies.framework.foundation.network.contracts;

import android.support.annotation.NonNull;

import com.lilarcor.popularmovies.framework.foundation.network.models.MovieReviewsDTO;
import com.lilarcor.popularmovies.framework.foundation.network.models.MovieVideosDTO;
import com.lilarcor.popularmovies.framework.foundation.network.models.MoviesDTO;

import retrofit.Callback;

/**
 * Created by Marcel Braghetto on 12/07/15.
 *
 * This contract provides methods to do basic network request
 * operations, and offers response caching configured on a per
 * request basis.
 *
 * Important note: Unless explicitly specified, ALL request
 * callback methods will be running in a background thread, so
 * if you need to do work on the main/UI thread, you need to do
 * that yourself from your callback implementation.
 *
 */
public interface NetworkRequestProvider {
    /**
     * The type of cache policy to apply to a network
     * request.
     */
    enum CacheRequestPolicy {
        Instant,
        Normal
    }

    /**
     * Clear any cached responses so requests will resolve to the server again.
     *
     * @return true if the cache was successfully cleared.
     */
    boolean clearResponseCache();

    /**
     * Given a 'request page' and some configuration, retrieve the list
     * of popular movies from the Movies DB server (or cache).
     *
     * @param requestPage to request from the server.
     * @param cacheRequestPolicy to determine whether a cached response is
     *                           acceptable and up to what age.
     * @param callback to delegate the responses to - NOTE the callback will
     *                 be invoked on a background thread. A successful callback will
     *                 contain an inflated DTO representing the server response.
     */
    void getPopularMovies(int requestPage, @NonNull CacheRequestPolicy cacheRequestPolicy, @NonNull Callback<MoviesDTO> callback);

    /**
     * Given a 'request page' and some configuration, retrieve the list
     * of top rated movies from the Movies DB server (or cache).
     *
     * @param requestPage to request from the server.
     * @param cacheRequestPolicy to determine whether a cached response is
     *                           acceptable and up to what age.
     * @param callback to delegate the responses to.
     */
    void getTopRatedMovies(int requestPage, @NonNull CacheRequestPolicy cacheRequestPolicy, @NonNull Callback<MoviesDTO> callback);

    /**
     * Given a movie id, retrieve tthe list of video clips associated with that
     * movie (if any) from the Movies DB server (or cache).
     *
     * @param movieId to retrieve the videos for.
     * @param callback to delegate the response to.
     */
    void getMovieVideos(int movieId, @NonNull Callback<MovieVideosDTO> callback);

    /**
     * Given a movie id, retrieve the list of reviews associated with that
     * movie (if any) from the Movies DB server (or cache).
     *
     * @param movieId to retrieve the reviews for.
     * @param callback to delegate the response to.
     */
    void getMovieReviews(int movieId, @NonNull Callback<MovieReviewsDTO> callback);
}