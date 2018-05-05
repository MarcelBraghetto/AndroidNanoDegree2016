package io.github.marcelbraghetto.sunshinewatch.weather.data;

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

import org.joda.time.DateTime;

import static io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherContract.CONTENT_AUTHORITY;
import static io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherContract.PATH_WEATHER;
import static io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherContract.WeatherForecastTable.COLUMN_DATE;
import static io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherContract.WeatherForecastTable.COLUMN_MAX_TEMP;
import static io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherContract.WeatherForecastTable.COLUMN_MIN_TEMP;
import static io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherContract.WeatherForecastTable.COLUMN_SHORT_DESC;
import static io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherContract.WeatherForecastTable.COLUMN_WEATHER_ID;
import static io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherContract.WeatherForecastTable.CONTENT_TYPE;
import static io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherContract.WeatherForecastTable.TABLE_NAME;
import static io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherContract.WeatherForecastTable.getCreateTableSql;
import static io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherContract.WeatherForecastTable.getForecastDayExistsSql;
import static io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherContract.WeatherForecastTable.getInsertForecastDaySql;
import static io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherContract.WeatherForecastTable.getUpdateForecastDaySql;

public class WeatherContentProvider extends ContentProvider {
    private static final int WEATHER = 100;

    private SQLiteDatabase mDatabase;
    private final UriMatcher mUriMatcher;

    public WeatherContentProvider() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(CONTENT_AUTHORITY, PATH_WEATHER, WEATHER);
    }

    @Override
    public boolean onCreate() {
        mDatabase = new WeatherDatabase(getContext()).getWritableDatabase();
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case WEATHER:
                return CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch(mUriMatcher.match(uri)) {
            case WEATHER: {
                long time = DateTime.now().withTimeAtStartOfDay().getMillis();
                String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + " >= ? ORDER BY " + COLUMN_DATE + " ASC";
                cursor = mDatabase.rawQuery(query, new String[] { String.valueOf(time) });
            }
            break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Context context = getContext();
        if(context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Individual inserts are not supported. Use bulk insert instead.");
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted;

        switch(mUriMatcher.match(uri)) {
            case WEATHER: {
                rowsDeleted = mDatabase.delete(TABLE_NAME, selection, selectionArgs);
            }
            break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Context context = getContext();
        if(context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    /**
     * Adjust all the date fields to be at the start of the given day so we can do comparisons
     * on the date to find out if a day matches a record in the content.
     * @param values to adjust the date field for.
     */
    private void normalizeDate(ContentValues values) {
        if (values.containsKey(COLUMN_DATE)) {
            long dateValue = new DateTime(values.getAsLong(COLUMN_DATE)).withTimeAtStartOfDay().getMillis();
            values.put(COLUMN_DATE, dateValue);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Updating data is not supported.");
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch(mUriMatcher.match(uri)) {
            case WEATHER: {
                SQLiteStatement existsStatement = mDatabase.compileStatement(getForecastDayExistsSql());
                SQLiteStatement insertStatement = mDatabase.compileStatement(getInsertForecastDaySql());
                SQLiteStatement updateStatement = mDatabase.compileStatement(getUpdateForecastDaySql());

                mDatabase.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);

                        existsStatement.clearBindings();
                        existsStatement.bindLong(1, value.getAsLong(COLUMN_DATE));

                        if (existsStatement.simpleQueryForLong() == 0L) {
                            insertStatement.clearBindings();

                            insertStatement.bindLong(1, value.getAsLong(COLUMN_DATE));
                            insertStatement.bindString(2, value.getAsString(COLUMN_SHORT_DESC));
                            insertStatement.bindLong(3, value.getAsLong(COLUMN_WEATHER_ID));
                            insertStatement.bindDouble(4, value.getAsDouble(COLUMN_MIN_TEMP));
                            insertStatement.bindDouble(5, value.getAsDouble(COLUMN_MAX_TEMP));

                            insertStatement.execute();
                        } else {
                            updateStatement.clearBindings();
                            updateStatement.bindLong(1, value.getAsLong(COLUMN_DATE));
                            updateStatement.bindString(2, value.getAsString(COLUMN_SHORT_DESC));
                            updateStatement.bindLong(3, value.getAsLong(COLUMN_WEATHER_ID));
                            updateStatement.bindDouble(4, value.getAsDouble(COLUMN_MIN_TEMP));
                            updateStatement.bindDouble(5, value.getAsDouble(COLUMN_MAX_TEMP));
                            updateStatement.bindLong(6, value.getAsLong(COLUMN_DATE));

                            updateStatement.execute();
                        }
                    }

                    mDatabase.setTransactionSuccessful();
                } finally {
                    mDatabase.endTransaction();
                }
            }
            break;

            default:
                throw new UnsupportedOperationException("Unsupported uri: " + uri);
        }

        Context context = getContext();
        if(context != null) {
            context.getContentResolver().notifyChange(uri, null, false);
        }

        return values.length;
    }

    private static class WeatherDatabase extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 1;
        static final String DATABASE_NAME = "weather.db";

        public WeatherDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(getCreateTableSql());
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) { }
    }
}