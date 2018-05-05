package io.github.marcelbraghetto.sunshinewatch.sharedlib.weather;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public final class WeatherContract {
    public static final String CONTENT_AUTHORITY = "io.github.marcelbraghetto.sunshinewatch";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WEATHER = "weather";

    public static final class WeatherForecastTable implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String TABLE_NAME = "weather";

        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_SHORT_DESC = "short_desc";
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        @NonNull
        public static String getCreateTableSql() {
            return "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DATE + " INTEGER NOT NULL, " +
                    COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                    COLUMN_WEATHER_ID + " INTEGER NOT NULL, " +
                    COLUMN_MIN_TEMP + " REAL NOT NULL, " +
                    COLUMN_MAX_TEMP + " REAL NOT NULL);";
        }

        @NonNull
        public static String getForecastDayExistsSql() {
            return "SELECT COUNT(" + COLUMN_DATE + ") FROM " + TABLE_NAME + " WHERE " + COLUMN_DATE + " = ?";
        }

        @NonNull
        public static String getInsertForecastDaySql() {
            return "INSERT INTO " + TABLE_NAME + "(" +
                    COLUMN_DATE + ", " +
                    COLUMN_SHORT_DESC + ", " +
                    COLUMN_WEATHER_ID + ", " +
                    COLUMN_MIN_TEMP + ", " +
                    COLUMN_MAX_TEMP + ") " +
                    "VALUES(" +
                    "?, " +     // COLUMN_DATE
                    "?, " +     // COLUMN_SHORT_DESC
                    "?, " +     // COLUMN_WEATHER_ID
                    "?, " +     // COLUMN_MIN_TEMP
                    "?)";       // COLUMN_MAX_TEMP
        }

        @NonNull
        public static String getUpdateForecastDaySql() {
            return "UPDATE " + TABLE_NAME + " SET " +
                    COLUMN_DATE + "= ?, " +
                    COLUMN_SHORT_DESC + "= ?, " +
                    COLUMN_WEATHER_ID + "= ?, " +
                    COLUMN_MIN_TEMP + "= ?, " +
                    COLUMN_MAX_TEMP + "= ? " +
                    "WHERE " + COLUMN_DATE + " = ?";
        }

        @NonNull
        public static Map<String, Integer> getAllColumnsIndexMap() {
            Map<String, Integer> map = new HashMap<>();

            map.put(_ID, 0);
            map.put(COLUMN_DATE, 1);
            map.put(COLUMN_SHORT_DESC, 2);
            map.put(COLUMN_WEATHER_ID, 3);
            map.put(COLUMN_MIN_TEMP, 4);
            map.put(COLUMN_MAX_TEMP, 5);

            return map;
        }

        @NonNull
        public static Uri getContentUriFromNow() {
            return CONTENT_URI;
        }
    }
}
