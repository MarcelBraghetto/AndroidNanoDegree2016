package io.github.marcelbraghetto.football.framework.providers.football.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marcel Braghetto on 6/12/15.
 *
 * Database contract for storing football games.
 */
public class FootballDatabaseContract {
    public static final String CONTENT_AUTHORITY = "io.github.marcelbraghetto.football.framework.providers.football.database.FootballContentProvider";
    public static final String CONTENT_PATH_GAMES = "games";

    public static final String QUERY_PARAM_SEARCH_YEAR = "searchYear";
    public static final String QUERY_PARAM_SEARCH_MONTH = "searchMonth";
    public static final String QUERY_PARAM_SEARCH_DAY = "searchDay";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class Games implements BaseColumns {
        public static final String TABLE_NAME = "games";
        public static final String COLUMN_MATCH_ID = "game_match_id";
        public static final String COLUMN_DATE_TEXT = "game_date_text";
        public static final String COLUMN_DATE_YEAR = "game_date_year";
        public static final String COLUMN_DATE_MONTH = "game_date_month";
        public static final String COLUMN_DATE_DAY = "game_date_day";
        public static final String COLUMN_MATCH_DAY = "game_match_day";
        public static final String COLUMN_HOME_TEAM_NAME = "game_home_team_name";
        public static final String COLUMN_HOME_TEAM_GOALS = "game_home_team_goals";
        public static final String COLUMN_AWAY_TEAM_NAME = "game_away_team_name";
        public static final String COLUMN_AWAY_TEAM_GOALS = "game_away_team_goals";
        public static final String COLUMN_STATUS = "game_status";

        private static final Uri ALL_GAMES_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(CONTENT_PATH_GAMES).build();

        private static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CONTENT_PATH_GAMES;
        private static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CONTENT_PATH_GAMES;

        @NonNull
        public static String getCreateTableSql() {
            return "CREATE TABLE " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MATCH_ID + " TEXT NOT NULL, " +
                    COLUMN_DATE_TEXT + " TEXT NOT NULL, " +
                    COLUMN_DATE_YEAR + " INTEGER NOT NULL, " +
                    COLUMN_DATE_MONTH + " INTEGER NOT NULL, " +
                    COLUMN_DATE_DAY + " INTEGER NOT NULL, " +
                    COLUMN_MATCH_DAY + " INTEGER NOT NULL, " +
                    COLUMN_HOME_TEAM_NAME + " TEXT NOT NULL, " +
                    COLUMN_HOME_TEAM_GOALS + " INTEGER NOT NULL, " +
                    COLUMN_AWAY_TEAM_NAME + " TEXT NOT NULL, " +
                    COLUMN_AWAY_TEAM_GOALS + " INTEGER NOT NULL, " +
                    COLUMN_STATUS + " TEXT NOT NULL);";
        }

        @NonNull
        public static String getGameExistsSql() {
            return "SELECT COUNT(" + COLUMN_MATCH_ID + ") FROM " + TABLE_NAME + " WHERE " + COLUMN_MATCH_ID + " = ?";
        }

        @NonNull
        public static String getUpdateSql() {
            return "UPDATE " + TABLE_NAME + " SET " +
                    COLUMN_DATE_TEXT + "= ?, " +
                    COLUMN_DATE_YEAR + "= ?, " +
                    COLUMN_DATE_MONTH + "= ?, " +
                    COLUMN_DATE_DAY + "= ?, " +
                    COLUMN_MATCH_DAY + "= ?, " +
                    COLUMN_HOME_TEAM_NAME + "= ?, " +
                    COLUMN_HOME_TEAM_GOALS + "= ?, " +
                    COLUMN_AWAY_TEAM_NAME + "= ?, " +
                    COLUMN_AWAY_TEAM_GOALS + "= ?, " +
                    COLUMN_STATUS + "= ? " +
                    "WHERE " + COLUMN_MATCH_ID + " = ?";
        }

        @NonNull
        public static String getInsertSql() {
            return "INSERT INTO " + TABLE_NAME + "(" +
                    COLUMN_MATCH_ID + ", " +
                    COLUMN_DATE_TEXT + ", " +
                    COLUMN_DATE_YEAR + ", " +
                    COLUMN_DATE_MONTH + ", " +
                    COLUMN_DATE_DAY + ", " +
                    COLUMN_MATCH_DAY + ", " +
                    COLUMN_HOME_TEAM_NAME + ", " +
                    COLUMN_HOME_TEAM_GOALS + ", " +
                    COLUMN_AWAY_TEAM_NAME + ", " +
                    COLUMN_AWAY_TEAM_GOALS + ", " +
                    COLUMN_STATUS + ") " +
                    "VALUES(" +
                    "?, " +     // COLUMN_MATCH_ID
                    "?, " +     // COLUMN_DATE_TEXT
                    "?, " +     // COLUMN_DATE_YEAR
                    "?, " +     // COLUMN_DATE_MONTH
                    "?, " +     // COLUMN_DATE_DAY
                    "?, " +     // COLUMN_MATCH_DAY
                    "?, " +     // COLUMN_HOME_TEAM_NAME
                    "?, " +     // COLUMN_HOME_TEAM_GOALS
                    "?, " +     // COLUMN_AWAY_TEAM_NAME
                    "?, " +     // COLUMN_AWAY_TEAM_GOALS
                    "?)";       // COLUMN_STATUS
        }

        @NonNull
        public static Uri getContentUriAllGames() {
            return ALL_GAMES_CONTENT_URI;
        }

        @NonNull
        public static Uri getContentUriGames(int year, int month, int day) {
            return ALL_GAMES_CONTENT_URI
                    .buildUpon()
                    .appendQueryParameter(QUERY_PARAM_SEARCH_YEAR, String.valueOf(year))
                    .appendQueryParameter(QUERY_PARAM_SEARCH_MONTH, String.valueOf(month))
                    .appendQueryParameter(QUERY_PARAM_SEARCH_DAY, String.valueOf(day))
                    .build();
        }

        @NonNull
        public static String getContentItemType() {
            return CONTENT_ITEM_TYPE;
        }

        @NonNull
        public static String getContentType() {
            return CONTENT_TYPE;
        }

        @NonNull
        public static Map<String, Integer> getAllColumnsIndexMap() {
            Map<String, Integer> map = new HashMap<>();

            map.put(_ID, 0);
            map.put(COLUMN_MATCH_ID, 1);
            map.put(COLUMN_DATE_TEXT, 2);
            map.put(COLUMN_DATE_YEAR, 3);
            map.put(COLUMN_DATE_MONTH, 4);
            map.put(COLUMN_DATE_DAY, 5);
            map.put(COLUMN_MATCH_DAY, 6);
            map.put(COLUMN_HOME_TEAM_NAME, 7);
            map.put(COLUMN_HOME_TEAM_GOALS, 8);
            map.put(COLUMN_AWAY_TEAM_NAME, 9);
            map.put(COLUMN_AWAY_TEAM_GOALS, 10);
            map.put(COLUMN_STATUS, 11);

            return map;
        }
    }
}