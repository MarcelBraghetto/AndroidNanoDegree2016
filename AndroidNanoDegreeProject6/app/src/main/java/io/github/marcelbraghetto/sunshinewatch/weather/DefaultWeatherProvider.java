package io.github.marcelbraghetto.sunshinewatch.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;

import io.github.marcelbraghetto.sunshinewatch.framework.strings.contracts.StringsProvider;
import io.github.marcelbraghetto.sunshinewatch.weather.contracts.WeatherProvider;
import io.github.marcelbraghetto.sunshinewatch.weather.wear.RefreshWearableDeviceIntentService;
import io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherContract;
import io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherForecast;

/**
 * Created by Marcel Braghetto on 16/04/16.
 *
 * Default implementation of the weather provider to access weather data and features.
 */
public class DefaultWeatherProvider implements WeatherProvider {
    // For this particular project assignment, we are just going to use a simple static location.
    // A fully featured version of this app would include all the location configuration and
    // settings integration - but the focus of this app is the Watch, not so much the app itself.
    private static final String APP_LOCATION = "94043";

    private final Context mApplicationContext;
    private final StringsProvider mStringsProvider;

    @Inject
    public DefaultWeatherProvider(@NonNull Context applicationContext,
                                  @NonNull StringsProvider stringsProvider) {

        mApplicationContext = applicationContext;
        mStringsProvider = stringsProvider;
    }

    @NonNull
    @Override
    public String getWeatherLocation() {
        return APP_LOCATION;
    }

    @NonNull
    @Override
    public Uri getWeatherForecastUri() {
        return WeatherContract.WeatherForecastTable.getContentUriFromNow();
    }

    @Nullable
    @Override
    public WeatherForecast getTodaysForecast() {
        Cursor cursor = mApplicationContext.getContentResolver().query(getWeatherForecastUri(), null, null, null, null);

        if(cursor != null) {
            if(cursor.moveToFirst()) {
                WeatherForecast forecast = new WeatherForecast();
                forecast.populateFromCursor(cursor, WeatherContract.WeatherForecastTable.getAllColumnsIndexMap());
                return forecast;
            }
            cursor.close();
        }

        // Nope, no such data record ...
        return null;
    }

    @Override
    public void saveWeatherForecasts(@NonNull List<WeatherForecast> forecasts) {
        ContentValues[] allContentValues = new ContentValues[forecasts.size()];

        for(int i = 0; i < forecasts.size(); i++) {
            allContentValues[i] = forecasts.get(i).getContentValues();
        }

        // Delete any existing weather data.
        mApplicationContext.getContentResolver().delete(WeatherContract.WeatherForecastTable.CONTENT_URI, null, null);

        // Insert all our new weather data.
        mApplicationContext.getContentResolver().bulkInsert(WeatherContract.WeatherForecastTable.CONTENT_URI, allContentValues);
    }

    @Override
    public void refreshWearableDevice() {
        WeatherForecast forecast = getTodaysForecast();

        // Nope, no data for today...
        if(forecast == null) {
            return;
        }

        // Kick off our intent service that is responsible for syncing the weather forecast to the
        // wearable device.
        mApplicationContext.startService(RefreshWearableDeviceIntentService.createServiceIntent(mApplicationContext, forecast));
    }
}
