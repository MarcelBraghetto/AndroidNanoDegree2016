package io.github.marcelbraghetto.sunshinewatch.framework.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.github.marcelbraghetto.sunshinewatch.framework.network.contracts.NetworkRequestProvider;

/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * Implementation of the default network request provider based on the OkHttp library.
 */
public class DefaultNetworkRequestProvider implements NetworkRequestProvider {
    private static final long REQUEST_TIMEOUT_SECONDS = 15L;
    private final OkHttpClient mOkHttpClient;

    @Inject
    public DefaultNetworkRequestProvider() {
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        mOkHttpClient.setWriteTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    @Nullable
    @Override
    public String startSynchronousGetRequest(@NonNull String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = mOkHttpClient.newCall(request).execute();

            if(!response.isSuccessful()) {
                return null;
            }

            String responseBody = "";

            if (response.body() != null) {
                responseBody = response.body().string();
                response.body().close();
            }

            return responseBody;
        } catch (IOException e) {
            return null;
        }
    }
}