package com.lilarcor.popularmovies.framework.movies.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.Movies;

/**
 * Created by Marcel Braghetto on 18/07/15.
 *
 * POJO describing a movie and its details.
 */
public class Movie implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String POSTER_IMAGE_URL_PREFIX = "http://image.tmdb.org/t/p/w185";
    private static final String BACKDROP_IMAGE_URL_PREFIX = "http://image.tmdb.org/t/p/w500";

    @SerializedName("id")
    private int mMovieId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("overview")
    private String mOverview;

    @SerializedName("backdrop_path")
    private String mBackdropImageUrl;

    @SerializedName("poster_path")
    private String mPosterImageUrl;

    @SerializedName("release_date")
    private String mReleaseDate;

    @SerializedName("vote_average")
    private float mVoteAverage;

    @SerializedName("vote_count")
    private int mVoteCount;

    private boolean mIsFavourite;

    public int getMovieId() {
        return mMovieId;
    }

    @NonNull
    public String getTitle() {
        return TextUtils.isEmpty(mTitle) ? "" : mTitle;
    }

    @NonNull
    public String getOverview() {
        return TextUtils.isEmpty(mOverview) ? "" : mOverview;
    }

    @NonNull
    public String getBackdropImageUrl() {
        return TextUtils.isEmpty(mBackdropImageUrl) ? "" : BACKDROP_IMAGE_URL_PREFIX + mBackdropImageUrl;
    }

    @NonNull
    public String getPosterImageUrl() {
        return TextUtils.isEmpty(mPosterImageUrl) ? "" : POSTER_IMAGE_URL_PREFIX + mPosterImageUrl;
    }

    @NonNull
    public String getReleaseDate() {
        return TextUtils.isEmpty(mReleaseDate) ? "" : mReleaseDate;
    }

    public float getVoteAverage() {
        return mVoteAverage;
    }

    public int getVoteCount() {
        return mVoteCount;
    }

    /**
     * Generate a set of content values that can be used for content provider
     * operations such as inserting movies into the database.
     *
     * @return collection of content values representing this movie instance.
     */
    @NonNull
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(Movies._ID, getMovieId());
        values.put(Movies.COLUMN_MOVIE_TITLE, getTitle());
        values.put(Movies.COLUMN_MOVIE_OVERVIEW, getOverview());
        values.put(Movies.COLUMN_MOVIE_BACKDROP_PATH, getBackdropImageUrl());
        values.put(Movies.COLUMN_MOVIE_POSTER_PATH, getPosterImageUrl());
        values.put(Movies.COLUMN_MOVIE_RELEASE_DATE, getReleaseDate());
        values.put(Movies.COLUMN_MOVIE_VOTE_AVERAGE, getVoteAverage());
        values.put(Movies.COLUMN_MOVIE_VOTE_COUNT, getVoteCount());
        values.put(Movies.COLUMN_MOVIE_IS_FAVOURITE, isFavourite() ? 1 : 0);

        return values;
    }

    /**
     * Given a cursor and a 'column index map', translate the data within the cursor into
     * the fields of this movie instance.
     *
     * The column index map is a hash map that would typically have come from the content
     * provider contract, which stores the table column names as keys, and the index of
     * where that column data can be found within the given cursor.
     *
     * @param cursor
     * @param columnIndexMap
     */
    public void populateFromCursor(@Nullable Cursor cursor, @NonNull Map<String, Integer> columnIndexMap) {
        if(cursor == null) {
            return;
        }

        mMovieId = cursor.getInt(columnIndexMap.get(Movies._ID));
        mTitle = cursor.getString(columnIndexMap.get(Movies.COLUMN_MOVIE_TITLE));
        mOverview = cursor.getString(columnIndexMap.get(Movies.COLUMN_MOVIE_OVERVIEW));
        mBackdropImageUrl = cursor.getString(columnIndexMap.get(Movies.COLUMN_MOVIE_BACKDROP_PATH));
        mPosterImageUrl = cursor.getString(columnIndexMap.get(Movies.COLUMN_MOVIE_POSTER_PATH));
        mReleaseDate = cursor.getString(columnIndexMap.get(Movies.COLUMN_MOVIE_RELEASE_DATE));
        mVoteAverage = cursor.getFloat(columnIndexMap.get(Movies.COLUMN_MOVIE_VOTE_AVERAGE));
        mVoteCount = cursor.getInt(columnIndexMap.get(Movies.COLUMN_MOVIE_VOTE_COUNT));
        mIsFavourite = cursor.getInt(columnIndexMap.get(Movies.COLUMN_MOVIE_IS_FAVOURITE)) > 0;
    }

    /**
     * Not populated directly from the request data, but set
     * from the data content provider.
     *
     * @return whether this movie is marked as a favourite
     * by the user.
     */
    public boolean isFavourite() {
        return mIsFavourite;
    }

    /**
     * Mark this movie as being a favourite for the user.
     *
     * @param isFavourite whether this movie is a favourite.
     */
    public void setIsFavourite(boolean isFavourite) {
        mIsFavourite = isFavourite;
    }
}