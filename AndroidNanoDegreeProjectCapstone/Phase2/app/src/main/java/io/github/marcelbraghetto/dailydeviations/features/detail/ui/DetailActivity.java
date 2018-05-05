package io.github.marcelbraghetto.dailydeviations.features.detail.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import javax.inject.Inject;

import io.github.marcelbraghetto.dailydeviations.R;
import io.github.marcelbraghetto.dailydeviations.databinding.DetailActivityBinding;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.features.detail.logic.DetailViewModel;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.ui.BaseActivity;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Marcel Braghetto on 2/03/16.
 *
 * Detail presentation activity to display the given artwork image.
 */
public class DetailActivity extends BaseActivity {
    private DetailActivityBinding mViews;
    private PhotoViewAttacher mAttacher;
    @Inject DetailViewModel mViewModel;
    private boolean mPreventSharedElementExitTransition;

    public DetailActivity() {
        MainApp.getDagger().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViews = DataBindingUtil.setContentView(this, R.layout.detail_activity);
        setSupportActionBar(mViews.detailsToolbar);
        initActionBar();
        initPhotoView();
        mViews.setViewModel(mViewModel);
        mViewModel.begin(mActions, getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.screenStarted();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewModel.screenStopped();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == android.R.id.home) {
            exitScreen();
            return true;
        }

        return mViewModel.artworkMenuItemSelected(itemId) || super.onOptionsItemSelected(item);
    }

    private void initPhotoView() {
        destroyPhotoAttacher();

        mAttacher = new PhotoViewAttacher(mViews.detailImage);
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                onBackPressed();
            }

            @Override
            public void onOutsidePhotoTap() {
                onBackPressed();
            }
        });
    }

    private final DetailViewModel.Actions mActions = new DetailViewModel.Actions() {
        @Override
        public void loadImage(@NonNull String url, int maximumImageSize) {
            // We need to tell the transition framework to wait until we've loaded the image that
            // should be part of the Lollipop shared element transition.
            supportPostponeEnterTransition();

            // Ask Glide to load the given image url, which should really come straight out of
            // the stored cache and therefore be very fast - not slowing down the shared element
            // transition by any noticeable amount.
            DrawableRequestBuilder<String> request = Glide.with(DetailActivity.this)
                    .load(url)
                    .override(maximumImageSize, maximumImageSize)
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
                            initPhotoView();
                            return false;
                        }
                    });

            request.into(mViews.detailImage);
        }

        @Override
        public void showSnackbar(@NonNull final String message) {
            DetailActivity.this.showSnackbar(message, mViews.getRoot());
        }

        @Override
        public void startActivity(@NonNull Intent intent) {
            DetailActivity.this.startActivity(intent);
        }

        @Override
        public void preventSharedElementExitTransition() {
            mPreventSharedElementExitTransition = true;
        }
    };

    @Override
    protected void onDestroy() {
        destroyPhotoAttacher();
        mViewModel.destroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        exitScreen();
    }

    private void exitScreen() {
        destroyPhotoAttacher();

        if (mPreventSharedElementExitTransition) {
            finish();
            overridePendingTransition(0, R.anim.fadeout);
        } else {
            supportFinishAfterTransition();
        }
    }

    /**
     * There is a bug in the PhotoView library where calling attacher cleanup in onDestroy
     * causes a crash if there is a shared element animation in progress.
     */
    private void destroyPhotoAttacher() {
        if(mAttacher != null) {
            mAttacher.cleanup();
            mAttacher = null;
        }
    }
}
