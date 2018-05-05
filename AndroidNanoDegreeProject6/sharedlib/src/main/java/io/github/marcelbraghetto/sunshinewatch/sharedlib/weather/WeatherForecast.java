package io.github.marcelbraghetto.sunshinewatch.sharedlib.weather;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.wearable.DataMap;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.Map;

import static io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherContract.WeatherForecastTable.*;

/**
 * Created by Marcel Braghetto on 23/04/16.
 *
 * Simple model describing a weather forecast for a particular day.
 *
 * Note: The fields in this model are deliberately package rather than private to allow
 * the Parceler library to operate without using reflection.
 */
@Parcel
public class WeatherForecast {
    public static final String DATA_MAP_KEY = "/WeatherForecastDataMap";
    public static final String DATA_MAP_TIMESTAMP = "TimeStamp";

    private static final String INTENT_EXTRA_KEY = "WeatherForecastBundleKey";

    private static final String DATA_MAP_DATE = "DataMapDate";
    private static final String DATA_MAP_WEATHER_ID = "DataMapWeatherId";
    private static final String DATA_MAP_MIN_TEMPERATURE = "DataMapMinTemperature";
    private static final String DATA_MAP_MAX_TEMPERATURE = "DataMapMaxTemperature";
    private static final String DATA_MAP_DESCRIPTION = "DataMapDescription";

    /* package */ long mDate;
    /* package */ int mWeatherId;
    /* package */ double mMinTemperature;
    /* package */ double mMaxTemperature;
    /* package */ String mDescription;

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        mDate = date;
    }

    public double getMinTemperature() {
        return mMinTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        mMinTemperature = minTemperature;
    }

    public double getMaxTemperature() {
        return mMaxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        mMaxTemperature = maxTemperature;
    }

    public int getWeatherId() {
        return mWeatherId;
    }

    public void setWeatherId(int weatherId) {
        mWeatherId = weatherId;
    }

    @NonNull
    public String getDescription() {
        return mDescription == null ? "" : mDescription;
    }

    public void setDescription(@Nullable  String description) {
        mDescription = description;
    }

    /**
     * Given a cursor and a 'column index map', translate the data within the cursor into
     * the fields of this forecast instance.
     *
     * The column index map is a hash map that would typically have come from the content
     * provider contract, which stores the table column names as keys, and the index of
     * where that column data can be found within the given cursor.
     *
     * @param cursor to populate from.
     * @param columnIndexMap to use as the data mapping.
     */
    public void populateFromCursor(@Nullable Cursor cursor, @NonNull Map<String, Integer> columnIndexMap) {
        if(cursor == null) {
            return;
        }

        if(columnIndexMap.get(COLUMN_DATE) != null) {
            setDate(cursor.getLong(columnIndexMap.get(COLUMN_DATE)));
        }

        if(columnIndexMap.get(COLUMN_WEATHER_ID) != null) {
            setWeatherId(cursor.getInt(columnIndexMap.get(COLUMN_WEATHER_ID)));
        }

        if(columnIndexMap.get(COLUMN_MIN_TEMP) != null) {
            setMinTemperature(cursor.getDouble(columnIndexMap.get(COLUMN_MIN_TEMP)));
        }

        if(columnIndexMap.get(COLUMN_MAX_TEMP) != null) {
            setMaxTemperature(cursor.getDouble(columnIndexMap.get(COLUMN_MAX_TEMP)));
        }

        if(columnIndexMap.get(COLUMN_SHORT_DESC) != null) {
            setDescription(cursor.getString(columnIndexMap.get(COLUMN_SHORT_DESC)));
        }
    }

    /**
     * Generate a set of content values that can be used for content provider
     * operations such as inserting forecasts into the database.
     * @return collection of content values representing this forecast instance.
     */
    @NonNull
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(COLUMN_DATE, getDate());
        values.put(COLUMN_WEATHER_ID, getWeatherId());
        values.put(COLUMN_MIN_TEMP, getMinTemperature());
        values.put(COLUMN_MAX_TEMP, getMaxTemperature());
        values.put(COLUMN_SHORT_DESC, getDescription());

        return values;
    }

    /**
     * Put the current forecast instance into an intent.
     * @param intent to put the forecast instance into.
     */
    public void putInto(@NonNull Intent intent) {
        intent.putExtra(INTENT_EXTRA_KEY, Parcels.wrap(this));
    }

    /**
     * Given a data map instance (typically from Android Wear APIs), populate ourselves
     * with the data values that should exist.
     * @param dataMap containing the data fields to populate from.
     */
    public void putInto(@NonNull DataMap dataMap) {
        dataMap.putLong(DATA_MAP_DATE, getDate());
        dataMap.putInt(DATA_MAP_WEATHER_ID, getWeatherId());
        dataMap.putDouble(DATA_MAP_MIN_TEMPERATURE, getMinTemperature());
        dataMap.putDouble(DATA_MAP_MAX_TEMPERATURE, getMaxTemperature());
        dataMap.putString(DATA_MAP_DESCRIPTION, getDescription());
    }

    /**
     * Extract a forecast instance from the given intent.
     * @param intent to extract the forecast data from.
     * @return extracted forecast instance.
     */
    @Nullable
    public static WeatherForecast getFrom(@Nullable Intent intent) {
        if(intent == null) {
            return null;
        }

        return Parcels.unwrap(intent.getParcelableExtra(INTENT_EXTRA_KEY));
    }

    /**
     * Extract a forecast instance from the given data map.
     * @param dataMap to extract the forecast data from.
     * @return extracted forecast instance.
     */
    @Nullable
    public static WeatherForecast getFrom(@NonNull DataMap dataMap) {
        WeatherForecast forecast = new WeatherForecast();

        forecast.setDate(dataMap.getLong(DATA_MAP_DATE));
        forecast.setWeatherId(dataMap.getInt(DATA_MAP_WEATHER_ID));
        forecast.setMinTemperature(dataMap.getDouble(DATA_MAP_MIN_TEMPERATURE));
        forecast.setMaxTemperature(dataMap.getDouble(DATA_MAP_MAX_TEMPERATURE));
        forecast.setDescription(dataMap.getString(DATA_MAP_DESCRIPTION));

        return forecast;
    }
}
