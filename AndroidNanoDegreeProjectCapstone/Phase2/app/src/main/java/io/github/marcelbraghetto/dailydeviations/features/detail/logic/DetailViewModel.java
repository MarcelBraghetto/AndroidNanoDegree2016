package io.github.marcelbraghetto.dailydeviations.features.detail.logic;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import io.github.marcelbraghetto.dailydeviations.R;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.features.info.ui.InfoActivity;
import io.github.marcelbraghetto.dailydeviations.features.wallpaper.events.WallpaperEvent;
import io.github.marcelbraghetto.dailydeviations.features.wallpaper.providers.contracts.WallpaperProvider;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.contracts.ArtworksProvider;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.Analytics;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.AnalyticsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.core.BaseViewModel;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusSubscriber;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.sharedpreferences.contracts.SharedPreferencesProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.strings.contracts.StringsProvider;

/**
 * Created by Marcel Braghetto on 2/03/16.
 *
 * View model for displaying the detail screen for a given selected artwork.
 */
public class DetailViewModel extends BaseViewModel<DetailViewModel.Actions> implements EventBusSubscriber {
    //region Data binding
    public static class Glue {
        public final ObservableField<String> navigationTitleText = new ObservableField<>("");
        public final ObservableField<String> navigationSubtitleText = new ObservableField<>("");
        public final ObservableField<String> imageAccessibilityText = new ObservableField<>("");
        public final ObservableInt toggleButtonDrawableId = new ObservableInt(R.drawable.icon_fab_off);

        private void setNavigationTitleText(@NonNull String value) {
            navigationTitleText.set(value);
        }

        private void setNavigationSubtitleText(@NonNull String value) {
            navigationSubtitleText.set(value);
        }

        private void setImageAccessibilityText(@NonNull String value) {
            imageAccessibilityText.set(value);
        }

        private void setToggleButtonOn() {
            toggleButtonDrawableId.set(R.drawable.icon_fab_on);
        }

        private void setToggleButtonOff() {
            toggleButtonDrawableId.set(R.drawable.icon_fab_off);
        }
    }
    public final Glue glue = new Glue();
    //endregion

    //region Private fields
    private static final String SCREEN_NAME = "ArtworkDetailsScreen";
    private static final String PREF_GREETING_SEEN = "ArtworkDetails.GreetingSeen";

    private final Context mApplicationContext;
    private final StringsProvider mStringsProvider;
    private final SharedPreferencesProvider mSharedPreferencesProvider;
    private final WallpaperProvider mWallpaperProvider;
    private final EventBusProvider mEventBusProvider;
    private final AnalyticsProvider mAnalyticsProvider;
    private final ArtworksProvider mArtworksProvider;

    private Artwork mArtwork;
    //endregion

    //region Public methods
    @Inject
    public DetailViewModel(@NonNull Context applicationContext,
                           @NonNull StringsProvider stringsProvider,
                           @NonNull SharedPreferencesProvider sharedPreferencesProvider,
                           @NonNull WallpaperProvider wallpaperProvider,
                           @NonNull EventBusProvider eventBusProvider,
                           @NonNull AnalyticsProvider analyticsProvider,
                           @NonNull ArtworksProvider artworksProvider) {

        super(Actions.class);

        mApplicationContext = applicationContext;
        mStringsProvider = stringsProvider;
        mSharedPreferencesProvider = sharedPreferencesProvider;
        mWallpaperProvider = wallpaperProvider;
        mEventBusProvider = eventBusProvider;
        mAnalyticsProvider = analyticsProvider;
        mArtworksProvider = artworksProvider;
    }

    /**
     * Initialise the presenter with the given callback and configuration bundle.
     * @param actionDelegate to callback to.
     * @param intent containing configuration data.
     */
    public void begin(@Nullable Actions actionDelegate, @NonNull Intent intent) {
        setActionDelegate(actionDelegate);

        mArtwork = Artwork.getFrom(intent);

        if(mArtwork == null) {
            throw new UnsupportedOperationException("A valid artwork instance must be passed into the detail presenter");
        }

        glue.setNavigationTitleText(mArtwork.getTitle());
        glue.setNavigationSubtitleText(mArtwork.getAuthor());
        glue.setImageAccessibilityText(mArtwork.getTitle());
        refreshToggleButton();

        mActionDelegate.loadImage(mArtwork.getImageUrl(), mApplicationContext.getResources().getInteger(R.integer.maximum_image_dimension));

        // If we've never shown the greeting message to hint to the user how to pinch and
        // zoom to interact with images, then show it!
        if(!mSharedPreferencesProvider.getBoolean(PREF_GREETING_SEEN, false)) {
            mSharedPreferencesProvider.saveBoolean(PREF_GREETING_SEEN, true);
            mActionDelegate.showSnackbar(mStringsProvider.getString(R.string.details_greeting_message));
        }
    }

