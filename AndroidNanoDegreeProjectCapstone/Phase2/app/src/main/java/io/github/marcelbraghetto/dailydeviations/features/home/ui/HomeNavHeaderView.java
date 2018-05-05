package io.github.marcelbraghetto.dailydeviations.features.home.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import javax.inject.Inject;

import io.github.marcelbraghetto.dailydeviations.databinding.HomeNavHeaderViewBinding;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.features.home.logic.HomeNavHeaderViewModel;

/**
 * Created by Marcel Braghetto on 6/06/16.
 *
 * Custom navigation header view to show random daily deviation images.
 */
public class HomeNavHeaderView extends RelativeLayout {
    @Inject HomeNavHeaderViewModel mViewModel;
    private HomeNavHeaderViewBinding mViews;

    public HomeNavHeaderView(Context context) {
        super(context);
    }

    public HomeNavHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeNavHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mViewModel.attached();
    }

    @Override
    protected void onDetachedFromWindow() {
        mViewModel.detached();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        MainApp.getDagger().inject(this);
        mViews = HomeNavHeaderViewBinding.inflate(LayoutInflater.from(getContext()), this, true);
        mViews.setViewModel(mViewModel);
        mViewModel.begin(mActions);
    }

    private final HomeNavHeaderViewModel.Actions mActions = new HomeNavHeaderViewModel.Actions() {
        @Override
        public void loadImage(@NonNull String url) {
            DrawableRequestBuilder<String> request = Glide.with(getContext())
                    .load(url)
                    .override(512, 512)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            mViewModel.imageLoadFailed();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mViewModel.imageLoadSuccess();
                            return false;
                        }
                    });

            request.into(mViews.navHeaderImage);
        }
    };
}
