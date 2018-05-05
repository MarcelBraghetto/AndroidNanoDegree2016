package io.github.marcelbraghetto.dailydeviations.features.home.logic;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableField;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.google.android.gms.appinvite.AppInviteInvitation;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import io.github.marcelbraghetto.dailydeviations.R;
import io.github.marcelbraghetto.dailydeviations.features.about.ui.AboutFragment;
import io.github.marcelbraghetto.dailydeviations.features.collection.logic.CollectionArguments;
import io.github.marcelbraghetto.dailydeviations.features.collection.logic.providers.contracts.CollectionProvider;
import io.github.marcelbraghetto.dailydeviations.features.collection.ui.CollectionFragment;
import io.github.marcelbraghetto.dailydeviations.features.detail.ui.DetailActivity;
import io.github.marcelbraghetto.dailydeviations.features.settings.ui.SettingsFragment;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.contracts.ArtworksProvider;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.CollectionFilterMode;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.Analytics;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.AnalyticsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.core.BaseViewModel;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusSubscriber;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.events.CollectionFilterModeToggleEvent;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.strings.contracts.StringsProvider;

/**
 * Created by Marcel Braghetto on 1/12/15.
 *
 * View model for the home activity which is responsible for the nav menu and content population.
 */
public class HomeViewModel extends BaseViewModel<HomeViewModel.Actions> implements EventBusSubscriber {

    //region Data binding
    public final Glue glue = new Glue();
    public static class Glue {
        public ObservableField<String> navigationTitle = new ObservableField<>("");
        public ObservableField<Integer> selectedMenuId = new ObservableField<>(R.id.nav_menu_browse);
    }
    //endregion

    //region Private fields
    private static final String SCREEN_NAME = "HomeScreen";
    private static final int INVITE_REQUEST_CODE = 666;

    private final Context mApplicationContext;
    private final ArtworksProvider mArtworksProvider;
    private final EventBusProvider mEventBusProvider;
    private final StringsProvider mStringsProvider;
    private final CollectionProvider mCollectionProvider;
    private final AnalyticsProvider mAnalyticsProvider;
    //endregion

    //region Public methods
    @Inject
    public HomeViewModel(@NonNull Context applicationContext,
                         @NonNull ArtworksProvider artworksProvider,
                         @NonNull EventBusProvider eventBusProvider,
                         @NonNull StringsProvider stringsProvider,
                         @NonNull CollectionProvider collectionProvider,
                         @NonNull AnalyticsProvider analyticsProvider) {
        super(Actions.class);

        mApplicationContext = applicationContext;
        mArtworksProvider = artworksProvider;
        mEventBusProvider = eventBusProvider;
        mStringsProvider = stringsProvider;
        mCollectionProvider = collectionProvider;
        mAnalyticsProvider = analyticsProvider;
    }

    /**
     * Initialize the view model with the given callback delegate and saved state if there is any.
     * @param actionDelegate to callback to.
     */
    public void begin(@Nullable Actions actionDelegate) {
        setActionDelegate(actionDelegate);
        showCurrentContentScreen(false);
        mActionDelegate.showToggleButtons();
    }

    /**
     * User has selected a menu item from the navigation drawer with the given item id.
     * @param itemId for the selected menu item.
     */
    public void menuItemSelected(@IdRes int itemId) {
        mActionDelegate.closeNavigationMenu();

        switch (itemId) {
            case R.id.nav_menu_browse: {
                setSelectedMenuItemId(itemId);
                mAnalyticsProvider.trackEvent(Analytics.CONTENT_TYPE_MENU_ITEM_SELECTED, SCREEN_NAME, "Browse");
                showCurrentContentScreen(true);
                mActionDelegate.showToggleButtons();
                break;
            }

            case R.id.nav_menu_tell_friends: {
                mAnalyticsProvider.trackEvent(Analytics.CONTENT_TYPE_MENU_ITEM_SELECTED, SCREEN_NAME, "TellFriends");
                Intent intent = new AppInviteInvitation.IntentBuilder(mStringsProvider.getString(R.string.app_invite_title))
                        .setMessage(mStringsProvider.getString(R.string.app_invite_message))
                        .setCallToActionText(mStringsProvider.getString(R.string.app_invite_call_to_action))
                        .build();
                mActionDelegate.startActivityForResult(intent, INVITE_REQUEST_CODE);
                break;
            }

            case R.id.nav_menu_rate_app: {
                mAnalyticsProvider.trackEvent(Analytics.CONTENT_TYPE_MENU_ITEM_SELECTED, SCREEN_NAME, "RateApp");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=io.github.marcelbraghetto.dailydeviations"));
                mActionDelegate.startActivity(intent);
                break;
            }

            case R.id.nav_menu_settings: {
                setSelectedMenuItemId(itemId);
                mAnalyticsProvider.trackEvent(Analytics.CONTENT_TYPE_MENU_ITEM_SELECTED, SCREEN_NAME, "Settings");
                setNavigationTitle(mStringsProvider.getString(R.string.nav_menu_settings));
                mActionDelegate.hideToggleButtons();
                mActionDelegate.replaceContent(SettingsFragment.newInstance(), true);
                break;
            }

            case R.id.nav_menu_about: {
                setSelectedMenuItemId(itemId);
                mAnalyticsProvider.trackEvent(Analytics.CONTENT_TYPE_MENU_ITEM_SELECTED, SCREEN_NAME, "About");
                setNavigationTitle(mStringsProvider.getString(R.string.nav_menu_about));
                mActionDelegate.hideToggleButtons();
                mActionDelegate.replaceContent(AboutFragment.newInstance(), true);
                break;
            }
        }
    }

