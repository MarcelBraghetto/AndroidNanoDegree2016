package io.github.marcelbraghetto.football.framework.providers.football.database;

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

import static io.github.marcelbraghetto.football.framework.providers.football.database.FootballDatabaseContract.CONTENT_AUTHORITY;
import static io.github.marcelbraghetto.football.framework.providers.football.database.FootballDatabaseContract.CONTENT_PATH_GAMES;
import static io.github.marcelbraghetto.football.framework.providers.football.database.FootballDatabaseContract.Games;
import static io.github.marcelbraghetto.football.framework.providers.football.database.FootballDatabaseContract.Games.COLUMN_DATE_DAY;
import static io.github.marcelbraghetto.football.framework.providers.football.database.FootballDatabaseContract.Games.COLUMN_DATE_MONTH;
import static io.github.marcelbraghetto.football.framework.providers.football.database.FootballDatabaseContract.Games.COLUMN_DATE_TEXT;
import static io.github.marcelbraghetto.football.framework.providers.football.database.FootballDatabaseContract.Games.COLUMN_DATE_YEAR;
import static io.github.marcelbraghetto.football.framework.providers.football.database.FootballDatabaseContract.Games.COLUMN_MATCH_ID;
import static io.github.marcelbraghetto.football.framework.providers.football.database.FootballDatabaseContract.Games.TABLE_NAME;
import static io.github.marcelbraghetto.football.framework.providers.football.database.FootballDatabaseContract.Games.getContentItemType;
import static io.github.marcelbraghetto.football.framework.providers.football.database.FootballDatabaseContract.Games.getContentType;
import static io.github.marcelbraghetto.football.framework.providers.football.database.FootballDatabaseContract.QUERY_PARAM_SEARCH_DAY;
import static io.github.marcelbraghetto.football.framework.providers.football.database.FootballDatabaseContract.QUERY_PARAM_SEARCH_MONTH;
import static io.github.marcelbraghetto.football.framework.providers.football.database.FootballDatabaseContract.QUERY_PARAM_SEARCH_YEAR;

/**
 * Created by Marcel Braghetto on 06/12/15.
 *
 * Football content provider user for all data access to
 * manage football related persistent data.
 *
 * This provider is backed by an SQLite database.
 */
public class FootballDatabaseProvider extends ContentProvider {
    private static final int MATCHER_ID_GAME = 100;
    private static final int MATCHER_ID_GAMES = 101;

    private final UriMatcher mUriMatcher;

    private SQLiteDatabase mFootballDatabase;

