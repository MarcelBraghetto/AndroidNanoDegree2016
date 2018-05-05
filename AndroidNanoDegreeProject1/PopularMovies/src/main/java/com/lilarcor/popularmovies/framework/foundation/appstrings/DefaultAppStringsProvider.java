package com.lilarcor.popularmovies.framework.foundation.appstrings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.lilarcor.popularmovies.framework.foundation.appstrings.contracts.AppStringsProvider;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Marcel Braghetto on 29/07/15.
 *
 * Implementation of the app strings provider.
 */
@Singleton
public class DefaultAppStringsProvider implements AppStringsProvider {
    private Context mApplicationContext;

    @Inject
    public DefaultAppStringsProvider(@NonNull Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    @Override public String getString(@StringRes int resourceId) {
        return mApplicationContext.getString(resourceId);
    }

    @Override public String getString(@StringRes int resourceId, @NonNull Object... args) {
        return mApplicationContext.getString(resourceId, args);
    }
}
