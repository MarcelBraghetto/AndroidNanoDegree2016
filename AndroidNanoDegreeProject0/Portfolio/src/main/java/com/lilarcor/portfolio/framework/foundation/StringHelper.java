package com.lilarcor.portfolio.framework.foundation;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Created by Marcel Braghetto on 11/07/15.
 */
public class StringHelper {
    /**
     * Prevent instantiation.
     */
    private StringHelper() { }

    /**
     * Retrieve the string with the given resource id.
     *
     * @param resourceId
     *
     * @return string representation of the given resource id.
     */
    public static @NonNull String getString(@StringRes int resourceId) {
        return MainApplication.getContext().getString(resourceId);
    }

    /**
     * Retrieve the string with the given resource id and trailing
     * arguments to pass in when it is resolved.
     *
     * @param resourceId
     *
     * @return string representation of the given resource id and arguments.
     */
    public static @NonNull String getString(@StringRes int resourceId, @NonNull Object ... args) {
        return MainApplication.getContext().getString(resourceId, args);
    }
}
