package io.github.marcelbraghetto.alexandria.features.home.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.marcelbraghetto.alexandria.R;
import io.github.marcelbraghetto.alexandria.features.home.logic.HomeController;
import io.github.marcelbraghetto.alexandria.framework.application.MainApp;
import io.github.marcelbraghetto.alexandria.framework.ui.BaseActivity;

public class HomeActivity extends BaseActivity {
    private static final int CONTENT_REPLACEMENT_DELAY = 150;

    @Bind(R.id.nav_view) NavigationView mNavigationView;

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

    @Override
    protected void onResume() {
        super.onResume();

        if(mController != null) {
            mController.screenResumed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mController != null) {
            mController.screenPaused();
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
        public void startActivityIntent(@NonNull Intent intent) {
            startActivity(intent);
        }

        @Override
        public void finishActivity() {
            finish();
        }
    };
}
