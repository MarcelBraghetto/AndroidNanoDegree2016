package io.github.marcelbraghetto.deviantartreader.features.collection.logic.providers;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.github.marcelbraghetto.deviantartreader.features.collection.logic.CollectionMode;
import io.github.marcelbraghetto.deviantartreader.features.collection.logic.providers.contracts.CollectionProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.sharedpreferences.contracts.SharedPreferencesProvider;

/**
 * Created by Marcel Braghetto on 12/03/16.
 *
 * Implementation of the collection provider which can interact with persistent storage.
 */
public class DefaultCollectionProvider implements CollectionProvider {
    private static final String PREF_COLLECTION_MODE = "Collection.CollectionMode";

    private final SharedPreferencesProvider mSharedPreferencesProvider;

    @Inject
    public DefaultCollectionProvider(@NonNull SharedPreferencesProvider sharedPreferencesProvider) {
        mSharedPreferencesProvider = sharedPreferencesProvider;
    }

    @Override
    @NonNull
    public CollectionMode getCollectionMode() {
        return CollectionMode.fromString(mSharedPreferencesProvider.getString(PREF_COLLECTION_MODE, ""));
    }

    @Override
    public void setCollectionMode(@NonNull CollectionMode collectionMode) {
        mSharedPreferencesProvider.saveString(PREF_COLLECTION_MODE, collectionMode.toString());
    }
}