    public FootballDatabaseProvider() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_GAMES + "/#", MATCHER_ID_GAME);
        mUriMatcher.addURI(CONTENT_AUTHORITY, CONTENT_PATH_GAMES, MATCHER_ID_GAMES);
    }

    @Override
    public boolean onCreate() {
        mFootballDatabase = new FootballDatabaseHelper(getContext()).getWritableDatabase();
        return true;
    }

    @Override
    public synchronized Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch(mUriMatcher.match(uri)) {
            case MATCHER_ID_GAME: {
                String matchId = uri.getLastPathSegment();
                String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_MATCH_ID + " = ?";
                cursor = mFootballDatabase.rawQuery(query, new String[] { matchId });
            }
            break;

            case MATCHER_ID_GAMES: {
                String year = uri.getQueryParameter(QUERY_PARAM_SEARCH_YEAR);
                String month = uri.getQueryParameter(QUERY_PARAM_SEARCH_MONTH);
                String day = uri.getQueryParameter(QUERY_PARAM_SEARCH_DAY);

                String query =
                        "SELECT * FROM " + TABLE_NAME + " WHERE " +
                        COLUMN_DATE_YEAR + " = ? AND " +
                        COLUMN_DATE_MONTH + " = ? AND " +
                        COLUMN_DATE_DAY + " = ? " +
                        " ORDER BY " + COLUMN_DATE_TEXT;

                cursor = mFootballDatabase.rawQuery(query, new String[] { year, month, day });
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
            case MATCHER_ID_GAME: {
                type = getContentItemType();
            }
            break;

            case MATCHER_ID_GAMES: {
                type = getContentType();
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
            case MATCHER_ID_GAMES:
                mFootballDatabase.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
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
            case MATCHER_ID_GAME: {
                String matchId = uri.getLastPathSegment();
                rowsDeleted = mFootballDatabase.delete(TABLE_NAME, COLUMN_MATCH_ID + " = ?", new String[] { matchId });
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
            case MATCHER_ID_GAME: {
                String matchId = uri.getLastPathSegment();
                rowsUpdated = mFootballDatabase.update(TABLE_NAME, values, COLUMN_MATCH_ID + " = ?", new String[]{matchId});
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
    public synchronized int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch(mUriMatcher.match(uri)) {
            case MATCHER_ID_GAMES: {
                SQLiteStatement gameExistsStatement = mFootballDatabase.compileStatement(Games.getGameExistsSql());
                SQLiteStatement gameInsertStatement = mFootballDatabase.compileStatement(Games.getInsertSql());
                SQLiteStatement gameUpdateStatement = mFootballDatabase.compileStatement(Games.getUpdateSql());

                mFootballDatabase.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        gameExistsStatement.clearBindings();
                        gameExistsStatement.bindString(1, value.getAsString(Games.COLUMN_MATCH_ID));

                        if (gameExistsStatement.simpleQueryForLong() == 0L) {
                            // Do insert
                            gameInsertStatement.clearBindings();
                            gameInsertStatement.bindString(1, value.getAsString(Games.COLUMN_MATCH_ID));
                            gameInsertStatement.bindString(2, value.getAsString(Games.COLUMN_DATE_TEXT));
                            gameInsertStatement.bindLong(3, value.getAsLong(Games.COLUMN_DATE_YEAR));
                            gameInsertStatement.bindLong(4, value.getAsLong(Games.COLUMN_DATE_MONTH));
                            gameInsertStatement.bindLong(5, value.getAsLong(Games.COLUMN_DATE_DAY));
                            gameInsertStatement.bindLong(6, value.getAsLong(Games.COLUMN_MATCH_DAY));
                            gameInsertStatement.bindString(7, value.getAsString(Games.COLUMN_HOME_TEAM_NAME));
                            gameInsertStatement.bindLong(8, value.getAsLong(Games.COLUMN_HOME_TEAM_GOALS));
                            gameInsertStatement.bindString(9, value.getAsString(Games.COLUMN_AWAY_TEAM_NAME));
                            gameInsertStatement.bindLong(10, value.getAsLong(Games.COLUMN_AWAY_TEAM_GOALS));
                            gameInsertStatement.bindString(11, value.getAsString(Games.COLUMN_STATUS));

                            gameInsertStatement.execute();
                        } else {
                            // update
                            gameUpdateStatement.clearBindings();
                            gameUpdateStatement.bindString(1, value.getAsString(Games.COLUMN_DATE_TEXT));
                            gameUpdateStatement.bindLong(2, value.getAsLong(Games.COLUMN_DATE_YEAR));
                            gameUpdateStatement.bindLong(3, value.getAsLong(Games.COLUMN_DATE_MONTH));
                            gameUpdateStatement.bindLong(4, value.getAsLong(Games.COLUMN_DATE_DAY));
                            gameUpdateStatement.bindLong(5, value.getAsLong(Games.COLUMN_MATCH_DAY));
                            gameUpdateStatement.bindString(6, value.getAsString(Games.COLUMN_HOME_TEAM_NAME));
                            gameUpdateStatement.bindLong(7, value.getAsLong(Games.COLUMN_HOME_TEAM_GOALS));
                            gameUpdateStatement.bindString(8, value.getAsString(Games.COLUMN_AWAY_TEAM_NAME));
                            gameUpdateStatement.bindLong(9, value.getAsLong(Games.COLUMN_AWAY_TEAM_GOALS));
                            gameUpdateStatement.bindString(10, value.getAsString(Games.COLUMN_STATUS));
                            gameUpdateStatement.bindString(11, value.getAsString(Games.COLUMN_MATCH_ID));

                            gameUpdateStatement.execute();
                        }
                    }

                    mFootballDatabase.setTransactionSuccessful();
                } finally {
                    mFootballDatabase.endTransaction();
                }
            }
            break;

            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }

        if(getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null, false);
        }

        return values.length;
    }

    /**
     * This is the implementation of the backing SQLite database
     * for storing and manipulating football content.
     *
     * This is specified as a private inner class to the provider
     * as we don't want it to be publicly available for any
     * operations outside the API made available by the host
     * provider implementation.
     */
    private static class FootballDatabaseHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 1;
        private static final String DATABASE_NAME = "football.db";

        public FootballDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(Games.getCreateTableSql());
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) { }
    }
}
