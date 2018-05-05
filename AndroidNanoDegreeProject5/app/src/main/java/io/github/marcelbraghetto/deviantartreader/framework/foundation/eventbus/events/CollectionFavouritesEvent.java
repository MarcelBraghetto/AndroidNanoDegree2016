package io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.events;

import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.contracts.EventBusEvent;

/**
 * Created by Marcel Braghetto on 12/03/16.
 *
 * Event to broadcast when the user interacts with the favourites toggle to switch between
 * viewing their favourites and the full collection.
 */
public class CollectionFavouritesEvent implements EventBusEvent {
    private boolean mIsOn;

    public CollectionFavouritesEvent(boolean isOn) {
        mIsOn = isOn;
    }

    public boolean isOn() {
        return mIsOn;
    }
}
