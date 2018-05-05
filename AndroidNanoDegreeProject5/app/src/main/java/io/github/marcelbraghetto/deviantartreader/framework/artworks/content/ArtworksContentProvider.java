package io.github.marcelbraghetto.deviantartreader.framework.artworks.content;

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

import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_AUTHOR;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_AUTHOR_IMAGE_URL;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_COPYRIGHT;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_DESCRIPTION;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_GUID;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_IMAGE_HEIGHT;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_IMAGE_URL;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_IMAGE_WIDTH;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_FAVOURITE;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_WEB_URL;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_PUBLISH_DATE;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_TIMESTAMP;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_TITLE;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.SAVE_FAVOURITE_PARAM_GUID;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.SAVE_FAVOURITE_PARAM_IS_FAVOURITE;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.TABLE_NAME;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks._ID;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.getContentItemType;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.getContentType;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.getCreateTableSql;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.BASE_CONTENT_URI;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.CONTENT_AUTHORITY;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.CONTENT_PATH_ARTWORKS;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.CONTENT_PATH_ARTWORKS_COUNT;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.CONTENT_PATH_ARTWORKS_FAVOURITES;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.CONTENT_PATH_ARTWORKS_SAVE_FAVOURITE;

/**
 * Created by Marcel Braghetto on 24/02/16.
 *
 * Content provider implementation for storing and retrieving artworks.
 */
public class ArtworksContentProvider extends ContentProvider {
    private static final int MATCHER_ID_ARTWORK = 100;
    private static final int MATCHER_ID_ALL_ARTWORKS = 101;
    private static final int MATCHER_ID_FAVOURITE_ARTWORKS = 102;
    private static final int MATCHER_ID_ARTWORKS_COUNT = 103;
    private static final int MATCHER_ID_SAVE_FAVOURITE = 104;

    private final UriMatcher mUriMatcher;

    private SQLiteDatabase mDatabase;

