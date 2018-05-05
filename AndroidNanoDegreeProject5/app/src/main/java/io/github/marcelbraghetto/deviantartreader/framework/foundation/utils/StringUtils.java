package io.github.marcelbraghetto.deviantartreader.framework.foundation.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * Simple string utility methods.
 */
public final class StringUtils {
    private StringUtils() { }

    /**
     * Verify if the given string is null or empty.
     * @param input string to check.
     * @return true if the input is null or empty.
     */
    public static boolean isEmpty(@Nullable String input) {
        return input == null || input.length() == 0;
    }

    /**
     * Check the input string for null and return an empty string if it is.
     * @param input string to check.
     * @return original string or empty string if it was null.
     */
    @NonNull
    public static String emptyIfNull(@Nullable String input) {
        return input == null ? "" : input;
    }
}
