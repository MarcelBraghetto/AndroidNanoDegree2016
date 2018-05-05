package io.github.marcelbraghetto.deviantartreader.features.application.receivers;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import io.github.marcelbraghetto.deviantartreader.framework.artworks.service.ArtworksDataService;

/**
 * Created by Marcel Braghetto on 6/12/15.
 *
 * Broadcast receiver to listen for device boot up and changes in network connectivity in order
 * to fetch data and reschedule any repeating alarms etc.
 */
public class DataRefreshBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // This broadcast receiver may have been triggered by multiple broadcasts, including
        // the phone booting up as well as network connectivity changes.
        ArtworksDataService.RefreshReason reason = ArtworksDataService.RefreshReason.ForceRefresh;

        // Set the intent reason appropriately if needed.
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            reason = ArtworksDataService.RefreshReason.NetworkChange;
        }

        startWakefulService(context, ArtworksDataService.createServiceIntent(context, reason));
    }
}