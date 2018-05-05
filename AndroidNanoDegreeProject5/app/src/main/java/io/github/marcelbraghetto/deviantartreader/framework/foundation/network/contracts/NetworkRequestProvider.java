package io.github.marcelbraghetto.deviantartreader.framework.foundation.network.contracts;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * Network request contract for calling server resources.
 */
public interface NetworkRequestProvider {
    /**
     * Determine whether the user has any kind of network connection.
     * @return true if there is a network connection.
     */
    boolean hasNetworkConnection();

    /**
     * Start a network request with the given configuration and callback delegate.
     * @param tag to associate with this request.
     * @param url to use in the request.
     * @param delegate to call back to.
     */
    void startGetRequest(@NonNull String tag, @NonNull String url, @NonNull NetworkRequestDelegate delegate);

    /**
     * Start a synchronous GET request which will run on the same thread it is invoked. Do NOT call
     * this method on the main thread. It is intended to be used in async tasks, threads or intent
     * services.
     * @param url to use in the request.
     * @return response text or null if the request failed.
     */
    @Nullable String startSynchronousGetRequest(@NonNull String url);

    /**
     * Attempt to cancel the given request tag.
     * @param tag to cancel the request for.
     */
    void cancelRequest(@Nullable String tag);
}
