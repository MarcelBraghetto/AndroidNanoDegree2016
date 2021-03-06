package io.github.marcelbraghetto.deviantartreader.framework.foundation.strings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.marcelbraghetto.deviantartreader.framework.foundation.strings.contracts.StringsProvider;

/**
 * Created by Marcel Braghetto on 02/12/15.
 *
 * Implementation of the app strings provider.
 */
@Singleton
public class DefaultStringsProvider implements StringsProvider {
    private Context mApplicationContext;

    @Inject
    public DefaultStringsProvider(@NonNull Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    @NonNull
    @Override public String getString(@StringRes int resourceId) {
        return mApplicationContext.getString(resourceId);
    }

    @NonNull
    @Override public String getString(@StringRes int resourceId, @NonNull Object... args) {
        return mApplicationContext.getString(resourceId, args);
    }

    @NonNull
    @Override
    public String generateGUID() {
        return UUID.randomUUID().toString();
    }
}
