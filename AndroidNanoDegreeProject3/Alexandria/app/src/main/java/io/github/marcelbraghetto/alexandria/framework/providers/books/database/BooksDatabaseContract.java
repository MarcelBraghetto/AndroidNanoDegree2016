package io.github.marcelbraghetto.alexandria.framework.providers.books.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marcel Braghetto on 3/12/15.
 *
 * Contract fot the books database storage.
 */
public class BooksDatabaseContract {
    public static final String CONTENT_AUTHORITY = "io.github.marcelbraghetto.alexandria.framework.providers.books.database.BooksContentProvider";
    public static final String CONTENT_PATH_BOOKS = "books";
    public static final String CONTENT_PATH_FILTERED = "filtered";
    public static final String CONTENT_PATH_BOOK_COUNT = "bookcount";

    public static final String QUERY_PARAM_SEARCH_FILTER = "searchFilter";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class Books implements BaseColumns {
        public static final String TABLE_NAME = "books";
        public static final String COLUMN_ISBN = "book_isbn";
        public static final String COLUMN_TITLE = "book_title";
        public static final String COLUMN_DESCRIPTION = "book_description";
        public static final String COLUMN_THUMBNAIL_URL = "book_thumbnail_url";
        public static final String COLUMN_PUBLISH_DATE = "book_publish_date";
        public static final String COLUMN_AUTHORS = "book_authors";
        public static final String COLUMN_CATEGORIES = "book_categories";
        public static final String COLUMN_PREVIEW_LINK = "book_preview_link";
        public static final String COLUMN_SEARCH_BLOB = "book_search_blob";

        private static final Uri ALL_BOOKS_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTENT_PATH_BOOKS).build();
        private static final Uri FILTERED_BOOKS_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTENT_PATH_FILTERED).build();
        private static final Uri ALL_BOOKS_COUNT_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTENT_PATH_BOOK_COUNT).build();
        
        private static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CONTENT_PATH_BOOKS;
        private static final String CONTENT_TYPE_FILTERED = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CONTENT_PATH_FILTERED;
        private static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CONTENT_PATH_BOOKS;

        @NonNull
        public static String getCreateTableSql() {
            return "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_ISBN + " TEXT NOT NULL, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                    COLUMN_THUMBNAIL_URL + " TEXT NOT NULL, " +
                    COLUMN_PUBLISH_DATE + " TEXT NOT NULL, " +
                    COLUMN_AUTHORS + " TEXT NOT NULL, " +
                    COLUMN_CATEGORIES + " TEXT NOT NULL, " +
                    COLUMN_PREVIEW_LINK + " TEXT NOT NULL, " +
                    COLUMN_SEARCH_BLOB + " TEXT NOT NULL);";
        }

        @NonNull
        public static String getBookExistsSql() {
            return "SELECT COUNT(" + _ID + ") FROM " + TABLE_NAME + " WHERE " + COLUMN_ISBN + " = ?";
        }

        @NonNull
        public static String getUpdateSql() {
            return "UPDATE " + TABLE_NAME + " SET " +
                    COLUMN_ISBN + "= ?, " +
                    COLUMN_TITLE + "= ?, " +
                    COLUMN_DESCRIPTION + "= ?, " +
                    COLUMN_THUMBNAIL_URL + "= ?, " +
                    COLUMN_PUBLISH_DATE + "= ?, " +
                    COLUMN_AUTHORS + "= ?, " +
                    COLUMN_CATEGORIES + "= ?, " +
                    COLUMN_PREVIEW_LINK + "= ?, " +
                    COLUMN_SEARCH_BLOB + "= ? " +
                    "WHERE " + COLUMN_ISBN + " = ?";
        }

        @NonNull
        public static String getInsertSql() {
            return "INSERT INTO " + TABLE_NAME + "(" +
                    _ID + ", " +
                    COLUMN_ISBN + ", " +
                    COLUMN_TITLE + ", " +
                    COLUMN_DESCRIPTION + ", " +
                    COLUMN_THUMBNAIL_URL + ", " +
                    COLUMN_PUBLISH_DATE + ", " +
                    COLUMN_AUTHORS + ", " +
                    COLUMN_CATEGORIES + ", " +
                    COLUMN_PREVIEW_LINK + ", " +
                    COLUMN_SEARCH_BLOB + ") " +
                    "VALUES(" +
                    "?, " +     // _ID
                    "?, " +     // COLUMN_ISBN
                    "?, " +     // COLUMN_TITLE
                    "?, " +     // COLUMN_DESCRIPTION
                    "?, " +     // COLUMN_THUMBNAIL_URL
                    "?, " +     // COLUMN_PUBLISH_DATE
                    "?, " +     // COLUMN_AUTHORS
                    "?, " +     // COLUMN_CATEGORIES
                    "?, " +     // COLUMN_PREVIEW_LINK
                    "?)";       // COLUMN_SEARCH_BLOB
        }

        @NonNull
        public static Uri getContentUriAllBooks() {
            return ALL_BOOKS_CONTENT_URI;
        }

        @NonNull
        public static Uri getContentUriFilteredBooks(@NonNull String searchFilter) {
            return FILTERED_BOOKS_CONTENT_URI.buildUpon().appendQueryParameter(QUERY_PARAM_SEARCH_FILTER, searchFilter).build();
        }

        @NonNull
        public static Uri getContentUriSpecificBook(@NonNull String isbn) {
            return Uri.withAppendedPath(ALL_BOOKS_CONTENT_URI, isbn);
        }

        @NonNull
        public static Uri getContentUriBookCount() {
            return ALL_BOOKS_COUNT_URI;
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
        public static String getContentTypeFiltered() {
            return CONTENT_TYPE_FILTERED;
        }

        @NonNull
        public static Map<String, Integer> getAllColumnsIndexMap() {
            Map<String, Integer> map = new HashMap<>();

            map.put(_ID, 0);
            map.put(COLUMN_ISBN, 1);
            map.put(COLUMN_TITLE, 2);
            map.put(COLUMN_DESCRIPTION, 3);
            map.put(COLUMN_THUMBNAIL_URL, 4);
            map.put(COLUMN_PUBLISH_DATE, 5);
            map.put(COLUMN_AUTHORS, 6);
            map.put(COLUMN_CATEGORIES, 7);
            map.put(COLUMN_PREVIEW_LINK, 8);
            map.put(COLUMN_SEARCH_BLOB, 9);

            return map;
        }
    }
}
