package com.lilarcor.popularmovies.framework.foundation.eventbus;

import android.support.annotation.NonNull;

import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusEvent;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusProvider;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusSubscriber;
import com.lilarcor.popularmovies.framework.foundation.threadutils.contracts.ThreadUtilsProvider;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

/**
 * Created by Marcel Braghetto on 15/07/15.
 *
 * Default implementation of the event bus system,
 * based on the Otto library.
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
    private final Bus mEventBus;
    private final ThreadUtilsProvider mThreadUtilsProvider;

    public DefaultEventBusProvider(@NonNull ThreadUtilsProvider threadUtilsProvider) {
        mThreadUtilsProvider = threadUtilsProvider;
        mEventBus = new Bus();
    }

    @Override
    public void subscribe(@NonNull final EventBusSubscriber subscriber) {
        mThreadUtilsProvider.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mEventBus.register(subscriber);
            }
        });
    }

    @Override
    public void unsubscribe(@NonNull final EventBusSubscriber subscriber) {
        mThreadUtilsProvider.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mEventBus.unregister(subscriber);
            }
        });
    }

    @Override
    public void postEvent(@NonNull final EventBusEvent event) {
        mThreadUtilsProvider.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                mEventBus.post(event);
            }
        });
    }
}
