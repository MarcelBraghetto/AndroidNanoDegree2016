package io.github.marcelbraghetto.alexandria.framework.providers.eventbus.contracts;

/**
 * Created by Marcel Braghetto on 02/12/15.
 *
 * Basic contract identifying an object as a
 * qualified event bus subscriber, forcing them
 * to implement the two required behaviours of an
 * event bus subscriber (subscribe/unsubscribe).
 *
 */
public interface EventBusSubscriber {
    /**
     * Implementation of this method should subscribe
     * the implementor to the framework event bus.
     */
    void subscribeToEventBus();

    /**
     * Implementation of this method should un subscribe
     * the implementor from the framework event bus.
     */
    void unsubscribeFromEventBus();
}