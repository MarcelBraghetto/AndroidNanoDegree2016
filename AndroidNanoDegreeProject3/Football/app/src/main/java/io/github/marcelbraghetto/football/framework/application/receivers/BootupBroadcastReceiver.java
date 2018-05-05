package io.github.marcelbraghetto.football.framework.application.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.github.marcelbraghetto.football.framework.providers.football.service.FootballDataService;

/**
 * Created by Marcel Braghetto on 6/12/15.
 *
 * Broadcast receiver to listen for device
 * boot up in order to reschedule any
 * repeating alarms etc.
 */
public class BootupBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, FootballDataService.class));
    }
}