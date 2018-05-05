package com.lilarcor.popularmovies.framework.movies.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.MovieVideos;

/**
 * Created by Marcel Braghetto on 8/08/15.
 *
 * Data class representing a movie video.
 */
public class MovieVideo {
    private int mMovieId;

    @SuppressWarnings("unused")
    @SerializedName("id")
    private String mVideoId;

    @SuppressWarnings("unused")
    @SerializedName("key")
    private String mVideoKey;

    @SuppressWarnings("unused")
    @SerializedName("name")
    private String mVideoTitle;

    @SuppressWarnings("unused")
    @SerializedName("site")
    private String mVideoSite;

    @SuppressWarnings("unused")
    @SerializedName("type")
    private String mVideoType;

    public int getMovieId() {
        return mMovieId;
    }

    public void setMovieId(int movieId) {
        mMovieId = movieId;
    }

    @NonNull
    public String getVideoId() {
        return TextUtils.isEmpty(mVideoId) ? "" : mVideoId;
    }

    @NonNull
    public String getVideoKey() {
        return TextUtils.isEmpty(mVideoKey) ? "" : mVideoKey;
    }

    @NonNull
    public String getVideoTitle() {
        return TextUtils.isEmpty(mVideoTitle) ? "" : mVideoTitle;
    }

    @NonNull
    public String getVideoSite() {
        return TextUtils.isEmpty(mVideoSite) ? "" : mVideoSite;
    }

    @NonNull
    public String getVideoType() {
        return TextUtils.isEmpty(mVideoType) ? "" : mVideoType;
    }

    @NonNull
    public String getYouTubeThumbnailUrl() {
        // http://img.youtube.com/vi/lP-sUUUfamw/mqdefault.jpg
        return new Uri.Builder()
                .scheme("http")
                .authority("img.youtube.com")
                .appendPath("vi")
                .appendPath(getVideoKey())
                .appendPath("mqdefault.jpg")
                .build()
                .toString();
    }

    @NonNull
    public String getYouTubeVideoUrl() {
        return new Uri.Builder()
                .scheme("http")
                .authority("www.youtube.com")
                .appendPath("watch")
                .appendQueryParameter("v", getVideoKey())
                .build()
                .toString();
    }

    /**
     * Generate a set of content values that can be used for content provider
     * operations such as inserting movies into the database.
     *
     * @return collection of content values representing this movie video instance.
     */
    @NonNull
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(MovieVideos.COLUMN_MOVIE_ID, getMovieId());
        values.put(MovieVideos.COLUMN_VIDEO_ID, getVideoId());
        values.put(MovieVideos.COLUMN_VIDEO_KEY, getVideoKey());
        values.put(MovieVideos.COLUMN_VIDEO_TITLE, getVideoTitle());
        values.put(MovieVideos.COLUMN_VIDEO_SITE, getVideoSite());

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

        mMovieId = cursor.getInt(columnIndexMap.get(MovieVideos.COLUMN_MOVIE_ID));
        mVideoId = cursor.getString(columnIndexMap.get(MovieVideos.COLUMN_VIDEO_ID));
        mVideoKey = cursor.getString(columnIndexMap.get(MovieVideos.COLUMN_VIDEO_KEY));
        mVideoTitle = cursor.getString(columnIndexMap.get(MovieVideos.COLUMN_VIDEO_TITLE));
        mVideoSite = cursor.getString(columnIndexMap.get(MovieVideos.COLUMN_VIDEO_SITE));
    }
}