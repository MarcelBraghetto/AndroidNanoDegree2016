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
import io.github.marcelbraghetto.deviantartreader.features.collection.logic.CollectionMode;
import io.github.marcelbraghetto.deviantartreader.features.collection.logic.providers.contracts.CollectionProvider;
import io.github.marcelbraghetto.deviantartreader.features.application.MainApp;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.events.CollectionModeToggleEvent;

/**
 * Created by Marcel Braghetto on 12/03/16.
 *
 * Grid mode toggle button to allow for some spiffy animations! it looks neat but its just a
 * simple trick of rotating and alpha animating two images in opposite directions.
 */
public class CollectionToggleActionView extends RelativeLayout {
    private static final int ANIMATION_DURATION = 400;
    private static final float ANIMATION_ROTATION = 180f;

    @Bind(R.id.grid_mode_icon_multi) View mIconMulti;
    @Bind(R.id.grid_mode_icon_single) View mIconSingle;

    private CollectionMode mMode;
    private boolean mBusy;

    private final EventBusProvider mEventBusProvider = MainApp.getDagger().getEventBusProvider();
    private final CollectionProvider mCollectionProvider = MainApp.getDagger().getCollectionProvider();

    @OnClick(R.id.grid_mode_button)
    void onButtonClick() {
        handleButtonClick();
    }

    public CollectionToggleActionView(Context context) {
        super(context);
        init();
    }

    public CollectionToggleActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CollectionToggleActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.collection_toggle_action_view, this);
        ButterKnife.bind(this);
        mMode = mCollectionProvider.getCollectionMode();

        switch(mMode) {
            case SingleColumn:
                mIconMulti.setAlpha(0f);
                break;
            default:
                mIconSingle.setAlpha(0f);
                break;
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

        switch(mMode) {
            case MultiColumn:
                mMode = CollectionMode.SingleColumn;
                animateToSingleMode();
                break;
            case SingleColumn:
                mMode = CollectionMode.MultiColumn;
                animateToMultiMode();
                break;
        }

        // Save the new collection mode so other consumers can see the change.
        mCollectionProvider.setCollectionMode(mMode);

        // Broadcast an event notifying of the change in collection mode.
        mEventBusProvider.postEvent(new CollectionModeToggleEvent());
    }

    private void animateToMultiMode() {
        mIconMulti.clearAnimation();
        mIconSingle.clearAnimation();

        mIconMulti.animate().rotation(ANIMATION_ROTATION).alpha(1f).setDuration(ANIMATION_DURATION).start();
        mIconSingle.animate().rotation(-ANIMATION_ROTATION).alpha(0f).setDuration(ANIMATION_DURATION).setListener(new Animator.AnimatorListener() {
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

    private void animateToSingleMode() {
        mIconMulti.clearAnimation();
        mIconSingle.clearAnimation();

        mIconMulti.animate().rotation(-ANIMATION_ROTATION).alpha(0f).setDuration(ANIMATION_DURATION).start();
        mIconSingle.animate().rotation(ANIMATION_ROTATION).alpha(1f).setDuration(ANIMATION_DURATION).setListener(new Animator.AnimatorListener() {
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
