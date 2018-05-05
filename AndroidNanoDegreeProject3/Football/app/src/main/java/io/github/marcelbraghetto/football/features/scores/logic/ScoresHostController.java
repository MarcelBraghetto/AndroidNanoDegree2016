package io.github.marcelbraghetto.football.features.scores.logic;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.joda.time.DateTime;

import javax.inject.Inject;

import io.github.marcelbraghetto.football.R;
import io.github.marcelbraghetto.football.features.scores.ui.ScoresFragment;
import io.github.marcelbraghetto.football.framework.core.BaseController;
import io.github.marcelbraghetto.football.framework.providers.dates.contracts.DateProvider;
import io.github.marcelbraghetto.football.framework.providers.strings.contracts.AppStringsProvider;

/**
 * Created by Marcel Braghetto on 5/12/15.
 *
 * Logic controller for the 'host' feature which
 * includes a view pager for managing child fragments.
 */
public class ScoresHostController extends BaseController<ScoresHostController.Delegate> {
    private static final int NUM_FRAGMENTS = 5;

    private final AppStringsProvider mAppStringsProvider;
    private final DateProvider mDateProvider;

    //region Public methods
    @Inject
    public ScoresHostController(@NonNull AppStringsProvider appStringsProvider,
                                @NonNull DateProvider dateProvider) {

        super(Delegate.class);

        mAppStringsProvider = appStringsProvider;
        mDateProvider = dateProvider;
    }

    public void initController(@Nullable Delegate delegate) {
        setDelegate(delegate);
        populateViewPager();
    }
    //endregion

    //region Private methods
    /**
     * Construct the collection of fragments to
     * display in the view pager. The fragments
     * will represent today and two days either
     * side of today.
     */
    private void populateViewPager() {
        DateTime now = mDateProvider.getNow();

        DateTime todayMinus2 = now.minusDays(2);
        DateTime todayMinus1 = now.minusDays(1);
        DateTime todayPlus2 = now.plusDays(2);
        DateTime todayPlus1 = now.plusDays(1);

        Fragment[] fragments = new Fragment[NUM_FRAGMENTS];
        String[] titles = new String[NUM_FRAGMENTS];

        fragments[0] = ScoresFragment.newInstance(todayMinus2.getYear(), todayMinus2.getMonthOfYear(), todayMinus2.getDayOfMonth());
        fragments[1] = ScoresFragment.newInstance(todayMinus1.getYear(), todayMinus1.getMonthOfYear(), todayMinus1.getDayOfMonth());
        fragments[2] = ScoresFragment.newInstance(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth());
        fragments[3] = ScoresFragment.newInstance(todayPlus1.getYear(), todayPlus1.getMonthOfYear(), todayPlus1.getDayOfMonth());
        fragments[4] = ScoresFragment.newInstance(todayPlus2.getYear(), todayPlus2.getMonthOfYear(), todayPlus2.getDayOfMonth());

        titles[0] = mDateProvider.getWeekdayName(todayMinus2.getDayOfWeek());
        titles[1] = mAppStringsProvider.getString(R.string.yesterday);
        titles[2] = mAppStringsProvider.getString(R.string.today);
        titles[3] = mAppStringsProvider.getString(R.string.tomorrow);
        titles[4] = mDateProvider.getWeekdayName(todayPlus2.getDayOfWeek());

        mDelegate.populateViewPager(fragments, titles, 2);
    }
    //endregion

    //region Delegate contract
    public interface Delegate {
        /**
         * Populate the tabs in the view pager
         * with the given collection of fragments
         * and titles, along with which fragment
         * to initially display.
         * @param fragments to populate.
         * @param titles to display on the tabs.
         * @param startingPosition to begin on.
         */
        void populateViewPager(@NonNull Fragment[] fragments, @NonNull String[] titles, int startingPosition);
    }
    //endregion
}
