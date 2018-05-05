package io.github.marcelbraghetto.jokemachine.lib.android.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.github.marcelbraghetto.jokemachine.lib.android.models.Joke;

/**
 * Created by Marcel Braghetto on 19/01/16.
 *
 * Event bus event class representing a Jokes Api result, indicating whether the given Api request
 * succeeded or not and who was the original owner of it.
 */
public class JokesApiEvent {

    /**
     * Type representing the success or failure of the related request.
     */
    public enum Result {
        Success,
        Failed
    }

    private String mOwnerId;
    private Joke mJoke;
    private Result mResult;

    private JokesApiEvent(@NonNull String ownerId, @NonNull Result result, @Nullable Joke joke) {
        mOwnerId = ownerId;
        mResult = result;
        mJoke = joke;
    }

    /**
     * Create a new event marked as being a failed Api request.
     * @param ownerId who originally owned the request.
     * @return new failure event instance.
     */
    public static JokesApiEvent makeFailedEvent(@NonNull String ownerId) {
        return new JokesApiEvent(ownerId, Result.Failed, null);
    }

    /**
     * Create a new event marked as being a successful Api request.
     * @param ownerId who originally owned the request.
     * @param joke object that was retrieved by the request.
     * @return new successful event instance.
     */
    public static JokesApiEvent makeSuccessEvent(@NonNull String ownerId, @NonNull Joke joke) {
        return new JokesApiEvent(ownerId, Result.Success, joke);
    }

    @NonNull
    public String getOwnerId() {
        return mOwnerId;
    }

    @NonNull
    public Result getResult() {
        return mResult;
    }

    @Nullable
    public Joke getJoke() {
        return mJoke;
    }
}
