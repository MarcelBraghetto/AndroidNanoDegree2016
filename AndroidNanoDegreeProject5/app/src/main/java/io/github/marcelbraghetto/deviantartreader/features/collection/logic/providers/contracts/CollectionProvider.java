package io.github.marcelbraghetto.deviantartreader.features.collection.logic.providers.contracts;

import android.support.annotation.NonNull;

import io.github.marcelbraghetto.deviantartreader.features.collection.logic.CollectionMode;

/**
 * Created by Marcel Braghetto on 12/03/16.
 *
 * Provider to give access to persistent or shared state about the collection feature.
 */
public interface CollectionProvider {
    /**
     * Get the current collection mode - typically used to find out whether to display the
     * collection content in multi column or single column mode.
     * @return persisted current collection mode.
     */
    @NonNull CollectionMode getCollectionMode();

    /**
     * Assign the given collection mode to persistent storage.
     * @param collectionMode to store.
     */
    void setCollectionMode(@NonNull CollectionMode collectionMode);
}
