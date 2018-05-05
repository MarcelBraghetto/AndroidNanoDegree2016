package io.github.marcelbraghetto.deviantartreader.features.collection.logic;

import android.os.Bundle;
import android.support.annotation.NonNull;

import io.github.marcelbraghetto.deviantartreader.framework.artworks.models.ArtworksCategory;

/**
 * Created by Marcel Braghetto on 24/02/16.
 */
public final class CollectionArguments {
    private static final String KEY_CATEGORY = "Category";

    private ArtworksCategory mCategory;

    public CollectionArguments(@NonNull ArtworksCategory category) {
        mCategory = category;
    }

    public CollectionArguments(@NonNull Bundle bundle) {
        mCategory = (ArtworksCategory) bundle.getSerializable(KEY_CATEGORY);

        // Basic validation
        if(mCategory == null) {
            throw new UnsupportedOperationException("The bundle passed into CollectionExtras constructor must have been originally created via a CollectionExtras instance.");
        }
    }

    @NonNull
    public ArtworksCategory getCategory() {
        return mCategory;
    }

    @NonNull
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_CATEGORY, mCategory);
        return bundle;
    }
}
