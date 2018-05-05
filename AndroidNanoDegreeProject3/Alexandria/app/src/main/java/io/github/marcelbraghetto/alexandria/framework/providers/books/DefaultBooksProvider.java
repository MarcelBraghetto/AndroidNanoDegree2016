package io.github.marcelbraghetto.alexandria.framework.providers.books;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import io.github.marcelbraghetto.alexandria.framework.providers.books.contracts.BookSearchDelegate;
import io.github.marcelbraghetto.alexandria.framework.providers.books.contracts.BooksProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.books.database.BooksDatabaseContract;
import io.github.marcelbraghetto.alexandria.framework.providers.books.models.Book;
import io.github.marcelbraghetto.alexandria.framework.providers.network.NetworkRequestError;
import io.github.marcelbraghetto.alexandria.framework.providers.network.contracts.NetworkRequestDelegate;
import io.github.marcelbraghetto.alexandria.framework.providers.network.contracts.NetworkRequestProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.strings.contracts.AppStringsProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.threading.contracts.ThreadUtilsProvider;

/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * Books provider containing various helper
 * methods related to interacting with books.
 */
public class DefaultBooksProvider implements BooksProvider {
    private final Context mApplicationContext;
    private final NetworkRequestProvider mNetworkRequestProvider;
    private final AppStringsProvider mAppStringsProvider;
    private final ThreadUtilsProvider mThreadUtilsProvider;

    private String mSearchRequestTag;

    public DefaultBooksProvider(@NonNull Context applicationContext,
                                @NonNull NetworkRequestProvider networkRequestProvider,
                                @NonNull AppStringsProvider appStringsProvider,
                                @NonNull ThreadUtilsProvider threadUtilsProvider) {

        mApplicationContext = applicationContext;
        mNetworkRequestProvider = networkRequestProvider;
        mAppStringsProvider = appStringsProvider;
        mThreadUtilsProvider = threadUtilsProvider;
    }

