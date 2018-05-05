package io.github.marcelbraghetto.alexandria.features.addbook.logic;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import javax.inject.Inject;

import io.github.marcelbraghetto.alexandria.R;
import io.github.marcelbraghetto.alexandria.features.scanner.ui.ScannerActivity;
import io.github.marcelbraghetto.alexandria.features.thumbnailviewer.ui.ThumbnailViewerActivity;
import io.github.marcelbraghetto.alexandria.framework.core.BaseController;
import io.github.marcelbraghetto.alexandria.framework.providers.books.contracts.BookSearchDelegate;
import io.github.marcelbraghetto.alexandria.framework.providers.books.contracts.BooksProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.books.models.Book;
import io.github.marcelbraghetto.alexandria.framework.providers.device.contracts.DeviceUtilsProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.network.NetworkRequestError;
import io.github.marcelbraghetto.alexandria.framework.providers.strings.contracts.AppStringsProvider;

/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * Controller logic for the add books feature which is responsible
 * for searching and adding books to the user's collection.
 */
public class AddBookController extends BaseController<AddBookController.Delegate> {
    private static final String ISBN_PREFIX = "978";
    private static final int VALID_INPUT_LENGTH = 10;

    private final Context mApplicationContext;
    private final AppStringsProvider mAppStringsProvider;
    private final BooksProvider mBooksProvider;

    private Book mSelectedBook;
    private boolean mScanSupported;

    @Inject
    public AddBookController(@NonNull Context applicationContext,
                             @NonNull AppStringsProvider appStringsProvider,
                             @NonNull BooksProvider booksProvider,
                             @NonNull DeviceUtilsProvider deviceUtilsProvider) {

        super(Delegate.class);

        mApplicationContext = applicationContext;
        mAppStringsProvider = appStringsProvider;
        mBooksProvider = booksProvider;
        mScanSupported = deviceUtilsProvider.hasBackCamera() && deviceUtilsProvider.isAtLeastIceCreamSandwich();
    }

    /**
     * Start the controller with the given delegate.
     * @param delegate to connect to.
     */
    public void initController(@Nullable Delegate delegate) {
        setDelegate(delegate);

        mDelegate.setScreenTitle(mAppStringsProvider.getString(R.string.add_book_screen_title));

        // Not all devices are capable of performing
        // barcode scanning - in particular if there
        // is no back camera or the device is running
        // an Android version lower than ICS.
        if(!mScanSupported) {
            mDelegate.hideScanButton();
        }
    }

    /**
     * User changed the text in the search input
     * field - it will be validated to determine
     * when a search should be initiated.
     * @param text that the user entered.
     */
    public void searchTextChanged(@NonNull String text) {
        mDelegate.hideBookDetails();

        if(text.length() == VALID_INPUT_LENGTH) {
            startSearch(ISBN_PREFIX + text);
        } else {
            mDelegate.hideSearchStatusText();
        }
    }

    @Override
    public void disconnect() {
        super.disconnect();
        mBooksProvider.disconnect();
    }

    /**
     * If the user taps the action button it could be
     * to either add or remove the book being presented
     * to them depending on whether it has previously
     * been added to their collection.
     */
    public void bookDetailsActionButtonSelected() {
        if(mSelectedBook == null) {
            return;
        }

        if(isSelectedBookAlreadyInCollection()) {
            mBooksProvider.deleteBook(mSelectedBook.getISBN());
            mDelegate.showSnackBarMessage(mAppStringsProvider.getString(R.string.add_book_removed, mSelectedBook.getTitle()));
            mDelegate.showBookDetails(mSelectedBook, mAppStringsProvider.getString(R.string.book_details_add_button));
        } else {
            mBooksProvider.saveBook(mSelectedBook);
            mDelegate.showSnackBarMessage(mAppStringsProvider.getString(R.string.add_book_added, mSelectedBook.getTitle()));
            mDelegate.showBookDetails(mSelectedBook, mAppStringsProvider.getString(R.string.book_details_remove_button));
        }
    }

    public void bookDetailsThumbnailSelected() {
        if(mSelectedBook == null) {
            return;
        }

        Intent intent = new Intent(mApplicationContext, ThumbnailViewerActivity.class);
        intent.putExtra(ThumbnailViewerActivity.EXTRA_THUMBNAIL_URL, mSelectedBook.getThumbnailUrl());

        mDelegate.startActivityIntent(intent);
    }

