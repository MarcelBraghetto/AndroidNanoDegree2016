package io.github.marcelbraghetto.jokemachine.lib.android.utils;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.parceler.Parcels;

import io.github.marcelbraghetto.jokemachine.lib.android.models.Joke;

/**
 * Created by Marcel Braghetto on 19/01/16.
 *
 * Utility methods for populating intents via a type explicit proxy helper. This avoids common
 * issues of not remembering or incorrectly spelling the names of intent extras associated with
 * Android components.
 */
public final class IntentUtils {
    private IntentUtils() { }

    private static final String EXTRA_JOKE = "joke";

    /**
     * Put the given joke object into the given intent.
     * @param intent to put the joke into.
     * @param joke to put into intent - should be parcelable.
     */
    public static void putJoke(@NonNull Intent intent, @NonNull Joke joke) {
        intent.putExtra(EXTRA_JOKE, Parcels.wrap(joke));
    }

    /**
     * Extract the joke extra from the given intent.
     * @param intent to extract the joke from.
     * @return the joke object or null if it doesn't exist.
     */
    @Nullable
    public static Joke getJoke(@NonNull Intent intent) {
        return Parcels.unwrap(intent.getParcelableExtra(EXTRA_JOKE));
    }
}
