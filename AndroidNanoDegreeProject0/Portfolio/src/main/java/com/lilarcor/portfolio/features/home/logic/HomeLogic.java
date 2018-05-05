package com.lilarcor.portfolio.features.home.logic;

import android.support.annotation.NonNull;

import com.lilarcor.portfolio.R;
import com.lilarcor.portfolio.framework.foundation.StringHelper;

/**
 * Created by Marcel Braghetto on 11/07/15.
 *
 * Logic controller for home screen.
 */
public class HomeLogic {
    //region Private fields
    private UIDelegate mDelegate;
    //endregion

    //region Public methods.
    /**
     * Connect the given UI to the logic instance with the
     * given callback delegate.
     *
     * @param delegate
     */
    public HomeLogic(@NonNull UIDelegate delegate) {
        mDelegate = delegate;
    }

    /**
     * User selects the 'Spotify Streamer' button.
     */
    public void spotifyStreamerSelected() {
        showButtonToast(StringHelper.getString(R.string.home_spotify_streamer_toast_suffix));
    }

    /**
     * User selects the 'Scores App' button.
     */
    public void scoresAppSelected() {
        showButtonToast(StringHelper.getString(R.string.home_super_duo_scores_toast_suffix));
    }

    /**
     * User selects the 'Library App' button.
     */
    public void libraryAppSelected() {
        showButtonToast(StringHelper.getString(R.string.home_super_duo_library_toast_suffix));
    }

    /**
     * User selects the 'Build It Bigger' button.
     */
    public void buildItBiggerSelected() {
        showButtonToast(StringHelper.getString(R.string.home_build_it_bigger_toast_suffix));
    }

    /**
     * User selects the 'XYZ Reader' button.
     */
    public void xyzReaderSelected() {
        showButtonToast(StringHelper.getString(R.string.home_xyz_reader_toast_suffix));
    }

    /**
     * User selects the 'Capstone' button.
     */
    public void capstoneSelected() {
        showButtonToast(StringHelper.getString(R.string.home_capstone_toast_suffix));
    }
    //endregion

    //region Private methods
    private void showButtonToast(String suffix) {
        mDelegate.showToast(StringHelper.getString(R.string.home_button_toast_prefix, suffix));
    }
    //endregion

    //region Logic delegate contract
    /**
     * UI logic contract for the home feature.
     */
    public interface UIDelegate {
        /**
         * Display the given message in an Android Toast.
         *
         * @param message to display
         */
        void showToast(@NonNull String message);
    }
    //endregion
}
