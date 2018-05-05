package io.github.marcelbraghetto.jokemachine.lib.android.features.settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import io.github.marcelbraghetto.jokemachine.lib.android.R;

/**
 * Created by Marcel Braghetto on 19/01/16.
 *
 * Preferences activity to allow editing of app settings, to allow the user to change the base url
 * of the server to connect to.
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
