package io.github.marcelbraghetto.dailydeviations.features.settings.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import io.github.marcelbraghetto.dailydeviations.databinding.SettingsFragmentBinding;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.features.settings.logic.SettingsViewModel;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.ui.BaseFragment;

/**
 * Created by Marcel Braghetto on 6/06/16.
 *
 * Settings screen to configure the app.
 */
public class SettingsFragment extends BaseFragment {

    private SettingsFragmentBinding mViews;
    @Inject SettingsViewModel mViewModel;

    @NonNull
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    public SettingsFragment() {
        MainApp.getDagger().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViews.setViewModel(mViewModel);
        mViewModel.begin(mActions);
    }

    @Override
    public void onDestroy() {
        mViewModel.destroy();
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewModel.screenStarted();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViews = SettingsFragmentBinding.inflate(inflater, container, false);
        return mViews.getRoot();
    }

    private final SettingsViewModel.Actions mActions = new SettingsViewModel.Actions() {
        @Override
        public void showSnackbar(@NonNull String message) {
            SettingsFragment.this.showSnackbar(message, mViews.getRoot());
        }
    };
}
