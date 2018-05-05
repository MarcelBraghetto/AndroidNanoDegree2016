package com.lilarcor.portfolio.features.home.logic;

import com.lilarcor.portfolio.BuildConfig;
import com.lilarcor.portfolio.testconfig.RobolectricProperties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.verify;

/**
 * Created by Marcel Braghetto on 11/07/15.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class HomeLogicTest {
    @Mock HomeLogic.UIDelegate mDelegate;

    private HomeLogic mLogic;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mLogic = new HomeLogic(mDelegate);
    }

    @Test
    public void testSpotifyStreamerSelected() {
        // Run
        mLogic.spotifyStreamerSelected();

        // Verify
        verify(mDelegate).showToast("This button will launch my Spotify streamer app!");
    }

    @Test
    public void testScoresAppSelected() {
        // Run
        mLogic.scoresAppSelected();

        // Verify
        verify(mDelegate).showToast("This button will launch my scores app!");
    }

    @Test
    public void testLibraryAppSelected() {
        // Run
        mLogic.libraryAppSelected();

        // Verify
        verify(mDelegate).showToast("This button will launch my library app!");
    }

    @Test
    public void testBuildItBiggerSelected() {
        // Run
        mLogic.buildItBiggerSelected();

        // Verify
        verify(mDelegate).showToast("This button will launch my 'Build It Bigger' app!");
    }

    @Test
    public void testXYZReaderSelected() {
        // Run
        mLogic.xyzReaderSelected();

        // Verify
        verify(mDelegate).showToast("This button will launch my 'XYZ Reader' app!");
    }

    @Test
    public void testCapstoneSelected() {
        // Run
        mLogic.capstoneSelected();

        // Verify
        verify(mDelegate).showToast("This button will launch my capstone app!");
    }
}