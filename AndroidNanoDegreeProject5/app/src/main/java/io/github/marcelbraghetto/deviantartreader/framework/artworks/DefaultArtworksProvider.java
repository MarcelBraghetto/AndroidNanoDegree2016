package io.github.marcelbraghetto.deviantartreader.framework.artworks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import io.github.marcelbraghetto.deviantartreader.framework.artworks.contracts.ArtworksProvider;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.models.ArtworksCategory;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.service.ArtworksDataService;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.sharedpreferences.contracts.SharedPreferencesProvider;

import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.getAllColumnsIndexMap;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.getContentUriAllArtworks;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.getContentUriArtworkCount;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.getContentUriFavouriteArtworks;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.getContentUriSaveFavourite;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.getContentUriSpecificArtwork;

/**
 * Created by Marcel Braghetto on 24/02/16.
 *
 * Implementation of the artworks provider to bridge to the content database and provider.
 */
public class DefaultArtworksProvider implements ArtworksProvider {
    private static final String PREF_UNSEEN_ARTWORKS_COUNT = "ArtworksCommonProperties.UnseenArtworksCount";

    private final Context mApplicationContext;
    private final SharedPreferencesProvider mSharedPreferencesProvider;

    public DefaultArtworksProvider(@NonNull Context applicationContext,
                                   @NonNull SharedPreferencesProvider sharedPreferencesProvider) {

        mApplicationContext = applicationContext;
        mSharedPreferencesProvider = sharedPreferencesProvider;
    }

    @Override
    public boolean hasSavedArtworks() {
        Cursor cursor = mApplicationContext.getContentResolver().query(getContentUriArtworkCount(), null, null, null, null);
        boolean result = false;

        if(cursor != null && cursor.moveToFirst()) {
            result = cursor.getInt(0) > 0;
            cursor.close();
        }

        return result;
    }

    @NonNull
    @Override
    public Uri getArtworks(@NonNull ArtworksCategory category) {
        switch(category) {
            case Favourites:
                return getContentUriFavouriteArtworks();
            default:
                return getContentUriAllArtworks();
        }
    }

    @Override
    public int saveArtworks(@NonNull List<Artwork> artworks) {
        ContentValues[] contentValues = new ContentValues[artworks.size()];

        for(int i = 0; i < artworks.size(); i++) {
            contentValues[i] = artworks.get(i).getContentValues();
        }

        // Save all the artworks and find out how many new records were created.
        int numNewArtworks = mApplicationContext.getContentResolver().bulkInsert(getContentUriAllArtworks(), contentValues);

        // Find out how many (if any) unseen artworks have been encountered before.
        int currentUnseenArtworksCount = getNumUnseenArtworks();

        // Update the persisted count of how many unseen artworks there are.
        currentUnseenArtworksCount += numNewArtworks;
        mSharedPreferencesProvider.saveInteger(PREF_UNSEEN_ARTWORKS_COUNT, currentUnseenArtworksCount);

        return numNewArtworks;
    }

    @Override
    public void resetNumUnseenArtworks() {
        mSharedPreferencesProvider.saveInteger(PREF_UNSEEN_ARTWORKS_COUNT, 0);
    }

    @Override
    public int getNumUnseenArtworks() {
        return mSharedPreferencesProvider.getInteger(PREF_UNSEEN_ARTWORKS_COUNT, 0);
    }

    @Override
    public void refreshData() {
        mApplicationContext.startService(ArtworksDataService.createServiceIntent(mApplicationContext, ArtworksDataService.RefreshReason.ForceRefresh));
    }

    @Nullable
    @Override
    public Artwork getArtwork(@NonNull String guid) {
        Cursor cursor = mApplicationContext.getContentResolver().query(getContentUriSpecificArtwork(guid), null, null, null, null);

        if(cursor != null && cursor.moveToFirst()) {
            Artwork artwork = new Artwork();
            artwork.populateFromCursor(cursor, getAllColumnsIndexMap());
            cursor.close();
            return artwork;
        }

        return null;
    }

    @Override
    public void saveFavourite(@NonNull String guid, boolean isFavourite) {
        Cursor cursor = mApplicationContext.getContentResolver().query(getContentUriSaveFavourite(guid, isFavourite), null, null, null, null);

        if(cursor != null) {
            cursor.close();
        }
    }
}
