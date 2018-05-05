package io.github.marcelbraghetto.dailydeviations.features.collection.logic;

import android.os.Bundle;
import android.support.annotation.NonNull;

import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.CollectionFilterMode;

/**
 * Created by Marcel Braghetto on 24/02/16.
 */
public final class CollectionArguments {
    private static final String KEY_CATEGORY = "Category";

    private CollectionFilterMode mFilterMode;

    public CollectionArguments(@NonNull CollectionFilterMode filterMode) {
        mFilterMode = filterMode;
    }

    public CollectionArguments(@NonNull Bundle bundle) {
        mFilterMode = (CollectionFilterMode) bundle.getSerializable(KEY_CATEGORY);

        // Basic validation
        if(mFilterMode == null) {
            throw new UnsupportedOperationException("The bundle passed into CollectionExtras constructor must have been originally created via a CollectionExtras instance.");
        }
    }

    @NonNull
    public CollectionFilterMode getFilterMode() {
        return mFilterMode;
    }

    @NonNull
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_CATEGORY, mFilterMode);
        return bundle;
    }
}
