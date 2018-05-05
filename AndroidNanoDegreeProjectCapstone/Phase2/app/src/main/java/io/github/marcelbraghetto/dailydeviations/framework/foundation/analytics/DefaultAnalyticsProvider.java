package io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;

import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.Analytics;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.AnalyticsProvider;

/**
 * Created by Marcel Braghetto on 6/06/16.
 *
 * Default implementation of analytics using Google Firebase.
 */
public class DefaultAnalyticsProvider implements AnalyticsProvider {
    private final FirebaseAnalytics mFirebaseAnalytics;

    public DefaultAnalyticsProvider(@NonNull Context applicationContext) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(applicationContext);
    }

    @Override
    public void trackEvent(@NonNull String contentType, @NonNull String scope, @NonNull String itemId) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, scope + "_" + itemId);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public void trackScreenView(@NonNull String screenName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, screenName);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, Analytics.CONTENT_TYPE_SCREEN_VIEW);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }
}
