package io.github.marcelbraghetto.alexandria.framework.providers.books.contracts;

import android.support.annotation.NonNull;

import io.github.marcelbraghetto.alexandria.framework.providers.books.models.Book;
import io.github.marcelbraghetto.alexandria.framework.providers.network.NetworkRequestError;

/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * Contract callback delegate for calling a
 * search request for a particular book.
 */
public interface BookSearchDelegate {
    /**
     * A book was found that matched
     * the search criteria.
     * @param book that was found.
     */
    void onSuccess(@NonNull Book book);

    /**
     * There were no search results
     * for the search criteria.
     */
    void onZeroResults();

    /**
     * A connection or other error occurred
     * while searching for the book.
     */
    void onError(@NonNull NetworkRequestError error);
}
