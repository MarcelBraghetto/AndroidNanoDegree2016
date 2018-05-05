package com.lilarcor.popularmovies.features.moviescollection.logic.events.topratedmovies;

import com.lilarcor.popularmovies.features.moviescollection.logic.events.common.BaseMoviesRequestEvent;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusEvent;

/**
 * Created by Marcel Braghetto on 1/08/15.
 *
 * Event that is broadcast when the top rated movies request fails.
 */
public class TopRatedMoviesRequestFailedEvent extends BaseMoviesRequestEvent implements EventBusEvent {
    public TopRatedMoviesRequestFailedEvent(int requestPage) {
        super(requestPage);
    }
}