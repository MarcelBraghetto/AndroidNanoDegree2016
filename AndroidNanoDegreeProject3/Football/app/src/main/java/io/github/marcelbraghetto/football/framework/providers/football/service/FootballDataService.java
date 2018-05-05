package io.github.marcelbraghetto.football.framework.providers.football.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.github.marcelbraghetto.football.features.widget.FootballWidgetProvider;
import io.github.marcelbraghetto.football.framework.application.MainApp;
import io.github.marcelbraghetto.football.framework.application.receivers.AlarmBroadcastReceiver;
import io.github.marcelbraghetto.football.framework.providers.football.models.FootballGame;
import io.github.marcelbraghetto.football.framework.providers.network.contracts.NetworkRequestProvider;

/**
 * Created by Marcel Braghetto on 6/12/15.
 *
 * Intent service that can be used from different
 * components to refresh football data. This service
 * has a relationship with the AlarmBroadcastReceiver
 * wherein it will close the wake lock that is opened
 * by the receiver.
 */
public class FootballDataService extends IntentService {
    private static final String TAG = FootballDataService.class.getSimpleName();

    private static final String TIMEFRAME_PREVIOUS = "p3";
    private static final String TIMEFRAME_NEXT = "n3";

    @Inject
    NetworkRequestProvider mNetworkRequestProvider;

    public FootballDataService() {
        super("FootballIntentService");
        MainApp.getDagger().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        loadFootballData(TIMEFRAME_PREVIOUS);
        loadFootballData(TIMEFRAME_NEXT);
        scheduleAlarm();
        AlarmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void scheduleAlarm() {
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HALF_DAY, AlarmManager.INTERVAL_HALF_DAY, pendingIntent);
    }

    private void loadFootballData(String timeFrame) {
        String url = Uri.parse("http://api.football-data.org/v1/fixtures/")
                .buildUpon()
                .appendQueryParameter("timeFrame", timeFrame)
                .build()
                .toString();

        Log.i(TAG, "Loading data: " + url);

        String response = mNetworkRequestProvider.startSynchronousGetRequest(url);

        // No response, there was a problem loading the data...
        if(response == null) {
            Log.i(TAG, "Loading data failed!");
            return;
        }

        List<FootballGame> games = parseGameData(response);

        // Couldn't parse the data, there was a problem loading the data...
        if(games == null) {
            Log.i(TAG, "Parsing data failed!");
            return;
        }

        Log.i(TAG, "Parsing data success! Saving data to content provider...");
        MainApp.getDagger().getFootballProvider().saveGames(games);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            Log.i(TAG, "Attempting to refresh any widgets...");
            try {
                Intent intent = new Intent();
                intent.setAction(FootballWidgetProvider.ACTION_UPDATE);
                getApplicationContext().sendBroadcast(intent);
            } catch (Exception e) {
                // Problem sending broadcast intent.
            }
        }
    }

    /**
     * Parse the raw response string into something we
     * can extract meaningful data from.
     * @param data to parse.
     * @return list of football games if they parsed correctly.
     */
    private List<FootballGame> parseGameData(String data) {
        try {
            FootballDataDTO dto = new Gson().fromJson(data, FootballDataDTO.class);
            List<FootballGame> result = new ArrayList<>();

            for(FootballDataDTO.FixtureDTO fixture : dto.fixtures) {
                result.add(fixture.exportAsFootballGame());
            }

            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
