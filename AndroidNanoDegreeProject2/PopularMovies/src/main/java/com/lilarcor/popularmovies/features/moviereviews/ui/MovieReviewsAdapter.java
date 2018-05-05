package com.lilarcor.popularmovies.features.moviereviews.ui;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.framework.movies.models.MovieReview;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.MovieReviews;

/**
 * Created by Marcel Braghetto on 9/08/15.
 *
 * Basic recycler view adapter to render user review items.
 */
public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ViewHolder> {
    private Cursor mCursor;
    private MovieReview mMovieReview;
    private Map<String, Integer> mReviewsColumnMap;

    public MovieReviewsAdapter() {
        mMovieReview = new MovieReview();
        mReviewsColumnMap = MovieReviews.getAllColumnsIndexMap();
    }

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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_reviews_adapter_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        mMovieReview.populateFromCursor(mCursor, mReviewsColumnMap);

        holder.authorTextView.setText(mMovieReview.getAuthorName());
        holder.contentTextView.setText(mMovieReview.getContent());
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.movie_reviews_adapter_item_author) TextView authorTextView;
        @Bind(R.id.movie_reviews_adapter_item_content) TextView contentTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
