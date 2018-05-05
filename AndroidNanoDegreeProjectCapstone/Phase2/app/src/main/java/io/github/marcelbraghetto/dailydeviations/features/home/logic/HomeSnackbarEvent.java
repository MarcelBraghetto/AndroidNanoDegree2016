package io.github.marcelbraghetto.dailydeviations.features.home.logic;

import android.support.annotation.NonNull;

import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusEvent;

/**
 * Created by Marcel Braghetto on 15/06/16.
 *
 * Event to broadcast a desire to display a snack bar with the given message.
 */
public class HomeSnackbarEvent implements EventBusEvent {
    private String mMessage;

    public HomeSnackbarEvent(@NonNull String message) {
        mMessage = message;
    }

    @NonNull
    public String getMessage() {
        return mMessage;
    }
}
