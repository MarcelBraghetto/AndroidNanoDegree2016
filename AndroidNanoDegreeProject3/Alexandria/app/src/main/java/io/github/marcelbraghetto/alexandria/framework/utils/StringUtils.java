package io.github.marcelbraghetto.alexandria.framework.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * Simple string utility methods.
 */
public final class StringUtils {
    private StringUtils() { }

    @NonNull
    public static String emptyIfNull(@Nullable String input) {
        return input == null ? "" : input;
    }
}
