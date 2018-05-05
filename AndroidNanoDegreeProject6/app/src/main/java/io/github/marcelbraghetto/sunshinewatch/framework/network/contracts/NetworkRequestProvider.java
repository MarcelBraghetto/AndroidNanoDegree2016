package io.github.marcelbraghetto.sunshinewatch.framework.network.contracts;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * Network request contract for calling server resources.
 */
public interface NetworkRequestProvider {
    /**
     * Start a synchronous GET request which will run on the same thread it is invoked. Do NOT call
     * this method on the main thread. It is intended to be used in async tasks, threads or intent
     * services.
     * @param url to use in the request.
     * @return response text or null if the request failed.
     */
    @Nullable String startSynchronousGetRequest(@NonNull String url);
}
