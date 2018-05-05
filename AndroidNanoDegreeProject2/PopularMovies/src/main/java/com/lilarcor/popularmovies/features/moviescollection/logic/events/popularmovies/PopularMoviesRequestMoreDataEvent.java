package com.lilarcor.popularmovies.features.moviescollection.logic.events.popularmovies;

import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusEvent;

/**
 * Created by Marcel Braghetto on 15/07/15.
 *
 * This event is broadcast when the user has scrolled to the bottom of
 * the popular movies collection, which should typically trigger the
 * next page of results to be fetched from the server.
 */
public final class PopularMoviesRequestMoreDataEvent implements EventBusEvent {
    private int mFromRequestPage;

    public PopularMoviesRequestMoreDataEvent(int fromRequestPage) {
        mFromRequestPage = fromRequestPage;
    }

    public int getFromRequestPage() {
        return mFromRequestPage;
    }
}
