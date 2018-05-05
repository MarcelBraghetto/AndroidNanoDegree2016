package io.github.marcelbraghetto.sunshinewatch;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import io.github.marcelbraghetto.sunshinewatch.sharedlib.weather.WeatherForecast;

/**
 * Created by Marcel Braghetto on 24/04/16.
 *
 * This listener service will detect data changes caused by the mobile app related to
 * refreshing the data for today's weather forecast.
 */
public class DataListenerService extends WearableListenerService {
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            String path = event.getDataItem().getUri().getPath();

            if (event.getType() == DataEvent.TYPE_CHANGED && path.equals(WeatherForecast.DATA_MAP_KEY)) {
                WeatherForecast weatherForecast = WeatherForecast.getFrom(DataMapItem.fromDataItem(event.getDataItem()).getDataMap());

                if(weatherForecast != null) {
                    Intent intent = new Intent(SunshineWatchFace.DATA_UPDATE_EVENT_ID);
                    weatherForecast.putInto(intent);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }
            }
        }
    }
}
