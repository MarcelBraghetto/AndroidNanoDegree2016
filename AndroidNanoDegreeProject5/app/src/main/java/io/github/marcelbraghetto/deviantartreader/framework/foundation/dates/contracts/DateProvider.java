package io.github.marcelbraghetto.deviantartreader.framework.foundation.dates.contracts;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTime;

/**
 * Created by Marcel Braghetto on 6/12/15.
 *
 * Abstraction of date methods. Most use Joda Time.
 */
public interface DateProvider {
    /**
     * Get the current date time.
     * @return date time now.
     */
    @NonNull DateTime getNow();

    /**
     * Return the text name for a
     * day of the week.
     * @param dayOfWeek index.
     * @return weekday text name.
     */
    @NonNull String getWeekdayName(int dayOfWeek);

    /**
     * Attempt to parse a string into a DateTime
     * object using the given format of the expected
     * input string. If the input string cannot be
     * parsed, null is returned.
     * @param format of the date in the input string.
     * @param input string to attempt to parse.
     * @return date time object or null if it couldn't
     * be parsed successfully.
     */
    @Nullable DateTime parseDateTimeString(@NonNull String format, @Nullable String input);

    /**
     * Given a date time object and a format string,
     * create a string representation.
     * @param format to use to generate the date string.
     * @param dateTime to convert into a string.
     * @return string representation of the given date time object
     * or null if the formatter failed to apply to the date time.
     */
    @Nullable String formatDateTime(@NonNull String format, @NonNull DateTime dateTime);

    /**
     * Get the number of days between the two given
     * date time objects.
     * @param start date to compare.
     * @param end date to compare.
     * @return number of days between the two.
     */
    int getDaysBetween(@NonNull DateTime start, @NonNull DateTime end);
}
