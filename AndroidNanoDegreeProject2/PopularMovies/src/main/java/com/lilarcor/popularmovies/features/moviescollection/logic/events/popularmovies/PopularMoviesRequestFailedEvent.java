package com.lilarcor.popularmovies.features.moviescollection.logic.events.popularmovies;

import com.lilarcor.popularmovies.features.moviescollection.logic.events.common.BaseMoviesRequestEvent;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusEvent;

/**
 * Created by Marcel Braghetto on 1/08/15.
 *
 * Event that is broadcast when the popular movies request fails.
 */
public final class PopularMoviesRequestFailedEvent extends BaseMoviesRequestEvent implements EventBusEvent {
    public PopularMoviesRequestFailedEvent(int requestPage) {
        super(requestPage);
    }
}