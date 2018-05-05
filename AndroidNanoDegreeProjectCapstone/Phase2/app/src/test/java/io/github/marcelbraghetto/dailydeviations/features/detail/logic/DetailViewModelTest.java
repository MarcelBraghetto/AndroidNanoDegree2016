package io.github.marcelbraghetto.dailydeviations.features.detail.logic;

import android.content.Intent;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import io.github.marcelbraghetto.dailydeviations.BuildConfig;
import io.github.marcelbraghetto.dailydeviations.R;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.features.wallpaper.events.WallpaperEvent;
import io.github.marcelbraghetto.dailydeviations.features.wallpaper.providers.contracts.WallpaperProvider;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.contracts.ArtworksProvider;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.AnalyticsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.sharedpreferences.contracts.SharedPreferencesProvider;
import io.github.marcelbraghetto.dailydeviations.testconfig.RobolectricProperties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
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
public class DetailViewModelTest {
    @Mock SharedPreferencesProvider mSharedPreferencesProvider;
    @Mock WallpaperProvider mWallpaperProvider;
    @Mock EventBusProvider mEventBusProvider;
    @Mock AnalyticsProvider mAnalyticsProvider;
    @Mock ArtworksProvider mArtworksProvider;
    @Mock DetailViewModel.Actions mDelegate;
    private DetailViewModel mViewModel;
    private Artwork mArtwork;
    private Intent mIntent;

    @Rule public ExpectedException mExpectedException = ExpectedException.none();

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

        mIntent = new Intent();
        mArtwork.putInto(mIntent);

