package io.github.marcelbraghetto.alexandria.framework.providers.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.github.marcelbraghetto.alexandria.framework.providers.network.contracts.NetworkRequestDelegate;
import io.github.marcelbraghetto.alexandria.framework.providers.network.contracts.NetworkRequestProvider;

/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * Implementation of the default network request provider
 * based on the OkHttp library.
 */
public class DefaultNetworkRequestProvider implements NetworkRequestProvider {
    private static final int DEFAULT_CACHE_AGE_HOURS = 1;
    private static final String HEADER_ACCEPT_JSON_KEY = "Accept";
    private static final String HEADER_ACCEPT_JSON_VALUE = "application/json;charset=utf-8";

    private static final String RESPONSE_CACHE_DIRECTORY = "okhttp_response_cache";
    private static final long RESPONSE_CACHE_SIZE_MEGABYTES = 5L * 1024L * 1024L; // 5 megabytes
    private static final long REQUEST_TIMEOUT_SECONDS = 15L;

    private final OkHttpClient mOkHttpClient;
    private final ConnectivityManager mConnectivityManager;

    @Inject
    public DefaultNetworkRequestProvider(@NonNull Context applicationContext) {
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        mOkHttpClient.setWriteTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        // Configure a response cache for OkHttp so we get some free bandwidth / battery preservation.
        File responseCacheDirectory = new File(applicationContext.getCacheDir(), RESPONSE_CACHE_DIRECTORY);
        mOkHttpClient.setCache(new Cache(responseCacheDirectory, RESPONSE_CACHE_SIZE_MEGABYTES));

        mConnectivityManager = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public boolean hasNetworkConnection() {
        NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void startGetRequest(@NonNull final String tag, @NonNull String url, @NonNull final NetworkRequestDelegate delegate) {
        // Build up a request object for OkHttp, using the provided max cache age from the caller.
        Request request = new Request.Builder()
                .url(url)
                .tag(tag)
                .cacheControl(new CacheControl.Builder().maxAge(DEFAULT_CACHE_AGE_HOURS, TimeUnit.HOURS).build())
                .addHeader(HEADER_ACCEPT_JSON_KEY, HEADER_ACCEPT_JSON_VALUE)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                if (e instanceof SocketException) {
                    return;
                }

                delegate.onRequestFailed(tag, NetworkRequestError.ConnectionProblem);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    delegate.onRequestFailed(tag, NetworkRequestError.ServerError);
                    return;
                }

                String responseBody = "";

                if (response.body() != null) {
                    responseBody = response.body().string();
                    response.body().close();
                }

                delegate.onRequestComplete(tag, responseBody);
            }
        });
    }

    @Override
    public void cancelRequest(@Nullable final String tag) {
        if(TextUtils.isEmpty(tag)) {
            return;
        }

        mOkHttpClient.getDispatcher().getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                mOkHttpClient.cancel(tag);
            }
        });
    }
}