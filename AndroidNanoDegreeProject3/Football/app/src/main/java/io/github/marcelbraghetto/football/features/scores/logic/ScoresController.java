package io.github.marcelbraghetto.football.features.scores.logic;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.github.marcelbraghetto.football.R;
import io.github.marcelbraghetto.football.framework.core.BaseController;
import io.github.marcelbraghetto.football.framework.providers.football.contracts.FootballProvider;
import io.github.marcelbraghetto.football.framework.providers.strings.contracts.AppStringsProvider;

/**
 * Created by Marcel Braghetto on 6/12/15.
 *
 * Logic controller for the scores screen.
 */
public class ScoresController extends BaseController<ScoresController.Delegate> {
    private final FootballProvider mFootballProvider;
    private final AppStringsProvider mAppStringsProvider;

    //region Public methods
    @Inject
    public ScoresController(@NonNull FootballProvider footballProvider,
                            @NonNull AppStringsProvider appStringsProvider) {

        super(Delegate.class);

        mFootballProvider = footballProvider;
        mAppStringsProvider = appStringsProvider;
    }

    public void initController(@Nullable Delegate delegate, int year, int month, int day) {
        setDelegate(delegate);
        mDelegate.hideNoResultsMessage();
        mDelegate.setDataSource(mFootballProvider.getGamesUri(year, month, day));
    }

    public void loaderManagerFinished(Cursor cursor) {
        // If we have loaded an empty cursor, show something
        // to the user indicating that there are no results.
        if(cursor == null || cursor.getCount() == 0) {
            mDelegate.showNoResultsMessage();
        } else {
            mDelegate.hideNoResultsMessage();
        }

        mDelegate.hideFaderView();
    }

    /**
     * User selected the share option for
     * a game in the adapter list.
     * @param text to share
     */
    public void shareSelected(@NonNull String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, mAppStringsProvider.getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");

        mDelegate.startActivityIntent(intent);
    }
    //endregion

    //region Delegate contract
    public interface Delegate {
        /**
         * Connect the given data source uri
         * to the adapter in the screen.
         * @param contentUri to connect to.
         */
        void setDataSource(@NonNull Uri contentUri);

        /**
         * Display a message when there are
         * no results in the data source.
         */
        void showNoResultsMessage();

        /**
         * Hide the no results message.
         */
        void hideNoResultsMessage();

        /**
         * Hide the 'fader' view whose
         * job it is to mask the loading
         * time of the cursor loaders.
         */
        void hideFaderView();

        /**
         * Start the given intent.
         * @param intent to start.
         */
        void startActivityIntent(@NonNull Intent intent);
    }
    //endregion
}
