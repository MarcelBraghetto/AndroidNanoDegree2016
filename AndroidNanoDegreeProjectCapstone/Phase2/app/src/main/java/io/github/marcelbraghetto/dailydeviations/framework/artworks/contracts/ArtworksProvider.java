package io.github.marcelbraghetto.dailydeviations.framework.artworks.contracts;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.CollectionFilterMode;

/**
 * Created by Marcel Braghetto on 24/02/16.
 *
 * Provider representing the interface to connect to artwork data.
 */
public interface ArtworksProvider {
    /**
     * Determine whether there are any saved artworks that have been previously saved.
     * @return true if there are more than 0 artworks that have been saved before.
     */
    boolean hasSavedArtworks();

    /**
     * Fetch the content URI for all artworks in one of the public RSS feeds.
     * @param category of artworks to fetch.
     * @return content URI for popular artworks in the last 24 hours.
     */
    @NonNull Uri getArtworks(@NonNull CollectionFilterMode category);

    /**
     * Save a collection of artworks to the content provider.
     * @param artworks to save.
     * @return number of artworks that were actually inserted. Artworks that already exist
     * in the content provider are skipped from the input list so there will possibly be
     * less artworks inserted than the length of the list.
     */
    int saveArtworks(@NonNull List<Artwork> artworks);

    /**
     * Find out how many saved artworks have yet to be seen by the user.
     * @return the number of unseen artworks that are saved.
     */
    int getNumUnseenArtworks();

    /**
     * Set the number of unseen artworks back to zero.
     */
    void resetNumUnseenArtworks();

    /**
     * Request a refresh of the artwork data from the server.
     */
    void refreshData();

    /**
     * Attempt to fetch an artwork with the given identifier from the content provider.
     * @param guid of the artwork to fetch.
     * @return the artwork instance if found, or null if it couldn't be found in the content.
     */
    @Nullable Artwork getArtwork(@NonNull String guid);

    /**
     * Set the favourite flag for the given artwork guid to the given favourite state.
     * @param guid of the selected artwork.
     * @param isFavourite flag to save.
     */
    void saveFavourite(@NonNull String guid, boolean isFavourite);

    /**
     * Attempt to get a random artwork from the content store.
     * @return a random artwork, or null if there are no artworks stored.
     */
    @Nullable Artwork getRandomArtwork();
}
