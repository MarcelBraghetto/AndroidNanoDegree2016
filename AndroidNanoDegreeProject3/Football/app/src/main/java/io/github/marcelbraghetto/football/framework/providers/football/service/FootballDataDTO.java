package io.github.marcelbraghetto.football.framework.providers.football.service;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import io.github.marcelbraghetto.football.framework.providers.football.models.FootballGame;

/**
 * Created by Marcel Braghetto on 6/12/15.
 *
 * Data transformation object for use in
 * parsing the data retrieved from the
 * Football APIs.
 */
/* package */ class FootballDataDTO {
    @SerializedName("fixtures")
    FixtureDTO[] fixtures;

    static class FixtureDTO {
        /**
         * Attempt to 'export' the given game
         * DTO into a correctly formed football
         * game instance. Some basic validation
         * is performed.
         * @return new instance of a football game.
         */
        @NonNull
        FootballGame exportAsFootballGame() {
            FootballGame game = new FootballGame();

            game.setDateText(dateText);
            game.setStatus(status);
            game.setHomeTeamName(homeTeamName);
            game.setAwayTeamName(awayTeamName);
            game.setMatchDay(matchDay);

            // If the 'result' object exists and both the goal
            // amounts for home and away teams are not null (they
            // are Integer objects rather than int objects to
            // allow null values) then set the goals.
            if(result != null && result.homeTeamGoals != null && result.awayTeamGoals != null) {
                game.setHomeTeamGoals(result.homeTeamGoals);
                game.setAwayTeamGoals(result.awayTeamGoals);
            }

            // The 'match id' is really just the URL
            // linking to this particular game. Its
            // about as good as we can get for such
            // a junk API...
            if(links != null && links.self != null) {
                game.setMatchId(links.self.url);
            }

            return game;
        }

        @SerializedName("date")
        String dateText;

        @SerializedName("status")
        String status;

        @SerializedName("matchday")
        int matchDay;

        @SerializedName("homeTeamName")
        String homeTeamName;

        @SerializedName("awayTeamName")
        String awayTeamName;

        @SerializedName("result")
        Result result;

        @SerializedName("_links")
        Links links;

        static class Result {
            @SerializedName("goalsHomeTeam")
            Integer homeTeamGoals;

            @SerializedName("goalsAwayTeam")
            Integer awayTeamGoals;
        }

        static class Links {
            @SerializedName("self")
            Link self;

            static class Link {
                @SerializedName("href")
                String url;
            }
        }
    }
}
