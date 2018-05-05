package com.lilarcor.popularmovies.features.moviescollection.ui;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.features.moviescollection.logic.models.MoviesCollectionFilter;
import com.lilarcor.popularmovies.framework.movies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.lilarcor.popularmovies.features.moviescollection.logic.models.MoviesCollectionFilter.Favourites;
import static com.lilarcor.popularmovies.features.moviescollection.logic.models.MoviesCollectionFilter.Popular;
import static com.lilarcor.popularmovies.features.moviescollection.logic.models.MoviesCollectionFilter.TopRated;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.Movies;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.PopularMovies;
import static com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract.TopRatedMovies;

/**
 * Created by Marcel Braghetto on 15/07/15.
 */
public class MoviesCollectionGridAdapter extends RecyclerView.Adapter<MoviesCollectionGridAdapter.ViewHolder> {
    private GridItemDelegate mGridItemDelegate;
    private Cursor mCursor;
    private MoviesCollectionFilter mFilter;
    private Movie mMovie;
    private int mItemCount;
    private int mResultPage;
    private int mMoviePosterHeightPx;
    private Map<String, Integer> mPopularMoviesAllColumnsMap;
    private Map<String, Integer> mTopRatedMoviesAllColumnsMap;
    private Map<String, Integer> mFavouritesAllColumnsMap;

    public interface GridItemDelegate {
        void movieIdSelected(int movieId);
        void starSelected(int movieId);
        void requestMoreData(int fromResultPage);
    }

    public MoviesCollectionGridAdapter(@NonNull MoviesCollectionFilter filter, int moviePosterHeightPx, @NonNull GridItemDelegate gridItemDelegate) {
        mFilter = filter;
        mMoviePosterHeightPx = moviePosterHeightPx;
        mGridItemDelegate = gridItemDelegate;
        mMovie = new Movie();

        mPopularMoviesAllColumnsMap = PopularMovies.getAllColumnsIndexMap();
        mTopRatedMoviesAllColumnsMap = TopRatedMovies.getAllColumnsIndexMap();
        mFavouritesAllColumnsMap = Movies.getAllColumnsIndexMap();
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
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.movies_collection_view_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        mCursor.moveToPosition(position);

        mItemCount = getItemCount();

        switch (mFilter) {
            case Popular:
                mMovie.populateFromCursor(mCursor, mPopularMoviesAllColumnsMap);
                mResultPage = mCursor.getInt(mPopularMoviesAllColumnsMap.get(PopularMovies.COLUMN_RESULT_PAGE));
                break;
            case TopRated:
                mMovie.populateFromCursor(mCursor, mTopRatedMoviesAllColumnsMap);
                mResultPage = mCursor.getInt(mTopRatedMoviesAllColumnsMap.get(TopRatedMovies.COLUMN_RESULT_PAGE));
                break;
            case Favourites:
                mMovie.populateFromCursor(mCursor, mFavouritesAllColumnsMap);
                break;
        }

        // If this is a popular or top rated movie, and there are at least the minimum number of results
        // and the currently bound item is near the start or end of the range, ask for more data.
        // The Movies DB API delivers a window of 20 records at a time, and indicates which 'page' of
        // data each result set belongs to.
        if(mFilter == Popular || mFilter == TopRated) {
            if(mItemCount >= 20 && (position == mItemCount - 19 || position == mItemCount - 1)) {
                mGridItemDelegate.requestMoreData(mResultPage);
            }
        }

        viewHolder.setMovieId(mMovie.getMovieId());

        // The favourites collection shouldn't display the ranking.
        if(mFilter == Favourites) {
            viewHolder.rankTextView.setVisibility(View.GONE);
        } else {
            viewHolder.rankTextView.setText(String.valueOf(position + 1));
        }

        String posterImageUrl = mMovie.getPosterImageUrl();

        // Picasso throws a wobbly if you ask it to load an empty string url.
        if(TextUtils.isEmpty(posterImageUrl)) {
            viewHolder.posterImageView.setImageBitmap(null);
        } else {
            Picasso.with(viewHolder.itemView.getContext())
                    .load(posterImageUrl)
                    .into(viewHolder.posterImageView);
        }

        if(mMovie.isFavourite()) {
            viewHolder.starImageView.setImageResource(R.drawable.star_on);
        } else {
            viewHolder.starImageView.setImageResource(R.drawable.star_off);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.movies_collection_item_poster) ImageView posterImageView;
        @Bind(R.id.movies_collection_item_rank_text) TextView rankTextView;
        @Bind(R.id.movies_collection_item_favourites_button) View starTapBar;
        @Bind(R.id.movies_collection_item_favourites_icon) ImageView starImageView;

        private int mMovieId;

        public ViewHolder(@NonNull View view) {
            super(view);

            view.getLayoutParams().height = mMoviePosterHeightPx;

            ButterKnife.bind(this, view);

            posterImageView.setOnClickListener(mPosterImageClickListener);
            starTapBar.setOnClickListener(mStarTapBarClickListener);
        }

        private View.OnClickListener mPosterImageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGridItemDelegate.movieIdSelected(mMovieId);
            }
        };

        private View.OnClickListener mStarTapBarClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGridItemDelegate.starSelected(mMovieId);
            }
        };

        public void setMovieId(int movieId) {
            mMovieId = movieId;
        }
    }
}