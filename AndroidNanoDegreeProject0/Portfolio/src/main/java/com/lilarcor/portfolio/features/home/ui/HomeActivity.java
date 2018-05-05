package com.lilarcor.portfolio.features.home.ui;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.lilarcor.portfolio.framework.ui.BasicFragmentActivity;

/**
 * Created by Marcel Braghetto on 11/07/15.
 */
public class HomeActivity extends BasicFragmentActivity {
    @NonNull
    @Override
    protected Fragment getContentFragment() {
        return HomeFragment.newInstance();
    }
}
