package io.github.marcelbraghetto.dailydeviations.features.collection.logic;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import io.github.marcelbraghetto.dailydeviations.R;
import io.github.marcelbraghetto.dailydeviations.features.collection.logic.providers.contracts.CollectionProvider;
import io.github.marcelbraghetto.dailydeviations.features.detail.ui.DetailActivity;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.contracts.ArtworksProvider;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.CollectionFilterMode;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.Analytics;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.AnalyticsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.core.BaseViewModel;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusSubscriber;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.events.CollectionModeToggleEvent;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.events.DataLoadEvent;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.strings.contracts.StringsProvider;

/**
 * Created by Marcel Braghetto on 24/02/16.
 *
 * View model for displaying a collection of artworks based on the arguments given when
 * constructing the host fragment.
 *
 */
public final class CollectionViewModel extends BaseViewModel<CollectionViewModel.Actions> implements EventBusSubscriber {
    //region Binding glue
    public final Glue glue = new Glue();
    public static class Glue {
        public final ObservableBoolean loadingIndicatorVisible = new ObservableBoolean(false);
        public final ObservableBoolean swipeRefreshEnabled = new ObservableBoolean(true);
        public final ObservableBoolean contentErrorMessageVisible = new ObservableBoolean(false);
        public final ObservableField<String> contentErrorMessageText = new ObservableField<>();

        private void showLoadingIndicator() {
            loadingIndicatorVisible.set(true);
        }

        private void hideLoadingIndicator() {
            loadingIndicatorVisible.set(false);
        }

        private void hideCollectionErrorMessage() {
            contentErrorMessageVisible.set(false);
        }

        private void showCollectionErrorMessage(@NonNull String message) {
            contentErrorMessageVisible.set(true);
            contentErrorMessageText.set(message);
        }

        private void disableSwipeToRefresh() {
            swipeRefreshEnabled.set(false);
        }
    }
    //endregion

    //region Private fields
    private static final String SCREEN_NAME_ALL = "CollectionScreenAll";
    private static final String SCREEN_NAME_FAVOURITES = "CollectionScreenFavourites";

    private final Context mApplicationContext;
    private final StringsProvider mStringsProvider;
    private final ArtworksProvider mArtworksProvider;
    private final EventBusProvider mEventBusProvider;
    private final CollectionProvider mCollectionProvider;
    private final AnalyticsProvider mAnalyticsProvider;

    private CollectionArguments mCollectionArguments;
    //endregion

    //region Public methods
    @Inject
    public CollectionViewModel(@NonNull Context applicationContext,
                               @NonNull StringsProvider stringsProvider,
                               @NonNull ArtworksProvider artworksProvider,
                               @NonNull EventBusProvider eventBusProvider,
                               @NonNull CollectionProvider collectionProvider,
                               @NonNull AnalyticsProvider analyticsProvider) {

        super(Actions.class);

        mApplicationContext = applicationContext;
        mStringsProvider = stringsProvider;
        mArtworksProvider = artworksProvider;
        mEventBusProvider = eventBusProvider;
        mCollectionProvider = collectionProvider;
        mAnalyticsProvider = analyticsProvider;
    }

    /**
     * Initialise the view model with the given bundle of data and action delegate.
     * @param bundle containing configuration data.
     * @param actionDelegate to send action commands to.
     */
    public void begin(@NonNull Bundle bundle, @Nullable Actions actionDelegate) {
        setActionDelegate(actionDelegate);
        mCollectionArguments = new CollectionArguments(bundle);
        refreshUI();
    }

    /**
     * User selected an artwork from the collection.
     * @param artwork that was selected.
     * @param sceneTransitionBundle this represents the Lollipop bundle for the scene transition.
     */
    public void artworkSelected(@NonNull Artwork artwork, @Nullable Bundle sceneTransitionBundle) {
        mAnalyticsProvider.trackEvent(Analytics.CONTENT_TYPE_ARTWORK_OPENED, getScreenName(), artwork.getWebUrl());
        Intent intent = new Intent(mApplicationContext, DetailActivity.class);
        artwork.putInto(intent);

        if(sceneTransitionBundle != null) {
            mActionDelegate.startActivityWithSceneTransition(intent, sceneTransitionBundle);
        } else {
            mActionDelegate.startActivity(intent);
        }
    }

