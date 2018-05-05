package io.github.marcelbraghetto.jokemachine.lib.android.api;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import io.github.marcelbraghetto.jokemachine.lib.android.MainApplication;
import io.github.marcelbraghetto.jokemachine.lib.android.R;

/**
 * Created by Marcel Braghetto on 19/01/16.
 *
 * Jokes Api manager to provide an easy way to fetch jokes from the server.
 */
public enum JokesApiManager {
    Instance;

    private static final String BASE_URL_EMULATOR = "10.0.2.2";
    private static final String BASE_URL_PREFIX = "http://";
    private static final String BASE_URL_SUFFIX = ":8080/_ah/api/";

    private final SharedPreferences mSharedPreferences;
    private final String mBaseUrlPreferenceKey;

    /**
     * The actual base Url to call is derived from the shared preferences for the app preferences
     * manager - or defaults to the typical url used by the Android emulator to reach the localhost
     * of the machine it is running on.
     */
    JokesApiManager() {
        mBaseUrlPreferenceKey = MainApplication.getContext().getString(R.string.pref_base_url_key);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext());
    }

    /**
     * Fetch a joke from the server and respond via the event bus with a Jokes Api Event populated
     * with the resulting state and data resulting from the response.
     * @param ownerId unique identifier of who 'owns' this request used for the event broadcast
     *                system. The owner id will be included in the completion event which is
     *                broadcast when the request completes (or fails) and can be used to determine
     *                if a particular request was originated by a given 'owner'.
     */
    public void fetchJoke(@NonNull String ownerId) {
        String url = BASE_URL_PREFIX + mSharedPreferences.getString(mBaseUrlPreferenceKey, BASE_URL_EMULATOR) + BASE_URL_SUFFIX;
        new JokesApiTask(ownerId, url).execute();
    }
}
