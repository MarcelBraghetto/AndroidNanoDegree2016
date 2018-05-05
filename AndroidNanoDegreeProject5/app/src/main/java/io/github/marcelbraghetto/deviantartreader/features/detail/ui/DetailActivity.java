package io.github.marcelbraghetto.deviantartreader.features.detail.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.marcelbraghetto.deviantartreader.R;
import io.github.marcelbraghetto.deviantartreader.features.application.MainApp;
import io.github.marcelbraghetto.deviantartreader.features.detail.logic.DetailPresenter;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.ui.BaseActivity;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Marcel Braghetto on 2/03/16.
 *
 * Detail presentation activity to display the given artwork image.
 */
public class DetailActivity extends BaseActivity {
    @Bind(R.id.details_root_view) View mRootView;
    @Bind(R.id.detail_image) ImageView mImageView;
    @Bind(R.id.details_toolbar) Toolbar mToolbar;

    @SuppressWarnings("unused")
    @OnClick(R.id.details_floating_action_button)
    void onFabClicked() {
        mPresenter.infoButtonSelected();
    }

    private Snackbar mSnackbar;

    @Inject DetailPresenter mPresenter;

    private PhotoViewAttacher mAttacher;
    private int mMaximumImageDimension;

    @Inject Context mApplicationContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        mMaximumImageDimension = getResources().getInteger(R.integer.maximum_image_dimension);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        initActionBar();

        mAttacher = new PhotoViewAttacher(mImageView);

        MainApp.getDagger().inject(this);
        mPresenter.init(mDelegate, getIntent().getExtras());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.action_share) {
            mPresenter.shareButtonSelected();
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

    private final DetailPresenter.Delegate mDelegate = new DetailPresenter.Delegate() {
        @Override
        public void setNavigationTitle(@NonNull String title) {
            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null) {
                actionBar.setTitle(title);
            }
        }

        @Override
        public void setNavigationSubtitle(@NonNull String subtitle) {
            mToolbar.setSubtitle(subtitle);
        }

        @Override
        public void loadImage(@NonNull String url) {
            // We need to tell the transition framework to wait until we've loaded the image that
            // should be part of the Lollipop shared element transition.
            supportPostponeEnterTransition();

            // Ask Glide to load the given image url, which should really come straight out of
            // the stored cache and therefore be very fast - not slowing down the shared element
            // transition by any noticable amount.
            DrawableRequestBuilder<String> request = Glide.with(mApplicationContext)
                    .load(url)
                    .override(mMaximumImageDimension, mMaximumImageDimension)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            // Allow the transition manager to continue even though we had an error.
                            supportStartPostponedEnterTransition();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            // Allow the transition manager to continue and start the photo view
                            // library to enable pinch to zoom and panning the image view.
                            supportStartPostponedEnterTransition();
                            //mAttacher = new PhotoViewAttacher(mImageView);
                            return false;
                        }
                    });

            request.into(mImageView);
        }

        @Override
        public void startActivityIntent(@NonNull Intent intent) {
            startActivity(intent);
        }

        @Override
        public void showGreetingMessage(@NonNull String message) {
            cancelSnackbar();
            mSnackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
            mSnackbar.getView().setBackgroundColor(getResources().getColor(R.color.primary));
            mSnackbar.show();
        }
    };

    @Override
    protected void onDestroy() {
        gracefullyCleanup();
        super.onDestroy();

        if(mPresenter != null) {
            mPresenter.disconnect();
            mPresenter = null;
        }
    }

    @Override
    protected void onHomeButtonSelected() {
        gracefullyCleanup();
        super.onHomeButtonSelected();
    }

    @Override
    public void onBackPressed() {
        gracefullyCleanup();
        super.onBackPressed();
    }

    /**
     * There is a bug in the PhotoView library where calling attacher cleanup in onDestroy
     * causes a crash if there is a shared element animation in progress.
     */
    private void gracefullyCleanup() {
        if(mAttacher != null) {
            mAttacher.cleanup();
            mAttacher = null;
        }
    }
}
