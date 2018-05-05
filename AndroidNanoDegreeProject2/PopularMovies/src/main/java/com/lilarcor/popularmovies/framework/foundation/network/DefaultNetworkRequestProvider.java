package com.lilarcor.popularmovies.framework.foundation.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.framework.foundation.appstrings.contracts.AppStringsProvider;
import com.lilarcor.popularmovies.framework.foundation.network.contracts.NetworkRequestProvider;
import com.lilarcor.popularmovies.framework.foundation.network.models.MovieReviewsDTO;
import com.lilarcor.popularmovies.framework.foundation.network.models.MovieVideosDTO;
import com.lilarcor.popularmovies.framework.foundation.network.models.MoviesDTO;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Marcel Braghetto on 12/07/15.
 *
 * Default implementation of the networking provider, all based on the Retrofit library.
 *
 * In particular this implementation uses response caching in an effort to provide
 * pressure relief for both the data connections used in the app, and the battery life
 * by not requiring to initiate network requests for previously cached requests, up
 * to a maximum age specified when starting a new request.
 *
 * This also allows a great deal of the app to function offline for any data that has
 * previously been retrieved.
 *
 */
@Singleton
public class DefaultNetworkRequestProvider implements NetworkRequestProvider {
    private static final String BASE_URL = "https://api.themoviedb.org/";

    private static final String RESPONSE_CACHE_DIRECTORY = "okhttp_response_cache";
    private static final long RESPONSE_CACHE_SIZE_MEGABYTES = 5L * 1024L * 1024L; // 5 megabytes
    private static final long REQUEST_TIMEOUT_SECONDS = 10L;

    private RestApi mRestApi;
    private final OkHttpClient mOkHttpClient;
    private final String mMoviesDbKey;

    private final String mHeaderCacheControlInstant;
    private final String mHeaderCacheControlNormal;

