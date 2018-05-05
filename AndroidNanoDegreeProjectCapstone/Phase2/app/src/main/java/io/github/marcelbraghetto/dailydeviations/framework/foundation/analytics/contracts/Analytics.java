package io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts;

/**
 * Created by Marcel Braghetto on 8/06/16.
 *
 * Categories that can be used to capture analytics events.
 */
public final class Analytics {
    private Analytics() { }

    public static final String CONTENT_TYPE_SCREEN_VIEW = "ScreenView";
    public static final String CONTENT_TYPE_BUTTON_CLICK = "ButtonClick";
    public static final String CONTENT_TYPE_ARTWORK_OPENED = "ArtworkOpened";
    public static final String CONTENT_TYPE_MENU_ITEM_SELECTED = "MenuItemSelected";
}
