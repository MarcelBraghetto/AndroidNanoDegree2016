package com.lilarcor.popularmovies.testhelpers.framework.foundation.network;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.lilarcor.popularmovies.framework.foundation.network.contracts.NetworkRequestProvider;
import com.lilarcor.popularmovies.framework.foundation.network.models.MovieReviewsDTO;
import com.lilarcor.popularmovies.framework.foundation.network.models.MovieVideosDTO;
import com.lilarcor.popularmovies.framework.foundation.network.models.MoviesDTO;

import javax.inject.Singleton;

import retrofit.Callback;

/**
 * Created by Marcel Braghetto on 30/07/15.
 *
 * Custom network request provider implementation that
 * extends the normal implementation and alters its behaviour.
 *
 * The idea is to stub out and mock any network requests
 * that occur during the Espresso tests with pre determined
 * responses based on what url etc is requested.
 */
@Singleton
public class EspressoNetworkRequestProvider implements NetworkRequestProvider {
    @Override
    public boolean clearResponseCache() {
        return true;
    }

    @Override
    public void getPopularMovies(int requestPage, @NonNull CacheRequestPolicy cacheRequestPolicy, @NonNull final Callback<MoviesDTO> callback) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MoviesDTO dto = new Gson().fromJson(FakeResponsePopularMovies.CONTENT, MoviesDTO.class);
                callback.success(dto, null);
            }
        }, 1000);
    }

    @Override
    public void getTopRatedMovies(int requestPage, @NonNull CacheRequestPolicy cacheRequestPolicy, @NonNull final Callback<MoviesDTO> callback) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MoviesDTO dto = new Gson().fromJson(FakeResponseTopRatedMovies.CONTENT, MoviesDTO.class);
                callback.success(dto, null);
            }
        }, 1000);
    }

    @Override
    public void getMovieVideos(int movieId, @NonNull final Callback<MovieVideosDTO> callback) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MovieVideosDTO dto = new Gson().fromJson(FakeResponseMovieVideos.CONTENT, MovieVideosDTO.class);
                callback.success(dto, null);
            }
        }, 1000);
    }

    @Override
    public void getMovieReviews(int movieId, @NonNull final Callback<MovieReviewsDTO> callback) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MovieReviewsDTO dto = new Gson().fromJson(FakeResponseMovieReviews.CONTENT, MovieReviewsDTO.class);
                callback.success(dto, null);
            }
        }, 1000);
    }
}