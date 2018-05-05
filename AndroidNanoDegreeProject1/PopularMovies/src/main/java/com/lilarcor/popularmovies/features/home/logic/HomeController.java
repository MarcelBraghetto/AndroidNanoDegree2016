package com.lilarcor.popularmovies.features.home.logic;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.features.moviescollection.logic.models.MoviesCollectionFilter;
import com.lilarcor.popularmovies.features.moviescollection.ui.MoviesCollectionFragment;
import com.lilarcor.popularmovies.framework.foundation.appstrings.contracts.AppStringsProvider;

import javax.inject.Inject;

/**
 * Created by Marcel Braghetto on 11/07/15.
 *
 * Logic controller for home screen.
 */
public class HomeController {
    //region Private fields
    private ControllerDelegate mDelegate;
    private final AppStringsProvider mAppStringsProvider;
    //endregion

    //region Public methods.
    @Inject
    public HomeController(@NonNull AppStringsProvider appStringsProvider) {
        mAppStringsProvider = appStringsProvider;
    }

    /**
     * Initialise and connect the controller to the host environment.
     *
     * @param delegate to send commands to.
     */
    public void initController(@NonNull ControllerDelegate delegate) {
        mDelegate = delegate;
        populateScreen();
    }
    //endregion

    //region Private methods
    private void populateScreen() {
        Fragment[] fragments = new Fragment[] {
                MoviesCollectionFragment.newInstance(MoviesCollectionFilter.Popular),
                MoviesCollectionFragment.newInstance(MoviesCollectionFilter.TopRated),
                MoviesCollectionFragment.newInstance(MoviesCollectionFilter.Favourites)
        };

        String[] fragmentTitles = new String[] {
                mAppStringsProvider.getString(R.string.home_view_pager_title_popular),
                mAppStringsProvider.getString(R.string.home_view_pager_title_top_rated),
                mAppStringsProvider.getString(R.string.home_view_pager_title_favourites)
        };

        mDelegate.initialiseViewPager(fragments, fragmentTitles);
    }
    //endregion

    //region Controller delegate contract
    /**
     * Controller delegate contract for the home feature.
     */
    public interface ControllerDelegate {
        /**
         * Given the array of fragments and fragment titles,
         * populate the view pager with them.
         *
         * @param fragments to display in the view pager.
         * @param fragmentTitles to associate with each fragment.
         */
        void initialiseViewPager(@NonNull Fragment[] fragments, @NonNull String[] fragmentTitles);
    }
    //endregion
}
