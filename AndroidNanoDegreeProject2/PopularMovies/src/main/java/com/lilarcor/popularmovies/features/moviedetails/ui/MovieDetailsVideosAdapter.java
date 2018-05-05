package com.lilarcor.popularmovies.features.moviedetails.ui;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.framework.movies.data.contentprovider.MoviesContentContract;
import com.lilarcor.popularmovies.framework.movies.models.MovieVideo;
import com.squareup.picasso.Picasso;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Marcel Braghetto on 9/08/15.
 *
 * Recycler view adapter to display a list of video trailers for
 * a given movie details screen.
 */
public class MovieDetailsVideosAdapter extends RecyclerView.Adapter<MovieDetailsVideosAdapter.ViewHolder> {
    private final VideosAdapterDelegate mDelegate;
    private Cursor mCursor;
    private MovieVideo mMovieVideo;
    private Map<String, Integer> mVideosColumnMap;

    public interface VideosAdapterDelegate {
        void playVideoSelected(@NonNull String url);
        void shareVideoSelected(@NonNull String title, @NonNull String url);
    }

    public MovieDetailsVideosAdapter(@NonNull VideosAdapterDelegate delegate) {
        mDelegate = delegate;
        mMovieVideo = new MovieVideo();
        mVideosColumnMap = MoviesContentContract.MovieVideos.getAllColumnsIndexMap();
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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_details_video_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        mMovieVideo.populateFromCursor(mCursor, mVideosColumnMap);

        holder.setVideoUrl(mMovieVideo.getYouTubeVideoUrl());
        holder.setVideoTitle(mMovieVideo.getVideoTitle());

        Picasso.with(holder.itemView.getContext())
                .load(mMovieVideo.getYouTubeThumbnailUrl())
                .into(holder.thumbnailImageView);

        holder.titleTextView.setText(mMovieVideo.getVideoTitle());
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.movie_details_video_item_image) ImageView thumbnailImageView;
        @Bind(R.id.movie_details_video_item_play_button) View playButton;
        @Bind(R.id.movie_details_video_item_title) TextView titleTextView;
        @Bind(R.id.movie_details_video_item_share_button) View shareButton;

        private String mVideoTitle;
        private String mVideoUrl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mVideoTitle = "";
            mVideoUrl = "";

            ButterKnife.bind(this, itemView);

            playButton.setOnClickListener(mPlayButtonClickListener);
            shareButton.setOnClickListener(mShareButtonClickListener);
        }

        public void setVideoTitle(@NonNull String videoTitle) {
            mVideoTitle = videoTitle;
        }

        public void setVideoUrl(@NonNull String videoUrl) {
            mVideoUrl = videoUrl;
        }

        private View.OnClickListener mPlayButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDelegate.playVideoSelected(mVideoUrl);
            }
        };

        private View.OnClickListener mShareButtonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDelegate.shareVideoSelected(mVideoTitle, mVideoUrl);
            }
        };
    }
}
