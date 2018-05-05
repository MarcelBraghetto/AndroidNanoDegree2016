package com.lilarcor.popularmovies.features.moviescollection.logic.events.topratedmovies;

import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusEvent;

/**
 * Created by Marcel Braghetto on 15/07/15.
 *
 * This event is broadcast when the user has performed a pull to refresh operation
 * in the Top Rated Movies feature.
 */
public final class TopRatedMoviesSwipeToRefreshEvent implements EventBusEvent { }
