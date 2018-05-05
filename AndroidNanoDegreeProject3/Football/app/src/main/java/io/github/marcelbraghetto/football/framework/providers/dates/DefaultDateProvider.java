package io.github.marcelbraghetto.football.framework.providers.dates;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import io.github.marcelbraghetto.football.R;
import io.github.marcelbraghetto.football.framework.providers.dates.contracts.DateProvider;
import io.github.marcelbraghetto.football.framework.providers.strings.contracts.AppStringsProvider;

/**
 * Created by Marcel Braghetto on 6/12/15.
 */
public class DefaultDateProvider implements DateProvider {

    private final AppStringsProvider mAppStringsProvider;

    public DefaultDateProvider(@NonNull AppStringsProvider appStringsProvider) {
        mAppStringsProvider = appStringsProvider;
    }

    @NonNull
    @Override
    public DateTime getNow() {
        return DateTime.now();
    }

    @NonNull
    @Override
    public String getWeekdayName(int dayOfWeek) {
        String result;

        switch (dayOfWeek) {
            case 1:
                result = mAppStringsProvider.getString(R.string.monday);
                break;
            case 2:
                result = mAppStringsProvider.getString(R.string.tuesday);
                break;
            case 3:
                result = mAppStringsProvider.getString(R.string.wednesday);
                break;
            case 4:
                result = mAppStringsProvider.getString(R.string.thursday);
                break;
            case 5:
                result = mAppStringsProvider.getString(R.string.friday);
                break;
            case 6:
                result = mAppStringsProvider.getString(R.string.saturday);
                break;
            case 7:
                result = mAppStringsProvider.getString(R.string.sunday);
                break;
            default:
                result = "";
                break;
        }

        return result;
    }

    @Nullable
    @Override
    public DateTime parseDateTimeString(@NonNull String format, @Nullable String input) {
        if(TextUtils.isEmpty(input)) {
            return null;
        }

        try {
            return DateTimeFormat.forPattern(format).parseDateTime(input);
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    @Override
    public String formatDateTime(@NonNull String format, @NonNull DateTime dateTime) {
        try {
            DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
            return formatter.print(dateTime);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int getDaysBetween(@NonNull DateTime start, @NonNull DateTime end) {
        return Days.daysBetween(start.toLocalDate(), end.toLocalDate()).getDays();
    }
}
