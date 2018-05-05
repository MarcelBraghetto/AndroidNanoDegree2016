package io.github.marcelbraghetto.dailydeviations.framework.foundation.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by Marcel Braghetto on 4/06/16.
 *
 * Basic empty implementation of the activity life cycle callbacks to allow optional
 * method overriding.
 */
public abstract class BasicActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) { }
    @Override public void onActivityStarted(Activity activity) { }
    @Override public void onActivityResumed(Activity activity) { }
    @Override public void onActivityPaused(Activity activity) { }
    @Override public void onActivityStopped(Activity activity) { }
    @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) { }
    @Override public void onActivityDestroyed(Activity activity) { }
}
