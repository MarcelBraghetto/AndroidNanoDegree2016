package io.github.marcelbraghetto.dailydeviations.features.info.logic;

import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTime;

import javax.inject.Inject;

import io.github.marcelbraghetto.dailydeviations.R;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.AnalyticsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.core.BaseViewModel;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.dates.contracts.DateProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.strings.contracts.StringsProvider;

/**
 * Created by Marcel Braghetto on 16/03/16.
 *
 * Artwork info view model to display all the extended details about the given artwork.
 */
public class InfoViewModel extends BaseViewModel<InfoViewModel.Actions> {

    //region Data binding
    public final Glue glue = new Glue();
    public static class Glue {
        public final ObservableField<String> titleText = new ObservableField<>("");
        public final ObservableField<String> authorText = new ObservableField<>("");
        public final ObservableField<String> authorImageUrl = new ObservableField<>("");
    }
    //endregion

    //region Private fields
    private static final String SCREEN_NAME = "AuthorInfoScreen";

    private final StringsProvider mStringsProvider;
    private final DateProvider mDateProvider;
    private final AnalyticsProvider mAnalyticsProvider;
    //endregion

    //region Public methods
    @Inject
    public InfoViewModel(@NonNull StringsProvider stringsProvider,
                         @NonNull DateProvider dateProvider,
                         @NonNull AnalyticsProvider analyticsProvider) {
        super(Actions.class);

        mStringsProvider = stringsProvider;
        mDateProvider = dateProvider;
        mAnalyticsProvider = analyticsProvider;
    }

    /**
     * Initialize the presenter with the given configuration bundle and callback delegate.
     * @param bundle containing configuration data.
     * @param actions to callback to.
     */
    public void begin(@NonNull Bundle bundle, @Nullable Actions actions) {
        setActionDelegate(actions);

        Artwork artwork = Artwork.getFrom(bundle);

        if(artwork == null) {
            throw new UnsupportedOperationException("A valid artwork instance must be passed into the info presenter");
        }

        mAnalyticsProvider.trackScreenView(SCREEN_NAME);
        setAuthorImageUrl(artwork.getAuthorImageUrl());
        setTitleText(artwork.getTitle());

        DateTime publishDate = mDateProvider.parseDateTimeString(Artwork.PUBLISH_DATE_FORMAT, artwork.getPublishDate());

        String formattedPublishDate = "";
        if(publishDate != null) {
            formattedPublishDate = mDateProvider.formatDateTime("dd/MM/yyyy", publishDate);
        }

        setAuthorText(mStringsProvider.getString(R.string.info_author, artwork.getAuthor(), formattedPublishDate));

        StringBuilder content = new StringBuilder(artwork.getDescription());
        if(content.length() > 0) {
            content.append("<br/><br/>");
        }
        content.append(artwork.getCopyright());

        mActionDelegate.populateWebView(content.length() == 0 ? mStringsProvider.getString(R.string.info_no_description) : content.toString());
    }

    /**
     * User triggered a close action.
     */
    public void closeSelected() {
        mActionDelegate.finishActivity();
    }
    //endregion

    //region Private methods
    private void setTitleText(@NonNull String value) {
        glue.titleText.set(value);
    }

    private void setAuthorText(@NonNull String value) {
        glue.authorText.set(value);
    }

    private void setAuthorImageUrl(@NonNull String value) {
        glue.authorImageUrl.set(value);
    }
    //endregion

    //region Actions delegate contract
    public interface Actions {
        /**
         * Close the info display.
         */
        void finishActivity();

        /**
         * Populate the web view with the given content.
         * @param htmlText to display.
         */
        void populateWebView(@NonNull String htmlText);
    }
    //endregion
}