    public DefaultNetworkRequestProvider(
            @NonNull Context applicationContext,
            @NonNull AppStringsProvider appStringsProvider) {

        mMoviesDbKey = appStringsProvider.getString(R.string.movies_db_api_key);

        // Cache header value for an 'instant' cache control - 0 length
        mHeaderCacheControlInstant = new CacheControl.Builder()
                .maxAge(0, TimeUnit.SECONDS)
                .maxStale(0, TimeUnit.SECONDS)
                .build()
                .toString();

        // Cache header value for 'normal' which allows responses to skip going
        // to the server for up to 12 hours and 1 week when network not available.
        mHeaderCacheControlNormal = new CacheControl.Builder()
                .maxAge(12, TimeUnit.HOURS)
                .maxStale(7, TimeUnit.DAYS)
                .build()
                .toString();

        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        mOkHttpClient.setWriteTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        // Configure a response cache for OkHttp so we get some free bandwidth / battery preservation.
        File responseCacheDirectory = new File(applicationContext.getCacheDir(), RESPONSE_CACHE_DIRECTORY);
        mOkHttpClient.setCache(new Cache(responseCacheDirectory, RESPONSE_CACHE_SIZE_MEGABYTES));

        // Need a custom executor for Retrofit to run async.
        Executor executor = Executors.newCachedThreadPool();

        // Every request will require us to send the Movies DB API key with it.
        RequestInterceptor mRequestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addQueryParam("api_key", mMoviesDbKey);
            }
        };

        RestAdapter mRestAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setClient(new OkClient(mOkHttpClient))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(mRequestInterceptor)
                .setExecutors(executor, executor)
                .build();

        mRestApi = mRestAdapter.create(RestApi.class);
    }

    @Override
    public void getPopularMovies(int requestPage, @NonNull CacheRequestPolicy cacheRequestPolicy, @NonNull Callback<MoviesDTO> callback) {
        mRestApi.getPopularMovies(getCacheControlHeaderForPolicy(cacheRequestPolicy), requestPage, "popularity.desc", callback);
    }

    @Override
    public void getTopRatedMovies(int requestPage, @NonNull CacheRequestPolicy cacheRequestPolicy, @NonNull Callback<MoviesDTO> callback) {
        mRestApi.getTopRatedMovies(getCacheControlHeaderForPolicy(cacheRequestPolicy), requestPage, "vote_average.desc", "1000", callback);
    }

    @Override
    public void getMovieVideos(int movieId, @NonNull Callback<MovieVideosDTO> callback) {
        mRestApi.getMovieVideos(movieId, mHeaderCacheControlNormal, callback);
    }

    @Override
    public void getMovieReviews(int movieId, @NonNull Callback<MovieReviewsDTO> callback) {
        mRestApi.getMovieReviews(movieId, mHeaderCacheControlNormal, callback);
    }

    @Override
    public synchronized boolean clearResponseCache() {
        try {
            mOkHttpClient.getCache().evictAll();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * REST API definition for all our networking requirements.
     */
    private interface RestApi {
        /**
         * Retrieve the list of 'popular movies' from the
         * movies server.
         *
         * @param cacheControl how to treat the caching for this.
         * @param requestPage which 'request page' to ask the server for.
         * @param sortBy popular movies are sorted by their popularity.
         * @param callback delegate to deliver the results to.
         */
        @GET("/3/discover/movie")
        void getPopularMovies(
                @NonNull @Header("Cache-Control") String cacheControl,
                @NonNull @Query("page") Integer requestPage,
                @NonNull @Query("sort_by") String sortBy,
                @NonNull Callback<MoviesDTO> callback);

        /**
         * Retrieve the list of 'top rated movies' from the
         * movies server.
         *
         * @param cacheControl how to treat the caching for this request.
         * @param requestPage which 'request page' to ask the server for.
         * @param sortBy top movies are sorted by their vote average.
         * @param voteCountMinimum for top rated movies, we only want a list
         *                         where the number of votes for a given
         *                         movie is high enough to be a reliable
         *                         indication that enough people have rated it.
         * @param callback delegate to deliver the results to.
         */
        @GET("/3/discover/movie")
        void getTopRatedMovies(
                @NonNull @Header("Cache-Control") String cacheControl,
                @NonNull @Query("page") Integer requestPage,
                @NonNull @Query("sort_by") String sortBy,
                @NonNull @Query("vote_count.gte") String voteCountMinimum,
                @NonNull Callback<MoviesDTO> callback);

        /**
         * Retrieve the list of movie video clips for a given movie
         * from the server.
         *
         * @param movieId to retrieve videos for.
         * @param cacheControl how to treat the caching for this request.
         * @param callback delegate to deliver the results to.
         */
        @GET("/3/movie/{movieId}/videos")
        void getMovieVideos(
                @Path("movieId") int movieId,
                @NonNull @Header("Cache-Control") String cacheControl,
                @NonNull Callback<MovieVideosDTO> callback);

        /**
         * Retrieve the list of movie reviews for a given movie
         * from the server.
         *
         * @param movieId to retrieve reviews for.
         * @param cacheControl how to treat the caching for this request.
         * @param callback delegate to deliver the results to.
         */
        @GET("/3/movie/{movieId}/reviews")
        void getMovieReviews(
                @Path("movieId") int movieId,
                @NonNull @Header("Cache-Control") String cacheControl,
                @NonNull Callback<MovieReviewsDTO> callback);
    }

    /**
     * Given a cache request policy type, return the header string
     * that can be used in the request header to control how a
     * request is cached.
     *
     * @param cacheRequestPolicy that describes which caching policy to
     *                           determine.
     *
     * @return request header value that can be used with the 'Cache-Control' header.
     */
    private String getCacheControlHeaderForPolicy(CacheRequestPolicy cacheRequestPolicy) {
        switch (cacheRequestPolicy) {
            case Instant:
                return mHeaderCacheControlInstant;
            case Normal:
                return mHeaderCacheControlNormal;
        }

        return mHeaderCacheControlNormal;
    }
}