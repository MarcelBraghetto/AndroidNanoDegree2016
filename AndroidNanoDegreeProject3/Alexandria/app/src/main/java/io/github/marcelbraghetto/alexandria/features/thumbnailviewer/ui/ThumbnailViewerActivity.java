package io.github.marcelbraghetto.alexandria.features.thumbnailviewer.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.marcelbraghetto.alexandria.R;
import io.github.marcelbraghetto.alexandria.framework.application.MainApp;
import io.github.marcelbraghetto.alexandria.framework.providers.strings.contracts.AppStringsProvider;
import io.github.marcelbraghetto.alexandria.framework.ui.BaseActivity;

/**
 * Created by Marcel Braghetto on 5/12/15.
 *
 * Simple activity to show a thumbnail image
 * at a larger preview size.
 */
public class ThumbnailViewerActivity extends BaseActivity {
    public static final String EXTRA_THUMBNAIL_URL = "TVAU";

    @Bind(R.id.thumbnail_viewer_image) ImageView mImageView;
    @Bind(R.id.thumbnail_viewer_status) TextView mStatusTextView;
    @OnClick(R.id.thumbnail_viewer_root)
    void onRootViewClicked() {
        finish();
    }

    @Inject AppStringsProvider mAppStringsProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFloatingWindow(getResources().getDimensionPixelSize(R.dimen.thumbnail_viewer_window_width), getResources().getDimensionPixelSize(R.dimen.thumbnail_viewer_window_height));
        setContentView(R.layout.thumbnail_viewer_activity);
        MainApp.getDagger().inject(this);

        ButterKnife.bind(this);
        Glide.with(this)
                .load(getIntent().getStringExtra(EXTRA_THUMBNAIL_URL))
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        mStatusTextView.setText(mAppStringsProvider.getString(R.string.thumbnail_viewer_status_failed));
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(mImageView);


    }
}
