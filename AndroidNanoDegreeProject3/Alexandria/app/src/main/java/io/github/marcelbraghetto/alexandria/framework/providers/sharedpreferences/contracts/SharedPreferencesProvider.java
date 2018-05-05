package io.github.marcelbraghetto.alexandria.framework.providers.sharedpreferences.contracts;

import android.support.annotation.NonNull;

/**
 * Created by Marcel Braghetto on 02/12/15.
 *
 * Abstracted access to read and write shared
 * preference values to persistent storage.
 */
public interface SharedPreferencesProvider {
    /**
     * Save the given string with the given key.
     * @param key to save the value for.
     * @param value to save.
     */
    void saveString(@NonNull String key, @NonNull String value);

    /**
     * Returns a string value or the given default value from the
     * shared preferences based on the given key.
     *
     * @param key The key mapping to the value.
     *
     * @return The value returned from the given key or the
     * default value if the key could not be found.
     */
    @NonNull String getString(@NonNull String key, @NonNull String defaultValue);

    /**
     * Delete the given key from storage if it exists.
     *
     * @param key for the data element to delete.
     */
    void delete(@NonNull String key);

    /**
     * Saves a boolean value in the shared preferences under the given key
     * @param key The key to store the value
     * @param value The value to store
     */
    void saveBoolean(@NonNull String key, boolean value);

    /**
     * Returns a boolean value or the default value provided from the shared preferences based on the given key
     *
     * @param key The key mapping to the value
     * @param defaultValue The default value to be returned if the value matching the key was not found
     *
     * @return The value returned from the given key or the default value if not found
     */
    boolean getBoolean(@NonNull String key, boolean defaultValue);
}