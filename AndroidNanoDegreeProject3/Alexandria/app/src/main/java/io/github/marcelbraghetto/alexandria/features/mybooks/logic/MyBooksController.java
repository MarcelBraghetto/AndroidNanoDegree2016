package io.github.marcelbraghetto.alexandria.features.mybooks.logic;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import javax.inject.Inject;

import io.github.marcelbraghetto.alexandria.R;
import io.github.marcelbraghetto.alexandria.features.thumbnailviewer.ui.ThumbnailViewerActivity;
import io.github.marcelbraghetto.alexandria.framework.core.BaseController;
import io.github.marcelbraghetto.alexandria.framework.providers.books.contracts.BooksProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.books.models.Book;
import io.github.marcelbraghetto.alexandria.framework.providers.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.eventbus.events.AddBookActionEvent;
import io.github.marcelbraghetto.alexandria.framework.providers.strings.contracts.AppStringsProvider;

/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * Controller logic for the 'my books' fragment
 * which displays all the user's added books.
 */
public class MyBooksController extends BaseController<MyBooksController.Delegate> {
    private final Context mApplicationContext;
    private final EventBusProvider mEventBusProvider;
    private final AppStringsProvider mAppStringsProvider;
    private final BooksProvider mBooksProvider;

    //region Public methods
    @Inject
    public MyBooksController(@NonNull Context applicationContext,
                             @NonNull EventBusProvider eventBusProvider,
                             @NonNull AppStringsProvider appStringsProvider,
                             @NonNull BooksProvider booksProvider) {

        super(Delegate.class);

        mApplicationContext = applicationContext;
        mEventBusProvider = eventBusProvider;
        mAppStringsProvider = appStringsProvider;
        mBooksProvider = booksProvider;
    }

    public void initController(@Nullable Delegate delegate) {
        setDelegate(delegate);
        mDelegate.setScreenTitle(mAppStringsProvider.getString(R.string.my_books_screen_title));
        mDelegate.hideNoBooksMessage();
        mDelegate.setDataSource(mBooksProvider.getSavedBooksUri(null));
    }

    /**
     * User selects the floating action button to
     * add a book, so broadcast the user's action.
     */
    public void actionButtonSelected() {
        mEventBusProvider.postEvent(new AddBookActionEvent());
    }

    /**
     * User selected the 'remove' action for a book
     * in their collection.
     * @param isbn of the book to remove.
     */
    public void removeSelected(@NonNull String isbn) {
        Book book = mBooksProvider.getSavedBookWithISBN(isbn);

        if(book == null) {
            return;
        }

        mBooksProvider.deleteBook(isbn);
        mDelegate.showSnackbarMessage(mAppStringsProvider.getString(R.string.add_book_removed, book.getTitle()));
    }

    /**
     * User selected the 'share' action for a book
     * in their collection.
     * @param isbn of the book to share.
     */
    public void shareSelected(@NonNull String isbn) {
        Book book = mBooksProvider.getSavedBookWithISBN(isbn);

        if(book == null) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, book.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT, book.getPreviewLink());
        intent.setType("text/plain");

        mDelegate.startActivityIntent(intent);
    }

    /**
     * User selected the 'view in browser' action
     * for a book in their collection.
     * @param isbn of the selected book.
     */
    public void viewInBrowserSelected(String isbn) {
        Book book = mBooksProvider.getSavedBookWithISBN(isbn);

        if(book == null) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(book.getPreviewLink()));
        intent.putExtra(Intent.EXTRA_TEXT, book.getPreviewLink());

        mDelegate.startActivityIntent(intent);
    }

    /**
     * User selected the thumbnail icon action for
     * a book in their collection. We want to display
     * a larger preview image version of it.
     * @param isbn of the selected book.
     */
    public void thumbnailSelected(@NonNull String isbn) {
        Book book = mBooksProvider.getSavedBookWithISBN(isbn);

        if(book == null || TextUtils.isEmpty(book.getThumbnailUrl())) {
            return;
        }

        Intent intent = new Intent(mApplicationContext, ThumbnailViewerActivity.class);
        intent.putExtra(ThumbnailViewerActivity.EXTRA_THUMBNAIL_URL, book.getThumbnailUrl());

        mDelegate.startActivityIntent(intent);
    }

    /**
     * The search text in the toolbar was changed
     * typically from the user trying to filter
     * their book results.
     * @param text that was entered.
     */
    public void searchTextChanged(@NonNull String text) {
        mDelegate.setDataSource(mBooksProvider.getSavedBooksUri(text));
    }

    /**
     * The loader manager has finished loading its cursor,
     * this is an opportunity to examine the cursor and
     * find out whether it has zero items in it.
     * @param cursor that was loaded.
     */
    public void loaderManagerFinished(@Nullable Cursor cursor) {
        // If we have loaded an empty cursor, show something
        // to the user indicating that they should add some
        // books.
        if(cursor == null || cursor.getCount() == 0) {
            mDelegate.showNoBooksMessage();
        } else {
            mDelegate.hideNoBooksMessage();
        }
    }
    //endregion

    //region Delegate contract
    public interface Delegate {
        /**
         * Display the given screen title.
         * @param title to display.
         */
        void setScreenTitle(@NonNull String title);

        /**
         * Initialise the screen with the given data
         * source Uri to apply to the loader.
         * @param dataSourceUri to apply to the loader.
         */
        void setDataSource(@NonNull Uri dataSourceUri);

        /**
         * Present the given message in a snack bar widget.
         * @param message to display.
         */
        void showSnackbarMessage(@NonNull String message);

        /**
         * Start the given intent.
         * @param intent to start.
         */
        void startActivityIntent(@NonNull Intent intent);

        /**
         * Show a message indicating that there are
         * no books to display
         */
        void showNoBooksMessage();

        /**
         * Hide the message indicating that there are
         * no books to display.
         */
        void hideNoBooksMessage();
    }
    //endregion
}
