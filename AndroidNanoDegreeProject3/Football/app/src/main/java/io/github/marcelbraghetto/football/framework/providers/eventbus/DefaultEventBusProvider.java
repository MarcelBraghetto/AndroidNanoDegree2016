package io.github.marcelbraghetto.football.framework.providers.eventbus;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import de.greenrobot.event.EventBus;
import io.github.marcelbraghetto.football.framework.providers.eventbus.contracts.EventBusEvent;
import io.github.marcelbraghetto.football.framework.providers.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.football.framework.providers.eventbus.contracts.EventBusSubscriber;

/**
 * Created by Marcel Braghetto on 15/07/15.
 *
 * Default implementation of the event bus system,
 * based on the Green Robot EventBus library.
 *
 * The operations in the event bus implementation
 * always perform their actions on the main thread,
 * in case of background threads wanting to use the
 * event bus (which would cause issues, as Otto must
 * broadcast etc on the main thread).
 *
 */
@Singleton
public final class DefaultEventBusProvider implements EventBusProvider {
    @Override
    public void subscribe(@NonNull final EventBusSubscriber subscriber) {
        EventBus.getDefault().register(subscriber);
    }

    @Override
    public void unsubscribe(@NonNull final EventBusSubscriber subscriber) {
        EventBus.getDefault().unregister(subscriber);
    }

    @Override
    public void postEvent(@NonNull final EventBusEvent event) {
        EventBus.getDefault().post(event);
    }
}