package io.github.marcelbraghetto.alexandria.features.mybooks.ui;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.marcelbraghetto.alexandria.R;
import io.github.marcelbraghetto.alexandria.framework.application.MainApp;
import io.github.marcelbraghetto.alexandria.framework.providers.books.database.BooksDatabaseContract;
import io.github.marcelbraghetto.alexandria.framework.providers.books.models.Book;
import io.github.marcelbraghetto.alexandria.framework.providers.strings.contracts.AppStringsProvider;

/**
 * Created by Marcel Braghetto on 4/12/15.
 *
 * Adapter for displaying books in the user's collection.
 */
public class MyBooksAdapter extends RecyclerView.Adapter<MyBooksAdapter.ViewHolder> {
    private Cursor mCursor;
    private Book mBook;
    private Map<String, Integer> mBookColumnMap;
    private Delegate mDelegate;
    private Set<String> mExpandedItems;
    private String mHintMore;
    private String mHintLess;

    /**
     * Interaction delegate for actions taken
     * within adapter items.
     */
    public interface Delegate {
        /**
         * User selected the 'remove' action for
         * a book in the adapter.
         * @param isbn of the selected book.
         */
        void removeSelected(@NonNull String isbn);

        /**
         * User selected the 'share' action for
         * a book in the adapter.
         * @param isbn of the selected book.
         */
        void shareSelected(@NonNull String isbn);

        /**
         * User selected the 'view in browser' action
         * for a book in the adapter.
         * @param isbn of the selected book.
         */
        void viewInBrowserSelected(@NonNull String isbn);

        /**
         * User selected the thumbnail image to initiate
         * a zoom action on the image.
         * @param isbn of the selected book.
         */
        void thumbnailSelected(@NonNull String isbn);
    }

    public MyBooksAdapter(@NonNull Delegate delegate) {
        mDelegate = delegate;
        mBook = new Book();
        mBookColumnMap = BooksDatabaseContract.Books.getAllColumnsIndexMap();
        mExpandedItems = new HashSet<>();

        AppStringsProvider appStringsProvider = MainApp.getDagger().getAppStringsProvider();
        mHintMore = appStringsProvider.getString(R.string.my_books_list_item_more);
        mHintLess = appStringsProvider.getString(R.string.my_books_list_item_less);
    }

    @Nullable
    public Cursor swapCursor(@Nullable Cursor cursor) {
        if(mCursor == cursor) {
            return null;
        }

        Cursor oldCursor = mCursor;

        mCursor = cursor;

        if(mCursor != null) {
            notifyDataSetChanged();
        }

        return oldCursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_books_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        mBook.populateFromCursor(mCursor, mBookColumnMap);

        holder.bookISBN = mBook.getISBN();

        // We keep track of any items the user has 'expanded'
        // in a set, using the book ISBN as the key.
        if(mExpandedItems.contains(mBook.getISBN())) {
            holder.detailsContainer.setVisibility(View.VISIBLE);
            holder.hintTextView.setText(mHintLess);
        } else {
            holder.detailsContainer.setVisibility(View.GONE);
            holder.hintTextView.setText(mHintMore);
        }

        if(!TextUtils.isEmpty(mBook.getThumbnailUrl())) {
            holder.thumbnailImageContainer.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(mBook.getThumbnailUrl())
                    .into(holder.thumbnailImageView);
        } else {
            holder.thumbnailImageContainer.setVisibility(View.GONE);
        }

        holder.titleTextView.setText(Html.fromHtml(mBook.getTitle()));
        holder.authorsTextView.setText(Html.fromHtml(mBook.getAuthors()));
        holder.publishedTextView.setText(Html.fromHtml(mBook.getPublishDate()));
        holder.categoriesTextView.setText(Html.fromHtml(mBook.getCategories()));
        holder.descriptionTextView.setText(Html.fromHtml(mBook.getDescription()));
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.my_books_list_item_toolbar) Toolbar toolbar;
        @Bind(R.id.my_books_list_item_icon_container) View thumbnailImageContainer;
        @Bind(R.id.my_books_list_item_icon) ImageView thumbnailImageView;
        @Bind(R.id.my_books_list_item_title) TextView titleTextView;
        @Bind(R.id.my_books_list_item_authors) TextView authorsTextView;
        @Bind(R.id.my_books_list_item_published) TextView publishedTextView;
        @Bind(R.id.my_books_list_item_categories) TextView categoriesTextView;
        @Bind(R.id.my_books_list_item_details) View detailsContainer;
        @Bind(R.id.my_books_list_item_description) TextView descriptionTextView;
        @Bind(R.id.my_books_list_item_hint) TextView hintTextView;

        String bookISBN;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(bookISBN)) {
                        return;
                    }

                    if (mExpandedItems.contains(bookISBN)) {
                        mExpandedItems.remove(bookISBN);
                        detailsContainer.setVisibility(View.GONE);
                        hintTextView.setText(mHintMore);
                    } else {
                        mExpandedItems.add(bookISBN);
                        detailsContainer.setVisibility(View.VISIBLE);
                        hintTextView.setText(mHintLess);
                    }
                }
            });

            thumbnailImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(bookISBN)) {
                        return;
                    }

                    if (mDelegate != null) {
                        mDelegate.thumbnailSelected(bookISBN);
                    }
                }
            });

            toolbar.inflateMenu(R.menu.my_books_list_item);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    boolean handled = true;

                    switch (item.getItemId()) {
                        case R.id.toolbar_action_remove:
                            if (mDelegate != null) {
                                mDelegate.removeSelected(bookISBN);
                            }
                            break;
                        case R.id.toolbar_action_share:
                            if (mDelegate != null) {
                                mDelegate.shareSelected(bookISBN);
                            }
                            break;
                        case R.id.toolbar_action_view_in_browser:
                            if (mDelegate != null) {
                                mDelegate.viewInBrowserSelected(bookISBN);
                            }
                            break;
                        default:
                            handled = false;
                            break;
                    }

                    return handled;
                }
            });
        }
    }
}
