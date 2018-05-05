package io.github.marcelbraghetto.dailydeviations.features.about.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import io.github.marcelbraghetto.dailydeviations.databinding.AboutFragmentBinding;
import io.github.marcelbraghetto.dailydeviations.features.about.logic.AboutViewModel;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.ui.BaseFragment;

/**
 * Created by Marcel Braghetto on 5/06/16.
 *
 * About fragment.
 */
public class AboutFragment extends BaseFragment {
    private AboutFragmentBinding mViews;
    @Inject AboutViewModel mViewModel;

    @NonNull
    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    public AboutFragment() {
        MainApp.getDagger().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViews.setViewModel(mViewModel);
        mViewModel.begin();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViews = AboutFragmentBinding.inflate(inflater, container, false);
        return mViews.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewModel.screenStarted();
    }
}
