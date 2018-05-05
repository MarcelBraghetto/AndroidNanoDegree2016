package io.github.marcelbraghetto.deviantartreader.framework.artworks.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import io.github.marcelbraghetto.deviantartreader.R;
import io.github.marcelbraghetto.deviantartreader.features.application.MainApp;
import io.github.marcelbraghetto.deviantartreader.features.application.receivers.AlarmBroadcastReceiver;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksParser;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.contracts.ArtworksProvider;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.events.DataLoadEvent;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.network.contracts.NetworkRequestProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.sharedpreferences.contracts.SharedPreferencesProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.strings.contracts.StringsProvider;

/**
 * Created by Marcel Braghetto on 24/02/16.
 *
 * Intent service to fetch, parse and populate artworks from the Deviant Art website RSS feed.
 */
public class ArtworksDataService extends IntentService {
    /**
     * Type to describe why this service was instantiated so it can alter its behaviour accordingly.
     */
    public enum RefreshReason {
        ForceRefresh,
        AlarmManager,
        NetworkChange
    }

    private static final String EXTRA_REASON = "ArtworksDataService.Reason";
    private static final String PREF_LAST_REFRESH_TIME = "ArtworksDataService.LastRefreshTime";
    private static final long REFRESH_INTERVAL = 4L * 60L * 60L * 1000L; // 4 hours

    @Inject SharedPreferencesProvider mSharedPreferencesProvider;
    @Inject NetworkRequestProvider mNetworkRequestProvider;
    @Inject StringsProvider mStringsProvider;
    @Inject EventBusProvider mEventBusProvider;
    @Inject ArtworksProvider mArtworksProvider;

    /**
     * Construct an intent to launch the service with a given refresh reason.
     * @param reason for launching the intent service.
     * @return service intent that can be started to begin the intent service.
     */
    @NonNull
    public static Intent createServiceIntent(@NonNull Context context, @NonNull RefreshReason reason) {
        Intent intent = new Intent(context, ArtworksDataService.class);
        intent.putExtra(EXTRA_REASON, reason);
        return intent;
    }

    public ArtworksDataService() {
        super("ArtworksDataService");
        MainApp.getDagger().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RefreshReason reason = (RefreshReason) intent.getSerializableExtra(EXTRA_REASON);

        if(reason == null) {
            throw new UnsupportedOperationException("ArtworksDataService must include a refresh reason in the intent extras.");
        }

        long now = System.currentTimeMillis();
        boolean needsRefresh = true;

        // If the service was invoked due to a network change, only execute it if the data is stale.
        if(reason == RefreshReason.NetworkChange) {
            // If we've never saved a last refresh time, or the interval since the last refresh
            // is greater than our threshold, we should perform a refresh - if there is a network
            // connection.
            long lastRefreshTime = mSharedPreferencesProvider.getLong(PREF_LAST_REFRESH_TIME, 0);
            needsRefresh = mNetworkRequestProvider.hasNetworkConnection() && (lastRefreshTime == 0 || now - lastRefreshTime >= REFRESH_INTERVAL);
        }

        if(needsRefresh) {
            // If we need to load data - get to it!
            loadArtworksData(mStringsProvider.getString(R.string.collection_url));
            mSharedPreferencesProvider.saveLong(PREF_LAST_REFRESH_TIME, now);
            scheduleAlarm();
        }

        AlarmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void scheduleAlarm() {
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HALF_DAY, AlarmManager.INTERVAL_HALF_DAY, pendingIntent);
    }

    private void loadArtworksData(String url) {
        String response = mNetworkRequestProvider.startSynchronousGetRequest(url);

        // No response, there was a problem loading the data...
        if(response == null) {
            mEventBusProvider.postEvent(DataLoadEvent.createFailedEvent());
            return;
        }

        List<Artwork> artworks = ArtworksParser.parse(response);

        // Couldn't parse the data, there was a problem loading the data...
        if(artworks == null) {
            mEventBusProvider.postEvent(DataLoadEvent.createFailedEvent());
            return;
        }

        mArtworksProvider.saveArtworks(artworks);
        mEventBusProvider.postEvent(DataLoadEvent.createLoadedEvent());
    }
}
