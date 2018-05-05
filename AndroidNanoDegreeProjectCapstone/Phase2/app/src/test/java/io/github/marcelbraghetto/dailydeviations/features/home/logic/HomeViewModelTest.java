package io.github.marcelbraghetto.dailydeviations.features.home.logic;

import android.content.Intent;
import android.support.v4.app.Fragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import io.github.marcelbraghetto.dailydeviations.BuildConfig;
import io.github.marcelbraghetto.dailydeviations.R;
import io.github.marcelbraghetto.dailydeviations.features.about.ui.AboutFragment;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.features.collection.logic.CollectionArguments;
import io.github.marcelbraghetto.dailydeviations.features.collection.logic.providers.contracts.CollectionProvider;
import io.github.marcelbraghetto.dailydeviations.features.collection.ui.CollectionFragment;
import io.github.marcelbraghetto.dailydeviations.features.settings.ui.SettingsFragment;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.contracts.ArtworksProvider;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.CollectionFilterMode;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.AnalyticsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.events.CollectionFilterModeToggleEvent;
import io.github.marcelbraghetto.dailydeviations.testconfig.RobolectricProperties;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by Marcel Braghetto on 13/06/16.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class HomeViewModelTest {
    @Mock ArtworksProvider mArtworksProvider;
    @Mock EventBusProvider mEventBusProvider;
    @Mock CollectionProvider mCollectionProvider;
    @Mock AnalyticsProvider mAnalyticsProvider;
    @Mock HomeViewModel.Actions mDelegate;
    private HomeViewModel mViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mViewModel = new HomeViewModel(
                MainApp.getDagger().getApplicationContext(),
                mArtworksProvider,
                mEventBusProvider,
                MainApp.getDagger().getAppStringsProvider(),
                mCollectionProvider,
                mAnalyticsProvider);
    }

    private void resetAll() {
        reset(mArtworksProvider, mEventBusProvider, mCollectionProvider, mAnalyticsProvider, mDelegate);
    }

    @Test
    public void verifyDefaultDataBindings() {
        // Verify
        assertThat(mViewModel.glue.navigationTitle.get(), is(""));
        assertThat(mViewModel.glue.selectedMenuId.get(), is(R.id.nav_menu_browse));
    }

    @Test
    public void beginFilterModeFavourites() {
        // Setup
        ArgumentCaptor<Fragment> fragmentCaptor = new ArgumentCaptor<>();
        when(mCollectionProvider.getCollectionFilterMode()).thenReturn(CollectionFilterMode.Favourites);

        // Run
        mViewModel.begin(mDelegate);

        // Verify
        assertThat(mViewModel.glue.navigationTitle.get(), is("My Deviations"));
        assertThat(mViewModel.glue.selectedMenuId.get(), is(R.id.nav_menu_browse));
        verify(mDelegate).showToggleButtons();
        verify(mDelegate).replaceContent(fragmentCaptor.capture(), eq(false));

        Fragment fragment = fragmentCaptor.getValue();
        CollectionArguments arguments = new CollectionArguments(fragment.getArguments());
        assertThat(fragment, instanceOf(CollectionFragment.class));
        assertThat(arguments.getFilterMode(), is(CollectionFilterMode.Favourites));
    }

    @Test
    public void beginFilterModeAll() {
        // Setup
        ArgumentCaptor<Fragment> fragmentCaptor = new ArgumentCaptor<>();
        when(mCollectionProvider.getCollectionFilterMode()).thenReturn(CollectionFilterMode.All);

        // Run
        mViewModel.begin(mDelegate);

        // Verify
        assertThat(mViewModel.glue.navigationTitle.get(), is("Browse"));
        assertThat(mViewModel.glue.selectedMenuId.get(), is(R.id.nav_menu_browse));
        verify(mDelegate).showToggleButtons();
        verify(mDelegate).replaceContent(fragmentCaptor.capture(), eq(false));

        Fragment fragment = fragmentCaptor.getValue();
        CollectionArguments arguments = new CollectionArguments(fragment.getArguments());
        assertThat(fragment, instanceOf(CollectionFragment.class));
        assertThat(arguments.getFilterMode(), is(CollectionFilterMode.All));
    }

    @Test
    public void menuItemSelectedUnknownId() {
        // Setup
        when(mCollectionProvider.getCollectionFilterMode()).thenReturn(CollectionFilterMode.All);
        mViewModel.begin(mDelegate);
        resetAll();

        // Run
        mViewModel.menuItemSelected(-1);

        // Verify
        verify(mDelegate).closeNavigationMenu();
        verifyNoMoreInteractions(mDelegate);
        verifyZeroInteractions(mAnalyticsProvider);
    }

    @Test
    public void menuItemSelectedBrowse() {
        // Setup
        when(mCollectionProvider.getCollectionFilterMode()).thenReturn(CollectionFilterMode.Favourites);
        mViewModel.begin(mDelegate);
        reset(mDelegate, mAnalyticsProvider);

        // Run
        mViewModel.menuItemSelected(R.id.nav_menu_browse);

        // Verify
        verify(mDelegate).closeNavigationMenu();
        assertThat(mViewModel.glue.selectedMenuId.get(), is(R.id.nav_menu_browse));
        verify(mAnalyticsProvider).trackEvent("MenuItemSelected", "HomeScreen", "Browse");
        verify(mDelegate).replaceContent(any(Fragment.class), eq(true));
        verify(mDelegate).showToggleButtons();
        verifyNoMoreInteractions(mDelegate);
    }

    @Test
    public void menuItemSelectedTellFriends() {
        // Setup
        ArgumentCaptor<Intent> intentCaptor = new ArgumentCaptor<>();
        when(mCollectionProvider.getCollectionFilterMode()).thenReturn(CollectionFilterMode.Favourites);
        mViewModel.begin(mDelegate);
        reset(mDelegate, mAnalyticsProvider);

        // Run
        mViewModel.menuItemSelected(R.id.nav_menu_tell_friends);

        // Verify
        verify(mDelegate).closeNavigationMenu();
        assertThat(mViewModel.glue.selectedMenuId.get(), is(R.id.nav_menu_browse));
        verify(mAnalyticsProvider).trackEvent("MenuItemSelected", "HomeScreen", "TellFriends");
        verify(mDelegate).startActivityForResult(intentCaptor.capture(), eq(666));

        Intent intent = intentCaptor.getValue();
        assertThat(intent.getAction(), is("com.google.android.gms.appinvite.ACTION_APP_INVITE"));
        assertThat(intent.getStringExtra("com.google.android.gms.appinvite.TITLE"), is("Daily Deviations App!"));
        assertThat(intent.getStringExtra("com.google.android.gms.appinvite.MESSAGE"), is("Keep up to date with the Daily Deviations app."));
        assertThat(intent.getStringExtra("com.google.android.gms.appinvite.BUTTON_TEXT"), is("Get the app!"));

        verifyNoMoreInteractions(mDelegate);
    }

    @Test
    public void menuItemSelectedRateApp() {
        // Setup
        ArgumentCaptor<Intent> intentCaptor = new ArgumentCaptor<>();
        when(mCollectionProvider.getCollectionFilterMode()).thenReturn(CollectionFilterMode.Favourites);
        mViewModel.begin(mDelegate);
        reset(mDelegate, mAnalyticsProvider);

        // Run
        mViewModel.menuItemSelected(R.id.nav_menu_rate_app);

        // Verify
        verify(mDelegate).closeNavigationMenu();
        assertThat(mViewModel.glue.selectedMenuId.get(), is(R.id.nav_menu_browse));
        verify(mAnalyticsProvider).trackEvent("MenuItemSelected", "HomeScreen", "RateApp");
        verify(mDelegate).startActivity(intentCaptor.capture());

        Intent intent = intentCaptor.getValue();
        assertThat(intent.getAction(), is(Intent.ACTION_VIEW));
        assertThat(intent.getData().toString(), is("https://play.google.com/store/apps/details?id=io.github.marcelbraghetto.dailydeviations"));

        verifyNoMoreInteractions(mDelegate);
    }

    @Test
    public void menuItemSelectedSettings() {
        // Setup
        ArgumentCaptor<Fragment> fragmentCaptor = new ArgumentCaptor<>();
        when(mCollectionProvider.getCollectionFilterMode()).thenReturn(CollectionFilterMode.Favourites);
        mViewModel.begin(mDelegate);
        reset(mDelegate, mAnalyticsProvider);

        // Run
        mViewModel.menuItemSelected(R.id.nav_menu_settings);

        // Verify
        verify(mDelegate).closeNavigationMenu();
        assertThat(mViewModel.glue.selectedMenuId.get(), is(R.id.nav_menu_settings));
        verify(mAnalyticsProvider).trackEvent("MenuItemSelected", "HomeScreen", "Settings");
        assertThat(mViewModel.glue.navigationTitle.get(), is("Settings"));
        verify(mDelegate).hideToggleButtons();
        verify(mDelegate).replaceContent(fragmentCaptor.capture(), eq(true));
        assertThat(fragmentCaptor.getValue(), instanceOf(SettingsFragment.class));

        verifyNoMoreInteractions(mDelegate);
    }

    @Test
    public void menuItemSelectedAbout() {
        // Setup
        ArgumentCaptor<Fragment> fragmentCaptor = new ArgumentCaptor<>();
        when(mCollectionProvider.getCollectionFilterMode()).thenReturn(CollectionFilterMode.Favourites);
        mViewModel.begin(mDelegate);
        reset(mDelegate, mAnalyticsProvider);

        // Run
        mViewModel.menuItemSelected(R.id.nav_menu_about);

        // Verify
        verify(mDelegate).closeNavigationMenu();
        assertThat(mViewModel.glue.selectedMenuId.get(), is(R.id.nav_menu_about));
        verify(mAnalyticsProvider).trackEvent("MenuItemSelected", "HomeScreen", "About");
        assertThat(mViewModel.glue.navigationTitle.get(), is("About"));
        verify(mDelegate).hideToggleButtons();
        verify(mDelegate).replaceContent(fragmentCaptor.capture(), eq(true));
        assertThat(fragmentCaptor.getValue(), instanceOf(AboutFragment.class));

        verifyNoMoreInteractions(mDelegate);
    }

    @Test
    public void backPressedOnBrowseScreen() {
        // Setup
        when(mCollectionProvider.getCollectionFilterMode()).thenReturn(CollectionFilterMode.Favourites);
        mViewModel.begin(mDelegate);
        resetAll();

        // Run
        mViewModel.backPressed();

        // Verify
        verify(mDelegate).finishActivity();
        verifyNoMoreInteractions(mDelegate);
    }

    @Test
    public void backPressedNotOnBrowseScreen() {
        // Setup
        when(mCollectionProvider.getCollectionFilterMode()).thenReturn(CollectionFilterMode.Favourites);
        mViewModel.begin(mDelegate);
        mViewModel.menuItemSelected(R.id.nav_menu_about);
        reset(mDelegate);

        // Run
        mViewModel.backPressed();

        // Verify
        verify(mDelegate, never()).finishActivity();
        verify(mCollectionProvider, times(2)).getCollectionFilterMode();
        verify(mDelegate).replaceContent(any(Fragment.class), eq(true));
        verify(mDelegate).showToggleButtons();
    }

    @Test
    public void screenStartedNoSavedArtworks() {
        // Setup
        when(mArtworksProvider.hasSavedArtworks()).thenReturn(false);

        // Run
        mViewModel.screenStarted();

        // Verify
        verify(mAnalyticsProvider).trackScreenView("HomeScreen");
        verify(mEventBusProvider).subscribe(mViewModel);
        verify(mArtworksProvider).hasSavedArtworks();
        verify(mArtworksProvider).refreshData();
    }

    @Test
    public void screenStartedWithSavedArtworks() {
        // Setup
        when(mArtworksProvider.hasSavedArtworks()).thenReturn(true);

        // Run
        mViewModel.screenStarted();

        // Verify
        verify(mAnalyticsProvider).trackScreenView("HomeScreen");
        verify(mEventBusProvider).subscribe(mViewModel);
        verify(mArtworksProvider).hasSavedArtworks();
        verify(mArtworksProvider, never()).refreshData();
    }

    @Test
    public void screenStopped() {
        // Run
        mViewModel.screenStopped();

        // Verify
        verify(mEventBusProvider).unsubscribe(mViewModel);
    }

    @Test
    public void onCollectionFilterModeToggleEvent() {
        // Setup
        when(mCollectionProvider.getCollectionFilterMode()).thenReturn(CollectionFilterMode.Favourites);
        mViewModel.begin(mDelegate);
        reset(mDelegate);

        // Run
        mViewModel.onEvent(new CollectionFilterModeToggleEvent());

        // Verify
        verify(mCollectionProvider, times(2)).getCollectionFilterMode();
        verify(mDelegate).replaceContent(any(Fragment.class), eq(true));
    }

    @Test
    public void onHomeNavHeaderDetailsEvent() {
        // Setup
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        Artwork artworkIn = new Artwork();
        artworkIn.setTitle("test artwork");
        HomeNavHeaderDetailsEvent event = new HomeNavHeaderDetailsEvent(artworkIn);
        when(mCollectionProvider.getCollectionFilterMode()).thenReturn(CollectionFilterMode.Favourites);
        mViewModel.begin(mDelegate);
        reset(mDelegate);

        // Run
        mViewModel.onEvent(event);

        // Verify
        verify(mDelegate).closeNavigationMenu();
        verify(mDelegate).startActivity(intentCaptor.capture());

        Intent intent = intentCaptor.getValue();
        assertThat(intent.getComponent().getClassName(), is("io.github.marcelbraghetto.dailydeviations.features.detail.ui.DetailActivity"));

        Artwork artworkOut = Artwork.getFrom(intent);
        assertThat(artworkOut.getTitle(), is("test artwork"));
    }

}