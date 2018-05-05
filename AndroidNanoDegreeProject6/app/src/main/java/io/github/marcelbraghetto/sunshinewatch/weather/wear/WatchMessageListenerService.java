package io.github.marcelbraghetto.sunshinewatch.weather.wear;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import io.github.marcelbraghetto.sunshinewatch.weather.sync.SunshineSyncAdapter;

/**
 * Created by Marcel Braghetto on 25/04/16.
 *
 * Entry point to listen for messages sent by the Watch. This service is filtered by its
 * manifest entry to only respond to the path assigned to the data refresh string resource
 * which means that any messages that are received here already belonged to the correct path.
 */
public class WatchMessageListenerService extends WearableListenerService {
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        // We got a request to refresh the data from the wear device, so initiate
        // a forced sync through our sync adapter.
        SunshineSyncAdapter.syncImmediately();
    }
}
