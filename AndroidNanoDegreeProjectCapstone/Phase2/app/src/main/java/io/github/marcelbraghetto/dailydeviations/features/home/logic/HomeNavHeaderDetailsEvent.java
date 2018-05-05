package io.github.marcelbraghetto.dailydeviations.features.home.logic;

import android.support.annotation.NonNull;

import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusEvent;

/**
 * Created by Marcel Braghetto on 6/06/16.
 *
 * Event raised when the user decides to view the details for the image being displayed in the
 * header view.
 */
public final class HomeNavHeaderDetailsEvent implements EventBusEvent {
    private final Artwork mArtwork;

    public HomeNavHeaderDetailsEvent(@NonNull  Artwork artwork) {
        mArtwork = artwork;
    }

    @NonNull
    public Artwork getArtwork() {
        return mArtwork;
    }
}
