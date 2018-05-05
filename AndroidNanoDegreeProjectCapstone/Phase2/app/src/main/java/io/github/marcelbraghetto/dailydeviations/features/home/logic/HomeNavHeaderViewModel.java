package io.github.marcelbraghetto.dailydeviations.features.home.logic;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import io.github.marcelbraghetto.dailydeviations.R;
import io.github.marcelbraghetto.dailydeviations.features.settings.logic.SettingsChangeEvent;
import io.github.marcelbraghetto.dailydeviations.features.settings.logic.providers.contracts.SettingsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.contracts.ArtworksProvider;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.Analytics;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.AnalyticsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.core.BaseViewModel;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusSubscriber;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.events.DataLoadEvent;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.sharedpreferences.contracts.SharedPreferencesProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.strings.contracts.StringsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.utils.StringUtils;

/**
 * Created by Marcel Braghetto on 6/06/16.
 *
 * View model for the home navigation header.
 */
public class HomeNavHeaderViewModel extends BaseViewModel<HomeNavHeaderViewModel.Actions> implements EventBusSubscriber {
    //region Data binding
    public final Glue glue = new Glue();
    public static class Glue {
        public final ObservableBoolean detailsButtonVisible = new ObservableBoolean(false);
        public final ObservableBoolean loadingIndicatorVisible = new ObservableBoolean(false);
        public final ObservableBoolean loadImageFailedVisible = new ObservableBoolean(false);
        public final ObservableInt lockIconResourceId = new ObservableInt(R.drawable.icon_lock_open);
    }
    //endregion

    //region Private fields
    private static final String PREF_LAST_HEADER_IMAGE_ID = "HomeNavHeader.LastHeaderImageId";
    private static final String SCREEN_NAME = "NavHeaderView";

    private final ArtworksProvider mArtworksProvider;
    private final EventBusProvider mEventBusProvider;
    private final AnalyticsProvider mAnalyticsProvider;
    private final SharedPreferencesProvider mSharedPreferencesProvider;
    private final SettingsProvider mSettingsProvider;
    private final StringsProvider mStringsProvider;

    private Artwork mArtwork;
    //endregion

    //region Public methods
    @Inject
    public HomeNavHeaderViewModel(@NonNull ArtworksProvider artworksProvider,
                                  @NonNull EventBusProvider eventBusProvider,
                                  @NonNull AnalyticsProvider analyticsProvider,
                                  @NonNull SharedPreferencesProvider sharedPreferencesProvider,
                                  @NonNull SettingsProvider settingsProvider,
                                  @NonNull StringsProvider stringsProvider) {
        super(Actions.class);

        mArtworksProvider = artworksProvider;
        mEventBusProvider = eventBusProvider;
        mAnalyticsProvider = analyticsProvider;
        mSharedPreferencesProvider = sharedPreferencesProvider;
        mSettingsProvider = settingsProvider;
        mStringsProvider = stringsProvider;
    }

    /**
     * Start the view model.
     */
    public void begin(@Nullable Actions actionDelegate) {
        setActionDelegate(actionDelegate);

        loadLastSeenArtwork();

        refreshLockIcon();
        if(mArtwork == null || mSettingsProvider.isAutomaticHeaderImageEnabled()) {
            chooseRandomImage();
        } else {
            displayCurrentImage();
        }
    }

    /**
     * User tapped the lock icon to toggle locking the header image.
     */
    public void lockImageButtonSelected() {
        if(mSettingsProvider.isAutomaticHeaderImageEnabled()) {
            mSettingsProvider.disableAutomaticHeaderImage();
            mEventBusProvider.postEvent(new HomeSnackbarEvent(mStringsProvider.getString(R.string.settings_option_header_image_disabled)));
        } else {
            mSettingsProvider.enableAutomaticHeaderImage();
            mEventBusProvider.postEvent(new HomeSnackbarEvent(mStringsProvider.getString(R.string.settings_option_header_image_enabled)));
        }
        refreshLockIcon();
    }

    /**
     * User tapped the 'random image' button so we should choose another image at random.
     */
    public void randomImageButtonSelected() {
        mAnalyticsProvider.trackEvent(Analytics.CONTENT_TYPE_BUTTON_CLICK, SCREEN_NAME, "RandomImage");
        chooseRandomImage();
    }

    /**
     * User tapped the 'image details' button so we should broadcast an event to trigger the
     * navigation to the details activity.
     */
    public void imageDetailsButtonSelected() {
        mAnalyticsProvider.trackEvent(Analytics.CONTENT_TYPE_BUTTON_CLICK, SCREEN_NAME, "ImageDetails");
        mEventBusProvider.postEvent(new HomeNavHeaderDetailsEvent(mArtwork));
    }

    /**
     * View was attached to its parent.
     */
    public void attached() {
        subscribeToEventBus();
    }

    /**
     * View was detached from its parent.
     */
    public void detached() {
        unsubscribeFromEventBus();
    }

    public void imageLoadSuccess() {
        glue.loadingIndicatorVisible.set(false);
        glue.detailsButtonVisible.set(true);
    }

    public void imageLoadFailed() {
        glue.loadingIndicatorVisible.set(false);
        glue.loadImageFailedVisible.set(true);
    }
    //endregion

    //region Event bus
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull DataLoadEvent event) {
        if(event.getEventType() == DataLoadEvent.EventType.Loaded) {
            chooseRandomImage();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull SettingsChangeEvent event) {
        refreshLockIcon();
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
    private void refreshLockIcon() {
        if(mSettingsProvider.isAutomaticHeaderImageEnabled()) {
            glue.lockIconResourceId.set(R.drawable.icon_lock_open);
        } else {
            glue.lockIconResourceId.set(R.drawable.icon_lock_shut);
        }
    }
    private void chooseRandomImage() {
        mArtwork = mArtworksProvider.getRandomArtwork();

        if(mArtwork == null) {
            glue.loadImageFailedVisible.set(true);
            return;
        }

        displayCurrentImage();
    }

    private void displayCurrentImage() {
        glue.loadImageFailedVisible.set(false);
        glue.loadingIndicatorVisible.set(true);
        glue.detailsButtonVisible.set(false);
        mSharedPreferencesProvider.saveString(PREF_LAST_HEADER_IMAGE_ID, mArtwork.getGuid());
        mActionDelegate.loadImage(mArtwork.getImageUrl());
    }

    private void loadLastSeenArtwork() {
        String artworkId = mSharedPreferencesProvider.getString(PREF_LAST_HEADER_IMAGE_ID, "");

        if(StringUtils.isEmpty(artworkId)) {
            return;
        }

        mArtwork = mArtworksProvider.getArtwork(artworkId);
    }
    //endregion

    //region Actions delegate contract
    public interface Actions {
        /**
         * Attempt to load the given image url.
         * @param url of image to load.
         */
        void loadImage(@NonNull String url);
    }
}
