package io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts;

import android.support.annotation.NonNull;

/**
 * Created by Marcel Braghetto on 6/06/16.
 *
 * Track analytics events.
 */
public interface AnalyticsProvider {
    /**
     * Track that the given content type and item id within the given scope
     * was selected by the user.
     * @param contentType eg, ScreenView, ButtonSelected etc.
     * @param scope of the owner of the event - perhaps the scren it belongs to.
     * @param itemId of the thing that was interacted with.
     */
    void trackEvent(@NonNull String contentType, @NonNull String scope, @NonNull String itemId);

    /**
     * Track that the given screen was viewed by the user.
     * @param screenName of the screen that was viewed.
     */
    void trackScreenView(@NonNull String screenName);
}
