package io.github.marcelbraghetto.deviantartreader.features.collection.ui;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.marcelbraghetto.deviantartreader.R;
import io.github.marcelbraghetto.deviantartreader.features.application.MainApp;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.events.CollectionFavouritesEvent;

/**
 * Created by Marcel Braghetto on 12/03/16.
 *
 * Custom action view for the favourites toolbar icon.
 */
public class CollectionFavouritesActionView extends RelativeLayout {
    private static final int ANIMATION_DURATION = 400;
    private static final float ANIMATION_ROTATION = 360f;

    @Bind(R.id.favourites_icon_off) View mIconOff;
    @Bind(R.id.favourites_icon_on) View mIconOn;

    @OnClick(R.id.favourites_button)
    void onButtonClick() {
        handleButtonClick();
    }

    // We want to retain this statically so it survives any config changes etc.
    private static boolean sIsOn;

    private boolean mBusy;

    private final EventBusProvider mEventBusProvider = MainApp.getDagger().getEventBusProvider();

    public CollectionFavouritesActionView(Context context) {
        super(context);
        init();
    }

    public CollectionFavouritesActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CollectionFavouritesActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.collection_favourites_action_view, this);
        ButterKnife.bind(this);

        if(sIsOn) {
            mIconOn.setAlpha(1f);
            mIconOff.setAlpha(0f);
        } else {
            mIconOn.setAlpha(0f);
            mIconOff.setAlpha(1f);
        }
    }

    private void handleButtonClick() {
        // Clicking is ignored if the view is currently in the 'busy' state, typically because
        // there is an animation still in progress.
        if(mBusy) {
            return;
        }

        // Get busy!
        mBusy = true;

        if(sIsOn) {
            animateToOff();
            sIsOn = false;
        } else {
            animateToOn();
            sIsOn = true;
        }

        // Broadcast an event notifying of the change in the favourites mode.
        mEventBusProvider.postEvent(new CollectionFavouritesEvent(sIsOn));
    }

    private void animateToOff() {
        mIconOff.clearAnimation();
        mIconOn.clearAnimation();

        mIconOff.animate().rotation(ANIMATION_ROTATION).alpha(1f).setDuration(ANIMATION_DURATION).start();
        mIconOn.animate().rotation(-ANIMATION_ROTATION).alpha(0f).setDuration(ANIMATION_DURATION).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {
                mBusy = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) { }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        }).start();
    }

    private void animateToOn() {
        mIconOff.clearAnimation();
        mIconOn.clearAnimation();

        mIconOff.animate().rotation(-ANIMATION_ROTATION).alpha(0f).setDuration(ANIMATION_DURATION).start();
        mIconOn.animate().rotation(ANIMATION_ROTATION).alpha(1f).setDuration(ANIMATION_DURATION).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mBusy = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }
}
