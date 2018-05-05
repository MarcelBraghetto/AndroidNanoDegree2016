package io.github.marcelbraghetto.football.features.home.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.marcelbraghetto.football.R;
import io.github.marcelbraghetto.football.features.home.logic.HomeController;
import io.github.marcelbraghetto.football.framework.application.MainApp;
import io.github.marcelbraghetto.football.framework.ui.BaseActivity;

public class HomeActivity extends BaseActivity {
    private static final int CONTENT_REPLACEMENT_DELAY = 150;

    @Bind(R.id.home_root_layout) View mRootView;
    @Bind(R.id.nav_view) NavigationView mNavigationView;
    @Bind(R.id.swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;

    private Snackbar mSnackbar;

    @Inject HomeController mController;

    public HomeActivity() {
        MainApp.getDagger().inject(this);
    }

    private void initUI() {
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.home_nav_drawer_open, R.string.home_nav_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(mNavigationListener);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mController != null) {
                    mController.swipeToRefreshInitiated();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mController != null) {
            mController.screenStarted();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        initUI();
        mController.initController(mControllerDelegate, savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(mController != null) {
                mController.backPressed();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mController != null) {
            mController.disconnect();
            mController = null;
        }
    }

    private final NavigationView.OnNavigationItemSelectedListener mNavigationListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            if(mController != null) {
                mController.menuItemSelected(item.getItemId());
            }
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
                            .replace(R.id.main_content_container, fragment)
                            .commit();
                }
            }, CONTENT_REPLACEMENT_DELAY);
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_content_container, fragment)
                    .commit();
        }
    }

    private void cancelSnackbar() {
        if(mSnackbar != null) {
            mSnackbar.dismiss();
            mSnackbar = null;
        }
    }

    private final HomeController.Delegate mControllerDelegate = new HomeController.Delegate() {
        @Override
        public void replaceContent(@NonNull Fragment fragment, boolean animated) {
            replaceContentFragment(fragment, animated);
        }

        @Override
        public void highlightMenuItem(@IdRes int itemId) {
            mNavigationView.setCheckedItem(itemId);
        }

        @Override
        public void closeNavigationMenu() {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        @Override
        public void finishActivity() {
            finish();
        }

        @Override
        public void hideSwipeToRefreshIndicator() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }, 1000);
        }

        @Override
        public void showSnackbarMessage(@NonNull String message) {
            cancelSnackbar();
            mSnackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT);
            mSnackbar.show();
        }
    };
}
