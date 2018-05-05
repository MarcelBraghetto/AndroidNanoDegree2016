package io.github.marcelbraghetto.deviantartreader.features.collection.logic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import io.github.marcelbraghetto.deviantartreader.R;
import io.github.marcelbraghetto.deviantartreader.features.collection.logic.providers.contracts.CollectionProvider;
import io.github.marcelbraghetto.deviantartreader.features.detail.ui.DetailActivity;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.contracts.ArtworksProvider;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.models.ArtworksCategory;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.core.BasePresenter;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.contracts.EventBusSubscriber;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.events.CollectionModeToggleEvent;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.events.DataLoadEvent;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.strings.contracts.StringsProvider;

/**
 * Created by Marcel Braghetto on 24/02/16.
 *
 * Presenter logic for displaying a collection of artworks based on the arguments given when
 * constructing the host fragment.
 */
public final class CollectionPresenter extends BasePresenter<CollectionPresenter.Delegate> implements EventBusSubscriber {
    private final Context mApplicationContext;
    private final StringsProvider mStringsProvider;
    private final ArtworksProvider mArtworksProvider;
    private final EventBusProvider mEventBusProvider;
    private final CollectionProvider mCollectionProvider;

    private CollectionArguments mCollectionArguments;

    //region Public methods
    @Inject
    public CollectionPresenter(@NonNull Context applicationContext,
                               @NonNull StringsProvider stringsProvider,
                               @NonNull ArtworksProvider artworksProvider,
                               @NonNull EventBusProvider eventBusProvider,
                               @NonNull CollectionProvider collectionProvider) {

        super(Delegate.class);

        mApplicationContext = applicationContext;
        mStringsProvider = stringsProvider;
        mArtworksProvider = artworksProvider;
        mEventBusProvider = eventBusProvider;
        mCollectionProvider = collectionProvider;
    }

    /**
     * Initialise the presenter with the given bundle of data and callback delegate.
     * @param bundle containing configuration data.
     * @param delegate to callback to.
     */
    public void init(@NonNull Bundle bundle, @Nullable Delegate delegate) {
        setDelegate(delegate);
        mCollectionArguments = new CollectionArguments(bundle);
        refreshUI();
    }

    /**
     * User selected an artwork from the collection.
     * @param artwork that was selected.
     * @param sceneTransitionBundle this represents the Lollipop bundle for the scene transition.
     */
    public void artworkSelected(@NonNull Artwork artwork, @Nullable Bundle sceneTransitionBundle) {
        Intent intent = new Intent(mApplicationContext, DetailActivity.class);
        artwork.putInto(intent);

        if(sceneTransitionBundle != null) {
            mDelegate.startActivityWithSceneTransition(intent, sceneTransitionBundle);
        } else {
            mDelegate.startActivity(intent);
        }
    }

    /**
     * User performed a swipe to refresh action, which should force the data to reload.
     */
    public void swipeToRefreshInitiated() {
        mArtworksProvider.refreshData();
    }

    /**
     * The screen was started.
     */
    public void screenStarted() {
        subscribeToEventBus();
    }

    /**
     * The screen was stopped.
     */
    public void screenStopped() {
        unsubscribeFromEventBus();
    }

    /**
     * The user tapped the favourite 'heart' icon for a given artwork which will be toggled as
     * a favourite that is persisted to storage.
     * @param artwork that was 'favourited'
     * @param isFavourite true if the user toggled the 'heart' on, or false if toggled off.
     */
    public void favouriteButtonSelected(@NonNull Artwork artwork, boolean isFavourite) {
        mArtworksProvider.saveFavourite(artwork.getGuid(), isFavourite);

        // After saving the artwork favourite, show the user a message to indicate success.
        if(isFavourite) {
            mDelegate.showInfoMessage(mStringsProvider.getString(R.string.collection_favourite_added, artwork.getTitle()));
        } else {
            mDelegate.showInfoMessage(mStringsProvider.getString(R.string.collection_favourite_removed, artwork.getTitle()));
        }
    }

