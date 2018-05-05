package com.lilarcor.popularmovies.framework.foundation.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.lilarcor.popularmovies.framework.foundation.network.contracts.NetworkRequestProvider;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

/**
 * Created by Marcel Braghetto on 12/07/15.
 *
 * Default implementation of the networking provider, all based on the OkHttp library.
 *
 * In particular this implementation uses response caching in an effort to provide
 * pressure relief for both the data connections used in the app, and the battery life
 * by not requiring to initiate network requests for previously cached requests, up
 * to a maximum age specified when starting a new request.
 *
 * This also allows a great deal of the app to function offline for any data that has
 * previously been retrieved.
 *
 */
@Singleton
public class DefaultNetworkRequestProvider implements NetworkRequestProvider {
    private static final String HEADER_ACCEPT_JSON_KEY = "Accept";
    private static final String HEADER_ACCEPT_JSON_VALUE = "application/json;charset=utf-8";

    private static final String RESPONSE_CACHE_DIRECTORY = "okhttp_response_cache";
    private static final long RESPONSE_CACHE_SIZE_MEGABYTES = 5L * 1024L * 1024L; // 5 megabytes
    private static final long REQUEST_TIMEOUT_SECONDS = 10L;

    protected final Context mApplicationContext;
    private final OkHttpClient mOkHttpClient;

    public DefaultNetworkRequestProvider(@NonNull Context applicationContext) {
        mApplicationContext = applicationContext;

        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        mOkHttpClient.setWriteTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        // Configure a response cache for OkHttp so we get some free bandwidth / battery preservation.
        File responseCacheDirectory = new File(mApplicationContext.getCacheDir(), RESPONSE_CACHE_DIRECTORY);
        mOkHttpClient.setCache(new Cache(responseCacheDirectory, RESPONSE_CACHE_SIZE_MEGABYTES));
    }

    @Override
    public void startGetRequest(@NonNull final String requestTag, @NonNull String url, int maxCacheAgeInHours, final @NonNull RequestDelegate callbackDelegate) {
        // Build up a request object for OkHttp, using the provided max cache age from the caller.
        Request request = new Request.Builder()
                .url(url)
                .cacheControl(new CacheControl.Builder().maxAge(maxCacheAgeInHours, TimeUnit.HOURS).build())
                .tag(requestTag)
                .addHeader(HEADER_ACCEPT_JSON_KEY, HEADER_ACCEPT_JSON_VALUE)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                callbackDelegate.onRequestFailed();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                int statusCode = response.code();

                if (!response.isSuccessful()) {
                    callbackDelegate.onRequestFailed();
                    return;
                }

                String responseBody = "";

                if (response.body() != null) {
                    responseBody = response.body().string();
                    response.body().close();
                }

                callbackDelegate.onRequestComplete(statusCode, responseBody);
            }
        });
    }

    @Override
    public synchronized boolean clearResponseCache() {
        try {
            mOkHttpClient.getCache().evictAll();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}