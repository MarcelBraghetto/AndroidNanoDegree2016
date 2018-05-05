package io.github.marcelbraghetto.sunshinewatch.weather.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.marcelbraghetto.sunshinewatch.BuildConfig;
import io.github.marcelbraghetto.sunshinewatch.R;
import io.github.marcelbraghetto.sunshinewatch.features.application.MainApp;
import io.github.marcelbraghetto.sunshinewatch.framework.network.contracts.NetworkRequestProvider;
import io.github.marcelbraghetto.sunshinewatch.weather.contracts.WeatherProvider;
import io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherForecast;

public class SunshineSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    @Inject WeatherProvider mWeatherProvider;
    @Inject NetworkRequestProvider mNetworkRequestProvider;

    public SunshineSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        MainApp.getInjector().inject(this);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        String location = mWeatherProvider.getWeatherLocation();

        String format = "json";
        String units = "metric";
        int numDays = 14;

        final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
        final String QUERY_PARAM = "q";
        final String FORMAT_PARAM = "mode";
        final String UNITS_PARAM = "units";
        final String DAYS_PARAM = "cnt";
        final String APPID_PARAM = "APPID";

        Uri uri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, location)
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                .build();

        String response = mNetworkRequestProvider.startSynchronousGetRequest(uri.toString());
        WeatherForecastDTO responseData = parseWeatherData(response);

        // Problem parsing data.
        if(responseData == null) {
            return;
        }


        // We are not going to use the date data from the API, instead we will generate our own
        // sequential dates.
        DateTime dayOne = DateTime.now().withTimeAtStartOfDay();

        // Build the list of properly formed weather forecast models to save.
        List<WeatherForecast> forecasts = new ArrayList<>();
        int days = 0;
        for(WeatherForecastDTO.ForecastDTO dto : responseData.forecasts) {
            WeatherForecast forecast = new WeatherForecast();
            forecast.setDate(dayOne.plusDays(days).getMillis());
            forecast.setMinTemperature(dto.temperature.min);
            forecast.setMaxTemperature(dto.temperature.max);
            forecast.setWeatherId(dto.weather[0].id);
            forecast.setDescription(dto.weather[0].description);
            forecasts.add(forecast);
            days++;
        }

        // Save the forecasts.
        mWeatherProvider.saveWeatherForecasts(forecasts);

        // Request that the wearable device be updated.
        mWeatherProvider.refreshWearableDevice();
    }

    /**
     * Start the sync adapter. If it is the first run or if there is no user account
     * configured for the app then all the account / sync setup will be triggered.
     */
    public static void initializeSyncAdapter() {
        getSyncAccount(MainApp.getInjector().getApplicationContext());
    }

    /**
     * Force the sync adapter to perform an update - useful for situations where the
     * data might be considered to be invalid for some reason.
     */
    public static void syncImmediately() {
        Context context = MainApp.getInjector().getApplicationContext();

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    private WeatherForecastDTO parseWeatherData(@Nullable String data) {
        if(TextUtils.isEmpty(data)) {
            return null;
        }

        try {
            WeatherForecastDTO dto = new Gson().fromJson(data, WeatherForecastDTO.class);

            if(dto.validate()) {
                return dto;
            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();

            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    private static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account account = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // There is no account yet, so create one.
        if(accountManager.getPassword(account) == null) {
            // Attempt to add the account with no specific credentials.
            if(!accountManager.addAccountExplicitly(account, "", null)) {
                return null;
            }

            onAccountCreated(account, context);
        }

        return account;
    }

    private static void onAccountCreated(Account account, Context context) {
        SunshineSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(account, context.getString(R.string.content_authority), true);
        syncImmediately();
    }

    //region JSON response model
    /**
     * Simple model to hold parse and hold the JSON response from the weather API.
     */
    private static class WeatherForecastDTO {
        boolean validate() {
            if(forecasts == null || forecasts.length == 0) {
                return false;
            }

            for(ForecastDTO forecast : forecasts) {
                if(!forecast.validate()) {
                    return false;
                }
            }

            return true;
        }

        @SerializedName("list")
        ForecastDTO[] forecasts;

        static class ForecastDTO {
            boolean validate() {
                if(dateMillis == 0L) {
                    return false;
                }

                if(temperature == null) {
                    return false;
                }

                if(weather == null || weather.length == 0) {
                    return false;
                }

                return true;
            }

            @SerializedName("dt")
            long dateMillis;

            @SerializedName("temp")
            TemperatureDTO temperature;

            @SerializedName("weather")
            WeatherDTO[] weather;

            static class TemperatureDTO {
                @SerializedName("max")
                double max;

                @SerializedName("min")
                double min;
            }

            static class WeatherDTO {
                @SerializedName("id")
                int id;

                @SerializedName("description")
                String description;
            }
        }
    }
    //endregion
}