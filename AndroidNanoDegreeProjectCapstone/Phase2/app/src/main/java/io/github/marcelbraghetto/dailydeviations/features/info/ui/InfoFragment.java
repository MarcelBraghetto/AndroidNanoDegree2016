package io.github.marcelbraghetto.dailydeviations.features.info.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import io.github.marcelbraghetto.dailydeviations.databinding.InfoFragmentBinding;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.features.info.logic.InfoViewModel;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.ui.BaseFragment;

/**
 * Created by Marcel Braghetto on 14/03/16.
 *
 * Artwork info fragment display to render the extended details of a given artwork.
 */
public class InfoFragment extends BaseFragment {
    private InfoFragmentBinding mViews;
    @Inject InfoViewModel mViewModel;

    @NonNull
    public static InfoFragment newInstance(@NonNull Bundle bundle) {
        InfoFragment fragment = new InfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public InfoFragment() {
        MainApp.getDagger().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViews.infoRootview.setAlpha(0f);

        // Due to a glitch with WebView flashing white with hardware acceleration turned on
        // this seems to correct the issue.
        mViews.infoDescriptionWebview.setBackgroundColor(Color.argb(1, 0, 0, 0));

        mViews.setViewModel(mViewModel);
        mViewModel.begin(getArguments(), mActions);
    }

    @Override
    public void onDestroy() {
        mViewModel.destroy();
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViews = InfoFragmentBinding.inflate(inflater, container, false);
        return mViews.getRoot();
    }

    private void populateWebView(String content) {
        mViews.infoDescriptionWebview.loadData(content, null, "UTF-8");

        // We want to wait until the web view has finished rendering itself with the new content before
        // displaying the content to avoid a jarring size change on the screen.
        mViews.infoDescriptionWebview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mViews.infoDescriptionWebview.removeOnLayoutChangeListener(this);

                // Once our web view has changed its layout we can animate in the whole view.
                if(mViews.infoRootview != null) {
                    mViews.infoRootview.animate().alpha(1).setStartDelay(250).setDuration(400).start();
                }
            }
        });
    }

    private final InfoViewModel.Actions mActions = new InfoViewModel.Actions() {
        @Override
        public void finishActivity() {
            getActivity().finish();
        }

        @Override
        public void populateWebView(@NonNull String htmlText) {
            InfoFragment.this.populateWebView(htmlText);
        }
    };
}