    /**
     * User selected the given menu options for the given artwork.
     * @param selectedItemId of the menu option chosen by the user.
     */
    public boolean artworkMenuItemSelected(@IdRes int selectedItemId) {
        boolean result = true;

        switch (selectedItemId) {
            case R.id.action_set_as_wallpaper: {
                mAnalyticsProvider.trackEvent(Analytics.CONTENT_TYPE_MENU_ITEM_SELECTED, SCREEN_NAME, "SetWallpaper");
                mWallpaperProvider.setPhoneWallpaper(mArtwork.getImageUrl(), false);
                break;
            }

            case R.id.action_author_info: {
                mAnalyticsProvider.trackEvent(Analytics.CONTENT_TYPE_MENU_ITEM_SELECTED, SCREEN_NAME, "AuthorInfo");
                Intent intent = new Intent(mApplicationContext, InfoActivity.class);
                mArtwork.putInto(intent);
                mActionDelegate.startActivity(intent);
                break;
            }

            case R.id.action_open_in_browser: {
                mAnalyticsProvider.trackEvent(Analytics.CONTENT_TYPE_MENU_ITEM_SELECTED, SCREEN_NAME, "OpenInBrowser");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mArtwork.getWebUrl()));
                mActionDelegate.startActivity(intent);
                break;
            }

            case R.id.action_share: {
                mAnalyticsProvider.trackEvent(Analytics.CONTENT_TYPE_MENU_ITEM_SELECTED, SCREEN_NAME, "Share");
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, mStringsProvider.getString(R.string.details_share_subject, mArtwork.getTitle()));
                intent.putExtra(Intent.EXTRA_TEXT, mArtwork.getWebUrl());
                mActionDelegate.startActivity(intent);

                MainApp.getDagger().getArtworksProvider().saveFavourite(mArtwork.getGuid(), !mArtwork.isFavourite());
                break;
            }

            default: {
                result = false;
            }
        }

        return result;
    }

    /**
     * User tapped the toggle button.
     */
    public void toggleFavouriteSelected() {
        mArtwork.setFavourite(!mArtwork.isFavourite());
        mArtworksProvider.saveFavourite(mArtwork.getGuid(), mArtwork.isFavourite());
        refreshToggleButton();

        if(mArtwork.isFavourite()) {
            mActionDelegate.showSnackbar(mStringsProvider.getString(R.string.details_favourite_added, mArtwork.getTitle()));
        } else {
            mActionDelegate.showSnackbar(mStringsProvider.getString(R.string.details_favourite_removed, mArtwork.getTitle()));
        }

        // Since we have triggered an action that changes the data in content provider, we have
        // to prevent the shared element exit transition animation because it causes some VERY
        // strange image glitches in the recycler view.
        mActionDelegate.preventSharedElementExitTransition();
    }
    //endregion

    //region Private methods
    private void refreshToggleButton() {
        if(mArtwork.isFavourite()) {
            glue.setToggleButtonOn();
        } else {
            glue.setToggleButtonOff();
        }
    }
    //endregion

    //region Event bus
    @Override
    public void subscribeToEventBus() {
        mEventBusProvider.subscribe(this);
    }

    @Override
    public void unsubscribeFromEventBus() {
        mEventBusProvider.unsubscribe(this);
    }

    public void screenStarted() {
        mAnalyticsProvider.trackScreenView(SCREEN_NAME);
        subscribeToEventBus();
    }

    public void screenStopped() {
        unsubscribeFromEventBus();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull WallpaperEvent event) {
        switch (event.getReason()) {
            case PhoneWallpaperSuccess:
                mActionDelegate.showSnackbar(mStringsProvider.getString(R.string.set_phone_wallpaper_success));
                break;
            case PhoneWallpaperFailed:
                mActionDelegate.showSnackbar(mStringsProvider.getString(R.string.set_phone_wallpaper_failed));
                break;
        }
    }
    //endregion

    //region View model action contract
    public interface Actions {
        /**
         * Load the given image url into the main view - this will typically be from the
         * local storage cache provided by the Glide image library so should be almost instant.
         * @param url to load into the image view.
         * @param maximumImageSize dimension to allow the bitmap to be loaded as.
         */
        void loadImage(@NonNull String url, int maximumImageSize);

        /**
         * Show the given message as a 'greeting' typically as a Snackbar.
         * @param message to display.
         */
        void showSnackbar(@NonNull String message);

        /**
         * Initiate the given intent from the host activity.
         * @param intent to start.
         */
        void startActivity(@NonNull Intent intent);

        /**
         * Prevent the shared element exit transition of the activity when it is finished
         * to correct a bug where changes in the content provider on a previous screen
         * cause weird image glitches when the shared element transition attempts to animate
         * back to the previous screen.
         */
        void preventSharedElementExitTransition();
    }
    //endregion
}
