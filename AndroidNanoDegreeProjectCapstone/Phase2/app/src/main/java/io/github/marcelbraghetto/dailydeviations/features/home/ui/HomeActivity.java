package io.github.marcelbraghetto.dailydeviations.features.home.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import io.github.marcelbraghetto.dailydeviations.R;
import io.github.marcelbraghetto.dailydeviations.databinding.HomeActivityBinding;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.features.home.logic.HomeViewModel;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.ui.BaseActivity;

public class HomeActivity extends BaseActivity {
    private static final int CONTENT_REPLACEMENT_DELAY = 150;

    private HomeActivityBinding mViews;
    @Inject HomeViewModel mViewModel;

    private boolean mShowToggleButtons;

    public HomeActivity() {
        MainApp.getDagger().inject(this);
    }

    private void initUI() {
        mViews = DataBindingUtil.setContentView(this, R.layout.home_activity);
        setSupportActionBar(mViews.homeToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mViews.drawerLayout, mViews.homeToolbar, R.string.nav_menu_drawer_open, R.string.nav_menu_drawer_close);
        mViews.drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        mViews.navView.setNavigationItemSelectedListener(mNavigationListener);
        mViews.setViewModel(mViewModel);

        // Just a smudge of introductory animation to spice things up a little.
        mViews.homeToolbar.setAlpha(0);
        mViews.homeToolbar.animate().setStartDelay(400).setDuration(500).alpha(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        mViewModel.begin(mActions);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);

        menu.findItem(R.id.action_favourites).setVisible(mShowToggleButtons);
        menu.findItem(R.id.action_toggle).setVisible(mShowToggleButtons);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.screenStarted();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewModel.screenStopped();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.destroy();
    }

    @Override
    public void onBackPressed() {
        if (mViews.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mViews.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mViewModel.backPressed();
        }
    }

    private final NavigationView.OnNavigationItemSelectedListener mNavigationListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            mViewModel.menuItemSelected(item.getItemId());
            return true;
        }
    };

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

    private final HomeViewModel.Actions mActions = new HomeViewModel.Actions() {
        @Override
        public void closeNavigationMenu() {
            mViews.drawerLayout.closeDrawer(GravityCompat.START);
        }

        @Override
        public void replaceContent(@NonNull Fragment fragment, boolean animated) {
            replaceContentFragment(fragment, animated);
        }

        @Override
        public void startActivity(@NonNull Intent intent) {
            HomeActivity.this.startActivity(intent);
        }

        @Override
        public void startActivityForResult(@NonNull Intent intent, int requestCode) {
            HomeActivity.this.startActivityForResult(intent, requestCode);
        }

        @Override
        public void finishActivity() {
            finish();
        }

        @Override
        public void showToggleButtons() {
            mShowToggleButtons = true;
            supportInvalidateOptionsMenu();
        }

        @Override
        public void hideToggleButtons() {
            mShowToggleButtons = false;
            supportInvalidateOptionsMenu();
        }

        @Override
        public void showSnackbar(@NonNull String message) {
            HomeActivity.this.showSnackbar(message, mViews.getRoot());
        }
    };
}
