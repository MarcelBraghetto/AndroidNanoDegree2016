package io.github.marcelbraghetto.deviantartreader.features.application.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import io.github.marcelbraghetto.deviantartreader.framework.artworks.service.ArtworksDataService;

/**
 * Created by Marcel Braghetto on 6/12/15.
 *
 * Whenever our repeating alarm has fired, we will kick off our artworks data service via the
 * artworks provider.
 */
public class AlarmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        startWakefulService(context, ArtworksDataService.createServiceIntent(context, ArtworksDataService.RefreshReason.AlarmManager));
    }
}
