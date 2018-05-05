package io.github.marcelbraghetto.deviantartreader.framework.foundation.threading.contracts;

import android.support.annotation.NonNull;

/**
 * Created by Marcel Braghetto on 02/12/15.
 *
 * A way to make a runnable execute on the main thread. This provider is to give a quick and easy
 * way to do this without littering lots of duplicated boilerplate main thread code.
 *
 * It also allows for calling a runnable on the main thread if you are not running within an
 * Activity context etc.
 */
public interface ThreadUtilsProvider {
    /**
     * Run the given runnable on the main thread.
     * @param runnable to execute on the main thread.
     */
    void runOnMainThread(@NonNull Runnable runnable);
}