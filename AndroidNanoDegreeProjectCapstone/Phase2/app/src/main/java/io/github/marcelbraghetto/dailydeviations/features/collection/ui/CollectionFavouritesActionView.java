package io.github.marcelbraghetto.dailydeviations.features.collection.ui;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import io.github.marcelbraghetto.dailydeviations.databinding.CollectionFavouritesActionViewBinding;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.features.collection.logic.providers.contracts.CollectionProvider;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.CollectionFilterMode;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.events.CollectionFilterModeToggleEvent;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.utils.BasicAnimationListener;

import static io.github.marcelbraghetto.dailydeviations.framework.artworks.models.CollectionFilterMode.All;
import static io.github.marcelbraghetto.dailydeviations.framework.artworks.models.CollectionFilterMode.Favourites;

/**
 * Created by Marcel Braghetto on 12/03/16.
 *
 * Custom action view for the favourites toolbar icon.
 */
public class CollectionFavouritesActionView extends RelativeLayout {
    private static final int ANIMATION_DURATION = 400;
    private static final float ANIMATION_ROTATION = 360f;

    private CollectionFavouritesActionViewBinding mViews;

    private CollectionFilterMode mMode;
    private boolean mBusy;

    private final EventBusProvider mEventBusProvider = MainApp.getDagger().getEventBusProvider();
    private final CollectionProvider mCollectionProvider = MainApp.getDagger().getCollectionProvider();

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
        mMode = mCollectionProvider.getCollectionFilterMode();
        mViews = CollectionFavouritesActionViewBinding.inflate(LayoutInflater.from(getContext()), this, true);

        if(mMode == All) {
            mViews.favouritesIconFavourites.setAlpha(1f);
            mViews.favouritesIconAll.setAlpha(0f);
        } else {
            mViews.favouritesIconFavourites.setAlpha(0f);
            mViews.favouritesIconAll.setAlpha(1f);
        }

        mViews.setViewModel(this);
    }

    public void handleButtonClick() {
        // Clicking is ignored if the view is currently in the 'busy' state, typically because
        // there is an animation still in progress.
        if(mBusy) {
            return;
        }

        // Get busy!
        mBusy = true;

        if(mMode == All) {
            animateToAll();
            mMode = Favourites;
        } else {
            animateToFavourites();
            mMode = All;
        }

        // Persist our selection.
        mCollectionProvider.setCollectionFilterMode(mMode);

        // Broadcast an event notifying of the change in the favourites mode.
        mEventBusProvider.postEvent(new CollectionFilterModeToggleEvent());
    }

    private void animateToAll() {
        mViews.favouritesIconAll.clearAnimation();
        mViews.favouritesIconFavourites.clearAnimation();

        mViews.favouritesIconAll
                .animate()
                .rotation(ANIMATION_ROTATION)
                .alpha(1f)
                .setDuration(ANIMATION_DURATION).start();

        mViews.favouritesIconFavourites
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

    private void animateToFavourites() {
        mViews.favouritesIconAll.clearAnimation();
        mViews.favouritesIconFavourites.clearAnimation();

        mViews.favouritesIconAll
                .animate()
                .rotation(-ANIMATION_ROTATION)
                .alpha(0f)
                .setDuration(ANIMATION_DURATION)
                .start();

        mViews.favouritesIconFavourites
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
