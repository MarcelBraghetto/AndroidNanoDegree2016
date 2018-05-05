package com.lilarcor.portfolio.features.home;

import android.test.ActivityInstrumentationTestCase2;

import com.lilarcor.portfolio.R;
import com.lilarcor.portfolio.features.home.ui.HomeActivity;
import com.lilarcor.portfolio.testhelpers.EspressoHelper;

/**
 * Created by Marcel Braghetto on 11/07/15.
 *
 * Test suite to verify the UI behaviour of the home feature.
 */
public class HomeFeatureTest extends ActivityInstrumentationTestCase2<HomeActivity> {
    public HomeFeatureTest() {
        super(HomeActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public void testContentTitle() {
        EspressoHelper.verifyTextWithId(R.id.home_content_title, "My Nanodegree Apps!");
    }

    public void testSpotifyStreamerButton() {
        EspressoHelper.verifyTextWithId(R.id.home_spotify_streamer_button, "Spotify Streamer");
        EspressoHelper.clickViewWithId(R.id.home_spotify_streamer_button);
        EspressoHelper.verifyToastMessage("This button will launch my Spotify streamer app!");
    }

    public void testScoresAppButton() {
        EspressoHelper.verifyTextWithId(R.id.home_scores_app_button, "Scores App");
        EspressoHelper.clickViewWithId(R.id.home_scores_app_button);
        EspressoHelper.verifyToastMessage("This button will launch my scores app!");
    }

    public void testLibraryAppButton() {
        EspressoHelper.verifyTextWithId(R.id.home_library_app_button, "Library App");
        EspressoHelper.clickViewWithId(R.id.home_library_app_button);
        EspressoHelper.verifyToastMessage("This button will launch my library app!");
    }

    public void testBuildItBiggerButton() {
        EspressoHelper.verifyTextWithId(R.id.home_build_it_bigger_button, "Build It Bigger");
        EspressoHelper.clickViewWithId(R.id.home_build_it_bigger_button);
        EspressoHelper.verifyToastMessage("This button will launch my 'Build It Bigger' app!");
    }

    public void testXYZReaderButton() {
        EspressoHelper.verifyTextWithId(R.id.home_xyz_reader_button, "XYZ Reader");
        EspressoHelper.clickViewWithId(R.id.home_xyz_reader_button);
        EspressoHelper.verifyToastMessage("This button will launch my 'XYZ Reader' app!");
    }

    public void testCapstoneButton() {
        EspressoHelper.verifyTextWithId(R.id.home_capstone_button, "Capstone");
        EspressoHelper.clickViewWithId(R.id.home_capstone_button);
        EspressoHelper.verifyToastMessage("This button will launch my capstone app!");
    }
}