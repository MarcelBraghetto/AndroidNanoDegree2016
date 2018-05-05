package io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.contracts.EventBusEvent;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.contracts.EventBusSubscriber;

/**
 * Created by Marcel Braghetto on 15/07/15.
 *
 * Implementation of the event bus system, based on the Green Robot EventBus library.
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