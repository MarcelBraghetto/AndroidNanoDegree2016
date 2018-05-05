package com.lilarcor.portfolio.framework.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.lilarcor.portfolio.R;

/**
 * Created by Marcel Braghetto on 11/07/15.
 *
 * Reusable host activity to present a single fragment.
 *
 */
public abstract class BasicFragmentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configureActionBar();

        setContentView(R.layout.basic_fragment_activity);

        if(savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.basic_fragment_activity_content_container, getContentFragment())
                    .commit();
        }
    }

    /**
     * Set some properties on the action bar.
     */
    private void configureActionBar() {
        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null) {
            actionBar.setElevation(0);
        }
    }

    /**
     * Implementations of this class must return a fragment to
     * populate into the screen.
     *
     * @return content fragment to display.
     */
    @NonNull
    protected abstract Fragment getContentFragment();
}
