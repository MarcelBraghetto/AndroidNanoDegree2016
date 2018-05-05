package io.github.marcelbraghetto.dailydeviations.framework.foundation.binding;

import android.animation.Animator;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import io.github.marcelbraghetto.dailydeviations.framework.foundation.utils.BasicAnimationListener;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.utils.StringUtils;

/**
 * Created by Marcel Braghetto on 1/06/16.
 *
 * Series of glue helpers and definitions to assist with data glue tasks.
 */
public final class CommonDataBindingRules {
    private CommonDataBindingRules() { }

    /**
     * Allow the 'src' attribute of an ImageView to be set through data glue.
     * @param view to apply the src drawable for.
     * @param src drawable to set.
     */
    @BindingAdapter("android:src")
    public static void setSrc(@NonNull ImageView view, @NonNull Drawable src) {
        view.setImageDrawable(src);
    }

    /**
     * If an image view has an 'imageUrl' property then attempt to load it using Glide.
     * @param view to load the image into.
     * @param imageUrl of the image to load.
     */
    @BindingAdapter("imageUrl")
    public static void setImageUrl(@NonNull ImageView view, @NonNull String imageUrl) {
        if(!StringUtils.isEmpty(imageUrl)) {
            Glide.with(view.getContext())
                    .load(imageUrl)
                    .into(view);
        }
    }

    /**
     * Custom binding adapter to allow for animated hiding and showing of views.
     * @param view to hide or show.
     * @param value to determine whether to hide or show the view.
     */
    @BindingAdapter("animatedVisibility")
    public static void setAnimatedVisibility(@NonNull final View view, boolean value) {
        view.clearAnimation();
        if(value) {
            view.setVisibility(View.VISIBLE);
            view.animate()
                .alpha(1f)
                .setDuration(250)
                .setStartDelay(100)
                .start();
        } else {
            view.animate()
                .alpha(0f)
                .setDuration(250)
                .setListener(new BasicAnimationListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                })
                .start();
        }
    }

    /**
     * Binding adapter to map boolean values to view visibility.
     * @param view to hide or show.
     * @param value true to set View.VISIBLE, false to set View.GONE.
     */
    @BindingAdapter("android:visibility")
    public static void setVisibility(@NonNull View view, boolean value) {
        view.setVisibility(value ? View.VISIBLE : View.GONE);
    }

    /**
     * Binding adapter to allow setting an image view resource from a drawable id.
     * @param view to load drawable into.
     * @param value of the drawable id to load.
     */
    @BindingAdapter("imageResource")
    public static void setImageResource(@NonNull ImageView view, @DrawableRes int value) {
        if(value > 0) {
            view.setImageResource(value);
        }
    }

    /**
     * Binding adapter to allow setting and loading a url into a web view.
     * @param view to load url into.
     * @param value of the url to load.
     */
    @BindingAdapter("webViewUrl")
    public static void setWebViewUrl(@NonNull WebView view, @Nullable String value) {
        if(StringUtils.isEmpty(value)) {
            return;
        }

        view.loadUrl(value);
    }
}
