package io.github.marcelbraghetto.dailydeviations.framework.foundation.sharedpreferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.github.marcelbraghetto.dailydeviations.framework.foundation.sharedpreferences.contracts.SharedPreferencesProvider;

/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * Implementation of the shared preferences provider used for storing persistent key/value pairs
 * of data.
 */
@SuppressLint("CommitPrefEdits")
public class DefaultSharedPreferencesProvider implements SharedPreferencesProvider {
    private static final String PREFS_NAME = "shared_preferences_storage";
    private final SharedPreferences mSharedPreferences;

    @Inject
    public DefaultSharedPreferencesProvider(@NonNull Context applicationContext) {
        mSharedPreferences = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void saveString(@NonNull String key, @NonNull String value) {
        mSharedPreferences.edit().putString(key, value).commit();
    }

    @NonNull
    @Override
    public String getString(@NonNull String key, @NonNull String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    @Override
    public void delete(@NonNull String key) {
        mSharedPreferences.edit().remove(key).apply();
    }

    @Override
    public void saveBoolean(@NonNull String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).commit();
    }

    @Override
    public boolean getBoolean(@NonNull String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    @Override
    public void saveInteger(@NonNull String key, int value) {
        mSharedPreferences.edit().putInt(key, value).commit();
    }

    @Override
    public int getInteger(@NonNull String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    @Override
    public void saveLong(@NonNull String key, long value) {
        mSharedPreferences.edit().putLong(key, value).commit();
    }

    @Override
    public long getLong(@NonNull String key, long defaultValue) {
        return mSharedPreferences.getLong(key, defaultValue);
    }
}