    /**
     * User performed a swipe to refresh action, which should force the data to reload.
     */
    public void swipeToRefreshInitiated() {
        mAnalyticsProvider.trackEvent(Analytics.CONTENT_TYPE_BUTTON_CLICK, getScreenName(), "SwipeRefresh");
        glue.showLoadingIndicator();
        mArtworksProvider.refreshData();
    }

    /**
     * The screen was started.
     */
    public void screenStarted() {
        mAnalyticsProvider.trackScreenView(getScreenName());
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
            mAnalyticsProvider.trackEvent(Analytics.CONTENT_TYPE_BUTTON_CLICK, getScreenName(), "AddFavourite");
            mActionDelegate.showSnackbar(mStringsProvider.getString(R.string.collection_favourite_added, artwork.getTitle()));
        } else {
            mAnalyticsProvider.trackEvent(Analytics.CONTENT_TYPE_BUTTON_CLICK, getScreenName(), "RemoveFavourite");
            mActionDelegate.showSnackbar(mStringsProvider.getString(R.string.collection_favourite_removed, artwork.getTitle()));
        }
    }

    /**
     * The data source in the collection adapter has been changed - typically from the content
     * provider via the loader.
     * @param numItems contained in the data source.
     */
    public void collectionDataSourceChanged(int numItems) {
        if(numItems == 0 && mCollectionArguments.getFilterMode() == CollectionFilterMode.Favourites) {
            // If the user is in the 'favourites' view and there are zero items, show a message.
            glue.showCollectionErrorMessage(mStringsProvider.getString(R.string.collection_empty_favourites));
        } else {
            // Otherwise don't show anything.
            glue.hideCollectionErrorMessage();
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
        glue.hideLoadingIndicator();

        switch(event.getEventType()) {
            case Loaded:
                // Let the user know that the data load operation succeeded.
                mActionDelegate.showSnackbar(mStringsProvider.getString(R.string.collection_data_load_success));
                break;
            case Failed:
                // If a request failed and we have zero saved artworks, then show a static error message.
                if(!mArtworksProvider.hasSavedArtworks()) {
                    glue.showCollectionErrorMessage(mStringsProvider.getString(R.string.collection_empty_daily_deviations));
                }

                // Let the user know that the data load operation failed.
                mActionDelegate.showSnackbar(mStringsProvider.getString(R.string.collection_data_load_failed));
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
    private String getScreenName() {
        return mCollectionArguments.getFilterMode() == CollectionFilterMode.Favourites ? SCREEN_NAME_FAVOURITES : SCREEN_NAME_ALL;
    }

    private void refreshUI() {
        // No point in allowing swipe to refresh for the 'favourites' view.
        if(mCollectionArguments.getFilterMode() == CollectionFilterMode.Favourites) {
            glue.disableSwipeToRefresh();
        }

        refreshCollectionMode();
        mActionDelegate.setDataSource(mArtworksProvider.getArtworks(mCollectionArguments.getFilterMode()));
    }

    private void refreshCollectionMode() {
        mActionDelegate.setCollectionMode(mCollectionProvider.getCollectionDisplayMode());
    }
    //endregion

    //region View model actions contract
    public interface Actions {
        /**
         * Connect the given Uri as a data source to the host loader and adapter.
         * @param dataSourceUri to use as a data source.
         */
        void setDataSource(@NonNull Uri dataSourceUri);

        /**
         * Change the 'collection mode' - toggling between multi and single column view.
         * @param collectionMode to apply to the collection adapter.
         */
        void setCollectionMode(@NonNull CollectionDisplayMode collectionMode);

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
         * Show a temporary information message in a momentary Snackbar.
         * @param message to display.
         */
        void showSnackbar(@NonNull String message);
    }
    //endregion
}
