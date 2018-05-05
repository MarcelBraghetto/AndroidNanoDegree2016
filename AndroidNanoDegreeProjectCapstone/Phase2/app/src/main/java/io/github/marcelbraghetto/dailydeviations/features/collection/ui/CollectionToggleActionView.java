package io.github.marcelbraghetto.dailydeviations.features.collection.ui;

import android.animation.Animator;
import android.content.Context;
import android.databinding.ObservableField;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import io.github.marcelbraghetto.dailydeviations.databinding.CollectionToggleActionViewBinding;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.features.collection.logic.CollectionDisplayMode;
import io.github.marcelbraghetto.dailydeviations.features.collection.logic.providers.contracts.CollectionProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.events.CollectionModeToggleEvent;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.utils.BasicAnimationListener;

/**
 * Created by Marcel Braghetto on 12/03/16.
 *
 * Grid mode toggle button to allow for some spiffy animations! it looks neat but its just a
 * simple trick of rotating and alpha animating two images in opposite directions.
 */
public class CollectionToggleActionView extends RelativeLayout {
    //region Binding glue
    public final Glue glue = new Glue();
    public static class Glue {
        public final ObservableField<Float> iconMultiAlpha = new ObservableField<>(1f);
        public final ObservableField<Float> iconSingleAlpha = new ObservableField<>(1f);
    }
    //endregion

    private static final int ANIMATION_DURATION = 400;
    private static final float ANIMATION_ROTATION = 180f;

    private CollectionToggleActionViewBinding mViews;
    private CollectionDisplayMode mMode;
    private boolean mBusy;

    private final EventBusProvider mEventBusProvider = MainApp.getDagger().getEventBusProvider();
    private final CollectionProvider mCollectionProvider = MainApp.getDagger().getCollectionProvider();

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
        mViews = CollectionToggleActionViewBinding.inflate(LayoutInflater.from(getContext()), this, true);
        mViews.setViewModel(this);
        mMode = mCollectionProvider.getCollectionDisplayMode();

        switch(mMode) {
            case SingleColumn:
                glue.iconMultiAlpha.set(0f);
                break;
            default:
                glue.iconSingleAlpha.set(0f);
                break;
        }
    }

    public void handleButtonClick() {
        // Clicking is ignored if the view is currently in the 'busy' state, typically because
        // there is an animation still in progress.
        if(mBusy) {
            return;
        }

        // Get busy!
        mBusy = true;

        switch(mMode) {
            case MultiColumn:
                mMode = CollectionDisplayMode.SingleColumn;
                animateToSingleMode();
                break;
            case SingleColumn:
                mMode = CollectionDisplayMode.MultiColumn;
                animateToMultiMode();
                break;
        }

        // Save the new collection mode so other consumers can see the change.
        mCollectionProvider.setCollectionDisplayMode(mMode);

        // Broadcast an event notifying of the change in collection mode.
        mEventBusProvider.postEvent(new CollectionModeToggleEvent());
    }

    private void animateToMultiMode() {
        mViews.gridModeIconMulti.clearAnimation();
        mViews.gridModeIconSingle.clearAnimation();

        mViews.gridModeIconMulti
                .animate()
                .rotation(ANIMATION_ROTATION)
                .alpha(1f)
                .setDuration(ANIMATION_DURATION)
                .start();

        mViews.gridModeIconSingle
                .animate()
                .rotation(-ANIMATION_ROTATION)
                .alpha(0f)
                .setDuration(ANIMATION_DURATION)
                .setListener(new BasicAnimationListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mBusy = false;
                    }
                })
                .start();
    }

    private void animateToSingleMode() {
        mViews.gridModeIconMulti.clearAnimation();
        mViews.gridModeIconSingle.clearAnimation();

        mViews.gridModeIconMulti
                .animate()
                .rotation(-ANIMATION_ROTATION)
                .alpha(0f)
                .setDuration(ANIMATION_DURATION)
                .start();

        mViews.gridModeIconSingle
                .animate()
                .rotation(ANIMATION_ROTATION)
                .alpha(1f)
                .setDuration(ANIMATION_DURATION)
                .setListener(new BasicAnimationListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mBusy = false;
                    }
                })
                .start();
    }
}
