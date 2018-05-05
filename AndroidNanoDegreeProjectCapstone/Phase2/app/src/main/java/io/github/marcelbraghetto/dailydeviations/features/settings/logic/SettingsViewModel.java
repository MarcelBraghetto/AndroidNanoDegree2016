package io.github.marcelbraghetto.dailydeviations.features.settings.logic;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.github.marcelbraghetto.dailydeviations.R;
import io.github.marcelbraghetto.dailydeviations.features.settings.logic.providers.contracts.SettingsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.analytics.contracts.AnalyticsProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.core.BaseViewModel;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.strings.contracts.StringsProvider;

/**
 * Created by Marcel Braghetto on 6/06/16.
 *
 * Logic to control the settings screen.
 */
public class SettingsViewModel extends BaseViewModel<SettingsViewModel.Actions> {

    //region Data binding
    public final Glue glue = new Glue();
    public static class Glue {
        public ObservableField<Boolean> backgroundDataEnabled = new ObservableField<>(true);
        public ObservableField<Boolean> notificationsEnabled = new ObservableField<>(true);
        public ObservableField<Boolean> automaticHeaderImageEnabled = new ObservableField<>(true);
    }
    //endregion

    //region Private fields
    private static final String SCREEN_NAME = "SettingsScreen";

    private final SettingsProvider mSettingsProvider;
    private final StringsProvider mStringsProvider;
    private final AnalyticsProvider mAnalyticsProvider;
    //endregion

    //region Public methods
    @Inject
    public SettingsViewModel(@NonNull SettingsProvider settingsProvider,
                             @NonNull StringsProvider stringsProvider,
                             @NonNull AnalyticsProvider analyticsProvider) {
        super(Actions.class);

        mSettingsProvider = settingsProvider;
        mStringsProvider = stringsProvider;
        mAnalyticsProvider = analyticsProvider;
    }

    /**
     * Begin the view model with the given actions delegate.
     * @param actionDelegate to send action commands to.
     */
    public void begin(@Nullable Actions actionDelegate) {
        setActionDelegate(actionDelegate);

        glue.backgroundDataEnabled.set(mSettingsProvider.isBackgroundDataEnabled());
        glue.notificationsEnabled.set(mSettingsProvider.isNotificationsEnabled());
        glue.automaticHeaderImageEnabled.set(mSettingsProvider.isAutomaticHeaderImageEnabled());
    }

    public void screenStarted() {
        mAnalyticsProvider.trackScreenView(SCREEN_NAME);
    }

    /**
     * User toggled the background data option.
     */
    public void toggleBackgroundData() {
        if(mSettingsProvider.isBackgroundDataEnabled()) {
            mSettingsProvider.disableBackgroundDate();
            glue.backgroundDataEnabled.set(false);
            mActionDelegate.showSnackbar(mStringsProvider.getString(R.string.settings_option_background_data_disabled));
        } else {
            mSettingsProvider.enableBackgroundData();
            glue.backgroundDataEnabled.set(true);
            mActionDelegate.showSnackbar(mStringsProvider.getString(R.string.settings_option_background_data_enabled));
        }
    }

    /**
     * User toggled the notifications option.
     */
    public void toggleNotifications() {
        if(mSettingsProvider.isNotificationsEnabled()) {
            mSettingsProvider.disableNotifications();
            glue.notificationsEnabled.set(false);
            mActionDelegate.showSnackbar(mStringsProvider.getString(R.string.settings_option_notifications_disabled));
        } else {
            mSettingsProvider.enableNotifications();
            glue.notificationsEnabled.set(true);
            mActionDelegate.showSnackbar(mStringsProvider.getString(R.string.settings_option_notifications_enabled));
        }
    }

    /**
     * User toggled the automatic header image option.
     */
    public void toggleAutomaticHeaderImage() {
        if(mSettingsProvider.isAutomaticHeaderImageEnabled()) {
            mSettingsProvider.disableAutomaticHeaderImage();
            glue.automaticHeaderImageEnabled.set(false);
            mActionDelegate.showSnackbar(mStringsProvider.getString(R.string.settings_option_header_image_disabled));
        } else {
            mSettingsProvider.enableAutomaticHeaderImage();
            glue.automaticHeaderImageEnabled.set(true);
            mActionDelegate.showSnackbar(mStringsProvider.getString(R.string.settings_option_header_image_enabled));
        }
    }
    //endregion

    //region Actions delegate contract
    public interface Actions {
        /**
         * Show the user a snackbar message.
         * @param message to display.
         */
        void showSnackbar(@NonNull String message);
    }
    //endregion
}
