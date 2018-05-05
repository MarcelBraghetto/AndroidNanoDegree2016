package com.lilarcor.popularmovies.features.moviedetails.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.features.application.MainApp;
import com.lilarcor.popularmovies.features.moviedetails.logic.MovieDetailsController;
import com.lilarcor.popularmovies.framework.foundation.device.contracts.DeviceInfoProvider;
import com.lilarcor.popularmovies.framework.ui.ViewUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Marcel Braghetto on 19/07/15.
 *
 * UI code for the movie details screen.
 */
public class MovieDetailsFragment extends Fragment {
    @Bind(R.id.movie_details_root_layout) CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.movie_details_toolbar) Toolbar mToolbar;
    @Bind(R.id.movie_details_collapsing_toolbar_layout) CollapsingToolbarLayout mCollapsingToolbarLayout;

    // Header view
    @Bind(R.id.movie_details_header_image) ImageView mHeaderImageView;
    @Bind(R.id.movie_details_header_progress_indicator) ProgressBar mHeaderLoadingIndicator;
    @Bind(R.id.movie_details_header_image_failed_icon) ImageView mHeaderImageFailedIndicator;
    @Bind(R.id.movie_details_vote_average_text) TextView mVoteAverageTextView;
    @Bind(R.id.movie_details_vote_total_text) TextView mVoteTotalTextView;
    @Bind(R.id.movie_details_header_video_button) FloatingActionButton mHeaderVideoButton;

    @OnClick(R.id.movie_details_header_video_button)
    void videoButtonClicked() {
        mController.videoHeaderButtonSelected();
    }

    // Content
    @Bind(R.id.movie_details_floating_action_button) FloatingActionButton mFloatingActionButton;
    @Bind(R.id.movie_details_about_text) TextView mAboutTextView;

    // Videos
    @Bind(R.id.movie_details_videos_container) View mVideosContainer;
    @Bind(R.id.movie_details_videos_recycler_view) RecyclerView mVideosRecyclerView;

    // Reviews
    @Bind(R.id.movie_details_reviews_container) View mReviewsContainer;
    @Bind(R.id.movie_details_reviews_more_button) View mReviewsMoreButton;
    @OnClick(R.id.movie_details_reviews_more_button)
    void reviewsMoreButtonClicked() {
        mController.reviewsMoreButtonSelected();
    }

    @Bind(R.id.movie_details_review1_container) View mReview1Container;
    @Bind(R.id.movie_details_review1_author) TextView mReview1Author;
    @Bind(R.id.movie_details_review1_content) TextView mReview1Content;

    @Bind(R.id.movie_details_review2_container) View mReview2Container;
    @Bind(R.id.movie_details_review2_author) TextView mReview2Author;
    @Bind(R.id.movie_details_review2_content) TextView mReview2Content;

    @Bind(R.id.movie_details_review3_container) View mReview3Container;
    @Bind(R.id.movie_details_review3_author) TextView mReview3Author;
    @Bind(R.id.movie_details_review3_content) TextView mReview3Content;

    private Snackbar mSnackbar;
    private MovieDetailsVideosAdapter mVideosAdapter;

    @Inject DeviceInfoProvider mDeviceInfoProvider;

    /**
     * Generate a new instance of this fragment with the given movie id.
     *
     * @param movieId to set for this fragment instance.
     *
     * @return new movie details fragment.
     */
    public static MovieDetailsFragment newInstance(int movieId) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(MovieDetailsController.ARG_MOVIE_ID, movieId);

        fragment.setArguments(arguments);

        return fragment;
    }

    @Inject
    MovieDetailsController mController;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mController.initController(mControllerDelegate, getArguments());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainApp.getDagger().inject(this);

        ButterKnife.bind(this, view);

        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
        parentActivity.setSupportActionBar(mToolbar);
        ActionBar actionBar = parentActivity.getSupportActionBar();

        if(actionBar != null && !mDeviceInfoProvider.isLargeDevice()) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ViewUtils.setProgressBarColour(mHeaderLoadingIndicator, android.R.color.white);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mController.toggleFavouriteSelected();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setRetainInstance(true);

        return inflater.inflate(R.layout.movie_details_fragment, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
        cancelSnackbar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mController != null) {
            mController.disconnect();
            mController = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            getActivity().finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cancelSnackbar() {
        if(mSnackbar != null) {
            mSnackbar.dismiss();
            mSnackbar = null;
        }
    }

    private MovieDetailsVideosAdapter.VideosAdapterDelegate mVideosAdapterDelegate = new MovieDetailsVideosAdapter.VideosAdapterDelegate() {
        @Override
        public void playVideoSelected(@NonNull String url) {
            if(mController != null) {
                mController.videoTrailerPlayButtonSelected(url);
            }
        }

        @Override
        public void shareVideoSelected(@NonNull String title, @NonNull String url) {
            if(mController != null) {
                mController.videoTrailerShareButtonSelected(title, url);
            }
        }
    };

    //region Controller delegate implementation
    private MovieDetailsController.ControllerDelegate mControllerDelegate = new MovieDetailsController.ControllerDelegate() {
        @Override
        public void loadHeaderImage(@NonNull String imageUrl) {
            Picasso.with(getActivity())
                    .load(imageUrl)
                    .into(mHeaderImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            if (mController != null) {
                                mController.headerImageFailedToLoad();
                            }
                        }
                    });
        }

        @Override
        public void setActivityTitle(@NonNull String title) {
            mCollapsingToolbarLayout.setTitle(title);
            mCollapsingToolbarLayout.setContentDescription(title);
        }

        @Override
        public void showHeaderImageProgressIndicator() {
            mHeaderLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        public void hideHeaderImageProgressIndicator() {
            mHeaderLoadingIndicator.setVisibility(View.GONE);
        }

        @Override
        public void showHeaderImageFailedIndicator() {
            mHeaderImageFailedIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        public void hideHeaderImageFailedIndicator() {
            mHeaderImageFailedIndicator.setVisibility(View.GONE);
        }

        @Override
        public void finishActivity() {
            getActivity().finish();
        }

        @Override
        public void showSnackbar(@NonNull String text) {
            cancelSnackbar();
            mSnackbar = Snackbar.make(mCoordinatorLayout, text, Snackbar.LENGTH_SHORT);
            mSnackbar.show();
        }

        @Override
        public void refreshFloatingActionButton(boolean isMovieFavourite) {
            if(isMovieFavourite) {
                mFloatingActionButton.setImageResource(R.drawable.star_fab_on);
            } else {
                mFloatingActionButton.setImageResource(R.drawable.star_fab_off);
            }
        }

        @Override
        public void setVoteAverageText(@NonNull String text) {
            mVoteAverageTextView.setText(text);
        }

        @Override
        public void setVoteTotalText(@NonNull String text) {
            mVoteTotalTextView.setText(text);
        }

        @Override
        public void setAboutText(@NonNull String text) {
            mAboutTextView.setText(text);
        }

        @Override
        public void hideHeaderVideoPlayButton() {
            mHeaderVideoButton.setVisibility(View.GONE);
        }

        @Override
        public void showHeaderVideoPlayButton() {
            mHeaderVideoButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void startActivityIntent(@NonNull Intent intent) {
            getActivity().startActivity(intent);
        }

        @Override
        public void hideVideos() {
            mVideosContainer.setVisibility(View.GONE);
        }

        @Override
        public void showAndPopulateVideos(@NonNull final Uri dataSourceUri) {
            mVideosContainer.setVisibility(View.VISIBLE);

            mVideosRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            mVideosAdapter = new MovieDetailsVideosAdapter(mVideosAdapterDelegate);
            mVideosRecyclerView.setAdapter(mVideosAdapter);

            getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    return new CursorLoader(getActivity(), dataSourceUri, null, null, null, null);
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    mVideosAdapter.swapCursor(data);
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    mVideosAdapter.swapCursor(null);
                }
            });
        }

        @Override
        public void hideAllReviews() {
            mReviewsContainer.setVisibility(View.GONE);
            mReview1Container.setVisibility(View.GONE);
            mReview2Container.setVisibility(View.GONE);
            mReview3Container.setVisibility(View.GONE);
        }

        @Override
        public void showReview1(@NonNull String author, @NonNull String content) {
            mReviewsContainer.setVisibility(View.VISIBLE);
            mReview1Container.setVisibility(View.VISIBLE);
            mReview1Author.setText(author);
            mReview1Content.setText(content);
        }

        @Override
        public void showReview2(@NonNull String author, @NonNull String content) {
            mReview2Container.setVisibility(View.VISIBLE);
            mReview2Author.setText(author);
            mReview2Content.setText(content);
        }

        @Override
        public void showReview3(@NonNull String author, @NonNull String content) {
            mReview3Container.setVisibility(View.VISIBLE);
            mReview3Author.setText(author);
            mReview3Content.setText(content);
        }

        @Override
        public void hideMoreReviewsButton() {
            mReviewsMoreButton.setVisibility(View.GONE);
        }

        @Override
        public void showMoreReviewsButton() {
            mReviewsMoreButton.setVisibility(View.VISIBLE);
        }
    };
    //endregion
}