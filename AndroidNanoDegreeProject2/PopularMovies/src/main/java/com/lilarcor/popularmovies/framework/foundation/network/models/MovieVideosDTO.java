package com.lilarcor.popularmovies.framework.foundation.network.models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.lilarcor.popularmovies.framework.movies.models.MovieVideo;

/**
 * Created by Marcel Braghetto on 8/08/15.
 *
 * DTO representing the data response for a given
 * movie when requesting the videos for that movie.
 */
public class MovieVideosDTO {
    @SerializedName("results")
    @SuppressWarnings("unused")
    private MovieVideo[] mVideos;

    @Nullable
    public MovieVideo[] getVideos() {
        return mVideos;
    }
}