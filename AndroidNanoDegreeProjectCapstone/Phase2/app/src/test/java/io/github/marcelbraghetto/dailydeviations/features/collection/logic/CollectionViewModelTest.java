package io.github.marcelbraghetto.dailydeviations.features.collection.logic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import io.github.marcelbraghetto.dailydeviations.BuildConfig;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.features.collection.logic.providers.contracts.CollectionProvider;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.contracts.ArtworksProvider;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.AnalyticsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.events.CollectionModeToggleEvent;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.events.DataLoadEvent;
import io.github.marcelbraghetto.dailydeviations.testconfig.RobolectricProperties;

import static io.github.marcelbraghetto.dailydeviations.features.collection.logic.CollectionDisplayMode.MultiColumn;
import static io.github.marcelbraghetto.dailydeviations.features.collection.logic.CollectionDisplayMode.SingleColumn;
import static io.github.marcelbraghetto.dailydeviations.framework.artworks.models.CollectionFilterMode.All;
import static io.github.marcelbraghetto.dailydeviations.framework.artworks.models.CollectionFilterMode.Favourites;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by Marcel Braghetto on 12/06/16.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class CollectionViewModelTest {
    @Mock ArtworksProvider mArtworksProvider;
    @Mock EventBusProvider mEventBusProvider;
    @Mock CollectionProvider mCollectionProvider;
    @Mock AnalyticsProvider mAnalyticsProvider;
    @Mock CollectionViewModel.Actions mDelegate;
    private CollectionViewModel mViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mViewModel = new CollectionViewModel(
                MainApp.getDagger().getApplicationContext(),
                MainApp.getDagger().getAppStringsProvider(),
                mArtworksProvider,
                mEventBusProvider,
                mCollectionProvider,
                mAnalyticsProvider);
    }

    @Test
    public void verifyDefaultDataBinding() {
        // Verify
        assertThat(mViewModel.glue.loadingIndicatorVisible.get(), is(false));
        assertThat(mViewModel.glue.swipeRefreshEnabled.get(), is(true));
        assertThat(mViewModel.glue.contentErrorMessageVisible.get(), is(false));
        assertThat(mViewModel.glue.contentErrorMessageText.get(), nullValue());
    }

    @Test
    public void beginFilterAll() {
        // Setup
        Uri uri = Uri.parse("some uri");
        when(mCollectionProvider.getCollectionDisplayMode()).thenReturn(MultiColumn);
        when(mArtworksProvider.getArtworks(All)).thenReturn(uri);
        Bundle bundle = new CollectionArguments(All).toBundle();

        // Run
        mViewModel.begin(bundle, mDelegate);

        // Verify
        assertThat(mViewModel.glue.swipeRefreshEnabled.get(), is(true));
        verify(mCollectionProvider).getCollectionDisplayMode();
        verify(mDelegate).setCollectionMode(MultiColumn);
        verify(mArtworksProvider).getArtworks(All);
        verify(mDelegate).setDataSource(uri);
    }

    @Test
    public void beginFilterFavourites() {
        // Setup
        Uri uri = Uri.parse("some uri");
        when(mCollectionProvider.getCollectionDisplayMode()).thenReturn(SingleColumn);
        when(mArtworksProvider.getArtworks(Favourites)).thenReturn(uri);
        Bundle bundle = new CollectionArguments(Favourites).toBundle();

        // Run
        mViewModel.begin(bundle, mDelegate);

        // Verify
        assertThat(mViewModel.glue.swipeRefreshEnabled.get(), is(false));
        verify(mCollectionProvider).getCollectionDisplayMode();
        verify(mDelegate).setCollectionMode(SingleColumn);
        verify(mArtworksProvider).getArtworks(Favourites);
        verify(mDelegate).setDataSource(uri);
    }

    @Test
    public void artworkSelectedWithSceneTransitionFavourites() {
        // Setup
        ArgumentCaptor<Intent> intentCaptor = new ArgumentCaptor<>();
        Artwork artwork = new Artwork();
        artwork.setWebUrl("web url");
        Bundle sceneTransitionBundle = new Bundle();
        mViewModel.begin(new CollectionArguments(Favourites).toBundle(), mDelegate);

        // Run
        mViewModel.artworkSelected(artwork, sceneTransitionBundle);

        // Verify
        verify(mAnalyticsProvider).trackEvent("ArtworkOpened", "CollectionScreenFavourites", "web url");
        verify(mDelegate).startActivityWithSceneTransition(intentCaptor.capture(), eq(sceneTransitionBundle));
        verify(mDelegate, never()).startActivity(any(Intent.class));
        assertThat(intentCaptor.getValue().getComponent().getClassName(), is("io.github.marcelbraghetto.dailydeviations.features.detail.ui.DetailActivity"));
    }

    @Test
    public void artworkSelectedWithoutSceneTransitionFavourites() {
        // Setup
        ArgumentCaptor<Intent> intentCaptor = new ArgumentCaptor<>();
        Artwork artwork = new Artwork();
        artwork.setWebUrl("web url");
        Bundle sceneTransitionBundle = null;
        mViewModel.begin(new CollectionArguments(Favourites).toBundle(), mDelegate);

        // Run
        mViewModel.artworkSelected(artwork, sceneTransitionBundle);

        // Verify
        verify(mAnalyticsProvider).trackEvent("ArtworkOpened", "CollectionScreenFavourites", "web url");
        verify(mDelegate, never()).startActivityWithSceneTransition(any(Intent.class), any(Bundle.class));
        verify(mDelegate).startActivity(intentCaptor.capture());
        assertThat(intentCaptor.getValue().getComponent().getClassName(), is("io.github.marcelbraghetto.dailydeviations.features.detail.ui.DetailActivity"));
    }

    @Test
    public void artworkSelectedWithSceneTransitionAll() {
        // Setup
        ArgumentCaptor<Intent> intentCaptor = new ArgumentCaptor<>();
        Artwork artwork = new Artwork();
        artwork.setWebUrl("web url");
        Bundle sceneTransitionBundle = new Bundle();
        mViewModel.begin(new CollectionArguments(All).toBundle(), mDelegate);

        // Run
        mViewModel.artworkSelected(artwork, sceneTransitionBundle);

        // Verify
        verify(mAnalyticsProvider).trackEvent("ArtworkOpened", "CollectionScreenAll", "web url");
        verify(mDelegate).startActivityWithSceneTransition(intentCaptor.capture(), eq(sceneTransitionBundle));
        verify(mDelegate, never()).startActivity(any(Intent.class));
        assertThat(intentCaptor.getValue().getComponent().getClassName(), is("io.github.marcelbraghetto.dailydeviations.features.detail.ui.DetailActivity"));
    }

    @Test
    public void artworkSelectedWithoutSceneTransitionAll() {
        // Setup
        ArgumentCaptor<Intent> intentCaptor = new ArgumentCaptor<>();
        Artwork artwork = new Artwork();
        artwork.setWebUrl("web url");
        Bundle sceneTransitionBundle = null;
        mViewModel.begin(new CollectionArguments(All).toBundle(), mDelegate);

        // Run
        mViewModel.artworkSelected(artwork, sceneTransitionBundle);

        // Verify
        verify(mAnalyticsProvider).trackEvent("ArtworkOpened", "CollectionScreenAll", "web url");
        verify(mDelegate, never()).startActivityWithSceneTransition(any(Intent.class), any(Bundle.class));
        verify(mDelegate).startActivity(intentCaptor.capture());
        assertThat(intentCaptor.getValue().getComponent().getClassName(), is("io.github.marcelbraghetto.dailydeviations.features.detail.ui.DetailActivity"));
    }

    @Test
    public void swipeToRefreshInitiatedAll() {
        // Setup
        mViewModel.begin(new CollectionArguments(All).toBundle(), mDelegate);

        // Run
        mViewModel.swipeToRefreshInitiated();

        // Verify
        verify(mAnalyticsProvider).trackEvent("ButtonClick", "CollectionScreenAll", "SwipeRefresh");
        assertThat(mViewModel.glue.loadingIndicatorVisible.get(), is(true));
        verify(mArtworksProvider).refreshData();
    }

    @Test
    public void swipeToRefreshInitiatedFavourites() {
        // Setup
        mViewModel.begin(new CollectionArguments(Favourites).toBundle(), mDelegate);

        // Run
        mViewModel.swipeToRefreshInitiated();

        // Verify
        verify(mAnalyticsProvider).trackEvent("ButtonClick", "CollectionScreenFavourites", "SwipeRefresh");
        assertThat(mViewModel.glue.loadingIndicatorVisible.get(), is(true));
        verify(mArtworksProvider).refreshData();
    }

    @Test
    public void screenStartedAll() {
        // Setup
        mViewModel.begin(new CollectionArguments(All).toBundle(), mDelegate);

        // Run
        mViewModel.screenStarted();

        // Verify
        verify(mAnalyticsProvider).trackScreenView("CollectionScreenAll");
        verify(mEventBusProvider).subscribe(mViewModel);
    }

    @Test
    public void screenStartedFavourites() {
        // Setup
        mViewModel.begin(new CollectionArguments(Favourites).toBundle(), mDelegate);

        // Run
        mViewModel.screenStarted();

        // Verify
        verify(mAnalyticsProvider).trackScreenView("CollectionScreenFavourites");
        verify(mEventBusProvider).subscribe(mViewModel);
    }

    @Test
    public void screenStopped() {
        // Run
        mViewModel.screenStopped();

        // Verify
        verify(mEventBusProvider).unsubscribe(mViewModel);
    }

    @Test
    public void favouriteButtonSelectedScreenAllAdd() {
        // Setup
        Artwork artwork = new Artwork();
        artwork.setGuid("guid");
        artwork.setTitle("artwork title");
        mViewModel.begin(new CollectionArguments(All).toBundle(), mDelegate);

        // Run
        mViewModel.favouriteButtonSelected(artwork, true);

        // Verify
        verify(mArtworksProvider).saveFavourite("guid", true);
        verify(mAnalyticsProvider).trackEvent("ButtonClick", "CollectionScreenAll", "AddFavourite");
        verify(mDelegate).showSnackbar("Added: artwork title");
    }

    @Test
    public void favouriteButtonSelectedScreenAllRemove() {
        // Setup
        Artwork artwork = new Artwork();
        artwork.setGuid("guid");
        artwork.setTitle("artwork title");
        mViewModel.begin(new CollectionArguments(All).toBundle(), mDelegate);

        // Run
        mViewModel.favouriteButtonSelected(artwork, false);

        // Verify
        verify(mArtworksProvider).saveFavourite("guid", false);
        verify(mAnalyticsProvider).trackEvent("ButtonClick", "CollectionScreenAll", "RemoveFavourite");
        verify(mDelegate).showSnackbar("Removed: artwork title");
    }

    @Test
    public void favouriteButtonSelectedScreenFavouritesAdd() {
        // Setup
        Artwork artwork = new Artwork();
        artwork.setGuid("guid");
        artwork.setTitle("artwork title");
        mViewModel.begin(new CollectionArguments(Favourites).toBundle(), mDelegate);

        // Run
        mViewModel.favouriteButtonSelected(artwork, true);

        // Verify
        verify(mArtworksProvider).saveFavourite("guid", true);
        verify(mAnalyticsProvider).trackEvent("ButtonClick", "CollectionScreenFavourites", "AddFavourite");
        verify(mDelegate).showSnackbar("Added: artwork title");
    }

    @Test
    public void favouriteButtonSelectedScreenFavouritesRemove() {
        // Setup
        Artwork artwork = new Artwork();
        artwork.setGuid("guid");
        artwork.setTitle("artwork title");
        mViewModel.begin(new CollectionArguments(Favourites).toBundle(), mDelegate);

        // Run
        mViewModel.favouriteButtonSelected(artwork, false);

        // Verify
        verify(mArtworksProvider).saveFavourite("guid", false);
        verify(mAnalyticsProvider).trackEvent("ButtonClick", "CollectionScreenFavourites", "RemoveFavourite");
        verify(mDelegate).showSnackbar("Removed: artwork title");
    }

    @Test
    public void collectionDataSourceChangedScreenFavouritesWithItems() {
        // Setup
        mViewModel.begin(new CollectionArguments(Favourites).toBundle(), mDelegate);

        // Run
        mViewModel.collectionDataSourceChanged(10);

        // Verify
        assertThat(mViewModel.glue.contentErrorMessageVisible.get(), is(false));
    }

    @Test
    public void collectionDataSourceChangedScreenFavouritesNoItems() {
        // Setup
        mViewModel.begin(new CollectionArguments(Favourites).toBundle(), mDelegate);

        // Run
        mViewModel.collectionDataSourceChanged(0);

        // Verify
        assertThat(mViewModel.glue.contentErrorMessageVisible.get(), is(true));
        assertThat(mViewModel.glue.contentErrorMessageText.get(), is("Tap the heart on an image to add to your collection."));
    }

    @Test
    public void collectionDataSourceChangedScreenAll() {
        // Setup
        mViewModel.begin(new CollectionArguments(All).toBundle(), mDelegate);

        // Run
        mViewModel.collectionDataSourceChanged(0);

        // Verify
        assertThat(mViewModel.glue.contentErrorMessageVisible.get(), is(false));
    }

    @Test
    public void onDataLoadEventSuccess() {
        // Setup
        DataLoadEvent event = DataLoadEvent.createLoadedEvent();
        mViewModel.begin(new CollectionArguments(All).toBundle(), mDelegate);
        reset(mArtworksProvider);

        // Run
        mViewModel.onEvent(event);

        // Verify
        assertThat(mViewModel.glue.loadingIndicatorVisible.get(), is(false));
        assertThat(mViewModel.glue.contentErrorMessageVisible.get(), is(false));

        verify(mDelegate).showSnackbar("Refresh complete");
        verifyZeroInteractions(mArtworksProvider);
    }

    @Test
    public void onDataLoadEventFailedNoSavedArtworks() {
        // Setup
        DataLoadEvent event = DataLoadEvent.createFailedEvent();
        when(mArtworksProvider.hasSavedArtworks()).thenReturn(false);
        mViewModel.begin(new CollectionArguments(All).toBundle(), mDelegate);
        reset(mArtworksProvider);

        // Run
        mViewModel.onEvent(event);

        // Verify
        assertThat(mViewModel.glue.loadingIndicatorVisible.get(), is(false));
        assertThat(mViewModel.glue.contentErrorMessageVisible.get(), is(true));
        assertThat(mViewModel.glue.contentErrorMessageText.get(), is("Check your connection then swipe to refresh."));

        verify(mArtworksProvider).hasSavedArtworks();
        verify(mDelegate).showSnackbar("Connection problem - refresh failed");
    }

    @Test
    public void onDataLoadEventFailedHasSavedArtworks() {
        // Setup
        DataLoadEvent event = DataLoadEvent.createFailedEvent();
        mViewModel.begin(new CollectionArguments(All).toBundle(), mDelegate);
        reset(mArtworksProvider);
        when(mArtworksProvider.hasSavedArtworks()).thenReturn(true);

        // Run
        mViewModel.onEvent(event);

        // Verify
        assertThat(mViewModel.glue.loadingIndicatorVisible.get(), is(false));
        assertThat(mViewModel.glue.contentErrorMessageVisible.get(), is(false));

        verify(mArtworksProvider).hasSavedArtworks();
        verify(mDelegate).showSnackbar("Connection problem - refresh failed");
    }

    @Test
    public void onCollectionModeToggleEventSingleColumn() {
        // Setup
        when(mCollectionProvider.getCollectionDisplayMode()).thenReturn(SingleColumn);
        mViewModel.begin(new CollectionArguments(All).toBundle(), mDelegate);
        reset(mCollectionProvider);

        // Run
        mViewModel.onEvent(new CollectionModeToggleEvent());

        // Verify
        verify(mCollectionProvider).getCollectionDisplayMode();
        verify(mDelegate).setCollectionMode(SingleColumn);
    }

    @Test
    public void onCollectionModeToggleEventMultiColumn() {
        // Setup
        when(mCollectionProvider.getCollectionDisplayMode()).thenReturn(MultiColumn);
        mViewModel.begin(new CollectionArguments(All).toBundle(), mDelegate);
        reset(mCollectionProvider);

        // Run
        mViewModel.onEvent(new CollectionModeToggleEvent());

        // Verify
        verify(mCollectionProvider).getCollectionDisplayMode();
        verify(mDelegate).setCollectionMode(MultiColumn);
    }
}