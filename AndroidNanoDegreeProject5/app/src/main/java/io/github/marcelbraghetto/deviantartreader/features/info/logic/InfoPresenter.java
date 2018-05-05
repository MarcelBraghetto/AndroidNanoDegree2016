package io.github.marcelbraghetto.deviantartreader.features.info.logic;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.DateTime;

import javax.inject.Inject;

import io.github.marcelbraghetto.deviantartreader.R;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.core.BasePresenter;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.dates.contracts.DateProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.strings.contracts.StringsProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.utils.StringUtils;

/**
 * Created by Marcel Braghetto on 16/03/16.
 *
 * Artwork info presenter logic to display all the extended details about the given artwork.
 */
public class InfoPresenter extends BasePresenter<InfoPresenter.Delegate> {
    private final StringsProvider mStringsProvider;
    private final DateProvider mDateProvider;

    //region Public methods
    @Inject
    public InfoPresenter(@NonNull StringsProvider stringsProvider,
                         @NonNull DateProvider dateProvider) {
        super(Delegate.class);
        mStringsProvider = stringsProvider;
        mDateProvider = dateProvider;
    }

    /**
     * Initialize the presenter with the given configuration bundle and callback delegate.
     * @param bundle containing configuration data.
     * @param delegate to callback to.
     */
    public void init(@NonNull Bundle bundle, @Nullable Delegate delegate) {
        setDelegate(delegate);

        Artwork artwork = Artwork.getFrom(bundle);

        if(artwork == null) {
            throw new UnsupportedOperationException("A valid artwork instance must be passed into the info presenter");
        }

        mDelegate.loadAuthorImage(artwork.getAuthorImageUrl());
        mDelegate.setTitleText(artwork.getTitle());

        DateTime publishDate = mDateProvider.parseDateTimeString(Artwork.PUBLISH_DATE_FORMAT, artwork.getPublishDate());

        String formattedPublishDate = "";
        if(publishDate != null) {
            formattedPublishDate = mDateProvider.formatDateTime("dd/MM/yyyy", publishDate);
        }

        String authorText = mStringsProvider.getString(R.string.info_author, artwork.getAuthor(), formattedPublishDate);
        mDelegate.setAuthorText(authorText);

        String description = artwork.getDescription();
        mDelegate.setDescriptionText(StringUtils.isEmpty(description) ? mStringsProvider.getString(R.string.info_no_description) : description);
    }
    //endregion

    //region Presenter delegate contract
    public interface Delegate {
        /**
         * Display the given title text for the info window.
         * @param titleText to display.
         */
        void setTitleText(@NonNull String titleText);

        /**
         * Display the given text about the author of the current artwork.
         * @param authorText to display.
         */
        void setAuthorText(@NonNull String authorText);

        /**
         * Display the given html text describing the current artwork - typically
         * within a web view component to allow for the html to be parsed and rendered
         * correctly and include rich content.
         * @param htmlDescriptionText to display in a web view component.
         */
        void setDescriptionText(@NonNull String htmlDescriptionText);

        /**
         * Load the given author's profile image url.
         * @param url of the image to load.
         */
        void loadAuthorImage(@NonNull String url);
    }
    //endregion
}