    public void backPressed() {
        // If we are already looking at the main content, then exit the app.
        if(getSelectedMenuItemId() == R.id.nav_menu_browse) {
            mActionDelegate.finishActivity();
            return;
        }

        // Otherwise default back to the main content.
        showCurrentContentScreen(true);
        mActionDelegate.showToggleButtons();
    }

    /**
     * The screen was started.
     */
    public void screenStarted() {
        mAnalyticsProvider.trackScreenView(SCREEN_NAME);
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
    public void onEvent(@NonNull CollectionFilterModeToggleEvent event) {
        showCurrentContentScreen(true);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull HomeNavHeaderDetailsEvent event) {
        Intent intent = new Intent(mApplicationContext, DetailActivity.class);
        event.getArtwork().putInto(intent);
        mActionDelegate.closeNavigationMenu();
        mActionDelegate.startActivity(intent);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(@NonNull HomeSnackbarEvent event) {
        mActionDelegate.showSnackbar(event.getMessage());
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
    private int getSelectedMenuItemId() {
        return glue.selectedMenuId.get();
    }

    private void setSelectedMenuItemId(@IdRes int selectedMenuItemId) {
        glue.selectedMenuId.set(selectedMenuItemId);
    }

    private void setNavigationTitle(String value) {
        glue.navigationTitle.set(value);
    }

    private void showCurrentContentScreen(boolean animated) {
        setSelectedMenuItemId(R.id.nav_menu_browse);

        CollectionFilterMode filterMode = mCollectionProvider.getCollectionFilterMode();

        switch (filterMode) {
            case Favourites: {
                setNavigationTitle(mStringsProvider.getString(R.string.nav_menu_favourites));
                mActionDelegate.replaceContent(CollectionFragment.newInstance(new CollectionArguments(CollectionFilterMode.Favourites)), animated);
                break;
            }

            default: {
                setNavigationTitle(mStringsProvider.getString(R.string.nav_menu_browse));
                mActionDelegate.replaceContent(CollectionFragment.newInstance(new CollectionArguments(CollectionFilterMode.All)), animated);
                break;
            }
        }
    }
    //endregion

    //region Actions delegate contract
    public interface Actions {
        /**
         * Close the navigation drawer programmatically.
         */
        void closeNavigationMenu();

        /**
         * Replace the currently displayed content
         * with the given fragment.
         * @param fragment to display
         */
        void replaceContent(@NonNull Fragment fragment, boolean animated);

        /**
         * Start the given activity intent.
         * @param intent to start.
         */
        void startActivity(@NonNull Intent intent);

        /**
         * Start the given activity intent for result.
         * @param intent to start.
         */
        void startActivityForResult(@NonNull Intent intent, int requestCode);

        /**
         * Finish the current activity.
         */
        void finishActivity();

        /**
         * Show the toggle buttons for navigating the collection views.
         */
        void showToggleButtons();

        /**
         * Hide the toggle buttons for navigating the collection views.
         */
        void hideToggleButtons();

        /**
         * Display a snack bar message.
         * @param message to display.
         */
        void showSnackbar(@NonNull String message);
    }
    //endregion
}
