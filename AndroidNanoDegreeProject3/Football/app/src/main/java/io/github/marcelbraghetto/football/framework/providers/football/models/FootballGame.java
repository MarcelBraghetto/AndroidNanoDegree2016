package io.github.marcelbraghetto.football.framework.providers.football.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Map;

import io.github.marcelbraghetto.football.R;
import io.github.marcelbraghetto.football.framework.application.MainApp;
import io.github.marcelbraghetto.football.framework.providers.football.database.FootballDatabaseContract;
import io.github.marcelbraghetto.football.framework.providers.strings.contracts.AppStringsProvider;
import io.github.marcelbraghetto.football.framework.utils.StringUtils;

/**
 * Created by Marcel Braghetto on 6/12/15.
 *
 * Data model representing a football game
 * or 'fixture' as it is known. I don't
 * understand football so 'game' makes
 * more sense to me...
 */
public class FootballGame {
    public static final int INVALID_GOALS = -1;

    // Need a date formatter in order to parse the given date strings.
    // http://www.joda.org/joda-time/apidocs/org/joda/time/format/DateTimeFormat.html
    // Sample date string from the data is: 2015-12-06T11:00:00Z
    private static final DateTimeFormatter sDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final DateTimeFormatter sTimeFormatter = DateTimeFormat.forPattern("h:mm a");

    private int mTableId;
    private String mMatchId;

    private DateTime mDateTime;
    private String mDateText;
    private int mDateYear;
    private int mDateMonth;
    private int mDateDay;

    private int mMatchDay;

    private String mHomeTeamName;
    private int mHomeTeamGoals;

    private String mAwayTeamName;
    private int mAwayTeamGoals;

    private String mStatus;

    private final AppStringsProvider mAppStringsProvider;

    public FootballGame() {
        mAppStringsProvider = MainApp.getDagger().getAppStringsProvider();

        mHomeTeamGoals = INVALID_GOALS;
        mAwayTeamGoals = INVALID_GOALS;
    }

    @NonNull
    public String getShareText() {
        if(hasGoals()) {
            return mAppStringsProvider.getString(
                    R.string.scores_list_item_share_message_game_over,
                    getHomeTeamName(),
                    getHomeTeamGoals(),
                    getAwayTeamGoals(),
                    getAwayTeamName());
        }

        return mAppStringsProvider.getString(
                R.string.scores_list_item_share_message_game_pending,
                getHomeTeamName(),
                getAwayTeamName(),
                getFormattedGameTime());
    }

    public void setDateText(@Nullable String dateText) {
        if(TextUtils.isEmpty(dateText)) {
            return;
        }

        mDateText = dateText;

        mDateTime = sDateFormatter.parseDateTime(mDateText);

        mDateYear = mDateTime.getYear();
        mDateMonth = mDateTime.getMonthOfYear();
        mDateDay = mDateTime.getDayOfMonth();
    }

    @NonNull
    public String getFormattedGameTime() {
        if(mDateTime == null) {
            return "";
        }

        return mDateTime.toString(sTimeFormatter);
    }

    public boolean hasGoals() {
        return mHomeTeamGoals != INVALID_GOALS && mAwayTeamGoals != INVALID_GOALS;
    }

    public int getTableId() {
        return mTableId;
    }

    public void setTableId(int tableId) {
        mTableId = tableId;
    }

    @NonNull
    public String getHomeTeamName() {
        return StringUtils.emptyIfNull(mHomeTeamName);
    }

    public void setHomeTeamName(@Nullable String homeTeamName) {
        mHomeTeamName = homeTeamName;
    }

    public int getHomeTeamGoals() {
        return mHomeTeamGoals;
    }

    public void setHomeTeamGoals(int homeTeamGoals) {
        mHomeTeamGoals = homeTeamGoals;
    }

    @NonNull
    public String getAwayTeamName() {
        return StringUtils.emptyIfNull(mAwayTeamName);
    }

    public void setAwayTeamName(@Nullable String awayTeamName) {
        mAwayTeamName = awayTeamName;
    }

    public int getAwayTeamGoals() {
        return mAwayTeamGoals;
    }

    public void setAwayTeamGoals(int awayTeamGoals) {
        mAwayTeamGoals = awayTeamGoals;
    }

    @NonNull
    public String getStatus() {
        return StringUtils.emptyIfNull(mStatus);
    }

    public void setStatus(@Nullable String status) {
        mStatus = status;
    }

    @NonNull
    public String getDateText() {
        return StringUtils.emptyIfNull(mDateText);
    }

