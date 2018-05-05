package io.github.marcelbraghetto.football.framework.providers.football.contracts;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;

import io.github.marcelbraghetto.football.framework.providers.football.models.FootballGame;

/**
 * Created by Marcel Braghetto on 6/12/15.
 *
 * Provider to give access to football related
 * requests and queries.
 */
public interface FootballProvider {
    /**
     * Get the content provider Uri for all
     * football games registered on the given
     * date specified as year/month/day.
     * @param year of the game.
     * @param month of the game.
     * @param day of the game.
     * @return content Uri that can fetch this
     * filtered set of data.
     */
    @NonNull Uri getGamesUri(int year, int month, int day);

    /**
     * Save or update the collection of football games.
     * @param games to save or update.
     */
    void saveGames(@NonNull List<FootballGame> games);

    /**
     * Initiate a data refresh request manually.
     */
    void startDataRefresh();
}
