package io.github.marcelbraghetto.sunshinewatch.weather.contracts;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherForecast;

/**
 * Created by Marcel Braghetto on 16/04/16.
 *
 * The weather provider enables access to underlying content for weather forecasts.
 */
public interface WeatherProvider {
    /**
     * Get the currently selected weather location that can be used for
     * network requests and to filter the data content.
     * @return current weather location.
     */
    @NonNull String getWeatherLocation();

    /**
     * Get the content Uri for the list of all items in the current weather forecast.
     * @return uri containing the current forecast from today.
     */
    @NonNull Uri getWeatherForecastUri();

    /**
     * Get the cursor containing the weather forecast for today if it exists.
     * @return dto representing todays forecast - or null if no data is found.
     */
    @Nullable WeatherForecast getTodaysForecast();

    /**
     * Save the given weather forecast data typically received from the server.
     * @param forecasts containing forecast data to save.
     */
    void saveWeatherForecasts(@NonNull List<WeatherForecast> forecasts);

    /**
     * Request that the Android watch (if available) be updated with the latest forecast
     * data for today.
     */
    void refreshWearableDevice();
}
