package io.github.marcelbraghetto.deviantartreader.framework.artworks.models;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.Map;

import io.github.marcelbraghetto.deviantartreader.framework.foundation.utils.StringUtils;

import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_AUTHOR;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_AUTHOR_IMAGE_URL;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_COPYRIGHT;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_DESCRIPTION;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_FAVOURITE;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_GUID;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_IMAGE_HEIGHT;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_IMAGE_URL;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_IMAGE_WIDTH;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_WEB_URL;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_PUBLISH_DATE;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_TIMESTAMP;
import static io.github.marcelbraghetto.deviantartreader.framework.artworks.content.ArtworksDatabaseContract.Artworks.COLUMN_TITLE;

/**
 * Created by Marcel Braghetto on 22/02/16.
 *
 * Model representing an 'artwork' from the Deviant Art RSS feed.
 */
@Parcel
public class Artwork {
    // http://www.joda.org/joda-time/apidocs/org/joda/time/format/DateTimeFormat.html
    // Example date from Deviant Art RSS: Wed, 16 Mar 2016 12:29:23 PDT
    public static final String PUBLISH_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final String BUNDLE_KEY = "ArtworkBundleKey";

    // Note: The fields in this model are deliberately package rather than private to allow
    // the Parceler library to operate without using reflection.

    /* package */ String mGuid;
    /* package */ String mTitle;
    /* package */ String mAuthor;
    /* package */ String mAuthorImageUrl;
    /* package */ String mDescription;
    /* package */ String mImageUrl;
    /* package */ int mImageWidth;
    /* package */ int mImageHeight;
    /* package */ String mPublishDate;
    /* package */ String mCopyright;
    /* package */ String mWebUrl;
    /* package */ long mTimestamp;
    /* package */ boolean mFavourite;

    /**
     * Determine if the current instance is 'valid' by examining some key fields that should
     * contain data to be deemed in a valid state.
     * @return true if the key fields are healthy or false if the model is sick.
     */
    public boolean isValid() {
        if(StringUtils.isEmpty(mGuid)) {
            return false;
        }

        if(StringUtils.isEmpty(mTitle)) {
            return false;
        }

        if(StringUtils.isEmpty(mImageUrl)) {
            return false;
        }

        if(mImageWidth <= 0) {
            return false;
        }

        if(mImageHeight <= 0) {
            return false;
        }

        return true;
    }

    @NonNull
    public String getGuid() {
        return StringUtils.emptyIfNull(mGuid);
    }

    public void setGuid(@Nullable String guid) {
        if(StringUtils.isEmpty(guid)) {
            mGuid = guid;
            return;
        }

        // TODO: CANT SHARE THE GUID DIRECTLY BECAUSE IT GETS MUNGED INTO A NON URL!!

        // We can't allow certain characters or sequences
        mGuid = guid.replace("http:", "").replace("https:", "").replace("/", "_").replace(" ", "_");
    }

    @NonNull
    public String getTitle() {
        return StringUtils.emptyIfNull(mTitle);
    }

    public void setTitle(@Nullable String title) {
        mTitle = title;
    }

    @NonNull
    public String getAuthor() {
        return StringUtils.emptyIfNull(mAuthor);
    }

    public void setAuthor(@Nullable String author) {
        mAuthor = author;
    }

    @NonNull
    public String getAuthorImageUrl() {
        return StringUtils.emptyIfNull(mAuthorImageUrl);
    }

    public void setAuthorImageUrl(@Nullable String authorImageUrl) {
        mAuthorImageUrl = authorImageUrl;
    }

    @NonNull
    public String getDescription() {
        return StringUtils.emptyIfNull(mDescription);
    }

    public void setDescription(@Nullable String description) {
        mDescription = description;
    }

    @NonNull
    public String getImageUrl() {
        return StringUtils.emptyIfNull(mImageUrl);
    }

    public void setImageUrl(@Nullable String url) {
        mImageUrl = url;
    }

    public int getImageWidth() {
        return mImageWidth;
    }

    public void setImageWidth(int imageWidth) {
        mImageWidth = imageWidth;
    }

