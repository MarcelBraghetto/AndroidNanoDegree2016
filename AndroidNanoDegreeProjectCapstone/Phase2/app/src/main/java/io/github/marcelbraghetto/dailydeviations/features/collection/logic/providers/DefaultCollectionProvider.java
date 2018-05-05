package io.github.marcelbraghetto.dailydeviations.features.collection.logic.providers;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.github.marcelbraghetto.dailydeviations.features.collection.logic.CollectionDisplayMode;
import io.github.marcelbraghetto.dailydeviations.features.collection.logic.providers.contracts.CollectionProvider;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.CollectionFilterMode;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.sharedpreferences.contracts.SharedPreferencesProvider;

/**
 * Created by Marcel Braghetto on 12/03/16.
 *
 * Implementation of the collection provider which can interact with persistent storage.
 */
public class DefaultCollectionProvider implements CollectionProvider {
    private static final String PREF_COLLECTION_DISPLAY_MODE = "Collection.CollectionDisplayMode";
    private static final String PREF_COLLECTION_FILTER_MODE = "Collection.CollectionFilterMode";

    private final SharedPreferencesProvider mSharedPreferencesProvider;

    @Inject
    public DefaultCollectionProvider(@NonNull SharedPreferencesProvider sharedPreferencesProvider) {
        mSharedPreferencesProvider = sharedPreferencesProvider;
    }

    @Override
    @NonNull
    public CollectionDisplayMode getCollectionDisplayMode() {
        return CollectionDisplayMode.fromString(mSharedPreferencesProvider.getString(PREF_COLLECTION_DISPLAY_MODE, ""));
    }

    @Override
    public void setCollectionDisplayMode(@NonNull CollectionDisplayMode displayMode) {
        mSharedPreferencesProvider.saveString(PREF_COLLECTION_DISPLAY_MODE, displayMode.toString());
    }

    @Override
    public void setCollectionFilterMode(@NonNull CollectionFilterMode filterMode) {
        mSharedPreferencesProvider.saveString(PREF_COLLECTION_FILTER_MODE, filterMode.toString());
    }

    @NonNull
    @Override
    public CollectionFilterMode getCollectionFilterMode() {
        return CollectionFilterMode.fromString(mSharedPreferencesProvider.getString(PREF_COLLECTION_FILTER_MODE, ""));
    }
}
