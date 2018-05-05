package io.github.marcelbraghetto.dailydeviations.framework.foundation.strings.contracts;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Created by Marcel Braghetto on 02/12/15.
 *
 * Helper methods for interacting with application string resources.
 */
public interface StringsProvider {
    /**
     * Given a string resource id, return the string from the matching Android string resource.
     * @param resourceId to resolve and return.
     * @return resolved string for the given resource id.
     */
    @NonNull String getString(@StringRes int resourceId);

    /**
     * Given a string resource id, and a collection of arguments, return the string from the
     * matching Android string resource which is inflated with the given arguments.
     * @param resourceId to resolve.
     * @param args to inflate the string resource with.
     * @return inflated string for the given resource id.
     */
    @NonNull String getString(@StringRes int resourceId, @NonNull Object... args);

    /**
     * Generate a unique id string.
     * @return unique id string.
     */
    @NonNull String generateGUID();
}