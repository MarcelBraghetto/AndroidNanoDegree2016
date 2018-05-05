package io.github.marcelbraghetto.deviantartreader.framework.foundation.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.github.marcelbraghetto.deviantartreader.framework.foundation.network.contracts.NetworkRequestDelegate;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.network.contracts.NetworkRequestProvider;


/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * Implementation of the default network request provider based on the OkHttp library.
 */
public class DefaultNetworkRequestProvider implements NetworkRequestProvider {
    private static final long REQUEST_TIMEOUT_SECONDS = 15L;
    private final OkHttpClient mOkHttpClient;
    private final ConnectivityManager mConnectivityManager;

    @Inject
    public DefaultNetworkRequestProvider(@NonNull Context applicationContext) {
        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        mOkHttpClient.setWriteTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);

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