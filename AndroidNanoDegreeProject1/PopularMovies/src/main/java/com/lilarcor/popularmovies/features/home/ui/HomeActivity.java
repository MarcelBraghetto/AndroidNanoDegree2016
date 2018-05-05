package com.lilarcor.popularmovies.features.home.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.features.application.MainApp;
import com.lilarcor.popularmovies.features.home.logic.HomeController;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Marcel Braghetto on 11/07/15.
 *
 * Host activity for the initial screen of the app
 * which displays the movie tabs.
 */
public class HomeActivity extends AppCompatActivity {
    @Bind(R.id.home_toolbar) Toolbar mToolbar;
    @Bind(R.id.home_tab_layout) TabLayout mTabLayout;
    @Bind(R.id.home_view_pager) ViewPager mViewPager;

    @Inject HomeController mController;

    private HomeActivityViewPagerAdapter mViewPagerAdapter;

    private void initUI() {
        setContentView(R.layout.home_activity);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
        MainApp.getDagger().inject(this);
        mController.initController(mControllerDelegate);
    }

    private HomeController.ControllerDelegate mControllerDelegate = new HomeController.ControllerDelegate() {
        @Override
        public void initialiseViewPager(@NonNull Fragment[] fragments, @NonNull String[] fragmentTitles) {
            mViewPager.setOffscreenPageLimit(fragments.length);
            mViewPagerAdapter = new HomeActivityViewPagerAdapter(getSupportFragmentManager(), fragments, fragmentTitles);
            mViewPager.setAdapter(mViewPagerAdapter);
            mTabLayout.setupWithViewPager(mViewPager);
        }
    };
}
