package com.lilarcor.popularmovies.features.moviescollection.logic.events.common;

/**
 * Created by Marcel Braghetto on 1/08/15.
 *
 * Common base for movie request events broadcast
 * by the event bus system.
 */
public abstract class BaseMoviesRequestEvent {
    public static final int REQUEST_PAGE_NONE = Integer.MIN_VALUE;

    private final int mRequestPage;

    /**
     * Create a movie request event that encapsulates the
     * 'request page' of the data.
     *
     * @param requestPage of the given request.
     */
    public BaseMoviesRequestEvent(int requestPage) {
        mRequestPage = requestPage;
    }

    /**
     * Retrieve the 'request page' that caused
     * this event to be broadcast for.
     *
     * @return request page for the request.
     */
    public int getRequestPage() {
        return mRequestPage;
    }
}
