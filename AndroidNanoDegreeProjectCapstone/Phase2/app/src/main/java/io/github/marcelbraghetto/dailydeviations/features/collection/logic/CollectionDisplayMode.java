package io.github.marcelbraghetto.dailydeviations.features.collection.logic;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Marcel Braghetto on 12/03/16.
 *
 * Declaration of a collection mode type to help with tracking the 'mode' being displayed.
 */

public enum CollectionDisplayMode {
    MultiColumn,
    SingleColumn;

    /**
     * Attempt to convert the given string into the related enum type. If the conversion fails
     * then a default value of MultiColumn is returned.
     * @param value representing a CollectionMode type as a string.
     * @return the converted enum type, or the default value if conversion fails.
     */
    @NonNull
    public static CollectionDisplayMode fromString(@Nullable String value) {
        try {
            return valueOf(value);
        } catch (Exception ex) {
            return MultiColumn;
        }
    }
}