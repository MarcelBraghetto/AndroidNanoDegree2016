package com.lilarcor.popularmovies.framework.movies.data.contentprovider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marcel Braghetto on 17/07/15.
 *
 * Data contract defining the movies provider and data tables and columns for
 * use when consuming or accessing the movies content provider.
 */
public final class MoviesContentContract {
    public static final String CONTENT_AUTHORITY = "com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentProvider";

    public static final String CONTENT_PATH_MOVIES = "movies";
    public static final String CONTENT_PATH_ALL_FAVOURITE_MOVIES = CONTENT_PATH_MOVIES + "/all_favourites";

    public static final String CONTENT_PATH_MOVIE_VIDEOS = "movie_videos";
    public static final String CONTENT_PATH_MOVIE_REVIEWS = "movie_reviews";

    public static final String CONTENT_PATH_POPULAR_MOVIES = "popular_movies";
    public static final String CONTENT_PATH_TOP_RATED_MOVIES = "top_rated_movies";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //region Movies
    public static final class Movies implements BaseColumns {
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_OVERVIEW = "movie_overview";
        public static final String COLUMN_MOVIE_BACKDROP_PATH = "movie_backdrop_path";
        public static final String COLUMN_MOVIE_POSTER_PATH = "movie_poster_path";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movie_release_date";
        public static final String COLUMN_MOVIE_VOTE_AVERAGE = "movie_vote_average";
        public static final String COLUMN_MOVIE_VOTE_COUNT = "movie_vote_count";
        public static final String COLUMN_MOVIE_IS_FAVOURITE = "is_favourite";