        mViewModel = new DetailViewModel(
                MainApp.getDagger().getApplicationContext(),
                MainApp.getDagger().getAppStringsProvider(),
                mSharedPreferencesProvider,
                mWallpaperProvider,
                mEventBusProvider,
                mAnalyticsProvider,
                mArtworksProvider);
    }

    @Test
    public void verifyDefaultDataBindings() {
        // Verify
        assertThat(mViewModel.glue.navigationTitleText.get(), is(""));
        assertThat(mViewModel.glue.navigationSubtitleText.get(), is(""));
        assertThat(mViewModel.glue.imageAccessibilityText.get(), is(""));
        assertThat(mViewModel.glue.toggleButtonDrawableId.get(), is(R.drawable.icon_fab_off));
    }

    @Test
    public void beginMissingIntentArtwork() {
        // Setup
        mExpectedException.expect(UnsupportedOperationException.class);

        mViewModel.begin(mDelegate, new Intent());
    }

    @Test
    public void beginHasNotSeenGreeting() {
        // Setup
        when(mSharedPreferencesProvider.getBoolean("ArtworkDetails.GreetingSeen", false)).thenReturn(false);

        // Run
        mViewModel.begin(mDelegate, mIntent);

        // Verify
        assertThat(mViewModel.glue.navigationTitleText.get(), is("title"));
        assertThat(mViewModel.glue.navigationSubtitleText.get(), is("author name"));
        assertThat(mViewModel.glue.imageAccessibilityText.get(), is("title"));
        assertThat(mViewModel.glue.toggleButtonDrawableId.get(), is(R.drawable.icon_fab_on));

        verify(mDelegate).loadImage("image url", 1024);
        verify(mSharedPreferencesProvider).getBoolean("ArtworkDetails.GreetingSeen", false);
        verify(mSharedPreferencesProvider).saveBoolean("ArtworkDetails.GreetingSeen", true);
        verify(mDelegate).showSnackbar("Pinch and zoom the image to explore it!");
    }

    @Test
    public void beginHasSeenGreeting() {
        // Setup
        when(mSharedPreferencesProvider.getBoolean("ArtworkDetails.GreetingSeen", false)).thenReturn(true);

        // Run
        mViewModel.begin(mDelegate, mIntent);

        // Verify
        assertThat(mViewModel.glue.navigationTitleText.get(), is("title"));
        assertThat(mViewModel.glue.navigationSubtitleText.get(), is("author name"));
        assertThat(mViewModel.glue.imageAccessibilityText.get(), is("title"));
        assertThat(mViewModel.glue.toggleButtonDrawableId.get(), is(R.drawable.icon_fab_on));

        verify(mDelegate).loadImage("image url", 1024);
        verify(mSharedPreferencesProvider).getBoolean("ArtworkDetails.GreetingSeen", false);
        verify(mSharedPreferencesProvider, never()).saveBoolean(anyString(), anyBoolean());
        verify(mDelegate, never()).showSnackbar(anyString());
    }

    @Test
    public void artworkMenuItemSelectedUnknownId() {
        // Setup
        mViewModel.begin(mDelegate, mIntent);
        reset(mDelegate);

        // Run
        boolean result = mViewModel.artworkMenuItemSelected(0);

        // Verify
        verifyZeroInteractions(mDelegate);
        verifyZeroInteractions(mAnalyticsProvider);
        verifyZeroInteractions(mWallpaperProvider);
        assertThat(result, is(false));
    }

    @Test
    public void artworkMenuItemSelectedSetWallpaper() {
        // Setup
        mViewModel.begin(mDelegate, mIntent);
        reset(mDelegate);

        // Run
        boolean result = mViewModel.artworkMenuItemSelected(R.id.action_set_as_wallpaper);

        // Verify
        verify(mAnalyticsProvider).trackEvent("MenuItemSelected", "ArtworkDetailsScreen", "SetWallpaper");
        verify(mWallpaperProvider).setPhoneWallpaper("image url", false);
        assertThat(result, is(true));
    }

    @Test
    public void artworkMenuItemSelectedAuthorInfo() {
        // Setup
        ArgumentCaptor<Intent> intentCaptor = new ArgumentCaptor<>();
        mViewModel.begin(mDelegate, mIntent);
        reset(mDelegate);

        // Run
        boolean result = mViewModel.artworkMenuItemSelected(R.id.action_author_info);

        // Verify
        verify(mAnalyticsProvider).trackEvent("MenuItemSelected", "ArtworkDetailsScreen", "AuthorInfo");
        verify(mDelegate).startActivity(intentCaptor.capture());

        Intent intent = intentCaptor.getValue();
        assertThat(intent.getComponent().getClassName(), is("io.github.marcelbraghetto.dailydeviations.features.info.ui.InfoActivity"));

        Artwork artwork = Artwork.getFrom(intent);
        assertThat(artwork.getTitle(), is("title"));

        assertThat(result, is(true));
    }

    @Test
    public void artworkMenuItemSelectedOpenInBrowser() {
        // Setup
        ArgumentCaptor<Intent> intentCaptor = new ArgumentCaptor<>();
        mViewModel.begin(mDelegate, mIntent);
        reset(mDelegate);

        // Run
        boolean result = mViewModel.artworkMenuItemSelected(R.id.action_open_in_browser);

        // Verify
        verify(mAnalyticsProvider).trackEvent("MenuItemSelected", "ArtworkDetailsScreen", "OpenInBrowser");
        verify(mDelegate).startActivity(intentCaptor.capture());

        Intent intent = intentCaptor.getValue();
        assertThat(intent.getAction(), is(Intent.ACTION_VIEW));
        assertThat(intent.getData().toString(), is("web url"));

        assertThat(result, is(true));
    }

    @Test
    public void artworkMenuItemSelectedShare() {
        // Setup
        ArgumentCaptor<Intent> intentCaptor = new ArgumentCaptor<>();
        mViewModel.begin(mDelegate, mIntent);
        reset(mDelegate);

        // Run
        boolean result = mViewModel.artworkMenuItemSelected(R.id.action_share);

        // Verify
        verify(mAnalyticsProvider).trackEvent("MenuItemSelected", "ArtworkDetailsScreen", "Share");
        verify(mDelegate).startActivity(intentCaptor.capture());

        Intent intent = intentCaptor.getValue();
        assertThat(intent.getAction(), is(Intent.ACTION_SEND));
        assertThat(intent.getType(), is("text/plain"));
        assertThat(intent.getStringExtra(Intent.EXTRA_SUBJECT), is("Daily Deviations: title"));
        assertThat(intent.getStringExtra(Intent.EXTRA_TEXT), is("web url"));

        assertThat(result, is(true));
    }

    @Test
    public void screenStarted() {
        // Run
        mViewModel.screenStarted();

        // Verify
        verify(mAnalyticsProvider).trackScreenView("ArtworkDetailsScreen");
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
    public void onWallpaperEventSuccess() {
        // Setup
        mViewModel.begin(mDelegate, mIntent);
        reset(mDelegate);

        // Run
        mViewModel.onEvent(new WallpaperEvent(WallpaperEvent.Reason.PhoneWallpaperSuccess));

        // Verify
        verify(mDelegate).showSnackbar("Wallpaper updated!");
    }

    @Test
    public void onWallpaperEventFailed() {
        // Setup
        mViewModel.begin(mDelegate, mIntent);
        reset(mDelegate);

        // Run
        mViewModel.onEvent(new WallpaperEvent(WallpaperEvent.Reason.PhoneWallpaperFailed));

        // Verify
        verify(mDelegate).showSnackbar("Unable to set wallpaper");
    }

    @Test
    public void toggleFavouriteSelectedTurnOff() {
        // Setup
        mArtwork.setFavourite(true);
        mViewModel.begin(mDelegate, mIntent);
        reset(mDelegate, mArtworksProvider);

        // Run
        mViewModel.toggleFavouriteSelected();

        // Verify
        verify(mArtworksProvider).saveFavourite("guid", false);
        assertThat(mViewModel.glue.toggleButtonDrawableId.get(), is(R.drawable.icon_fab_off));
        verify(mDelegate).showSnackbar("title removed from your collection");
        verify(mDelegate).preventSharedElementExitTransition();
    }

    @Test
    public void toggleFavouriteSelectedTurnOn() {
        // Setup
        mArtwork.setFavourite(false);
        mViewModel.begin(mDelegate, mIntent);
        reset(mDelegate, mArtworksProvider);

        // Run
        mViewModel.toggleFavouriteSelected();

        // Verify
        verify(mArtworksProvider).saveFavourite("guid", true);
        assertThat(mViewModel.glue.toggleButtonDrawableId.get(), is(R.drawable.icon_fab_on));
        verify(mDelegate).showSnackbar("title added to your collection");
        verify(mDelegate).preventSharedElementExitTransition();
    }
}