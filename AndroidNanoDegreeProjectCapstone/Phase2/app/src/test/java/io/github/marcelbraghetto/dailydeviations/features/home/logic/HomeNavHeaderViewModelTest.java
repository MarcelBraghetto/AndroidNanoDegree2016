package io.github.marcelbraghetto.dailydeviations.features.home.logic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import io.github.marcelbraghetto.dailydeviations.BuildConfig;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.features.settings.logic.providers.contracts.SettingsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.contracts.ArtworksProvider;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.AnalyticsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.events.DataLoadEvent;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.sharedpreferences.contracts.SharedPreferencesProvider;
import io.github.marcelbraghetto.dailydeviations.testconfig.RobolectricProperties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by Marcel Braghetto on 13/06/16.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class HomeNavHeaderViewModelTest {
    @Mock ArtworksProvider mArtworksProvider;
    @Mock EventBusProvider mEventBusProvider;
    @Mock AnalyticsProvider mAnalyticsProvider;
    @Mock SharedPreferencesProvider mSharedPreferencesProvider;
    @Mock SettingsProvider mSettingsProvider;
    @Mock HomeNavHeaderViewModel.Actions mDelegate;
    private HomeNavHeaderViewModel mViewModel;
    private Artwork mArtwork;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mArtwork = new Artwork();
        mArtwork.setGuid("guid");
        mArtwork.setTitle("title");
        mArtwork.setAuthor("author name");
        mArtwork.setAuthorImageUrl("author image url");
        mArtwork.setImageUrl("image url");
        mArtwork.setWebUrl("web url");
        mArtwork.setFavourite(true);

        mViewModel = new HomeNavHeaderViewModel(
                mArtworksProvider,
                mEventBusProvider,
                mAnalyticsProvider,
                mSharedPreferencesProvider,
                mSettingsProvider,
                MainApp.getDagger().getAppStringsProvider());
    }

    @Test
    public void verifyDefaultDataBinding() {
        // Verify
        assertThat(mViewModel.glue.detailsButtonVisible.get(), is(false));
        assertThat(mViewModel.glue.loadingIndicatorVisible.get(), is(false));
        assertThat(mViewModel.glue.loadImageFailedVisible.get(), is(false));
    }

    @Test
    public void beginNoLastSeenArtworkRandomFailed() {
        // Setup
        when(mSharedPreferencesProvider.getString("HomeNavHeader.LastHeaderImageId", "")).thenReturn("");
        when(mArtworksProvider.getRandomArtwork()).thenReturn(null);

        // Run
        mViewModel.begin(mDelegate);

        // Verify
        verify(mSharedPreferencesProvider).getString("HomeNavHeader.LastHeaderImageId", "");
        verify(mArtworksProvider, never()).getArtwork(anyString());
        verify(mArtworksProvider).getRandomArtwork();
        assertThat(mViewModel.glue.loadImageFailedVisible.get(), is(true));

        verifyNoMoreInteractions(mDelegate);
        verifyNoMoreInteractions(mSharedPreferencesProvider);
    }

    @Test
    public void beginNoLastSeenArtworkRandomSucceeded() {
        // Setup
        when(mSharedPreferencesProvider.getString("HomeNavHeader.LastHeaderImageId", "")).thenReturn("");
        when(mArtworksProvider.getRandomArtwork()).thenReturn(mArtwork);

        // Run
        mViewModel.begin(mDelegate);

        // Verify
        verify(mSharedPreferencesProvider).getString("HomeNavHeader.LastHeaderImageId", "");
        verify(mArtworksProvider, never()).getArtwork(anyString());
        verify(mArtworksProvider).getRandomArtwork();
        assertThat(mViewModel.glue.loadImageFailedVisible.get(), is(false));
        assertThat(mViewModel.glue.loadingIndicatorVisible.get(), is(true));
        verify(mSharedPreferencesProvider).saveString("HomeNavHeader.LastHeaderImageId", "guid");
        verify(mDelegate).loadImage("image url");

        verifyNoMoreInteractions(mDelegate);
        verifyNoMoreInteractions(mSharedPreferencesProvider);
    }

    @Test
    public void beginSettingEnabledRandomFailed() {
        // Setup
        when(mSharedPreferencesProvider.getString("HomeNavHeader.LastHeaderImageId", "")).thenReturn("");
        when(mSettingsProvider.isAutomaticHeaderImageEnabled()).thenReturn(true);
        when(mArtworksProvider.getRandomArtwork()).thenReturn(null);

        // Run
        mViewModel.begin(mDelegate);

        // Verify
        verify(mSharedPreferencesProvider).getString("HomeNavHeader.LastHeaderImageId", "");
        verify(mArtworksProvider, never()).getArtwork(anyString());
        verify(mArtworksProvider).getRandomArtwork();
        assertThat(mViewModel.glue.loadImageFailedVisible.get(), is(true));

        verifyNoMoreInteractions(mDelegate);
        verifyNoMoreInteractions(mSharedPreferencesProvider);
    }

    @Test
    public void beginSettingEnabledRandomSuccess() {
        // Setup
        when(mSharedPreferencesProvider.getString("HomeNavHeader.LastHeaderImageId", "")).thenReturn("");
        when(mSettingsProvider.isAutomaticHeaderImageEnabled()).thenReturn(true);
        when(mArtworksProvider.getRandomArtwork()).thenReturn(mArtwork);

        // Run
        mViewModel.begin(mDelegate);

        // Verify
        verify(mSharedPreferencesProvider).getString("HomeNavHeader.LastHeaderImageId", "");
        verify(mArtworksProvider, never()).getArtwork(anyString());
        verify(mArtworksProvider).getRandomArtwork();
        assertThat(mViewModel.glue.loadImageFailedVisible.get(), is(false));
        assertThat(mViewModel.glue.loadingIndicatorVisible.get(), is(true));
        verify(mSharedPreferencesProvider).saveString("HomeNavHeader.LastHeaderImageId", "guid");
        verify(mDelegate).loadImage("image url");

        verifyNoMoreInteractions(mDelegate);
        verifyNoMoreInteractions(mSharedPreferencesProvider);
    }

    @Test
    public void beginHasLastSeenArtwork() {
        // Setup
        when(mSharedPreferencesProvider.getString("HomeNavHeader.LastHeaderImageId", "")).thenReturn("guid");
        when(mArtworksProvider.getArtwork("guid")).thenReturn(mArtwork);

        // Run
        mViewModel.begin(mDelegate);

        // Verify
        verify(mSharedPreferencesProvider).getString("HomeNavHeader.LastHeaderImageId", "");
        verify(mArtworksProvider).getArtwork("guid");
        verify(mArtworksProvider, never()).getRandomArtwork();
        assertThat(mViewModel.glue.loadImageFailedVisible.get(), is(false));
        assertThat(mViewModel.glue.loadingIndicatorVisible.get(), is(true));
        verify(mSharedPreferencesProvider).saveString("HomeNavHeader.LastHeaderImageId", "guid");
        verify(mDelegate).loadImage("image url");

        verifyNoMoreInteractions(mDelegate);
        verifyNoMoreInteractions(mSharedPreferencesProvider);
    }

    @Test
    public void randomImageButtonSelected() {
        // Setup
        mViewModel.begin(mDelegate);
        reset(mAnalyticsProvider, mArtworksProvider, mSharedPreferencesProvider, mDelegate);

        // Run
        mViewModel.randomImageButtonSelected();

        // Verify
        verify(mAnalyticsProvider).trackEvent("ButtonClick", "NavHeaderView", "RandomImage");

        // remaining paths already covered.
    }

    @Test
    public void imageDetailsButtonSelected() {
        // Run
        mViewModel.imageDetailsButtonSelected();

        // Verify
        verify(mAnalyticsProvider).trackEvent("ButtonClick", "NavHeaderView", "ImageDetails");
        verify(mEventBusProvider).postEvent(any(HomeNavHeaderDetailsEvent.class));
    }

    @Test
    public void attached() {
        // Run
        mViewModel.attached();

        // Verify
        verify(mEventBusProvider).subscribe(mViewModel);
    }

    @Test
    public void detached() {
        // Run
        mViewModel.detached();

        // Verify
        verify(mEventBusProvider).unsubscribe(mViewModel);
    }

    @Test
    public void imageLoadSuccess() {
        // Run
        mViewModel.imageLoadSuccess();

        // Verify
        assertThat(mViewModel.glue.loadImageFailedVisible.get(), is(false));
        assertThat(mViewModel.glue.detailsButtonVisible.get(), is(true));
    }

    @Test
    public void imageLoadFailed() {
        // Run
        mViewModel.imageLoadFailed();

        // Verify
        assertThat(mViewModel.glue.loadImageFailedVisible.get(), is(true));
        assertThat(mViewModel.glue.detailsButtonVisible.get(), is(false));
    }

    @Test
    public void onDataLoadEventLoaded() {
        // Run
        mViewModel.onEvent(DataLoadEvent.createLoadedEvent());

        // Verify
        verify(mArtworksProvider).getRandomArtwork();

        // Rest of random artwork already covered.
    }

    @Test
    public void onDataLoadEventFailed() {
        // Run
        mViewModel.onEvent(DataLoadEvent.createFailedEvent());

        // Verify
        verifyZeroInteractions(mArtworksProvider);
    }
}