package com.lilarcor.popularmovies.framework.foundation.network.contracts;

import android.support.annotation.NonNull;

/**
 * Created by Marcel Braghetto on 12/07/15.
 *
 * This contract provides methods to do basic network request
 * operations, and offers response caching configured on a per
 * request basis.
 *
 */
public interface NetworkRequestProvider {
    /**
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
    interface RequestDelegate {
        /**
         * If the request reports to have completed successfully, this method
         * will be called with the resulting status code and raw string response.
         *
         * @param statusCode of the network operation.
         * @param response the raw string response text received.
         */
        void onRequestComplete(int statusCode, @NonNull String response);

        /**
         * If the request reports to have failed, this method will be called.
         */
        void onRequestFailed();
    }

    /**
     * Begin a new GET request with the given tag, url and callback
     * delegate.
     *
     * @param requestTag a unique tag for the request, to allow it to be identified for operations such as cancelling.
     * @param url the full http url to request.
     * @param maxCacheAgeInHours the value of this argument will determine how old the given request can be without attempting to connect
     *                           to the server. If the request has been cached previously and is younger than the max age, then it will
     *                           be returned instead of the full network request. For requests that should always go to the server, this
     *                           argument can be specified as 0 which will cause the next request to immediately be too old.
     * @param callbackDelegate the request callback delegate for when the request completes.
     */
    void startGetRequest(@NonNull String requestTag, @NonNull String url, int maxCacheAgeInHours, @NonNull RequestDelegate callbackDelegate);

    /**
     * Clear any cached responses so requests will resolve to the server again.
     *
     * @return true if the cache was successfully cleared.
     */
    boolean clearResponseCache();
}