    /**
     * The data source in the collection adapter has been changed - typically from the content
     * provider via the loader.
     * @param numItems contained in the data source.
     */
    public void collectionDataSourceChanged(int numItems) {
        if(numItems == 0 && mCollectionArguments.getCategory() == ArtworksCategory.Favourites) {
            // If the user is in the 'favourites' view and there are zero items, show a message.
            mDelegate.showEmptyCollectionMessage(mStringsProvider.getString(R.string.collection_empty_favourites));
        } else {
            // Otherwise don't show anything.
            mDelegate.hideEmptyCollectionMessage();
        }
    }
    //endregion

    //region Event bus
    /**
     * Broadcast received for data load events, which we can decide how to react in the screen.
     * @param event that was broadcast.
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull DataLoadEvent event) {
        mDelegate.hideLoadingIndicator();

        switch(event.getEventType()) {
            case Loaded:
                // Let the user know that the data load operation succeeded.
                mDelegate.showInfoMessage(mStringsProvider.getString(R.string.collection_data_load_success));
                break;
            case Failed:
                // If a request failed and we have zero saved artworks, then show a static error message.
                if(!mArtworksProvider.hasSavedArtworks()) {
                    mDelegate.showEmptyCollectionMessage(mStringsProvider.getString(R.string.collection_empty_daily_deviations));
                }

                // Let the user know that the data load operation failed.
                mDelegate.showInfoMessage(mStringsProvider.getString(R.string.collection_data_load_failed));
                break;
        }
    }

    /**
     * Broadcast received when the user has changed the 'mode' of operation between a multi
     * column view and a single column view.
     * @param event that was broadcast.
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull CollectionModeToggleEvent event) {
        refreshCollectionMode();
    }

    @Override
    public void subscribeToEventBus() {
        mEventBusProvider.subscribe(this);
    }

    @Override
    public void unsubscribeFromEventBus() {
        mEventBusProvider.unsubscribe(this);
    }
    //endregion

    //region Private methods
    private void refreshUI() {
        // No point in allowing swipe to refresh for the 'favourites' view.
        if(mCollectionArguments.getCategory() == ArtworksCategory.Favourites) {
            mDelegate.disableSwipeToRefresh();
        }

        refreshCollectionMode();
        mDelegate.setDataSource(mArtworksProvider.getArtworks(mCollectionArguments.getCategory()));
    }

    private void refreshCollectionMode() {
        mDelegate.setCollectionMode(mCollectionProvider.getCollectionMode());
    }
    //endregion

    //region Presenter delegate contract
    public interface Delegate {
        /**
         * Connect the given Uri as a data source to the host loader and adapter.
         * @param dataSourceUri to use as a data source.
         */
        void setDataSource(@NonNull Uri dataSourceUri);

        /**
         * Change the 'collection mode' - toggling between multi and single column view.
         * @param collectionMode to apply to the collection adapter.
         */
        void setCollectionMode(@NonNull CollectionMode collectionMode);

        /**
         * Initiate the given intent from the host activity.
         * @param intent to start.
         */
        void startActivity(@NonNull Intent intent);

        /**
         * Initiate the given intent from the host activity, applying Lollipop scene transitions.
         * @param intent to start.
         * @param sceneTransitionBundle to apply to the transition.
         */
        void startActivityWithSceneTransition(@NonNull Intent intent, @NonNull Bundle sceneTransitionBundle);

        /**
         * Hide any loading indicators.
         */
        void hideLoadingIndicator();

        /**
         * Show a temporary information message - typically in a Snackbar.
         * @param message to display.
         */
        void showInfoMessage(@NonNull String message);

        /**
         * Request the view to disable the swipe to refresh functionality.
         */
        void disableSwipeToRefresh();

        /**
         * Hide the message that is displayed when there are no items in the data source.
         */
        void hideEmptyCollectionMessage();

        /**
         * Show the given message for when there are no items in the data source.
         * @param message to display.
         */
        void showEmptyCollectionMessage(@NonNull String message);
    }
    //endregion
}
