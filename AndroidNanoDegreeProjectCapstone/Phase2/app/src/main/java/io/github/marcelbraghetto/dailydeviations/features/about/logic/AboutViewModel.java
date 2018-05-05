package io.github.marcelbraghetto.dailydeviations.features.about.logic;

import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.github.marcelbraghetto.dailydeviations.R;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.AnalyticsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.strings.contracts.StringsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.utils.AssetUtils;

/**
 * Created by Marcel Braghetto on 5/06/16.
 *
 * View model for the 'about' feature.
 */
public class AboutViewModel {

    //region Data binding
    public final Glue glue = new Glue();
    public static class Glue {
        public final ObservableField<String> navigationTitle = new ObservableField<>("");
        public final ObservableField<String> htmlPath = new ObservableField<>(AssetUtils.getLocalAssetPath("about.html"));
    }
    //endregion

    //region Private fields
    private static final String SCREEN_NAME = "AboutScreen";

    private final Context mApplicationContext;
    private final StringsProvider mStringsProvider;
    private final AnalyticsProvider mAnalyticsProvider;
    //endregion

    //region Public methods
    @Inject
    public AboutViewModel(@NonNull Context applicationContext,
                          @NonNull StringsProvider stringsProvider,
                          @NonNull AnalyticsProvider analyticsProvider) {

        mStringsProvider = stringsProvider;
        mApplicationContext = applicationContext;
        mAnalyticsProvider = analyticsProvider;
    }

    public void begin() {
        glue.navigationTitle.set(mStringsProvider.getString(R.string.about_navigation_title));
    }

    public void screenStarted() {
        mAnalyticsProvider.trackScreenView(SCREEN_NAME);
    }
    //endregion
}
