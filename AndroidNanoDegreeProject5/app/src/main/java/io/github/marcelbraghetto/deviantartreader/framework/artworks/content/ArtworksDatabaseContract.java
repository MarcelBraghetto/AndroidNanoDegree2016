package io.github.marcelbraghetto.deviantartreader.framework.artworks.content;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marcel Braghetto on 24/02/16.
 *
 * Database model contract for storing artwork objects.
 */
public final class ArtworksDatabaseContract {
    public static final String CONTENT_AUTHORITY = "io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksContentProvider";
    public static final String CONTENT_PATH_ARTWORKS = "artworks";
    public static final String CONTENT_PATH_ARTWORKS_FAVOURITES = "artworks_favourites";
    public static final String CONTENT_PATH_ARTWORKS_SAVE_FAVOURITE = "artworks_save_favourite";
    public static final String CONTENT_PATH_ARTWORKS_COUNT = "artworks_count";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class Artworks implements BaseColumns {
        public static final String TABLE_NAME = "artworks";
        public static final String COLUMN_GUID = "artwork_guid";
        public static final String COLUMN_TITLE = "artwork_title";
        public static final String COLUMN_AUTHOR = "artwork_author";
        public static final String COLUMN_AUTHOR_IMAGE_URL = "artwork_author_url";
        public static final String COLUMN_DESCRIPTION = "artwork_description";
        public static final String COLUMN_IMAGE_URL = "artwork_image_url";
        public static final String COLUMN_IMAGE_WIDTH = "artwork_image_width";
        public static final String COLUMN_IMAGE_HEIGHT = "artwork_image_height";
        public static final String COLUMN_PUBLISH_DATE = "artwork_publish_date";
        public static final String COLUMN_COPYRIGHT = "artwork_copyright";
        public static final String COLUMN_WEB_URL = "artwork_web_url";
        public static final String COLUMN_TIMESTAMP = "artwork_timestamp";
        public static final String COLUMN_FAVOURITE = "artwork_favourite";

        public static final String SAVE_FAVOURITE_PARAM_GUID = "guid";
        public static final String SAVE_FAVOURITE_PARAM_IS_FAVOURITE = "is_favourite";

        private static final Uri ALL_ARTWORKS_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTENT_PATH_ARTWORKS).build();
        private static final Uri FAVOURITE_ARTWORKS_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTENT_PATH_ARTWORKS_FAVOURITES).build();
        private static final Uri ALL_ARTWORKS_COUNT_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTENT_PATH_ARTWORKS_COUNT).build();
        private static final Uri SAVE_FAVOURITE_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTENT_PATH_ARTWORKS_SAVE_FAVOURITE).build();

        private static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CONTENT_PATH_ARTWORKS;
        private static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CONTENT_PATH_ARTWORKS;

        @NonNull
        public static String getCreateTableSql() {
            return "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_GUID + " TEXT NOT NULL, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_AUTHOR + " TEXT NOT NULL, " +
                    COLUMN_AUTHOR_IMAGE_URL + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                    COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                    COLUMN_IMAGE_WIDTH + " INTEGER NOT NULL, " +
                    COLUMN_IMAGE_HEIGHT + " INTEGER NOT NULL, " +
                    COLUMN_PUBLISH_DATE + " TEXT NOT NULL, " +
                    COLUMN_COPYRIGHT + " TEXT NOT NULL, " +
                    COLUMN_WEB_URL + " TEXT NOT NULL, " +
                    COLUMN_TIMESTAMP + " INTEGER NOT NULL, " +
                    COLUMN_FAVOURITE + " INTEGER DEFAULT 0)";
        }

        @NonNull
        public static String getExistsSql() {
            return "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COLUMN_GUID + " = ?";
        }

        @NonNull
        public static String getInsertSql() {
            return "INSERT INTO " + TABLE_NAME + "(" +
                    _ID + ", " +
                    COLUMN_GUID + ", " +
                    COLUMN_TITLE + ", " +
                    COLUMN_AUTHOR + ", " +
                    COLUMN_AUTHOR_IMAGE_URL + ", " +
                    COLUMN_DESCRIPTION + ", " +
                    COLUMN_IMAGE_URL + ", " +
                    COLUMN_IMAGE_WIDTH + ", " +
                    COLUMN_IMAGE_HEIGHT + ", " +
                    COLUMN_PUBLISH_DATE + ", " +
                    COLUMN_COPYRIGHT + ", " +
                    COLUMN_WEB_URL + ", " +
                    COLUMN_TIMESTAMP + ", " +
                    COLUMN_FAVOURITE + ") " +
                    "VALUES(" +
                    "?, " +     // _ID
                    "?, " +     // COLUMN_GUID
                    "?, " +     // COLUMN_TITLE
                    "?, " +     // COLUMN_AUTHOR
                    "?, " +     // COLUMN_AUTHOR_IMAGE_URL
                    "?, " +     // COLUMN_DESCRIPTION
                    "?, " +     // COLUMN_IMAGE_URL
                    "?, " +     // COLUMN_IMAGE_WIDTH
                    "?, " +     // COLUMN_IMAGE_HEIGHT
                    "?, " +     // COLUMN_PUBLISH_DATE
                    "?, " +     // COLUMN_COPYRIGHT
                    "?, " +     // COLUMN_WEB_URL
                    "?, " +     // COLUMN_TIMESTAMP
                    "?)";       // COLUMN_FAVOURITE
        }

        @NonNull
        public static Uri getContentUriAllArtworks() {
            return ALL_ARTWORKS_CONTENT_URI;
        }

        @NonNull
        public static Uri getContentUriFavouriteArtworks() {
            return FAVOURITE_ARTWORKS_CONTENT_URI;
        }

        @NonNull
        public static Uri getContentUriSpecificArtwork(@NonNull String guid) {
            return Uri.withAppendedPath(ALL_ARTWORKS_CONTENT_URI, guid);
        }

        @NonNull
        public static Uri getContentUriSaveFavourite(@NonNull String guid, boolean isFavourite) {
            int favouriteValueToSave = isFavourite ? 1 : 0;

            return SAVE_FAVOURITE_URI
                    .buildUpon()
                    .appendQueryParameter(SAVE_FAVOURITE_PARAM_GUID, guid)
                    .appendQueryParameter(SAVE_FAVOURITE_PARAM_IS_FAVOURITE, String.valueOf(favouriteValueToSave))
                    .build();
        }

        @NonNull
        public static Uri getContentUriArtworkCount() {
            return ALL_ARTWORKS_COUNT_URI;
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
            map.put(COLUMN_GUID, 1);
            map.put(COLUMN_TITLE, 2);
            map.put(COLUMN_AUTHOR, 3);
            map.put(COLUMN_AUTHOR_IMAGE_URL, 4);
            map.put(COLUMN_DESCRIPTION, 5);
            map.put(COLUMN_IMAGE_URL, 6);
            map.put(COLUMN_IMAGE_WIDTH, 7);
            map.put(COLUMN_IMAGE_HEIGHT, 8);
            map.put(COLUMN_PUBLISH_DATE, 9);
            map.put(COLUMN_COPYRIGHT, 10);
            map.put(COLUMN_WEB_URL, 11);
            map.put(COLUMN_TIMESTAMP, 12);
            map.put(COLUMN_FAVOURITE, 13);

            return map;
        }
    }
}
