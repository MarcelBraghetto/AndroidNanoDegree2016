package io.github.marcelbraghetto.alexandria.framework.providers.books.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

import io.github.marcelbraghetto.alexandria.framework.providers.books.database.BooksDatabaseContract;
import io.github.marcelbraghetto.alexandria.framework.utils.StringUtils;

/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * Representation of a 'book'.
 */
public class Book {
    private String mISBN;
    private String mTitle;
    private String mDescription;
    private String mThumbnailUrl;
    private String mPublishDate;
    private String mAuthors;
    private String mCategories;
    private String mPreviewLink;

    @NonNull
    public String getTitle() {
        return StringUtils.emptyIfNull(mTitle);
    }

    @NonNull
    public String getAuthors() {
        return StringUtils.emptyIfNull(mAuthors);
    }

    @NonNull
    public String getDescription() {
        return StringUtils.emptyIfNull(mDescription);
    }

    @NonNull
    public String getThumbnailUrl() {
        return StringUtils.emptyIfNull(mThumbnailUrl);
    }

    public void setTitle(@Nullable String title) {
        mTitle = title;
    }

    public void setAuthors(@Nullable String authors) {
        mAuthors = authors;
    }

    public void setDescription(@Nullable String description) {
        mDescription = description;
    }

    public void setThumbnailUrl(@Nullable String thumbnailUrl) {
        mThumbnailUrl = thumbnailUrl;
    }

    @NonNull
    public String getPublishDate() {
        return StringUtils.emptyIfNull(mPublishDate);
    }

    public void setPublishDate(@Nullable String publishDate) {
        mPublishDate = publishDate;
    }

    @NonNull
    public String getCategories() {
        return StringUtils.emptyIfNull(mCategories);
    }

    public void setCategories(@Nullable String categories) {
        mCategories = categories;
    }

    @NonNull
    public String getISBN() {
        return StringUtils.emptyIfNull(mISBN);
    }

    public void setISBN(@NonNull String ISBN) {
        mISBN = ISBN;
    }

    @NonNull
    public String getPreviewLink() {
        return StringUtils.emptyIfNull(mPreviewLink);
    }

    public void setPreviewLink(@Nullable String previewLink) {
        mPreviewLink = previewLink;
    }

    /**
     * Create the 'search blob' to help us with
     * searching the database, by blobbing together
     * all data fields into a single text field so
     * we can easily do a SQL search against it.
     * @return search blob string.
     */
    private String getSearchBlob() {
        StringBuilder sb = new StringBuilder();

        sb.append(getISBN()).append(" ");
        sb.append(getTitle()).append(" ");
        sb.append(getAuthors()).append(" ");
        sb.append(getCategories()).append(" ");
        sb.append(getPublishDate()).append(" ");
        sb.append(getDescription());

        return sb.toString();
    }

    /**
     * Generate a set of content values that can be used for content provider
     * operations such as inserting books into the database.
     *
     * @return collection of content values representing this book instance.
     */
    @NonNull
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();

        values.put(BooksDatabaseContract.Books.COLUMN_ISBN, getISBN());
        values.put(BooksDatabaseContract.Books.COLUMN_TITLE, getTitle());
        values.put(BooksDatabaseContract.Books.COLUMN_DESCRIPTION, getDescription());
        values.put(BooksDatabaseContract.Books.COLUMN_THUMBNAIL_URL, getThumbnailUrl());
        values.put(BooksDatabaseContract.Books.COLUMN_PUBLISH_DATE, getPublishDate());
        values.put(BooksDatabaseContract.Books.COLUMN_AUTHORS, getAuthors());
        values.put(BooksDatabaseContract.Books.COLUMN_CATEGORIES, getCategories());
        values.put(BooksDatabaseContract.Books.COLUMN_PREVIEW_LINK, getPreviewLink());
        values.put(BooksDatabaseContract.Books.COLUMN_SEARCH_BLOB, getSearchBlob());

        return values;
    }

    /**
     * Given a cursor and a 'column index map', translate the data within the cursor into
     * the fields of this book instance.
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

        setISBN(cursor.getString(columnIndexMap.get(BooksDatabaseContract.Books.COLUMN_ISBN)));
        setTitle(cursor.getString(columnIndexMap.get(BooksDatabaseContract.Books.COLUMN_TITLE)));
        setDescription(cursor.getString(columnIndexMap.get(BooksDatabaseContract.Books.COLUMN_DESCRIPTION)));
        setThumbnailUrl(cursor.getString(columnIndexMap.get(BooksDatabaseContract.Books.COLUMN_THUMBNAIL_URL)));
        setPublishDate(cursor.getString(columnIndexMap.get(BooksDatabaseContract.Books.COLUMN_PUBLISH_DATE)));
        setAuthors(cursor.getString(columnIndexMap.get(BooksDatabaseContract.Books.COLUMN_AUTHORS)));
        setCategories(cursor.getString(columnIndexMap.get(BooksDatabaseContract.Books.COLUMN_CATEGORIES)));
        setPreviewLink(cursor.getString(columnIndexMap.get(BooksDatabaseContract.Books.COLUMN_PREVIEW_LINK)));
    }
}