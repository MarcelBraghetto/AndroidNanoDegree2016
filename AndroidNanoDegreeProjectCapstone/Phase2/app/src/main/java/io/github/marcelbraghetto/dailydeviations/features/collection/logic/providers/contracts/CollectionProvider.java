package io.github.marcelbraghetto.dailydeviations.features.collection.logic.providers.contracts;

import android.support.annotation.NonNull;

import io.github.marcelbraghetto.dailydeviations.features.collection.logic.CollectionDisplayMode;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.CollectionFilterMode;

/**
 * Created by Marcel Braghetto on 12/03/16.
 *
 * Provider to give access to persistent or shared state about the collection feature.
 */
public interface CollectionProvider {
    /**
     * Get the current collection display mode - typically used to find out whether to display
     * the collection content in multi column or single column mode.
     * @return persisted current collection display mode.
     */
    @NonNull CollectionDisplayMode getCollectionDisplayMode();

    /**
     * Assign the given collection display mode to persistent storage.
     * @param displayMode to store.
     */
    void setCollectionDisplayMode(@NonNull CollectionDisplayMode displayMode);

    /**
     * Get the current collection filter mode - typically used to find out whether to display
     * the favourites or all collections.
     * @return persisted current collection filter mode.
     */
    @NonNull CollectionFilterMode getCollectionFilterMode();

    /**
     * Assign the given collection filter mode to persistent storage.
     * @param filterMode to store.
     */
    void setCollectionFilterMode(@NonNull CollectionFilterMode filterMode);
}