    public int getMatchDay() {
        return mMatchDay;
    }

    public void setMatchDay(int matchDay) {
        mMatchDay = matchDay;
    }

    @NonNull
    public String getMatchId() {
        return StringUtils.emptyIfNull(mMatchId);
    }

    public void setMatchId(@Nullable String matchId) {
        mMatchId = matchId;
    }

    public int getDateYear() {
        return mDateYear;
    }

    public void setDateYear(int dateYear) {
        mDateYear = dateYear;
    }

    public int getDateMonth() {
        return mDateMonth;
    }

    public void setDateMonth(int dateMonth) {
        mDateMonth = dateMonth;
    }

    public int getDateDay() {
        return mDateDay;
    }

    public void setDateDay(int dateDay) {
        mDateDay = dateDay;
    }

    /**
     * Generate a set of content values that can be used for content provider
     * operations such as inserting games into the database.
     *
     * @return collection of content values representing this game instance.
     */
    @NonNull
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(FootballDatabaseContract.Games._ID, getTableId());
        values.put(FootballDatabaseContract.Games.COLUMN_MATCH_ID, getMatchId());
        values.put(FootballDatabaseContract.Games.COLUMN_DATE_TEXT, getDateText());
        values.put(FootballDatabaseContract.Games.COLUMN_DATE_YEAR, getDateYear());
        values.put(FootballDatabaseContract.Games.COLUMN_DATE_MONTH, getDateMonth());
        values.put(FootballDatabaseContract.Games.COLUMN_DATE_DAY, getDateDay());
        values.put(FootballDatabaseContract.Games.COLUMN_MATCH_DAY, getMatchDay());
        values.put(FootballDatabaseContract.Games.COLUMN_HOME_TEAM_NAME, getHomeTeamName());
        values.put(FootballDatabaseContract.Games.COLUMN_HOME_TEAM_GOALS, getHomeTeamGoals());
        values.put(FootballDatabaseContract.Games.COLUMN_AWAY_TEAM_NAME, getAwayTeamName());
        values.put(FootballDatabaseContract.Games.COLUMN_AWAY_TEAM_GOALS, getAwayTeamGoals());
        values.put(FootballDatabaseContract.Games.COLUMN_STATUS, getStatus());

        return values;
    }

    /**
     * Given a cursor and a 'column index map', translate the data within the cursor into
     * the fields of this game instance.
     *
     * The column index map is a hash map that would typically have come from the content
     * provider contract, which stores the table column names as keys, and the index of
     * where that column data can be found within the given cursor.
     *
     * @param cursor to populate from.
     * @param columnIndexMap to use as the data mapping.
     */
    public void populateFromCursor(@Nullable Cursor cursor, @NonNull Map<String, Integer> columnIndexMap) {
        if(cursor == null) {
            return;
        }

        setTableId(cursor.getInt(columnIndexMap.get(FootballDatabaseContract.Games._ID)));
        setMatchId(cursor.getString(columnIndexMap.get(FootballDatabaseContract.Games.COLUMN_MATCH_ID)));
        setDateText(cursor.getString(columnIndexMap.get(FootballDatabaseContract.Games.COLUMN_DATE_TEXT)));
        setDateYear(cursor.getInt(columnIndexMap.get(FootballDatabaseContract.Games.COLUMN_DATE_YEAR)));
        setDateMonth(cursor.getInt(columnIndexMap.get(FootballDatabaseContract.Games.COLUMN_DATE_MONTH)));
        setDateDay(cursor.getInt(columnIndexMap.get(FootballDatabaseContract.Games.COLUMN_DATE_DAY)));
        setMatchDay(cursor.getInt(columnIndexMap.get(FootballDatabaseContract.Games.COLUMN_MATCH_DAY)));
        setHomeTeamName(cursor.getString(columnIndexMap.get(FootballDatabaseContract.Games.COLUMN_HOME_TEAM_NAME)));
        setHomeTeamGoals(cursor.getInt(columnIndexMap.get(FootballDatabaseContract.Games.COLUMN_HOME_TEAM_GOALS)));
        setAwayTeamName(cursor.getString(columnIndexMap.get(FootballDatabaseContract.Games.COLUMN_AWAY_TEAM_NAME)));
        setAwayTeamGoals(cursor.getInt(columnIndexMap.get(FootballDatabaseContract.Games.COLUMN_AWAY_TEAM_GOALS)));
        setStatus(cursor.getString(columnIndexMap.get(FootballDatabaseContract.Games.COLUMN_STATUS)));
    }
}
