package com.lilarcor.popularmovies.framework.movies.data.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.*;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.CONTENT_AUTHORITY;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.CONTENT_PATH_ALL_FAVOURITE_MOVIES;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.CONTENT_PATH_MOVIES;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.CONTENT_PATH_MOVIE_REVIEWS;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.CONTENT_PATH_MOVIE_VIDEOS;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.CONTENT_PATH_POPULAR_MOVIES;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.CONTENT_PATH_TOP_RATED_MOVIES;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.MovieVideos;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.Movies;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.PopularMovies;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.TopRatedMovies;

/**
 * Created by Marcel Braghetto on 14/07/15.
 *
 * Movies content provider user for all data access to
 * manage movie related persistent data.
 *
 * This provider is backed by an SQLite database.
 */
public class MoviesContentProvider extends ContentProvider {
    private static final int MATCHER_ID_MOVIE = 100;
    private static final int MATCHER_ID_ALL_MOVIES = 101;

    private static final int MATCHER_ID_ALL_FAVOURITE_MOVIES = 201;

    private static final int MATCHER_ID_MOVIE_VIDEO = 250;
    private static final int MATCHER_ID_ALL_MOVIE_VIDEOS = 251;

    private static final int MATCHER_ID_MOVIE_REVIEW = 260;
    private static final int MATCHER_ID_ALL_MOVIE_REVIEWS = 261;

    private static final int MATCHER_ID_POPULAR_MOVIE = 300;
    private static final int MATCHER_ID_ALL_POPULAR_MOVIES = 301;

    private static final int MATCHER_ID_TOP_RATED_MOVIE = 400;
    private static final int MATCHER_ID_ALL_TOP_RATED_MOVIES = 401;

    private final UriMatcher mUriMatcher;

    private SQLiteDatabase mMoviesDatabase;

