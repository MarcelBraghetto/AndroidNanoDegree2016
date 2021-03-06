package com.lilarcor.popularmovies.framework.foundation.appstrings.contracts;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Created by Marcel Braghetto on 29/07/15.
 *
 * Helper methods for interacting with application
 * string resources.
 */
public interface AppStringsProvider {
    /**
     * Given a string resource id, return the string from the
     * matching Android string resource.
     *
     * @param resourceId to resolve and return.
     *
     * @return resolved string for the given resource id.
     */
    String getString(@StringRes int resourceId);

    /**
     * Given a string resource id, and a collection of arguments,
     * return the string from the matching Android string resource
     * which is inflated with the given arguments.
     *
     * @param resourceId to resolve.
     * @param args to inflate the string resource with.
     *
     * @return inflated string for the given resource id.
     */
    String getString(@StringRes int resourceId, @NonNull Object ... args);
}
