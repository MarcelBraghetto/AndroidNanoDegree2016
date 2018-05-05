package io.github.marcelbraghetto.alexandria.framework.providers.books.contracts;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.github.marcelbraghetto.alexandria.framework.core.Disconnectable;
import io.github.marcelbraghetto.alexandria.framework.providers.books.models.Book;

/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * Provider to allow searching for books.
 */
public interface BooksProvider extends Disconnectable {
    /**
     * Determine whether the user has any saved books
     * or not.
     * @return true if the user has at least 1 saved book.
     */
    boolean hasSavedBooks();

    /**
     * Attempt to find a book with the
     * given ISBN.
     * @param isbn for the book to search for.
     * @param delegate callback.
     */
    void startBookSearch(@NonNull String isbn, @NonNull BookSearchDelegate delegate);

    /**
     * Get the saved book with the given ISBN if
     * it exists in the user's collection of books
     * already.
     * @param isbn of the book to fetch.
     * @return book with the given ISBN or null
     * if no book was found stored.
     */
    @Nullable Book getSavedBookWithISBN(@NonNull String isbn);

    /**
     * Determine if a book with the given ISBN is
     * already in the saved collection.
     * @param isbn for the book to check.
     * @return true if the book is already saved
     * into the local collection.
     */
    boolean isBookInCollection(@NonNull String isbn);

    /**
     * Retrieve the Uri representing the collection of
     * all the user's saved books.
     * @param searchFilter optional text to filter results on.
     * @return uri for all the saved books.
     */
    @NonNull Uri getSavedBooksUri(@Nullable String searchFilter);

    /**
     * Add the given book to the persistent collection
     * @param book to add.
     */
    void saveBook(@NonNull Book book);

    /**
     * Remove the given book from the persistent collection.
     * @param isbn of the book to remove.
     */
    void deleteBook(@NonNull String isbn);
}
