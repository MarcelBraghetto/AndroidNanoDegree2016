package io.github.marcelbraghetto.jokemachine.lib.android.api;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

import de.greenrobot.event.EventBus;
import io.github.marcelbraghetto.jokemachine.lib.android.models.Joke;
import io.github.marcelbraghetto.jokemachine.server.jokesApi.JokesApi;
import io.github.marcelbraghetto.jokemachine.server.jokesApi.model.JokeDTO;

/**
 * Created by Marcel Braghetto on 19/01/16.
 *
 * Async task used for fetching and translating jokes from the server instance.
 */
/* package */ class JokesApiTask extends AsyncTask<Void, Void, Joke> {
    // This is to give us a way to load for at least a minimum amount of time so our app has a
    // chance to cleanly show loading UI if the server response is lightning fast. Though we don't
    // really *need* this, it gives a nicer flow for the app implementation :)
    private static final long MINIMUM_TIME = 1000L;

    private String mOwnerId;
    private String mBaseUrl;

    /**
     * Create a new fetch joke task, which will proceed to fetch a joke from the server. On
     * completion, an event will be broadcast across the event bus with the result of the operation.
     */
    public JokesApiTask(@NonNull String ownerId, @NonNull String baseUrl) {
        mOwnerId = ownerId;
        mBaseUrl = baseUrl;
    }

    @Override
    protected Joke doInBackground(Void... params) {
        long startTime = System.currentTimeMillis();

        // Connect to our backend Api
        JokesApi api = new JokesApi.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                .setRootUrl(mBaseUrl).build();

        JokeDTO dto;

        try {
            // The backend will return us a DTO if successful.
            dto = api.getJoke().execute();
        } catch (IOException e) {
            // If a failure occurs, we just return null to indicate that the request was not
            // successful.
            return null;
        }

        // See if we need to pause for a little while to make the Api requests feel natural and not
        // too fast.
        long timeDelta = MINIMUM_TIME - (System.currentTimeMillis() - startTime);

        if(timeDelta > 0L) {
            try {
                Thread.sleep(timeDelta);
            } catch (InterruptedException e) {
                return null;
            }
        }

        // We return a new joke object that is constructed from the server DTO that was returned.
        return Joke.fromDTO(dto);
    }

    @Override
    protected void onPostExecute(Joke joke) {
        super.onPostExecute(joke);

        // If the joke was null there must have been an error, so post a new event bus event
        // indicating the failed request.
        if(joke == null) {
            EventBus.getDefault().post(JokesApiEvent.makeFailedEvent(mOwnerId));
            return;
        }

        // Otherwise, post a new event bus event indicating a successful joke request (including
        // the 'owner id' of who originally initiated this request).
        EventBus.getDefault().post(JokesApiEvent.makeSuccessEvent(mOwnerId, joke));
    }
}
