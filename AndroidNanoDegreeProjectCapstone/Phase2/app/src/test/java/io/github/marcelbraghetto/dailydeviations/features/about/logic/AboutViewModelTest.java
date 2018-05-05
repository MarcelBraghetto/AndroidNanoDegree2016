package io.github.marcelbraghetto.dailydeviations.features.about.logic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import io.github.marcelbraghetto.dailydeviations.BuildConfig;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.AnalyticsProvider;
import io.github.marcelbraghetto.dailydeviations.testconfig.RobolectricProperties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Created by Marcel Braghetto on 10/06/16.
 *
 * Unit test suite for the AboutViewModel class.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class AboutViewModelTest {
    @Mock AnalyticsProvider mAnalyticsProvider;
    private AboutViewModel mViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mViewModel = new AboutViewModel(
                MainApp.getDagger().getApplicationContext(),
                MainApp.getDagger().getAppStringsProvider(),
                mAnalyticsProvider);
    }

    @Test
    public void verifyDefaultDataBinding() {
        // Verify
        assertThat(mViewModel.glue.htmlPath.get(), is("file:///android_asset/about.html"));
        assertThat(mViewModel.glue.navigationTitle.get(), is(""));
    }

    @Test
    public void begin() {
        // Run
        mViewModel.begin();

        // Verify
        assertThat(mViewModel.glue.navigationTitle.get(), is("About"));
    }

    @Test
    public void screenStarted() {
        // Run
        mViewModel.screenStarted();

        // Verify
        verify(mAnalyticsProvider).trackScreenView("AboutScreen");
    }
}