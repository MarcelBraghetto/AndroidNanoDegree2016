package io.github.marcelbraghetto.sunshinewatch.weather.wear;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;

import io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherForecast;

/**
 * Created by Marcel Braghetto on 23/04/16.
 *
 * Intent service to perform the updating of the watch via Google API Client.
 */
public class RefreshWearableDeviceIntentService extends IntentService {
    public RefreshWearableDeviceIntentService() {
        super("RefreshWatch");
    }

    @NonNull
    public static Intent createServiceIntent(@NonNull Context context, @NonNull WeatherForecast forecast) {
        Intent intent = new Intent(context, RefreshWearableDeviceIntentService.class);
        forecast.putInto(intent);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        WeatherForecast weatherForecast = WeatherForecast.getFrom(intent);

        // Need a forecast to operate, not much point going further otherwise...
        if(weatherForecast == null) {
            throw new UnsupportedOperationException("RefreshWatchIntentService must include a parcelled WeatherForecast extra");
        }

        // We need an instance of the Google API Client to use Wear.
        GoogleApiClient client = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Wearable.API)
                .build();

        // Attempt to connect to the Google Api Client synchronously.
        client.blockingConnect(10, TimeUnit.SECONDS);

        // We failed to get access to the Google Apis, can't do much from the intent service...
        if(!client.isConnected()) {
            return;
        }

        // Build up the data to sync with the wear device.
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(WeatherForecast.DATA_MAP_KEY);
        weatherForecast.putInto(putDataMapRequest.getDataMap());
        putDataMapRequest.getDataMap().putLong(WeatherForecast.DATA_MAP_TIMESTAMP, System.currentTimeMillis());
        PutDataRequest request = putDataMapRequest.asPutDataRequest().setUrgent();

        // Save the data item exposed to the wear device and wait for it to process.
        Wearable.DataApi.putDataItem(client, request).await();

        // Kill off our Google client instance - we are done with it.
        client.disconnect();
    }
}
