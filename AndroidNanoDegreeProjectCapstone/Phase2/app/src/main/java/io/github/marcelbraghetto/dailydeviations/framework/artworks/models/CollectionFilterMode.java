package io.github.marcelbraghetto.dailydeviations.framework.artworks.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Marcel Braghetto on 24/02/16.
 *
 * Simple type to describe a filter of artworks to display.
 */
public enum CollectionFilterMode {
    All,
    Favourites;

    /**
     * Attempt to convert the given string into the related enum type. If the conversion fails
     * then a default value of All is returned.
     * @param value representing a filter type as a string.
     * @return the converted enum type, or the default value if conversion fails.
     */
    @NonNull
    public static CollectionFilterMode fromString(@Nullable String value) {
        try {
            return valueOf(value);
        } catch (Exception ex) {
            return All;
        }
    }

}