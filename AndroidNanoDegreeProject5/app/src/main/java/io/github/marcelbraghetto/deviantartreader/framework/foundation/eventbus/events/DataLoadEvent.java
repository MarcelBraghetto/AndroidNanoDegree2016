package io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.events;

import android.support.annotation.NonNull;

import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.contracts.EventBusEvent;

/**
 * Created by Marcel Braghetto on 9/03/16.
 *
 * Representation of a data load event which can have a given type depending on what occurred.
 */
public class DataLoadEvent implements EventBusEvent {
    public enum EventType {
        Loaded,
        Failed
    }

    private EventType mEventType;

    private DataLoadEvent(@NonNull EventType eventType) {
        mEventType = eventType;
    }

    @NonNull
    public static DataLoadEvent createLoadedEvent() {
        return new DataLoadEvent(EventType.Loaded);
    }

    @NonNull
    public static DataLoadEvent createFailedEvent() {
        return new DataLoadEvent(EventType.Failed);
    }

    @NonNull
    public EventType getEventType() {
        return mEventType;
    }
}
