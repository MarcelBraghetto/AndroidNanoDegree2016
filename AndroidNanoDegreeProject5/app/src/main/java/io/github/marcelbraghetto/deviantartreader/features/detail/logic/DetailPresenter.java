package io.github.marcelbraghetto.deviantartreader.features.detail.logic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.github.marcelbraghetto.deviantartreader.R;
import io.github.marcelbraghetto.deviantartreader.features.info.ui.InfoActivity;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.core.BasePresenter;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.sharedpreferences.contracts.SharedPreferencesProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.strings.contracts.StringsProvider;

/**
 * Created by Marcel Braghetto on 2/03/16.
 *
 * Presentation logic for displaying the detail screen for a given selected artwork.
 */
public class DetailPresenter extends BasePresenter<DetailPresenter.Delegate> {
    private static final String PREF_GREETING_SEEN = "DetailPresenter.GreetingSeen";

    private final Context mApplicationContext;
    private final StringsProvider mStringsProvider;
    private final SharedPreferencesProvider mSharedPreferencesProvider;

    private Artwork mArtwork;

    //region Public methods
    @Inject
    public DetailPresenter(@NonNull Context applicationContext,
                           @NonNull StringsProvider stringsProvider,
                           @NonNull SharedPreferencesProvider sharedPreferencesProvider) {
        super(Delegate.class);

        mApplicationContext = applicationContext;
        mStringsProvider = stringsProvider;
        mSharedPreferencesProvider = sharedPreferencesProvider;
    }

    /**
     * Initialise the presenter with the given callback and configuration bundle.
     * @param delegate to callback to.
     * @param bundle containing configuration data.
     */
    public void init(@Nullable Delegate delegate, @NonNull Bundle bundle) {
        setDelegate(delegate);

        mArtwork = Artwork.getFrom(bundle);

        if(mArtwork == null) {
            throw new UnsupportedOperationException("A valid artwork instance must be passed into the detail presenter");
        }

        mDelegate.setNavigationTitle(mArtwork.getTitle());
        mDelegate.setNavigationSubtitle(mArtwork.getAuthor());
        mDelegate.loadImage(mArtwork.getImageUrl());

        // If we've never shown the greeting message to hint to the user how to pinch and
        // zoom to interact with images, then show it!
        if(!mSharedPreferencesProvider.getBoolean(PREF_GREETING_SEEN, false)) {
            mSharedPreferencesProvider.saveBoolean(PREF_GREETING_SEEN, true);
            mDelegate.showGreetingMessage(mStringsProvider.getString(R.string.details_greeting_message));
        }
    }

    /**
     * User selected the 'info' button on the top of the screen to see the author information
     * for the given artwork.
     */
    public void infoButtonSelected() {
        Intent intent = new Intent(mApplicationContext, InfoActivity.class);
        mArtwork.putInto(intent);
        mDelegate.startActivityIntent(intent);
    }

    /**
     * User selected the 'share' button on the top of the screen to share a link to the given arwork.
     */
    public void shareButtonSelected() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, mStringsProvider.getString(R.string.details_share_subject, mArtwork.getTitle()));
        intent.putExtra(Intent.EXTRA_TEXT, mArtwork.getWebUrl());
        mDelegate.startActivityIntent(intent);
    }
    //endregion

    //region Presenter delegate contract
    public interface Delegate {
        /**
         * Display the given title text as the navigation bar title.
         * @param title to display.
         */
        void setNavigationTitle(@NonNull String title);

        /**
         * Display the given subtitle text as the navigation subtitle.
         * @param subtitle to display.
         */
        void setNavigationSubtitle(@NonNull String subtitle);

        /**
         * Load the given image url into the main view - this will typically be from the
         * local storage cache provided by the Glide image library so should be almost instant.
         * @param url to load into the image view.
         */
        void loadImage(@NonNull String url);

        /**
         * Start the given intent from the host activity.
         * @param intent to start.
         */
        void startActivityIntent(@NonNull Intent intent);

        /**
         * Show the given message as a 'greeting' typically as a Snackbar.
         * @param message to display.
         */
        void showGreetingMessage(@NonNull String message);
    }
    //endregion
}
