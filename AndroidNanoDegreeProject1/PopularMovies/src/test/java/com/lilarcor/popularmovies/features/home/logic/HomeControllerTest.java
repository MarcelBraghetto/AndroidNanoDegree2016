package com.lilarcor.popularmovies.features.home.logic;

import android.support.v4.app.Fragment;

import com.lilarcor.popularmovies.BuildConfig;
import com.lilarcor.popularmovies.features.application.MainApp;
import com.lilarcor.popularmovies.features.moviescollection.logic.models.MoviesCollectionFilter;
import com.lilarcor.popularmovies.features.moviescollection.ui.MoviesCollectionFragment;
import com.lilarcor.popularmovies.framework.foundation.appstrings.contracts.AppStringsProvider;
import com.lilarcor.popularmovies.testutils.RobolectricProperties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.Serializable;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by Marcel Braghetto on 27/07/15.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class HomeControllerTest {
    @Mock HomeController.ControllerDelegate mDelegate;

    private HomeController mController;

    @Captor ArgumentCaptor<Fragment[]> mCaptorFragmentArray;
    @Captor ArgumentCaptor<String[]> mCaptorStringArray;

    private AppStringsProvider mAppStringsProvider;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mAppStringsProvider = MainApp.getDagger().getAppStringsProvider();

        mController = new HomeController(mAppStringsProvider);
    }

    @Test
    public void testInitController() {
        // Run
        mController.initController(mDelegate);

        // Verify
        verify(mDelegate).initialiseViewPager(mCaptorFragmentArray.capture(), mCaptorStringArray.capture());

        Fragment[] fragments = mCaptorFragmentArray.getValue();

        assertThat(fragments.length, is(3));

        assertThat(fragments[0], instanceOf(MoviesCollectionFragment.class));
        assertThat(fragments[0].getArguments().getSerializable("MCCMMF"), is((Serializable) MoviesCollectionFilter.Popular));

        assertThat(fragments[1], instanceOf(MoviesCollectionFragment.class));
        assertThat(fragments[1].getArguments().getSerializable("MCCMMF"), is((Serializable) MoviesCollectionFilter.TopRated));

        assertThat(fragments[2], instanceOf(MoviesCollectionFragment.class));
        assertThat(fragments[2].getArguments().getSerializable("MCCMMF"), is((Serializable) MoviesCollectionFilter.Favourites));

        String[] titles = mCaptorStringArray.getValue();

        assertThat(titles.length, is(3));
        assertThat(titles[0], is("Popular"));
        assertThat(titles[1], is("Top Rated"));
        assertThat(titles[2], is("Favourites"));
    }
}