        private static final Uri ALL_MOVIES_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTENT_PATH_MOVIES).build();
        private static final Uri ALL_FAVOURITE_MOVIES_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTENT_PATH_MOVIES).appendPath("all_favourites").build();

        private static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CONTENT_PATH_MOVIES;
        private static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CONTENT_PATH_MOVIES;

        @NonNull
        public static String getCreateTableSql() {
            return "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                    COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                    COLUMN_MOVIE_BACKDROP_PATH + " TEXT NOT NULL, " +
                    COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                    COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                    COLUMN_MOVIE_VOTE_AVERAGE + " REAL NOT NULL, " +
                    COLUMN_MOVIE_VOTE_COUNT + " INTEGER NOT NULL, " +
                    COLUMN_MOVIE_IS_FAVOURITE + " INTEGER NOT NULL DEFAULT 0);";
        }

        @NonNull
        public static String getMovieExistsSql() {
            return "SELECT COUNT(" + _ID + ") FROM " + TABLE_NAME + " WHERE " + _ID + " = ?";
        }

        @NonNull
        public static String getUpdateSql() {
            return "UPDATE " + TABLE_NAME + " SET " +
                    COLUMN_MOVIE_TITLE + "= ?, " +
                    COLUMN_MOVIE_OVERVIEW + "= ?, " +
                    COLUMN_MOVIE_RELEASE_DATE + "= ?, " +
                    COLUMN_MOVIE_POSTER_PATH + "= ?, " +
                    COLUMN_MOVIE_BACKDROP_PATH + "= ?, " +
                    COLUMN_MOVIE_VOTE_AVERAGE + "= ?, " +
                    COLUMN_MOVIE_VOTE_COUNT + "= ? " +
                    // Note: We don't want to update the favourite flag in a bulk update
                    // so the favourite column is not included.
                    "WHERE " + _ID + " = ?";
        }

        @NonNull
        public static String getInsertSql() {
            return "INSERT INTO " + TABLE_NAME + "(" +
                    _ID + ", " +
                    COLUMN_MOVIE_TITLE + ", " +
                    COLUMN_MOVIE_OVERVIEW + ", " +
                    COLUMN_MOVIE_RELEASE_DATE + ", " +
                    COLUMN_MOVIE_POSTER_PATH + ", " +
                    COLUMN_MOVIE_BACKDROP_PATH + ", " +
                    COLUMN_MOVIE_VOTE_AVERAGE + ", " +
                    COLUMN_MOVIE_VOTE_COUNT + ", " +
                    COLUMN_MOVIE_IS_FAVOURITE + ") " +
                    "VALUES(" +
                    "?, " +     // _ID
                    "?, " +     // COLUMN_MOVIE_TITLE
                    "?, " +     // COLUMN_MOVIE_OVERVIEW
                    "?, " +     // COLUMN_MOVIE_RELEASE_DATE
                    "?, " +     // COLUMN_MOVIE_POSTER_PATH
                    "?, " +     // COLUMN_MOVIE_BACKDROP_PATH
                    "?, " +     // COLUMN_MOVIE_VOTE_AVERAGE
                    "?, " +     // COLUMN_MOVIE_VOTE_COUNT
                    "?)";       // COLUMN_MOVIE_IS_FAVOURITE
        }

        @NonNull
        public static Uri getContentUriAllMovies() {
            return ALL_MOVIES_CONTENT_URI;
        }

        @NonNull
        public static Uri getContentUriAllFavouriteMovies() {
            return ALL_FAVOURITE_MOVIES_CONTENT_URI;
        }

        @NonNull
        public static Uri getContentUriSpecificMovie(int movieId) {
            return Uri.withAppendedPath(ALL_MOVIES_CONTENT_URI, String.valueOf(movieId));
        }

        @NonNull
        public static String getContentItemType() {
            return CONTENT_ITEM_TYPE;
        }

        @NonNull
        public static String getContentType() {
            return CONTENT_TYPE;
        }

        @NonNull
        public static Map<String, Integer> getAllColumnsIndexMap() {
            Map<String, Integer> map = new HashMap<>();

            map.put(_ID, 0);
            map.put(COLUMN_MOVIE_TITLE, 1);
            map.put(COLUMN_MOVIE_OVERVIEW, 2);
            map.put(COLUMN_MOVIE_BACKDROP_PATH, 3);
            map.put(COLUMN_MOVIE_POSTER_PATH, 4);
            map.put(COLUMN_MOVIE_RELEASE_DATE, 5);
            map.put(COLUMN_MOVIE_VOTE_AVERAGE, 6);
            map.put(COLUMN_MOVIE_VOTE_COUNT, 7);
            map.put(COLUMN_MOVIE_IS_FAVOURITE, 8);

            return map;
        }
    }
    //endregion

    //region Movie videos
    public static final class MovieVideos implements BaseColumns {
        public static final String TABLE_NAME = "movie_videos";
        public static final String COLUMN_MOVIE_ID = "video_movie_id";
        public static final String COLUMN_VIDEO_ID = "video_id";
        public static final String COLUMN_VIDEO_KEY = "video_key";
        public static final String COLUMN_VIDEO_TITLE = "video_title";
        public static final String COLUMN_VIDEO_SITE = "video_site";

        private static final Uri MOVIE_VIDEOS_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTENT_PATH_MOVIE_VIDEOS).build();

        private static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CONTENT_PATH_MOVIE_VIDEOS;
        private static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CONTENT_PATH_MOVIE_VIDEOS;

        @NonNull
        public static String getCreateTableSql() {
            return "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                    COLUMN_VIDEO_ID + " TEXT NOT NULL, " +
                    COLUMN_VIDEO_KEY + " TEXT NOT NULL, " +
                    COLUMN_VIDEO_TITLE + " TEXT NOT NULL, " +
                    COLUMN_VIDEO_SITE + " TEXT NOT NULL)";
        }

        @NonNull
        public static String getInsertSql() {
            return "INSERT INTO " + TABLE_NAME + "(" +
                    COLUMN_MOVIE_ID + ", " +
                    COLUMN_VIDEO_ID + ", " +
                    COLUMN_VIDEO_KEY + ", " +
                    COLUMN_VIDEO_TITLE + ", " +
                    COLUMN_VIDEO_SITE + ") " +
                    "VALUES(" +
                    "?, " +     // COLUMN_MOVIE_ID
                    "?, " +     // COLUMN_VIDEO_ID
                    "?, " +     // COLUMN_VIDEO_KEY
                    "?, " +     // COLUMN_VIDEO_TITLE
                    "?)";       // COLUMN_VIDEO_SITE
        }

        @NonNull
        public static Uri getContentUriForMovieId(int movieId) {
            return Uri.withAppendedPath(MOVIE_VIDEOS_CONTENT_URI, String.valueOf(movieId));
        }

        @NonNull
        public static Uri getContentUriAllVideos() {
            return MOVIE_VIDEOS_CONTENT_URI;
        }

        @NonNull
        public static String getContentItemType() {
            return CONTENT_ITEM_TYPE;
        }


        @NonNull
        public static String getContentType() {
            return CONTENT_TYPE;
        }

        @NonNull
        public static Map<String, Integer> getAllColumnsIndexMap() {
            Map<String, Integer> map = new HashMap<>();

            map.put(_ID, 0);
            map.put(COLUMN_MOVIE_ID, 1);
            map.put(COLUMN_VIDEO_ID, 2);
            map.put(COLUMN_VIDEO_KEY, 3);
            map.put(COLUMN_VIDEO_TITLE, 4);
            map.put(COLUMN_VIDEO_SITE, 5);

            return map;
        }
    }
    //endregion

    //region Movie reviews
    public static final class MovieReviews implements BaseColumns {
        public static final String TABLE_NAME = "movie_reviews";
        public static final String COLUMN_MOVIE_ID = "review_movie_id";
        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_REVIEW_AUTHOR = "review_author";
        public static final String COLUMN_REVIEW_CONTENT = "review_content";

        private static final Uri MOVIE_REVIEWS_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTENT_PATH_MOVIE_REVIEWS).build();

        private static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CONTENT_PATH_MOVIE_REVIEWS;
        private static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CONTENT_PATH_MOVIE_REVIEWS;

        @NonNull
        public static String getCreateTableSql() {
            return "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                    COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                    COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL, " +
                    COLUMN_REVIEW_CONTENT + " TEXT NOT NULL)";
        }

        @NonNull
        public static String getInsertSql() {
            return "INSERT INTO " + TABLE_NAME + "(" +
                    COLUMN_MOVIE_ID + ", " +
                    COLUMN_REVIEW_ID + ", " +
                    COLUMN_REVIEW_AUTHOR + ", " +
                    COLUMN_REVIEW_CONTENT + ") " +
                    "VALUES(" +
                    "?, " +     // COLUMN_MOVIE_ID
                    "?, " +     // COLUMN_REVIEW_ID
                    "?, " +     // COLUMN_REVIEW_AUTHOR
                    "?)";       // COLUMN_REVIEW_CONTENT
        }

        @NonNull
        public static Uri getContentUriForMovieId(int movieId) {
            return Uri.withAppendedPath(MOVIE_REVIEWS_CONTENT_URI, String.valueOf(movieId));
        }

        @NonNull
        public static Uri getContentUriAllReviews() {
            return MOVIE_REVIEWS_CONTENT_URI;
        }

        @NonNull
        public static String getContentItemType() {
            return CONTENT_ITEM_TYPE;
        }


        @NonNull
        public static String getContentType() {
            return CONTENT_TYPE;
        }

        @NonNull
        public static Map<String, Integer> getAllColumnsIndexMap() {
            Map<String, Integer> map = new HashMap<>();

            map.put(_ID, 0);
            map.put(COLUMN_MOVIE_ID, 1);
            map.put(COLUMN_REVIEW_ID, 2);
            map.put(COLUMN_REVIEW_AUTHOR, 3);
            map.put(COLUMN_REVIEW_CONTENT, 4);

            return map;
        }
    }
    //endregion

    //region Popular movies
    public static final class PopularMovies implements BaseColumns {
        public static final String TABLE_NAME = "popular_movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_RESULT_PAGE = "result_page";

        private static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTENT_PATH_POPULAR_MOVIES).build();
        private static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CONTENT_PATH_POPULAR_MOVIES;

        @NonNull
        public static String getCreateTableSql() {
            return "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                    COLUMN_RESULT_PAGE + " INTEGER NOT NULL, " +
                    "UNIQUE (" + COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE)";
        }

        @NonNull
        public static String getInsertSql() {
            return "INSERT INTO " + TABLE_NAME + "(" +
                    COLUMN_MOVIE_ID + ", " +
                    COLUMN_RESULT_PAGE + ") " +
                    "VALUES(" +
                    "?, " +     // COLUMN_MOVIE_ID
                    "?)";       // COLUMN_RESULT_PAGE
        }
        
        @NonNull
        public static Uri getContentUriAllPopularMovies() {
            return CONTENT_URI;
        }

        @NonNull
        public static String getContentType() {
            return CONTENT_TYPE;
        }        
        
        @NonNull
        public static Map<String, Integer> getAllColumnsIndexMap() {
            Map<String, Integer> map = new HashMap<>();

            // Popular movie _id is at position 0 but we don't care
            map.put(COLUMN_MOVIE_ID, 1);
            map.put(COLUMN_RESULT_PAGE, 2);

            map.put(Movies._ID, 3);
            map.put(Movies.COLUMN_MOVIE_TITLE, 4);
            map.put(Movies.COLUMN_MOVIE_OVERVIEW, 5);
            map.put(Movies.COLUMN_MOVIE_BACKDROP_PATH, 6);
            map.put(Movies.COLUMN_MOVIE_POSTER_PATH, 7);
            map.put(Movies.COLUMN_MOVIE_RELEASE_DATE, 8);
            map.put(Movies.COLUMN_MOVIE_VOTE_AVERAGE, 9);
            map.put(Movies.COLUMN_MOVIE_VOTE_COUNT, 10);
            map.put(Movies.COLUMN_MOVIE_IS_FAVOURITE, 11);

            return map;
        }
    }
    //endregion

    //region Top rated movies
    public static final class TopRatedMovies implements BaseColumns {
        public static final String TABLE_NAME = "top_rated_movies";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_RESULT_PAGE = "result_page";

        private static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTENT_PATH_TOP_RATED_MOVIES).build();
        private static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CONTENT_PATH_TOP_RATED_MOVIES;

        @NonNull
        public static String getCreateTableSql() {
            return "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                    COLUMN_RESULT_PAGE + " INTEGER NOT NULL, " +
                    "UNIQUE (" + COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE)";
        }

        @NonNull
        public static String getInsertSql() {
            return "INSERT INTO " + TABLE_NAME + "(" +
                    COLUMN_MOVIE_ID + ", " +
                    COLUMN_RESULT_PAGE + ") " +
                    "VALUES(" +
                    "?, " +     // COLUMN_MOVIE_ID
                    "?)";       // COLUMN_RESULT_PAGE
        }

        @NonNull
        public static Uri getContentUriAllTopRatedMovies() {
            return CONTENT_URI;
        }

        @NonNull
        public static String getContentType() {
            return CONTENT_TYPE;
        }

        @NonNull
        public static Map<String, Integer> getAllColumnsIndexMap() {
            Map<String, Integer> map = new HashMap<>();

            // Top rated movie _id is at position 0 but we don't care
            map.put(COLUMN_MOVIE_ID, 1);
            map.put(COLUMN_RESULT_PAGE, 2);

            map.put(Movies._ID, 3);
            map.put(Movies.COLUMN_MOVIE_TITLE, 4);
            map.put(Movies.COLUMN_MOVIE_OVERVIEW, 5);
            map.put(Movies.COLUMN_MOVIE_BACKDROP_PATH, 6);
            map.put(Movies.COLUMN_MOVIE_POSTER_PATH, 7);
            map.put(Movies.COLUMN_MOVIE_RELEASE_DATE, 8);
            map.put(Movies.COLUMN_MOVIE_VOTE_AVERAGE, 9);
            map.put(Movies.COLUMN_MOVIE_VOTE_COUNT, 10);
            map.put(Movies.COLUMN_MOVIE_IS_FAVOURITE, 11);

            return map;
        }
    }
    //endregion
}