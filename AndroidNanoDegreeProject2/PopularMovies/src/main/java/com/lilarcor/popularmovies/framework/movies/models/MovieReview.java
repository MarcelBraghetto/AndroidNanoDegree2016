package com.lilarcor.popularmovies.framework.movies.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.MovieReviews;

/**
 * Created by Marcel Braghetto on 8/08/15.
 *
 * Data class to represent a movie review item.
 */
public class MovieReview {
    private int mMovieId;

    @SerializedName("id")
    @SuppressWarnings("unused")
    private String mReviewId;

    @SerializedName("author")
    @SuppressWarnings("unused")
    private String mAuthorName;

    @SerializedName("content")
    @SuppressWarnings("unused")
    private String mContent;

    public int getMovieId() {
        return mMovieId;
    }

    public void setMovieId(int movieId) {
        mMovieId = movieId;
    }

    @NonNull
    public String getReviewId() {
        return TextUtils.isEmpty(mReviewId) ? "" : mReviewId;
    }

    @NonNull
    public String getAuthorName() {
        return TextUtils.isEmpty(mAuthorName) ? "" : mAuthorName;
    }

    @NonNull
    public String getContent() {
        return TextUtils.isEmpty(mContent) ? "" : mContent;
    }

    /**
     * Generate a set of content values that can be used for content provider
     * operations such as inserting movies into the database.
     *
     * @return collection of content values representing this movie review instance.
     */
    @NonNull
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(MovieReviews.COLUMN_MOVIE_ID, getMovieId());
        values.put(MovieReviews.COLUMN_REVIEW_ID, getReviewId());
        values.put(MovieReviews.COLUMN_REVIEW_AUTHOR, getAuthorName());
        values.put(MovieReviews.COLUMN_REVIEW_CONTENT, getContent());

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

        mMovieId = cursor.getInt(columnIndexMap.get(MovieReviews.COLUMN_MOVIE_ID));
        mReviewId = cursor.getString(columnIndexMap.get(MovieReviews.COLUMN_REVIEW_ID));
        mAuthorName = cursor.getString(columnIndexMap.get(MovieReviews.COLUMN_REVIEW_AUTHOR));
        mContent = cursor.getString(columnIndexMap.get(MovieReviews.COLUMN_REVIEW_CONTENT));
    }
}