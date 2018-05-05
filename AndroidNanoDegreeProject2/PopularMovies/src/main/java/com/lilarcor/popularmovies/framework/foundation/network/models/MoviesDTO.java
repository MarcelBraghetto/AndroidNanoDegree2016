package com.lilarcor.popularmovies.framework.foundation.network.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.lilarcor.popularmovies.framework.movies.models.Movie;

/**
 * This is a simple data transformation object class that represents
 * the API response from the Movies DB API. It is inflated by GSON
 * during the parsing of the response.
 */
public class MoviesDTO {
    @SuppressWarnings("unused")
    @SerializedName("page")
    private int mResultPage;

    @SuppressWarnings("unused")
    @SerializedName("results")
    private Movie[] mMovies;

    @Nullable
    public Movie[] getMovies() {
        return mMovies;
    }

    public int getResultPage() {
        return mResultPage;
    }
}