    public ArtworksContentProvider() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_ARTWORKS + "/*", MATCHER_ID_ARTWORK);
        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_ARTWORKS, MATCHER_ID_ALL_ARTWORKS);
        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_ARTWORKS_FAVOURITES, MATCHER_ID_FAVOURITE_ARTWORKS);
        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_ARTWORKS_COUNT, MATCHER_ID_ARTWORKS_COUNT);
        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_ARTWORKS_SAVE_FAVOURITE, MATCHER_ID_SAVE_FAVOURITE);
    }

    @Override
    public boolean onCreate() {
        mDatabase = new ArtworksDatabaseHelper(getContext()).getWritableDatabase();
        return true;
    }

    @Override
    public synchronized Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        boolean destructive = false;

        switch(mUriMatcher.match(uri)) {
            case MATCHER_ID_ARTWORK: {
                String guid = uri.getLastPathSegment();
                String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_GUID + " = ?";
                cursor = mDatabase.rawQuery(query, new String[] { guid });
            }
            break;

            case MATCHER_ID_ALL_ARTWORKS: {
                String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";
                cursor = mDatabase.rawQuery(query, null);
            }
            break;

            case MATCHER_ID_FAVOURITE_ARTWORKS: {
                String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_FAVOURITE + " = 1 " + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";
                cursor = mDatabase.rawQuery(query, null);
            }
            break;

            case MATCHER_ID_ARTWORKS_COUNT: {
                String query = "SELECT COUNT(" + _ID + ") FROM " + TABLE_NAME;
                cursor = mDatabase.rawQuery(query, null);
            }
            break;

            case MATCHER_ID_SAVE_FAVOURITE: {
                destructive = true;
                String guid = uri.getQueryParameter(SAVE_FAVOURITE_PARAM_GUID);
                String isFavourite = uri.getQueryParameter(SAVE_FAVOURITE_PARAM_IS_FAVOURITE);
                String query = "UPDATE " + TABLE_NAME + " SET " + COLUMN_FAVOURITE + " = ? WHERE " + COLUMN_GUID + " = ?";
                cursor = mDatabase.rawQuery(query, new String[] { isFavourite, guid });
            }
            break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(cursor != null && getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);

            if(destructive) {
                getContext().getContentResolver().notifyChange(BASE_CONTENT_URI, null, false);
            }
        }

        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = mUriMatcher.match(uri);

        String type;

        switch(match) {
            case MATCHER_ID_ARTWORK: {
                type = getContentItemType();
            }
            break;

            case MATCHER_ID_ALL_ARTWORKS: {
                type = getContentType();
            }
            break;

            case MATCHER_ID_FAVOURITE_ARTWORKS: {
                type = getContentType();
            }
            break;

            case MATCHER_ID_SAVE_FAVOURITE: {
                type = getContentItemType();
            }
            break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return type;
    }

    @Override
    public synchronized Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException("Inserting individual artworks is not supported. Use bulk insert instead.");
    }

    @Override
    public synchronized int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Deleting artworks is not supported.");
    }

    @Override
    public synchronized int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated;

        switch(mUriMatcher.match(uri)) {
            case MATCHER_ID_ARTWORK: {
                String guid = uri.getLastPathSegment();
                rowsUpdated = mDatabase.update(TABLE_NAME, values, COLUMN_GUID + " = ?", new String[] { guid });
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

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        if(mUriMatcher.match(uri) == MATCHER_ID_ALL_ARTWORKS) {
            int insertedCount = 0;

            SQLiteStatement existsStatement = mDatabase.compileStatement(ArtworksDatabaseContract.Artworks.getExistsSql());
            SQLiteStatement insertStatement = mDatabase.compileStatement(ArtworksDatabaseContract.Artworks.getInsertSql());

            mDatabase.beginTransaction();

            try {
                for(ContentValues value : values) {
                    existsStatement.clearBindings();
                    existsStatement.bindString(1, value.getAsString(COLUMN_GUID));

                    // We only want to include new items, and ignore any that already exist
                    if(existsStatement.simpleQueryForLong() == 0L) {
                        // Do insert
                        insertStatement.clearBindings();

                        insertStatement.bindString(2, value.getAsString(COLUMN_GUID));
                        insertStatement.bindString(3, value.getAsString(COLUMN_TITLE));
                        insertStatement.bindString(4, value.getAsString(COLUMN_AUTHOR));
                        insertStatement.bindString(5, value.getAsString(COLUMN_AUTHOR_IMAGE_URL));
                        insertStatement.bindString(6, value.getAsString(COLUMN_DESCRIPTION));
                        insertStatement.bindString(7, value.getAsString(COLUMN_IMAGE_URL));
                        insertStatement.bindLong(8, value.getAsInteger(COLUMN_IMAGE_WIDTH));
                        insertStatement.bindLong(9, value.getAsInteger(COLUMN_IMAGE_HEIGHT));
                        insertStatement.bindString(10, value.getAsString(COLUMN_PUBLISH_DATE));
                        insertStatement.bindString(11, value.getAsString(COLUMN_COPYRIGHT));
                        insertStatement.bindString(12, value.getAsString(COLUMN_WEB_URL));
                        insertStatement.bindLong(13, value.getAsLong(COLUMN_TIMESTAMP));
                        insertStatement.bindLong(14, value.getAsLong(COLUMN_FAVOURITE));

                        insertStatement.execute();

                        insertedCount++;
                    }
                }

                mDatabase.setTransactionSuccessful();
            } finally {
                mDatabase.endTransaction();
            }

            if(getContext() != null) {
                getContext().getContentResolver().notifyChange(uri, null, false);
            }

            return insertedCount;
        }

        throw new UnsupportedOperationException("Unsupported uri: " + uri);
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
    private static class ArtworksDatabaseHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "artworks.db";

        public ArtworksDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(getCreateTableSql());
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) { }
    }
}
