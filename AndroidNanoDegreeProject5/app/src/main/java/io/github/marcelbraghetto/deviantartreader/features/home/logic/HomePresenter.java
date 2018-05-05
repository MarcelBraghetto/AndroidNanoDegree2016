package io.github.marcelbraghetto.deviantartreader.features.home.logic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import io.github.marcelbraghetto.deviantartreader.features.collection.logic.CollectionArguments;
import io.github.marcelbraghetto.deviantartreader.features.collection.ui.CollectionFragment;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.contracts.ArtworksProvider;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.models.ArtworksCategory;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.core.BasePresenter;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.contracts.EventBusSubscriber;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.events.CollectionFavouritesEvent;

/**
 * Created by Marcel Braghetto on 1/12/15.
 *
 * Controller logic for the home activity which is responsible for the nav menu and content
 * population.
 */
public class HomePresenter extends BasePresenter<HomePresenter.Delegate> implements EventBusSubscriber {
    private final ArtworksProvider mArtworksProvider;
    private final EventBusProvider mEventBusProvider;

    private enum ContentScreen {
        All,
        Favourites
    }

    private ContentScreen mContentScreen;

    //region Public methods
    @Inject
    public HomePresenter(@NonNull ArtworksProvider artworksProvider,
                         @NonNull EventBusProvider eventBusProvider) {
        super(Delegate.class);

        mArtworksProvider = artworksProvider;
        mEventBusProvider = eventBusProvider;
        mContentScreen = ContentScreen.All;
    }

    /**
     * Initialize the presenter with the given callback delegate and saved state if there is any.
     * @param delegate to callback to.
     * @param savedInstanceState if any.
     */
    public void init(@Nullable Delegate delegate, @Nullable Bundle savedInstanceState) {
        setDelegate(delegate);

        if(savedInstanceState == null) {
            showCurrentContentScreen(false);
        }
    }

    /**
     * The screen was started.
     */
    public void screenStarted() {
        subscribeToEventBus();

        // If we have no saved artworks, then we probably have never fetched any so
        // proceed with triggering a fetch operation. Over time though, the alarm
        // manager driven intent service should periodically attempt to update the
        // data set.
        if(!mArtworksProvider.hasSavedArtworks()) {
            mArtworksProvider.refreshData();
        }
    }

    /**
     * The screen was stopped.
     */
    public void screenStopped() {
        unsubscribeFromEventBus();
    }
    //endregion

    //region Event bus
    /**
     * Broadcast received for a 'favourites' event indicating that the user had toggled
     * between the main collection view and their favourites view.
     * @param event that was broadcast.
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull CollectionFavouritesEvent event) {
        if(event.isOn()) {
            // The 'on' state indicates that favourites was selected.
            mContentScreen = ContentScreen.Favourites;
        } else {
            // Otherwise just show the full collection view.
            mContentScreen = ContentScreen.All;
        }

        showCurrentContentScreen(true);
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
    private void showCurrentContentScreen(boolean animated) {
        switch (mContentScreen) {
            case Favourites:
                mDelegate.replaceContent(CollectionFragment.newInstance(new CollectionArguments(ArtworksCategory.Favourites)), animated);
                mDelegate.showFavouritesTitle();
                break;
            default:
                mDelegate.replaceContent(CollectionFragment.newInstance(new CollectionArguments(ArtworksCategory.All)), animated);
                mDelegate.showDailyDeviationsTitle();
                break;
        }
    }
    //endregion

    //region Controller delegate
    public interface Delegate {
        /**
         * Replace the currently displayed content
         * with the given fragment.
         * @param fragment to display
         */
        void replaceContent(@NonNull Fragment fragment, boolean animated);

        /**
         * Request the 'daily deviations' title be displayed which includes a cross fade.
         */
        void showDailyDeviationsTitle();

        /**
         * Request the 'favourites' title be displayed which includes a cross fade.
         */
        void showFavouritesTitle();
    }
    //endregion
}
