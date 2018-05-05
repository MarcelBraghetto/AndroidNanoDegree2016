package io.github.marcelbraghetto.alexandria.framework.providers.books.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

import io.github.marcelbraghetto.alexandria.framework.providers.books.database.BooksDatabaseContract.Books;

import static io.github.marcelbraghetto.alexandria.framework.providers.books.database.BooksDatabaseContract.CONTENT_AUTHORITY;
import static io.github.marcelbraghetto.alexandria.framework.providers.books.database.BooksDatabaseContract.CONTENT_PATH_BOOKS;
import static io.github.marcelbraghetto.alexandria.framework.providers.books.database.BooksDatabaseContract.CONTENT_PATH_BOOK_COUNT;
import static io.github.marcelbraghetto.alexandria.framework.providers.books.database.BooksDatabaseContract.CONTENT_PATH_FILTERED;
import static io.github.marcelbraghetto.alexandria.framework.providers.books.database.BooksDatabaseContract.QUERY_PARAM_SEARCH_FILTER;

/**
 * Created by Marcel Braghetto on 14/07/15.
 *
 * Movies content provider user for all data access to
 * manage movie related persistent data.
 *
 * This provider is backed by an SQLite database.
 */
public class BooksDatabaseProvider extends ContentProvider {
    private static final int MATCHER_ID_BOOK = 100;
    private static final int MATCHER_ID_ALL_BOOKS = 101;
    private static final int MATCHER_ID_FILTERED_BOOKS = 102;
    private static final int MATCHER_ID_BOOK_COUNT = 103;

    private final UriMatcher mUriMatcher;

    private SQLiteDatabase mBooksDatabase;

    public BooksDatabaseProvider() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_BOOKS + "/#", MATCHER_ID_BOOK);
        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_BOOKS, MATCHER_ID_ALL_BOOKS);
        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_FILTERED, MATCHER_ID_FILTERED_BOOKS);
        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_BOOK_COUNT, MATCHER_ID_BOOK_COUNT);
    }

    @Override
    public boolean onCreate() {
        mBooksDatabase = new BooksDatabaseHelper(getContext()).getWritableDatabase();
        return true;
    }

    @Override
    public synchronized Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch(mUriMatcher.match(uri)) {
            case MATCHER_ID_BOOK: {
                String isbn = uri.getLastPathSegment();
                String query = "SELECT * FROM " + Books.TABLE_NAME + " WHERE " + Books.COLUMN_ISBN + " = ?";
                cursor = mBooksDatabase.rawQuery(query, new String[] { isbn });
            }
            break;

            case MATCHER_ID_ALL_BOOKS: {
                String query = "SELECT * FROM " + Books.TABLE_NAME + " ORDER BY " + Books.COLUMN_TITLE;
                cursor = mBooksDatabase.rawQuery(query, null);
            }
            break;

            case MATCHER_ID_FILTERED_BOOKS: {
                String searchFilter = uri.getQueryParameter(QUERY_PARAM_SEARCH_FILTER);
                String query = "SELECT * FROM " + Books.TABLE_NAME + " WHERE " + Books.COLUMN_SEARCH_BLOB + " LIKE ? " + " ORDER BY " + Books.COLUMN_TITLE;
                cursor = mBooksDatabase.rawQuery(query, new String[] { "%" + searchFilter + "%" });
            }
            break;

            case MATCHER_ID_BOOK_COUNT: {
                String query = "SELECT COUNT(" + Books._ID + ") FROM " + Books.TABLE_NAME;
                cursor = mBooksDatabase.rawQuery(query, null);
            }
            break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(cursor != null && getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = mUriMatcher.match(uri);

        String type;

        switch(match) {
            case MATCHER_ID_BOOK: {
                type = Books.getContentItemType();
            }
            break;

            case MATCHER_ID_ALL_BOOKS: {
                type = Books.getContentType();
            }
            break;

            case MATCHER_ID_FILTERED_BOOKS: {
                type = Books.getContentTypeFiltered();
            }
            break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return type;
    }

    @Override
    public synchronized Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = mUriMatcher.match(uri);

        switch(match) {
            case MATCHER_ID_ALL_BOOKS:
                mBooksDatabase.insertWithOnConflict(Books.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null, false);
        }

        return uri;
    }

    @Override
    public synchronized int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;

        switch(mUriMatcher.match(uri)) {
            case MATCHER_ID_BOOK: {
                String isbn = uri.getLastPathSegment();
                rowsDeleted = mBooksDatabase.delete(Books.TABLE_NAME, Books.COLUMN_ISBN + " = ?", new String[] { isbn });
            }
            break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public synchronized int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated;

        switch(mUriMatcher.match(uri)) {
            case MATCHER_ID_BOOK: {
                String isbn = uri.getLastPathSegment();
                rowsUpdated = mBooksDatabase.update(Books.TABLE_NAME, values, Books.COLUMN_ISBN + " = ?", new String[] { isbn });
            }
            break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    /**
     * This is the implementation of the backing SQLite database
     * for storing and manipulating books content.
     *
     * This is specified as a private inner class to the provider
     * as we don't want it to be publicly available for any
     * operations outside the API made available by the host
     * provider implementation.
     */
    private static class BooksDatabaseHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "books.db";

        public BooksDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(Books.getCreateTableSql());
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) { }
    }
}
