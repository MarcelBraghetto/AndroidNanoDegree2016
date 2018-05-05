package io.github.marcelbraghetto.dailydeviations.framework.foundation.utils;

import android.animation.Animator;

/**
 * Created by Marcel Braghetto on 4/06/16.
 *
 * Basic implementation of animation listener to allow for selective method implementation.
 */
public abstract class BasicAnimationListener implements Animator.AnimatorListener {
    @Override public void onAnimationStart(Animator animation) { }
    @Override public void onAnimationEnd(Animator animation) { }
    @Override public void onAnimationCancel(Animator animation) { }
    @Override public void onAnimationRepeat(Animator animation) { }
}
