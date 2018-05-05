package com.lilarcor.popularmovies.features.moviescollection.logic.events.topratedmovies;

import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusEvent;

/**
 * Created by Marcel Braghetto on 15/07/15.
 *
 * This event is broadcast when the user has scrolled to the end of the top rated
 * movies collection, this should initiate another request to fetch the next page
 * of results from the server.
 */
public final class TopRatedMoviesRequestMoreDataEvent implements EventBusEvent {
    private int mFromRequestPage;

    public TopRatedMoviesRequestMoreDataEvent(int fromRequestPage) {
        mFromRequestPage = fromRequestPage;
    }

    public int getFromRequestPage() {
        return mFromRequestPage;
    }
}
