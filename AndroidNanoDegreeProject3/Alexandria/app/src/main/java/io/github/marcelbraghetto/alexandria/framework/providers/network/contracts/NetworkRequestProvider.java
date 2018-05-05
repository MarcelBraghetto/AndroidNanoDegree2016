package io.github.marcelbraghetto.alexandria.framework.providers.network.contracts;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * Network request contract for calling server resources.
 */
public interface NetworkRequestProvider {
    /**
     * Determine whether the user has any kind of
     * network connection.
     * @return true if there is a network connection.
     */
    boolean hasNetworkConnection();

    /**
     * Start a network request with the given configuration
     * and callback delegate.
     * @param tag to associate with this request.
     * @param url to use in the request.
     * @param delegate to call back to.
     */
    void startGetRequest(@NonNull String tag, @NonNull String url, @NonNull NetworkRequestDelegate delegate);

    /**
     * Attempt to cancel the given request tag.
     * @param tag to cancel the request for.
     */
    void cancelRequest(@Nullable String tag);
}
