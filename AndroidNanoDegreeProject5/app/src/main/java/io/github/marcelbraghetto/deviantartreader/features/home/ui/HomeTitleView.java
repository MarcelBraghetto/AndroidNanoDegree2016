package io.github.marcelbraghetto.deviantartreader.features.home.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.marcelbraghetto.deviantartreader.R;

/**
 * Created by Marcel Braghetto on 14/03/16.
 *
 * Simple custom view to hold a rich title view that can cross fade between two titles.
 */
public class HomeTitleView extends FrameLayout {
    private static final int ANIMATION_DURATION = 400;

    @Bind(R.id.home_title_view_daily_deviations_text_view) View mDailyDeviationsTitleView;
    @Bind(R.id.home_title_view_favourites_text_view) View mFavouritesTitleView;

    public HomeTitleView(Context context) {
        super(context);
        init();
    }

    public HomeTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HomeTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void showDailyDeviationsTitle() {
        mDailyDeviationsTitleView.clearAnimation();
        mFavouritesTitleView.clearAnimation();

        mDailyDeviationsTitleView.animate().alpha(1f).setDuration(ANIMATION_DURATION).start();
        mFavouritesTitleView.animate().alpha(0f).setDuration(ANIMATION_DURATION).start();
    }

    public void showFavouritesTitle() {
        mDailyDeviationsTitleView.clearAnimation();
        mFavouritesTitleView.clearAnimation();

        mDailyDeviationsTitleView.animate().alpha(0f).setDuration(ANIMATION_DURATION).start();
        mFavouritesTitleView.animate().alpha(1f).setDuration(ANIMATION_DURATION).start();
    }

    private void init() {
        inflate(getContext(), R.layout.home_title_view, this);
        ButterKnife.bind(this);
        mFavouritesTitleView.setAlpha(0f);
    }
}
