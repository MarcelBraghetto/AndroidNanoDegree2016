package com.lilarcor.popularmovies.features.home.logic;

import android.support.v4.app.Fragment;

import com.lilarcor.popularmovies.BuildConfig;
import com.lilarcor.popularmovies.features.application.MainApp;
import com.lilarcor.popularmovies.features.home.logic.events.ShowTabletMovieDetailsEvent;
import com.lilarcor.popularmovies.features.moviedetails.ui.MovieDetailsFragment;
import com.lilarcor.popularmovies.features.moviescollection.logic.models.MoviesCollectionFilter;
import com.lilarcor.popularmovies.features.moviescollection.ui.MoviesCollectionFragment;
import com.lilarcor.popularmovies.framework.foundation.appstrings.contracts.AppStringsProvider;
import com.lilarcor.popularmovies.framework.foundation.device.contracts.DeviceInfoProvider;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusProvider;
import com.lilarcor.popularmovies.framework.movies.provider.contracts.MoviesProvider;
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
import static org.mockito.Mockito.when;

/**
 * Created by Marcel Braghetto on 27/07/15.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class HomeControllerTest {
    @Mock EventBusProvider mEventBusProvider;
    @Mock DeviceInfoProvider mDeviceInfoProvider;
    @Mock MoviesProvider mMoviesProvider;
    @Mock HomeController.ControllerDelegate mDelegate;

    private HomeController mController;

    @Captor ArgumentCaptor<Fragment[]> mCaptorFragmentArray;
    @Captor ArgumentCaptor<String[]> mCaptorStringArray;
    @Captor ArgumentCaptor<Fragment> mCaptorFragment;

    private AppStringsProvider mAppStringsProvider;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mAppStringsProvider = MainApp.getDagger().getAppStringsProvider();

        mController = new HomeController(mAppStringsProvider, mEventBusProvider, mDeviceInfoProvider, mMoviesProvider);
    }

    @Test
    public void testInitControllerIsLargeDevice() {
        // Setup
        when(mDeviceInfoProvider.isLargeDevice()).thenReturn(true);

        // Run
        mController.initController(mDelegate);

        // Verify
        verify(mDeviceInfoProvider).isLargeDevice();
        verify(mDelegate).setActionBarTitle("");

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

    @Test
    public void testInitControllerNotLargeDevice() {
        // Setup
        when(mDeviceInfoProvider.isLargeDevice()).thenReturn(false);

        // Run
        mController.initController(mDelegate);

        // Verify
        verify(mDeviceInfoProvider).isLargeDevice();
        verify(mDelegate).setActionBarTitle("Marcel's Movies!");

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

    @Test
    public void testScreenStarted() {
        // Setup
        mController.initController(mDelegate);

        // Run
        mController.screenStarted();

        // Verify
        verify(mEventBusProvider).subscribe(mController);
    }

    @Test
    public void testScreenStopped() {
        // Setup
        mController.initController(mDelegate);

        // Run
        mController.screenStopped();

        // Verify
        verify(mEventBusProvider).unsubscribe(mController);
    }

    @Test
    public void testOnShowTabletMovieDetailsEvent() {
        // Setup
        ShowTabletMovieDetailsEvent event = new ShowTabletMovieDetailsEvent(11);
        mController.initController(mDelegate);

        // Run
        mController.onShowTabletMovieDetailsEvent(event);

        // Verify
        verify(mDelegate).loadTabletMovieDetailsFragment(mCaptorFragment.capture());

        MovieDetailsFragment fragment = (MovieDetailsFragment) mCaptorFragment.getValue();

        assertThat(fragment, instanceOf(MovieDetailsFragment.class));
        assertThat(fragment.getArguments().getInt("MDCMI"), is(11));
    }
}