package io.github.marcelbraghetto.football.features.home.logic;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import org.joda.time.DateTime;

import javax.inject.Inject;

import io.github.marcelbraghetto.football.R;
import io.github.marcelbraghetto.football.features.about.ui.AboutFragment;
import io.github.marcelbraghetto.football.features.scores.ui.ScoresHostFragment;
import io.github.marcelbraghetto.football.framework.core.BaseController;
import io.github.marcelbraghetto.football.framework.providers.dates.contracts.DateProvider;
import io.github.marcelbraghetto.football.framework.providers.football.contracts.FootballProvider;
import io.github.marcelbraghetto.football.framework.providers.sharedpreferences.contracts.SharedPreferencesProvider;
import io.github.marcelbraghetto.football.framework.providers.strings.contracts.AppStringsProvider;

/**
 * Created by Marcel Braghetto on 1/12/15.
 *
 * Controller logic for the home activity which is
 * responsible for the nav menu and content
 * population.
 */
public class HomeController extends BaseController<HomeController.Delegate> {
    private static final String PREF_LAST_REFRESH_DATE = "HomeLastRefreshDate";
    private static final String PREF_LAST_REFRESH_DATE_FORMAT = "yyyy-MM-dd";

    private final SharedPreferencesProvider mSharedPreferencesProvider;
    private final AppStringsProvider mAppStringsProvider;
    private final FootballProvider mFootballProvider;
    private final DateProvider mDateProvider;

    @IdRes private int mSelectedMenuItemId;

    //region Public methods
    @Inject
    public HomeController(@NonNull SharedPreferencesProvider sharedPreferencesProvider,
                          @NonNull AppStringsProvider appStringsProvider,
                          @NonNull FootballProvider footballProvider,
                          @NonNull DateProvider dateProvider) {

        super(Delegate.class);

        mSharedPreferencesProvider = sharedPreferencesProvider;
        mAppStringsProvider = appStringsProvider;
        mFootballProvider = footballProvider;
        mDateProvider = dateProvider;
    }

    public void initController(@Nullable Delegate delegate, @Nullable Bundle savedInstanceState) {
        setDelegate(delegate);

        if(savedInstanceState == null) {
            showInitialContent(false);
        }
    }

    /**
     * User selects a different menu item from
     * the navigation drawer.
     * @param itemId of the selected menu item.
     */
    public void menuItemSelected(@IdRes int itemId) {
        mDelegate.closeNavigationMenu();
        mSelectedMenuItemId = itemId;

        switch (itemId) {
            case R.id.nav_menu_football_scores:
                mDelegate.replaceContent(ScoresHostFragment.newInstance(), true);
                break;
            case R.id.nav_menu_about:
                mDelegate.replaceContent(AboutFragment.newInstance(), true);
                break;
        }
    }

    /**
     * User selected the back key, determine
     * whether to show them the initial
     * content fragment or exit the app.
     */
    public void backPressed() {
        if(mSelectedMenuItemId != R.id.nav_menu_football_scores) {
            mSelectedMenuItemId = R.id.nav_menu_football_scores;
            mDelegate.highlightMenuItem(mSelectedMenuItemId);
            mDelegate.replaceContent(ScoresHostFragment.newInstance(), true);
            return;
        }

        mDelegate.finishActivity();
    }

    /**
     * The screen was resumed, we should
     * check to see if we should refresh
     * the data if the last saved 'today'
     * time stamp is different to now.
     */
    public void screenStarted() {
        DateTime lastRefreshTime = mDateProvider.parseDateTimeString(PREF_LAST_REFRESH_DATE_FORMAT, mSharedPreferencesProvider.getString(PREF_LAST_REFRESH_DATE, ""));

        // We've never 'refreshed' before, so kick it off
        if(lastRefreshTime == null) {
            refreshData();
            return;
        }

        // If the difference in days between the two dates is more than 0, then
        // we are now on a different day, so trigger a data refresh.
        if(mDateProvider.getDaysBetween(lastRefreshTime, mDateProvider.getNow()) > 0) {
            refreshData();
        }

        // Otherwise do nothing, we are probably fairly synced up with the data...
    }

    /**
     * User performs a swipe to refresh to manually trigger
     * a data download.
     */
    public void swipeToRefreshInitiated() {
        mDelegate.hideSwipeToRefreshIndicator();
        mDelegate.showSnackbarMessage(mAppStringsProvider.getString(R.string.scores_refresh_in_progress));
        refreshData();
    }
    //endregion

    //region Private methods
    private void refreshData() {
        // Attempt to convert the date time of now into a string to be saved
        // into shared preferences.
        String refreshTime = mDateProvider.formatDateTime(PREF_LAST_REFRESH_DATE_FORMAT, mDateProvider.getNow());

        // If the conversion failed, we can't really do anything further...
        if(TextUtils.isEmpty(refreshTime)) {
            return;
        }

        // Save the last time we refreshed
        mSharedPreferencesProvider.saveString(PREF_LAST_REFRESH_DATE, refreshTime);

        // Kick off the refresh
        mFootballProvider.startDataRefresh();
    }

    private void showInitialContent(boolean animated) {
        mSelectedMenuItemId = R.id.nav_menu_football_scores;
        mDelegate.replaceContent(ScoresHostFragment.newInstance(), animated);
        mDelegate.highlightMenuItem(mSelectedMenuItemId);
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
         * Request the navigation menu 'highlight'
         * the given item id.
         * @param itemId to highlight.
         */
        void highlightMenuItem(@IdRes int itemId);

        /**
         * Close the sliding navigation menu.
         */
        void closeNavigationMenu();

        /**
         * Tell the current activity to finish itself.
         */
        void finishActivity();

        /**
         * Tell the swipe to refresh indicator
         * to hide itself.
         */
        void hideSwipeToRefreshIndicator();

        /**
         * Show a snack bar message to the user.
         * @param message to display.
         */
        void showSnackbarMessage(@NonNull String message);
    }
    //endregion
}
