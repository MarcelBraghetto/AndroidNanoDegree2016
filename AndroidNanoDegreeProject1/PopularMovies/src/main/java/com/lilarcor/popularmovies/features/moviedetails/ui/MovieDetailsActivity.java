package com.lilarcor.popularmovies.features.moviedetails.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.features.application.MainApp;
import com.lilarcor.popularmovies.features.moviedetails.logic.MovieDetailsController;
import com.lilarcor.popularmovies.framework.ui.ViewUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Marcel Braghetto on 19/07/15.
 */
public class MovieDetailsActivity extends AppCompatActivity {
    @Bind(R.id.movie_details_root_layout) CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.movie_details_toolbar) Toolbar mToolbar;
    @Bind(R.id.movie_details_collapsing_toolbar_layout) CollapsingToolbarLayout mCollapsingToolbarLayout;

    // Header image view
    @Bind(R.id.movie_details_header_image) ImageView mHeaderImageView;
    @Bind(R.id.movie_details_header_progress_indicator_container) View mHeaderLoadingIndicatorContainer;
    @Bind(R.id.movie_details_header_progress_indicator) ProgressBar mHeaderLoadingIndicator;
    @Bind(R.id.movie_details_header_image_failed_icon) ImageView mHeaderImageFailedIndicator;
    @Bind(R.id.movie_details_vote_average_text) TextView mVoteAverageTextView;
    @Bind(R.id.movie_details_vote_total_text) TextView mVoteTotalTextView;

    // Content
    @Bind(R.id.movie_details_floating_action_button) FloatingActionButton mFloatingActionButton;
    @Bind(R.id.movie_details_release_date_text) TextView mReleaseDateTextView;
    @Bind(R.id.movie_details_overview_text) TextView mOverviewTextView;

    private Snackbar mSnackbar;

    @Inject
    MovieDetailsController mController;

    private void initUI() {
        setContentView(R.layout.movie_details_activity);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
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

    private void setupFloatingWindow() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = getResources().getDimensionPixelSize(R.dimen.movie_details_floating_window_width);
        params.height = getResources().getDimensionPixelSize(R.dimen.movie_details_floating_window_height);
        params.alpha = 1;
        params.dimAmount = 0.7f;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        getWindow().setAttributes(params);
    }

    private boolean shouldBeFloatingWindow() {
        return MainApp.getDagger().getDeviceInfoProvider().isLargeDevice();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(shouldBeFloatingWindow()) {
            setupFloatingWindow();
        }

        initUI();
        MainApp.getDagger().inject(this);
        mController.initController(mControllerDelegate, getIntent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
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

    //region Controller delegate implementation
    private MovieDetailsController.ControllerDelegate mControllerDelegate = new MovieDetailsController.ControllerDelegate() {
        @Override
        public void loadHeaderImage(@NonNull String imageUrl) {
            Picasso.with(MovieDetailsActivity.this)
                    .load(imageUrl)
                    .into(mHeaderImageView, new Callback() {
                        @Override
                        public void onSuccess() { }

                        @Override
                        public void onError() {
                            if(mController != null) {
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
            mHeaderLoadingIndicatorContainer.setVisibility(View.VISIBLE);
        }

        @Override
        public void hideHeaderImageProgressIndicator() {
            mHeaderLoadingIndicatorContainer.setVisibility(View.GONE);
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
            finish();
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
        public void setReleaseDateText(@NonNull String text) {
            mReleaseDateTextView.setText(text);
        }

        @Override
        public void setOverviewText(@NonNull String text) {
            mOverviewTextView.setText(text);
        }
    };
    //endregion
}