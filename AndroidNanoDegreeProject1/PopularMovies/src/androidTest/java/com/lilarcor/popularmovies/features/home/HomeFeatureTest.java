package com.lilarcor.popularmovies.features.home;

import android.app.Activity;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.features.home.ui.HomeActivity;
import com.lilarcor.popularmovies.testhelpers.EspressoHelper;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

/**
 * Created by Marcel Braghetto on 11/07/15.
 *
 * Test suite to verify the UI behaviour of the home feature.
 */
@RunWith(AndroidJUnit4.class)
public class HomeFeatureTest {
    private Activity mActivity;

    @Rule public final ActivityTestRule<HomeActivity> testRule = new ActivityTestRule<>(HomeActivity.class);

    @Before
    public void setUp() {
        mActivity = testRule.getActivity();
    }

    @Test
    public void verifyPopularMovie() {
        String wordFavourites = mActivity.getResources().getString(R.string.word_favourites);

        EspressoHelper.verifyActionBarTitle(mActivity, "Marcel's Movies!");

        // Open a popular movie
        onView(withText("Popular")).perform(click());

        selectFavouriteButtonInDisplayedCollection(0);
        onView(withText("Jurassic World added to your " + wordFavourites + ".")).check(matches(isDisplayed()));

        selectMovieFromDisplayedCollection(0);

        onView(withId(R.id.movie_details_vote_average_text)).check(matches(withText("70%")));
        onView(withId(R.id.movie_details_vote_total_text)).check(matches(withText("1369 votes")));
        onView(withId(R.id.movie_details_collapsing_toolbar_layout)).check(matches(withContentDescription("Jurassic World")));
        onView(withId(R.id.movie_details_release_date_label)).check(matches(withText("Released")));
        onView(withId(R.id.movie_details_release_date_text)).check(matches(withText("Friday 12 June 2015")));
        onView(withId(R.id.movie_details_overview_text)).check(matches(withText("Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.")));

        onView(withId(R.id.movie_details_floating_action_button)).perform(click());
        onView(withText("Jurassic World removed from your " + wordFavourites + ".")).check(matches(isDisplayed()));

        pressBack();
    }

    @Test
    public void verifyTopRatedMovie() {
        String wordFavourites = mActivity.getResources().getString(R.string.word_favourites);

        EspressoHelper.verifyActionBarTitle(mActivity, "Marcel's Movies!");

        // Open a popular movie
        onView(withText("Top Rated")).perform(click());

        selectFavouriteButtonInDisplayedCollection(0);
        onView(withText("Interstellar added to your " + wordFavourites + ".")).check(matches(isDisplayed()));

        selectMovieFromDisplayedCollection(0);

        onView(withId(R.id.movie_details_vote_average_text)).check(matches(withText("84%")));
        onView(withId(R.id.movie_details_vote_total_text)).check(matches(withText("2874 votes")));
        onView(withId(R.id.movie_details_collapsing_toolbar_layout)).check(matches(withContentDescription("Interstellar")));
        onView(withId(R.id.movie_details_release_date_label)).check(matches(withText("Released")));
        onView(withId(R.id.movie_details_release_date_text)).check(matches(withText("Wednesday 5 November 2014")));
        onView(withId(R.id.movie_details_overview_text)).check(matches(withText("Interstellar chronicles the adventures of a group of explorers who make use of a newly discovered wormhole to surpass the limitations on human space travel and conquer the vast distances involved in an interstellar voyage.")));

        onView(withId(R.id.movie_details_floating_action_button)).perform(click());
        onView(withText("Interstellar removed from your " + wordFavourites + ".")).check(matches(isDisplayed()));

        pressBack();
    }

    @Test
    public void verifyFavouriteMovie() {
        String tabFavourites = mActivity.getResources().getString(R.string.home_view_pager_title_favourites);
        String wordFavourites = mActivity.getResources().getString(R.string.word_favourites);

        EspressoHelper.verifyActionBarTitle(mActivity, "Marcel's Movies!");

        // First, add a popular movie to our favourites
        onView(withText("Popular")).perform(click());
        selectFavouriteButtonInDisplayedCollection(1);

        // Go to the 'favourites' tab and open the first movie in there
        onView(withText(tabFavourites)).perform(click());
        selectMovieFromDisplayedCollection(0);

        onView(withId(R.id.movie_details_vote_average_text)).check(matches(withText("64%")));
        onView(withId(R.id.movie_details_vote_total_text)).check(matches(withText("428 votes")));
        onView(withId(R.id.movie_details_collapsing_toolbar_layout)).check(matches(withContentDescription("Terminator Genisys")));
        onView(withId(R.id.movie_details_release_date_label)).check(matches(withText("Released")));
        onView(withId(R.id.movie_details_release_date_text)).check(matches(withText("Wednesday 1 July 2015")));
        onView(withId(R.id.movie_details_overview_text)).check(matches(withText("The year is 2029. John Connor, leader of the resistance continues the war against the machines. At the Los Angeles offensive, John's fears of the unknown future begin to emerge when TECOM spies reveal a new plot by SkyNet that will attack him from both fronts; past and future, and will ultimately change warfare forever.")));

        onView(withId(R.id.movie_details_floating_action_button)).perform(click());
        onView(withText("Terminator Genisys removed from your " + wordFavourites + ".")).check(matches(isDisplayed()));

        pressBack();
    }

    private void selectMovieFromDisplayedCollection(int position) {
        onView(allOf(withId(R.id.movie_collection_recycler_view), isDisplayed()))
                .perform(RecyclerViewActions.actionOnItemAtPosition(position, clickChildViewWithId(R.id.movies_collection_item_poster)));
    }

    private void selectFavouriteButtonInDisplayedCollection(int position) {
        onView(allOf(withId(R.id.movie_collection_recycler_view), isDisplayed()))
                .perform(RecyclerViewActions.actionOnItemAtPosition(position, clickChildViewWithId(R.id.movies_collection_item_favourites_button)));
    }

    private static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Click on a child view with specified id.";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                if (v != null) {
                    v.performClick();
                }
            }
        };
    }
}