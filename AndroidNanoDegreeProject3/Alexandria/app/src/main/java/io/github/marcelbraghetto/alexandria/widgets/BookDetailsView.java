package io.github.marcelbraghetto.alexandria.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.marcelbraghetto.alexandria.R;
import io.github.marcelbraghetto.alexandria.framework.providers.books.models.Book;

/**
 * Created by Marcel Braghetto on 3/12/15.
 *
 * Custom view to render a book's details.
 */
public class BookDetailsView extends RelativeLayout {
    @Bind(R.id.book_details_thumbnail) ImageView mThumbnailImageView;
    @Bind(R.id.book_details_title) TextView mTitleTextView;
    @Bind(R.id.book_details_button) Button mActionButton;
    @Bind(R.id.book_details_authors) TextView mAuthorsTextView;
    @Bind(R.id.book_details_published) TextView mPublishedTextView;
    @Bind(R.id.book_details_categories) TextView mCategoriesTextView;
    @Bind(R.id.book_details_description) TextView mDescriptionTextView;

    @OnClick(R.id.book_details_button)
    void onActionButtonClicked() {
        if(mActionDelegate != null) {
            mActionDelegate.actionButtonSelected();
        }
    }

    @OnClick(R.id.book_details_thumbnail)
    void onThumbnailClicked() {
        if(mActionDelegate != null) {
            mActionDelegate.thumbnailSelected();
        }
    }

    public interface ActionDelegate {
        void actionButtonSelected();

        void thumbnailSelected();
    }

    private ActionDelegate mActionDelegate;

    public BookDetailsView(Context context) {
        super(context);
        init();
    }

    public BookDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BookDetailsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.book_details_view, this);
        ButterKnife.bind(this);
    }

    public void setActionDelegate(@Nullable ActionDelegate actionDelegate) {
        mActionDelegate = actionDelegate;
    }

    public void populate(@NonNull Book book, @NonNull String actionLabel) {
        mActionButton.setText(actionLabel);

        if(!TextUtils.isEmpty(book.getThumbnailUrl())) {
            mThumbnailImageView.setVisibility(VISIBLE);
            Glide.with(getContext()).load(book.getThumbnailUrl()).into(mThumbnailImageView);
        } else {
            mThumbnailImageView.setVisibility(GONE);
        }

        mTitleTextView.setText(Html.fromHtml(book.getTitle()));
        mAuthorsTextView.setText(Html.fromHtml(book.getAuthors()));
        mPublishedTextView.setText(Html.fromHtml(book.getPublishDate()));
        mCategoriesTextView.setText(Html.fromHtml(book.getCategories()));
        mDescriptionTextView.setText(Html.fromHtml(book.getDescription()));
    }
}
