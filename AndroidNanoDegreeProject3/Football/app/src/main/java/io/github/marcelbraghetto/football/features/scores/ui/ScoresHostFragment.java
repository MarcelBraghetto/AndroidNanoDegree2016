package io.github.marcelbraghetto.football.features.scores.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.marcelbraghetto.football.R;
import io.github.marcelbraghetto.football.features.scores.logic.ScoresHostController;
import io.github.marcelbraghetto.football.framework.application.MainApp;
import io.github.marcelbraghetto.football.framework.ui.BaseFragment;

/**
 * Created by Marcel Braghetto on 5/12/15.
 *
 * Host fragment for the football scores view pager system.
 */
public class ScoresHostFragment extends BaseFragment {
    @Bind(R.id.scores_host_tab_layout) TabLayout mTabLayout;
    @Bind(R.id.scores_host_view_pager) ViewPager mViewPager;

    @Inject ScoresHostController mController;

    public static ScoresHostFragment newInstance() {
        return new ScoresHostFragment();
    }

    private void initUI(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApp.getDagger().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        mController.initController(mControllerDelegate);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mController != null) {
            mController.disconnect();
            mController = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.scores_host_fragment, container, false);
    }

    private final ScoresHostController.Delegate mControllerDelegate = new ScoresHostController.Delegate() {
        @Override
        public void populateViewPager(@NonNull Fragment[] fragments, @NonNull String[] titles, int startingPosition) {
            mViewPager.setOffscreenPageLimit(fragments.length);
            mViewPager.setAdapter(new ScoresViewPagerAdapter(getChildFragmentManager(), fragments, titles));
            mTabLayout.setupWithViewPager(mViewPager);
            mViewPager.setCurrentItem(startingPosition);
        }
    };
}
