package io.github.marcelbraghetto.football.framework.application.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import io.github.marcelbraghetto.football.framework.providers.football.service.FootballDataService;

/**
 * Created by Marcel Braghetto on 6/12/15.
 *
 * Whenever our repeating alarm has fired, we will kick
 * off our football data service via the football provider.
 */
public class AlarmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        startWakefulService(context, new Intent(context, FootballDataService.class));
    }
}
