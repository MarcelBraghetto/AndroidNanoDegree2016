package com.lilarcor.popularmovies.features.home.logic.events;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.lilarcor.popularmovies.features.moviedetails.ui.MovieDetailsFragment;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusEvent;

/**
 * Created by Marcel Braghetto on 2/08/15.
 *
 * When a movie is selected from a collection view on a tablet, we
 * want to display its details in a fragment for the 'split view'
 * effect, rather than open a new activity.
 *
 * The collection view fragments are decoupled from the home activity
 * so cannot directly show the details, so this event will be received
 * by the home controller and the enclosed fragment displayed in an
 * appropriate way for a tablet.
 */
public final class ShowTabletMovieDetailsEvent implements EventBusEvent {
    private final int mMovieId;

    public ShowTabletMovieDetailsEvent(int movieId) {
        mMovieId = movieId;
    }

    @NonNull
    public Fragment createFragment() {
        return MovieDetailsFragment.newInstance(mMovieId);
    }
}
