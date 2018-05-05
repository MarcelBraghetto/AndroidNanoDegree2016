package com.lilarcor.popularmovies.framework.foundation.network.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.lilarcor.popularmovies.framework.movies.models.MovieReview;

/**
 * Created by Marcel Braghetto on 8/08/15.
 *
 * DTO representing the data response for a given
 * movie when requesting the reviews for that movie.
 */
public class MovieReviewsDTO {
    @SerializedName("results")
    @SuppressWarnings("unused")
    private MovieReview[] mReviews;

    @Nullable
    public MovieReview[] getReviews() {
        return mReviews;
    }
}