    /**
     * User selected the scan button, start
     * the barcode scanning activity.
     */
    public void scanButtonSelected() {
        mDelegate.clearSearchText();
        mDelegate.hideKeyboard();
        mDelegate.startActivityIntentForResult(new Intent(mApplicationContext, ScannerActivity.class), ScannerActivity.SCANNER_REQUEST_CODE);
    }

    /**
     * A barcode was successfully scanned and returned
     * a barcode string that we can attempt to parse
     * and validate as being a book ISBN.
     * @param barCode that was scanned in.
     */
    public void barCodeScanned(@Nullable String barCode) {
        // If the bar code is not valid then show an error.
        if(TextUtils.isEmpty(barCode) || barCode.length() != 13 || !barCode.startsWith(ISBN_PREFIX)) {
            mDelegate.showSnackBarMessage(mAppStringsProvider.getString(R.string.add_book_scan_invalid_isbn));
            return;
        }

        // We are good!
        mDelegate.setSearchText(barCode.substring(3, barCode.length()));
    }

    //region Private methods
    private boolean isSelectedBookAlreadyInCollection() {
        return mSelectedBook != null && mBooksProvider.isBookInCollection(mSelectedBook.getISBN());
    }

    private void startSearch(@NonNull String isbn) {
        mDelegate.showSearchStatusText(mAppStringsProvider.getString(R.string.add_book_status_searching));

        mBooksProvider.startBookSearch(isbn, new BookSearchDelegate() {
            @Override
            public void onSuccess(@NonNull Book book) {
                mSelectedBook = book;

                mDelegate.hideKeyboard();
                mDelegate.hideSearchStatusText();

                String actionLabel = isSelectedBookAlreadyInCollection() ? mAppStringsProvider.getString(R.string.book_details_remove_button) : mAppStringsProvider.getString(R.string.book_details_add_button);
                mDelegate.showBookDetails(book, actionLabel);
            }

            @Override
            public void onZeroResults() {
                mDelegate.showSearchStatusText(mAppStringsProvider.getString(R.string.add_book_status_no_result));
            }

            @Override
            public void onError(@NonNull NetworkRequestError error) {
                switch (error) {
                    case ServerError:
                        mDelegate.showSearchStatusText(mAppStringsProvider.getString(R.string.add_book_status_server_error));
                        break;
                    default:
                        mDelegate.showSearchStatusText(mAppStringsProvider.getString(R.string.add_book_status_connection_error));
                        break;
                }
            }
        });
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
         * Display the given snack bar message
         * to the user.
         * @param message to display.
         */
        void showSnackBarMessage(@NonNull String message);

        /**
         * Display the given status text related to
         * the searching in progress action.
         * @param text to display.
         */
        void showSearchStatusText(@NonNull String text);

        /**
         * Hide the status text from view.
         */
        void hideSearchStatusText();

        /**
         * Hide the book details preview content.
         */
        void hideBookDetails();

        /**
         * Display the given book in a preview
         * area typically after a search has
         * successfully returned the data for
         * the book to display.
         * @param book to display.
         * @param actionLabel to display (add or remove).
         */
        void showBookDetails(@NonNull Book book, @NonNull String actionLabel);

        /**
         * Dismiss the keyboard if it is open.
         */
        void hideKeyboard();

        /**
         * Reset the search input text field.
         */
        void clearSearchText();

        /**
         * Set the search text in the input field
         * manually - typically after a successful
         * barcode scan which obtained an ISBN code.
         * @param text to set.
         */
        void setSearchText(@NonNull String text);

        /**
         * Begin an activity intent for result, for
         * example starting the barcode scanner
         * activity and waiting for it to finish.
         * @param intent to start.
         * @param requestCode to start intent with.
         */
        void startActivityIntentForResult(@NonNull Intent intent, int requestCode);

        /**
         * Hide the scan button - this would typically
         * happen if the current device is unable to
         * perform barcode scanning for some reason.
         */
        void hideScanButton();

        /**
         * Start the given intent.
         * @param intent to start.
         */
        void startActivityIntent(@NonNull Intent intent);
    }
    //endregion
}
