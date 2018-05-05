package io.github.marcelbraghetto.football.framework.providers.football;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import io.github.marcelbraghetto.football.framework.providers.football.contracts.FootballProvider;
import io.github.marcelbraghetto.football.framework.providers.football.database.FootballDatabaseContract;
import io.github.marcelbraghetto.football.framework.providers.football.models.FootballGame;
import io.github.marcelbraghetto.football.framework.providers.football.service.FootballDataService;

/**
 * Created by Marcel Braghetto on 6/12/15.
 *
 * Implementation of the football provider which
 * provides access to APIs and helper methods.
 *
 * http://api.football-data.org/documentation
 */
public class DefaultFootballProvider implements FootballProvider {
    private final Context mApplicationContext;

    @Inject
    public DefaultFootballProvider(@NonNull Context applicationContext) {
        mApplicationContext = applicationContext;
    }

    @NonNull
    @Override
    public Uri getGamesUri(int year, int month, int day) {
        return FootballDatabaseContract.Games.getContentUriGames(year, month, day);
    }

    @Override
    public void saveGames(@NonNull List<FootballGame> games) {
        ContentValues[] contentValues = new ContentValues[games.size()];

        for(int i = 0; i < games.size(); i++) {
            contentValues[i] = games.get(i).getContentValues();
        }

        mApplicationContext.getContentResolver().bulkInsert(FootballDatabaseContract.Games.getContentUriAllGames(), contentValues);
    }

    @Override
    public void startDataRefresh() {
        mApplicationContext.startService(new Intent(mApplicationContext, FootballDataService.class));
    }
}