    public MoviesContentProvider() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_MOVIES + "/#", MATCHER_ID_MOVIE);
        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_MOVIES, MATCHER_ID_ALL_MOVIES);

        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_ALL_FAVOURITE_MOVIES, MATCHER_ID_ALL_FAVOURITE_MOVIES);

        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_MOVIE_VIDEOS + "/#", MATCHER_ID_MOVIE_VIDEO);
        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_MOVIE_VIDEOS, MATCHER_ID_ALL_MOVIE_VIDEOS);

        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_MOVIE_REVIEWS + "/#", MATCHER_ID_MOVIE_REVIEW);
        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_MOVIE_REVIEWS, MATCHER_ID_ALL_MOVIE_REVIEWS);

        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_POPULAR_MOVIES + "/#", MATCHER_ID_POPULAR_MOVIE);
        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_POPULAR_MOVIES, MATCHER_ID_ALL_POPULAR_MOVIES);

        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_TOP_RATED_MOVIES + "/#", MATCHER_ID_TOP_RATED_MOVIE);
        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_TOP_RATED_MOVIES, MATCHER_ID_ALL_TOP_RATED_MOVIES);
    }

    @Override
    public boolean onCreate() {
        mMoviesDatabase = new MoviesDatabaseHelper(getContext()).getWritableDatabase();
        return true;
    }

    @Override
    public synchronized Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch(mUriMatcher.match(uri)) {
            case MATCHER_ID_MOVIE: {
                String id = uri.getLastPathSegment();

                String query = "SELECT * FROM " + Movies.TABLE_NAME + " WHERE " + Movies._ID + " = ?";

                cursor = mMoviesDatabase.rawQuery(query, new String[]{id});
            }
            break;

            case MATCHER_ID_ALL_MOVIES: {
                String query = "SELECT * FROM " + Movies.TABLE_NAME;
                cursor = mMoviesDatabase.rawQuery(query, null);
            }
            break;

            case MATCHER_ID_ALL_FAVOURITE_MOVIES: {
                String query = "SELECT * FROM " + Movies.TABLE_NAME + " WHERE " + Movies.COLUMN_MOVIE_IS_FAVOURITE + " > 0";
                cursor = mMoviesDatabase.rawQuery(query, null);
            }
            break;

            case MATCHER_ID_MOVIE_VIDEO: {
                String id = uri.getLastPathSegment();

                String query = "SELECT * FROM " + MovieVideos.TABLE_NAME + " WHERE " + MovieVideos.COLUMN_MOVIE_ID + " = ?";

                cursor = mMoviesDatabase.rawQuery(query, new String[]{id});
            }
            break;

            case MATCHER_ID_ALL_MOVIE_VIDEOS: {
                String query = "SELECT * FROM " + MovieVideos.TABLE_NAME;
                cursor = mMoviesDatabase.rawQuery(query, null);
            }
            break;

            case MATCHER_ID_MOVIE_REVIEW: {
                String id = uri.getLastPathSegment();

                String query = "SELECT * FROM " + MovieReviews.TABLE_NAME + " WHERE " + MovieReviews.COLUMN_MOVIE_ID + " = ?";

                cursor = mMoviesDatabase.rawQuery(query, new String[]{id});
            }
            break;

            case MATCHER_ID_ALL_MOVIE_REVIEWS: {
                String query = "SELECT * FROM " + MovieReviews.TABLE_NAME;
                cursor = mMoviesDatabase.rawQuery(query, null);
            }
            break;

            case MATCHER_ID_ALL_POPULAR_MOVIES: {
                String query =
                        "SELECT * FROM " + PopularMovies.TABLE_NAME + " a " +
                        "INNER JOIN " + Movies.TABLE_NAME + " b " +
                        "ON a." + PopularMovies.COLUMN_MOVIE_ID + " = " + "b." + Movies._ID;

                cursor = mMoviesDatabase.rawQuery(query, null);
            }
            break;

            case MATCHER_ID_ALL_TOP_RATED_MOVIES: {
                String query =
                        "SELECT * FROM " + TopRatedMovies.TABLE_NAME + " a " +
                                "INNER JOIN " + Movies.TABLE_NAME + " b " +
                                "ON a." + TopRatedMovies.COLUMN_MOVIE_ID + " = " + "b." + Movies._ID;

                cursor = mMoviesDatabase.rawQuery(query, null);
            }
            break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = mUriMatcher.match(uri);

        String type;

        switch(match) {
            case MATCHER_ID_MOVIE: {
                type = Movies.getContentItemType();
            }
            break;

            case MATCHER_ID_ALL_MOVIES: {
                type = Movies.getContentType();
            }
            break;

            case MATCHER_ID_ALL_FAVOURITE_MOVIES: {
                type = Movies.getContentType();
            }
            break;

            case MATCHER_ID_MOVIE_VIDEO: {
                type = MovieVideos.getContentItemType();
            }
            break;

            case MATCHER_ID_ALL_MOVIE_VIDEOS: {
                type = MovieVideos.getContentType();
            }
            break;

            case MATCHER_ID_MOVIE_REVIEW: {
                type = MovieReviews.getContentItemType();
            }
            break;

            case MATCHER_ID_ALL_MOVIE_REVIEWS: {
                type = MovieReviews.getContentType();
            }
            break;

            case MATCHER_ID_ALL_POPULAR_MOVIES: {
                type = PopularMovies.getContentType();
            }
            break;

            case MATCHER_ID_ALL_TOP_RATED_MOVIES: {
                type = TopRatedMovies.getContentType();
            }
            break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return type;
    }

    @Override
    public synchronized Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException("Individual inserts are not supported in this content provider. Use bulk insert instead.");

        /*
        final int match = mUriMatcher.match(uri);

        switch(match) {
            case MATCHER_ID_ALL_POPULAR_MOVIES:
                mMoviesDatabase.insertWithOnConflict(PopularMovies.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                break;

            case MATCHER_ID_ALL_TOP_RATED_MOVIES:
                mMoviesDatabase.insertWithOnConflict(TopRatedMovies.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null, false);

        return uri;
        */
    }

    @Override
    public synchronized int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;

        switch(mUriMatcher.match(uri)) {
            case MATCHER_ID_MOVIE: {
                String id = uri.getLastPathSegment();
                rowsDeleted = mMoviesDatabase.delete(Movies.TABLE_NAME, Movies._ID + " = ?", new String[]{id});
            }
            break;

            case MATCHER_ID_ALL_MOVIES: {
                rowsDeleted = mMoviesDatabase.delete(Movies.TABLE_NAME, selection, selectionArgs);
            }
            break;

            case MATCHER_ID_ALL_POPULAR_MOVIES: {
                rowsDeleted = mMoviesDatabase.delete(PopularMovies.TABLE_NAME, selection, selectionArgs);
            }
            break;

            case MATCHER_ID_MOVIE_VIDEO: {
                String id = uri.getLastPathSegment();
                rowsDeleted = mMoviesDatabase.delete(MovieVideos.TABLE_NAME, MovieVideos.COLUMN_MOVIE_ID + " = ?", new String[]{id});
            }
            break;

            case MATCHER_ID_ALL_MOVIE_VIDEOS: {
                rowsDeleted = mMoviesDatabase.delete(MovieVideos.TABLE_NAME, selection, selectionArgs);
            }
            break;

            case MATCHER_ID_MOVIE_REVIEW: {
                String id = uri.getLastPathSegment();
                rowsDeleted = mMoviesDatabase.delete(MovieReviews.TABLE_NAME, MovieReviews.COLUMN_MOVIE_ID + " = ?", new String[]{id});
            }
            break;

            case MATCHER_ID_ALL_MOVIE_REVIEWS: {
                rowsDeleted = mMoviesDatabase.delete(MovieReviews.TABLE_NAME, selection, selectionArgs);
            }
            break;

            case MATCHER_ID_ALL_TOP_RATED_MOVIES: {
                rowsDeleted = mMoviesDatabase.delete(TopRatedMovies.TABLE_NAME, selection, selectionArgs);
            }
            break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public synchronized int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated;

        switch(mUriMatcher.match(uri)) {
            case MATCHER_ID_MOVIE: {
                String id = uri.getLastPathSegment();
                rowsUpdated = mMoviesDatabase.update(Movies.TABLE_NAME, values, Movies._ID + " = ?", new String[]{id});
            }
            break;

            case MATCHER_ID_ALL_MOVIES: {
                rowsUpdated = mMoviesDatabase.update(Movies.TABLE_NAME, values, selection, selectionArgs);
            }
            break;

            case MATCHER_ID_POPULAR_MOVIE: {
                String id = uri.getLastPathSegment();
                rowsUpdated = mMoviesDatabase.update(PopularMovies.TABLE_NAME, values, PopularMovies._ID + " = ?", new String[]{id});
            }
            break;

            case MATCHER_ID_ALL_POPULAR_MOVIES: {
                rowsUpdated = mMoviesDatabase.update(PopularMovies.TABLE_NAME, values, selection, selectionArgs);
            }
            break;

            case MATCHER_ID_TOP_RATED_MOVIE: {
                String id = uri.getLastPathSegment();
                rowsUpdated = mMoviesDatabase.update(TopRatedMovies.TABLE_NAME, values, TopRatedMovies._ID + " = ?", new String[]{id});
            }
            break;

            case MATCHER_ID_ALL_TOP_RATED_MOVIES: {
                rowsUpdated = mMoviesDatabase.update(TopRatedMovies.TABLE_NAME, values, selection, selectionArgs);
            }
            break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    @Override
    public synchronized int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch(mUriMatcher.match(uri)) {
            case MATCHER_ID_ALL_MOVIES: {
                SQLiteStatement movieExistsStatement = mMoviesDatabase.compileStatement(Movies.getMovieExistsSql());
                SQLiteStatement movieInsertStatement = mMoviesDatabase.compileStatement(Movies.getInsertSql());
                SQLiteStatement movieUpdatedStatement = mMoviesDatabase.compileStatement(Movies.getUpdateSql());

                mMoviesDatabase.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        movieExistsStatement.clearBindings();
                        movieExistsStatement.bindString(1, value.getAsString(Movies._ID));

                        if (movieExistsStatement.simpleQueryForLong() == 0L) {
                            // Do insert
                            movieInsertStatement.clearBindings();
                            movieInsertStatement.bindLong(1, value.getAsLong(Movies._ID));
                            movieInsertStatement.bindString(2, value.getAsString(Movies.COLUMN_MOVIE_TITLE));
                            movieInsertStatement.bindString(3, value.getAsString(Movies.COLUMN_MOVIE_OVERVIEW));
                            movieInsertStatement.bindString(4, value.getAsString(Movies.COLUMN_MOVIE_RELEASE_DATE));
                            movieInsertStatement.bindString(5, value.getAsString(Movies.COLUMN_MOVIE_POSTER_PATH));
                            movieInsertStatement.bindString(6, value.getAsString(Movies.COLUMN_MOVIE_BACKDROP_PATH));
                            movieInsertStatement.bindDouble(7, value.getAsDouble(Movies.COLUMN_MOVIE_VOTE_AVERAGE));
                            movieInsertStatement.bindLong(8, value.getAsLong(Movies.COLUMN_MOVIE_VOTE_COUNT));
                            movieInsertStatement.bindLong(9, value.getAsLong(Movies.COLUMN_MOVIE_IS_FAVOURITE));

                            movieInsertStatement.execute();
                        } else {
                            // update
                            movieUpdatedStatement.clearBindings();
                            movieUpdatedStatement.bindString(1, value.getAsString(Movies.COLUMN_MOVIE_TITLE));
                            movieUpdatedStatement.bindString(2, value.getAsString(Movies.COLUMN_MOVIE_OVERVIEW));
                            movieUpdatedStatement.bindString(3, value.getAsString(Movies.COLUMN_MOVIE_RELEASE_DATE));
                            movieUpdatedStatement.bindString(4, value.getAsString(Movies.COLUMN_MOVIE_POSTER_PATH));
                            movieUpdatedStatement.bindString(5, value.getAsString(Movies.COLUMN_MOVIE_BACKDROP_PATH));
                            movieUpdatedStatement.bindDouble(6, value.getAsDouble(Movies.COLUMN_MOVIE_VOTE_AVERAGE));
                            movieUpdatedStatement.bindLong(7, value.getAsLong(Movies.COLUMN_MOVIE_VOTE_COUNT));
                            movieUpdatedStatement.bindLong(8, value.getAsLong(Movies._ID));

                            movieUpdatedStatement.execute();
                        }
                    }

                    mMoviesDatabase.setTransactionSuccessful();
                } finally {
                    mMoviesDatabase.endTransaction();
                }
            }
            break;

            case MATCHER_ID_ALL_MOVIE_VIDEOS: {
                SQLiteStatement insertStatement = mMoviesDatabase.compileStatement(MovieVideos.getInsertSql());

                mMoviesDatabase.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        insertStatement.clearBindings();
                        insertStatement.bindLong(1, value.getAsLong(MovieVideos.COLUMN_MOVIE_ID));
                        insertStatement.bindString(2, value.getAsString(MovieVideos.COLUMN_VIDEO_ID));
                        insertStatement.bindString(3, value.getAsString(MovieVideos.COLUMN_VIDEO_KEY));
                        insertStatement.bindString(4, value.getAsString(MovieVideos.COLUMN_VIDEO_TITLE));
                        insertStatement.bindString(5, value.getAsString(MovieVideos.COLUMN_VIDEO_SITE));
                        insertStatement.execute();
                    }

                    mMoviesDatabase.setTransactionSuccessful();
                } finally {
                    mMoviesDatabase.endTransaction();
                }
            }
            break;

            case MATCHER_ID_ALL_MOVIE_REVIEWS: {
                SQLiteStatement insertStatement = mMoviesDatabase.compileStatement(MovieReviews.getInsertSql());

                mMoviesDatabase.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        insertStatement.clearBindings();
                        insertStatement.bindLong(1, value.getAsLong(MovieReviews.COLUMN_MOVIE_ID));
                        insertStatement.bindString(2, value.getAsString(MovieReviews.COLUMN_REVIEW_ID));
                        insertStatement.bindString(3, value.getAsString(MovieReviews.COLUMN_REVIEW_AUTHOR));
                        insertStatement.bindString(4, value.getAsString(MovieReviews.COLUMN_REVIEW_CONTENT));
                        insertStatement.execute();
                    }

                    mMoviesDatabase.setTransactionSuccessful();
                } finally {
                    mMoviesDatabase.endTransaction();
                }
            }
            break;

            case MATCHER_ID_ALL_POPULAR_MOVIES: {
                SQLiteStatement insertStatement = mMoviesDatabase.compileStatement(PopularMovies.getInsertSql());

                mMoviesDatabase.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        insertStatement.clearBindings();
                        insertStatement.bindLong(1, value.getAsLong(PopularMovies.COLUMN_MOVIE_ID));
                        insertStatement.bindLong(2, value.getAsLong(PopularMovies.COLUMN_RESULT_PAGE));
                        insertStatement.execute();
                    }

                    mMoviesDatabase.setTransactionSuccessful();
                } finally {
                    mMoviesDatabase.endTransaction();
                }
            }
            break;

            case MATCHER_ID_ALL_TOP_RATED_MOVIES: {
                SQLiteStatement insertStatement = mMoviesDatabase.compileStatement(TopRatedMovies.getInsertSql());

                mMoviesDatabase.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        insertStatement.clearBindings();
                        insertStatement.bindLong(1, value.getAsLong(PopularMovies.COLUMN_MOVIE_ID));
                        insertStatement.bindLong(2, value.getAsLong(PopularMovies.COLUMN_RESULT_PAGE));
                        insertStatement.execute();
                    }

                    mMoviesDatabase.setTransactionSuccessful();
                } finally {
                    mMoviesDatabase.endTransaction();
                }
            }
            break;

            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }


        getContext().getContentResolver().notifyChange(uri, null, false);

        return values.length;
    }

    /**
     * This is the implementation of the backing SQLite database
     * for storing and manipulating movie content.
     *
     * This is specified as a private inner class to the provider
     * as we don't want it to be publicly available for any
     * operations outside the API made available by the host
     * provider implementation.
     */
    private static class MoviesDatabaseHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "movies.db";

        public MoviesDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(Movies.getCreateTableSql());
            database.execSQL(MovieVideos.getCreateTableSql());
            database.execSQL(MovieReviews.getCreateTableSql());
            database.execSQL(PopularMovies.getCreateTableSql());
            database.execSQL(TopRatedMovies.getCreateTableSql());
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) { }
    }
}
