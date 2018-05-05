package io.github.marcelbraghetto.deviantartreader.features.home.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.marcelbraghetto.deviantartreader.R;
import io.github.marcelbraghetto.deviantartreader.features.home.logic.HomePresenter;
import io.github.marcelbraghetto.deviantartreader.features.application.MainApp;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.ui.BaseActivity;

public class HomeActivity extends BaseActivity {
    private static final int CONTENT_REPLACEMENT_DELAY = 150;

    @Bind(R.id.home_toolbar) Toolbar mToolbar;
    @Bind(R.id.home_toolbar_title) HomeTitleView mTitleView;

    @Inject HomePresenter mPresenter;

    public HomeActivity() {
        MainApp.getDagger().inject(this);
    }

    private void initUI() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        // Just a smudge of introductory animation to spice things up a little.
        mToolbar.setNavigationIcon(R.drawable.icon_toolbar);
        mToolbar.setAlpha(0);
        mToolbar.animate().setStartDelay(400).setDuration(500).alpha(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        initUI();
        mPresenter.init(mPresenterDelegate, savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mPresenter != null) {
            mPresenter.screenStarted();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mPresenter != null) {
            mPresenter.screenStopped();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mPresenter != null) {
            mPresenter.disconnect();
            mPresenter = null;
        }
    }

    private void replaceContentFragment(final Fragment fragment, boolean animated) {
        if(animated) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fadein, R.anim.fadeout)
                            .replace(R.id.home_content_container, fragment)
                            .commit();
                }
            }, CONTENT_REPLACEMENT_DELAY);
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_content_container, fragment)
                    .commit();
        }
    }

    private final HomePresenter.Delegate mPresenterDelegate = new HomePresenter.Delegate() {
        @Override
        public void replaceContent(@NonNull Fragment fragment, boolean animated) {
            replaceContentFragment(fragment, animated);
        }

        @Override
        public void showDailyDeviationsTitle() {
            mTitleView.showDailyDeviationsTitle();
        }

        @Override
        public void showFavouritesTitle() {
            mTitleView.showFavouritesTitle();
        }
    };
}