    public int getImageHeight() {
        return mImageHeight;
    }

    public void setImageHeight(int imageHeight) {
        mImageHeight = imageHeight;
    }

    @NonNull
    public String getPublishDate() {
        return StringUtils.emptyIfNull(mPublishDate);
    }

    public void setPublishDate(@Nullable String publishDate) {
        mPublishDate = publishDate;
    }

    @NonNull
    public String getCopyright() {
        return StringUtils.emptyIfNull(mCopyright);
    }

    public void setCopyright(@Nullable String copyright) {
        mCopyright = copyright;
    }

    @NonNull
    public String getWebUrl() {
        return StringUtils.emptyIfNull(mWebUrl);
    }

    public void setWebUrl(@Nullable String webUrl) {
        mWebUrl = webUrl;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

    public boolean isFavourite() {
        return mFavourite;
    }

    public void setFavourite(boolean favourite) {
        mFavourite = favourite;
    }

    /**
     * Generate a set of content values that can be used for content provider
     * operations such as inserting artworks into the database.
     *
     * @return collection of content values representing this artwork instance.
     */
    @NonNull
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(COLUMN_GUID, getGuid());
        values.put(COLUMN_TITLE, getTitle());
        values.put(COLUMN_AUTHOR, getAuthor());
        values.put(COLUMN_AUTHOR_IMAGE_URL, getAuthorImageUrl());
        values.put(COLUMN_DESCRIPTION, getDescription());
        values.put(COLUMN_IMAGE_URL, getImageUrl());
        values.put(COLUMN_IMAGE_WIDTH, getImageWidth());
        values.put(COLUMN_IMAGE_HEIGHT, getImageHeight());
        values.put(COLUMN_PUBLISH_DATE, getPublishDate());
        values.put(COLUMN_COPYRIGHT, getCopyright());
        values.put(COLUMN_WEB_URL, getWebUrl());
        values.put(COLUMN_TIMESTAMP, getTimestamp());
        values.put(COLUMN_FAVOURITE, isFavourite() ? 1 : 0);

        return values;
    }

    /**
     * Given a cursor and a 'column index map', translate the data within the cursor into
     * the fields of this artwork instance.
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

        setGuid(cursor.getString(columnIndexMap.get(COLUMN_GUID)));
        setTitle(cursor.getString(columnIndexMap.get(COLUMN_TITLE)));
        setAuthor(cursor.getString(columnIndexMap.get(COLUMN_AUTHOR)));
        setAuthorImageUrl(cursor.getString(columnIndexMap.get(COLUMN_AUTHOR_IMAGE_URL)));
        setDescription(cursor.getString(columnIndexMap.get(COLUMN_DESCRIPTION)));
        setImageUrl(cursor.getString(columnIndexMap.get(COLUMN_IMAGE_URL)));
        setImageWidth(cursor.getInt(columnIndexMap.get(COLUMN_IMAGE_WIDTH)));
        setImageHeight(cursor.getInt(columnIndexMap.get(COLUMN_IMAGE_HEIGHT)));
        setPublishDate(cursor.getString(columnIndexMap.get(COLUMN_PUBLISH_DATE)));
        setCopyright(cursor.getString(columnIndexMap.get(COLUMN_COPYRIGHT)));
        setWebUrl(cursor.getString(columnIndexMap.get(COLUMN_WEB_URL)));
        setTimestamp(cursor.getLong(columnIndexMap.get(COLUMN_TIMESTAMP)));
        setFavourite(cursor.getLong(columnIndexMap.get(COLUMN_FAVOURITE)) == 1);
    }

    /**
     * Put the current artwork instance into an intent.
     * @param intent to put the artwork instance into.
     */
    public void putInto(@NonNull Intent intent) {
        intent.putExtra(BUNDLE_KEY, Parcels.wrap(this));
    }

    /**
     * Extract an artwork instance parcelable from the given bundle.
     * @param bundle to extract the artwork from.
     * @return extracted artwork instance.
     */
    @Nullable
    public static Artwork getFrom(@NonNull Bundle bundle) {
        return Parcels.unwrap(bundle.getParcelable(BUNDLE_KEY));
    }
}
