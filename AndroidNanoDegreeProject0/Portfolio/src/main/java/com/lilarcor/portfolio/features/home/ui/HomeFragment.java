package com.lilarcor.portfolio.features.home.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lilarcor.portfolio.R;
import com.lilarcor.portfolio.features.home.logic.HomeLogic;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Marcel Braghetto on 11/07/15.
 *
 * Home screen of the app.
 */
public class HomeFragment extends Fragment {
    @OnClick(R.id.home_spotify_streamer_button)
    void spotifyStreamerButtonClicked() {
        mLogic.spotifyStreamerSelected();
    }

    @OnClick(R.id.home_scores_app_button)
    void scoresAppButtonClicked() {
        mLogic.scoresAppSelected();
    }

    @OnClick(R.id.home_library_app_button)
    void libraryAppButtonClicked() {
        mLogic.libraryAppSelected();
    }

    @OnClick(R.id.home_build_it_bigger_button)
    void buildItBiggerButtonClicked() {
        mLogic.buildItBiggerSelected();
    }

    @OnClick(R.id.home_xyz_reader_button)
    void xyzReaderButtonClicked() {
        mLogic.xyzReaderSelected();
    }

    @OnClick(R.id.home_capstone_button)
    void capstoneButtonClicked() {
        mLogic.capstoneSelected();
    }

    private Toast mToast;
    private HomeLogic mLogic;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        mLogic = new HomeLogic(mLogicDelegate);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
        removeToast();
    }

    private void removeToast() {
        if(mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }

    //region Logic delegate implementation
    private HomeLogic.UIDelegate mLogicDelegate = new HomeLogic.UIDelegate() {
        @Override
        public void showToast(@NonNull String message) {
            removeToast();
            mToast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
            mToast.show();
        }
    };
    //endregion
}