    @Override
    public void startBookSearch(@NonNull final String isbn, @NonNull final BookSearchDelegate delegate) {
        cancelSearchRequest();

        // First check if the book is already saved locally - no
        // need to hit the network if we don't need to.
        Book savedBook = getSavedBookWithISBN(isbn);

        if(savedBook != null) {
            delegate.onSuccess(savedBook);
            return;
        }

        // Otherwise, proceed to downloading the search result.
        mSearchRequestTag = mAppStringsProvider.generateGUID();

        String url = Uri.parse("https://www.googleapis.com/books/v1/volumes")
                    .buildUpon()
                    .appendQueryParameter("q", "ISBN:" + isbn)
                    .build()
                    .toString();

        mNetworkRequestProvider.startGetRequest(mSearchRequestTag, url, new NetworkRequestDelegate() {
            @Override
            public void onRequestComplete(@NonNull String requestTag, @NonNull String response) {
                // This was a different request than was originally triggered
                if (!requestTag.equals(mSearchRequestTag)) {
                    return;
                }

                final Book book = parseSearchResult(response);

                if (book == null) {
                    mThreadUtilsProvider.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            delegate.onZeroResults();
                        }
                    });
                    return;
                }

                // Apply the given ISBN to the book instance that was found.
                book.setISBN(isbn);

                mThreadUtilsProvider.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        delegate.onSuccess(book);
                    }
                });
            }

            @Override
            public void onRequestFailed(@NonNull String requestTag, @NonNull final NetworkRequestError error) {
                // This was a different request than was originally triggered
                if (!requestTag.equals(mSearchRequestTag)) {
                    return;
                }

                mThreadUtilsProvider.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        delegate.onError(error);
                    }
                });
            }
        });
    }

    @Override
    public boolean hasSavedBooks() {
        Cursor cursor = mApplicationContext.getContentResolver().query(BooksDatabaseContract.Books.getContentUriBookCount(), null, null, null, null);
        boolean result = false;

        if(cursor != null && cursor.moveToFirst()) {
            result = cursor.getInt(0) > 0;
            cursor.close();
        }

        return result;
    }

    @Nullable
    @Override
    public Book getSavedBookWithISBN(@NonNull String isbn) {
        Cursor cursor = mApplicationContext.getContentResolver().query(BooksDatabaseContract.Books.getContentUriSpecificBook(isbn), null, null, null, null);

        if(cursor != null && cursor.moveToFirst()) {
            Book book = new Book();
            book.populateFromCursor(cursor, BooksDatabaseContract.Books.getAllColumnsIndexMap());
            cursor.close();
            return book;
        }

        return null;
    }

    @Override
    public boolean isBookInCollection(@NonNull String isbn) {
        Cursor cursor = mApplicationContext.getContentResolver().query(BooksDatabaseContract.Books.getContentUriSpecificBook(isbn), null, null, null, null);

        if(cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;
        }

        return false;
    }

    @NonNull
    @Override
    public Uri getSavedBooksUri(@Nullable String searchFilter) {
        if(TextUtils.isEmpty(searchFilter)) {
            return BooksDatabaseContract.Books.getContentUriAllBooks();
        }

        return BooksDatabaseContract.Books.getContentUriFilteredBooks(searchFilter);
    }

    @Override
    public void saveBook(@NonNull Book book) {
        mApplicationContext.getContentResolver().insert(BooksDatabaseContract.Books.getContentUriAllBooks(), book.getContentValues());
    }

    @Override
    public void deleteBook(@NonNull String isbn) {
        mApplicationContext.getContentResolver().delete(BooksDatabaseContract.Books.getContentUriSpecificBook(isbn), null, null);
    }

    @Override
    public void disconnect() {
        cancelSearchRequest();
    }

    private void cancelSearchRequest() {
        mNetworkRequestProvider.cancelRequest(mSearchRequestTag);
    }

    /**
     * Attempt to parse the first 'book' result
     * returned by the server.
     * @param input expected json input.
     * @return first book if found, or null if no
     * books were returned or there was a problem
     * while parsing the response.
     */
    private Book parseSearchResult(String input) {
        try {
            SearchResultDTO dto = new Gson().fromJson(input, SearchResultDTO.class);
            return dto.getFirstBook();
        } catch (Exception e) {
            return null;
        }
    }

    //region Search result data transformation object
    /**
     * Data transformation object that has an inner structure
     * matching the expected Json response.
     */
    static class SearchResultDTO {
        @Nullable
        Book getFirstBook() {
            if(bookDTOs == null || bookDTOs.length == 0) {
                return null;
            }

            return bookDTOs[0].exportAsBook();
        }

        @SerializedName("items")
        BookDTO[] bookDTOs;

        static class BookDTO {
            /**
             * Export the DTO in its current state into
             * a correctly formed 'book' object.
             * @return book populated from the DTO.
             */
            @NonNull
            Book exportAsBook() {
                Book book = new Book();

                book.setTitle(volumeInfo.title);
                book.setDescription(volumeInfo.description);
                book.setThumbnailUrl(volumeInfo.imageLinks.thumbnailUrl);
                book.setPublishDate(volumeInfo.publishDate);
                book.setPreviewLink(volumeInfo.previewLink);

                if(volumeInfo.authors != null) {
                    StringBuilder sb = new StringBuilder();
                    int numAuthors = volumeInfo.authors.length;

                    for(int i = 0; i < numAuthors; i++) {
                        sb.append(volumeInfo.authors[i]);
                        if(i < numAuthors - 1) {
                            sb.append(", ");
                        }
                    }

                    book.setAuthors(sb.toString());
                }

                if(volumeInfo.categories != null) {
                    StringBuilder sb = new StringBuilder();
                    int numCategories = volumeInfo.categories.length;

                    for(int i = 0; i < numCategories; i++) {
                        sb.append(volumeInfo.categories[i]);
                        if(i < numCategories - 1) {
                            sb.append(", ");
                        }
                    }

                    book.setCategories(sb.toString());
                }

                return book;
            }

            @SerializedName("volumeInfo")
            VolumeInfo volumeInfo;

            public BookDTO() {
                volumeInfo = new VolumeInfo();
            }

            static class VolumeInfo {
                @SerializedName("title")
                String title;

                @SerializedName("authors")
                String[] authors;

                @SerializedName("categories")
                String[] categories;

                @SerializedName("description")
                String description;

                @SerializedName("imageLinks")
                ImageLinks imageLinks;

                @SerializedName("publishedDate")
                String publishDate;

                @SerializedName("previewLink")
                String previewLink;

                public VolumeInfo() {
                    imageLinks = new ImageLinks();
                }

                static class ImageLinks {
                    @SerializedName("thumbnail")
                    String thumbnailUrl;
                }
            }
        }
    }
    //endregion

    //region Sample JSON response
    /*
    Sample JSON response:

    {
        "items": [
            {
                "accessInfo": {
                    "accessViewStatus": "SAMPLE",
                    "country": "NZ",
                    "embeddable": true,
                    "epub": {
                        "isAvailable": false
                    },
                    "pdf": {
                        "isAvailable": false
                    },
                    "publicDomain": false,
                    "quoteSharingAllowed": false,
                    "textToSpeechPermission": "ALLOWED_FOR_ACCESSIBILITY",
                    "viewability": "PARTIAL",
                    "webReaderLink": "http://books.google.co.nz/books/reader?id=RFPpbaIAL_kC&hl=&printsec=frontcover&output=reader&source=gbs_api"
                },
                "etag": "UbJAd6bAwdA",
                "id": "RFPpbaIAL_kC",
                "kind": "books#volume",
                "saleInfo": {
                    "country": "NZ",
                    "isEbook": false,
                    "saleability": "NOT_FOR_SALE"
                },
                "searchInfo": {
                    "textSnippet": "Timeline cements Michael Crichton&#39;s place as the king of the high-concept thriller, and a master storyteller to boot."
                },
                "selfLink": "https://www.googleapis.com/books/v1/volumes/RFPpbaIAL_kC",
                "volumeInfo": {
                    "allowAnonLogging": false,
                    "authors": [
                        "Michael Crichton"
                    ],
                    "averageRating": 3.5,
                    "canonicalVolumeLink": "http://books.google.co.nz/books/about/Timeline.html?hl=&id=RFPpbaIAL_kC",
                    "categories": [
                        "France"
                    ],
                    "contentVersion": "0.20.1.0.preview.0",
                    "description": "An old man wearing a brown robe is found wandering disoriented in the Arizona desert. He is miles from any human habitation and has no memory of how he got to be there, or who he is. The only clue to his identity is the plan of a medieval monastery in his pocket. So begins the mystery of Timeline, a story that will catapult a group of young scientists back to the Middle Ages and into the heart of the Hundred Years' War. Timeline cements Michael Crichton's place as the king of the high-concept thriller, and a master storyteller to boot.",
                    "imageLinks": {
                        "smallThumbnail": "http://books.google.co.nz/books/content?id=RFPpbaIAL_kC&printsec=frontcover&img=1&zoom=5&edge=curl&source=gbs_api",
                        "thumbnail": "http://books.google.co.nz/books/content?id=RFPpbaIAL_kC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
                    },
                    "industryIdentifiers": [
                        {
                            "identifier": "9780099244721",
                            "type": "ISBN_13"
                        },
                        {
                            "identifier": "0099244721",
                            "type": "ISBN_10"
                        }
                    ],
                    "infoLink": "http://books.google.co.nz/books?id=RFPpbaIAL_kC&dq=isbn:9780099244721&hl=&source=gbs_api",
                    "language": "en",
                    "maturityRating": "NOT_MATURE",
                    "pageCount": 496,
                    "previewLink": "http://books.google.co.nz/books?id=RFPpbaIAL_kC&printsec=frontcover&dq=isbn:9780099244721&hl=&cd=1&source=gbs_api",
                    "printType": "BOOK",
                    "publishedDate": "2000",
                    "publisher": "Random House",
                    "ratingsCount": 3244,
                    "readingModes": {
                        "image": false,
                        "text": false
                    },
                    "subtitle": "",
                    "title": "Timeline"
                }
            }
        ],
        "kind": "books#volumes",
        "totalItems": 1
    }
     */
    //endregion
}
