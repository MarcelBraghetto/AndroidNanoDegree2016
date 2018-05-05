package io.github.marcelbraghetto.football.framework.providers.network.contracts;

import android.support.annotation.NonNull;

import io.github.marcelbraghetto.football.framework.providers.network.NetworkRequestError;

/**
 * Created by Marcel Braghetto on 02/12/15.
 *
 * Request callback delegate to return successful or failed
 * network request attempts. Is used for each request attempt.
 *
 * IMPORTANT NOTE: Any callback delegate methods are NOT executed
 * on the main thread. It is the caller's responsibility to make
 * any code after the callback invocation run on the main thread if
 * required.
 *
 * This design decision was deliberate - because it will allow the
 * receiver of the callback to continue to run in the worker thread
 * to complete any parsing of the response data etc, and therefore
 * get some async benefit for free.
 */
public interface NetworkRequestDelegate {
    /**
     * If the request reports to have completed successfully, this method
     * will be called with the resulting raw string response.
     * @param requestTag of the request.
     * @param response the raw string response text received.
     */
    void onRequestComplete(@NonNull String requestTag, @NonNull String response);

    /**
     * If the request reports to have failed, this method will be called.
     * @param requestTag of the request.
     * @param error type that occurred.
     */
    void onRequestFailed(@NonNull String requestTag, @NonNull NetworkRequestError error);
}