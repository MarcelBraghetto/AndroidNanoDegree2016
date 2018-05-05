package io.github.marcelbraghetto.dailydeviations.framework.foundation.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import io.github.marcelbraghetto.dailydeviations.R;

/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * Base activity class for the app.
 */
public class BaseActivity extends AppCompatActivity {
    private Snackbar mSnackbar;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onHomeButtonSelected();
            supportFinishAfterTransition();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onHomeButtonSelected() {
        // Override to include custom behaviour
    }

    protected void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void setupFloatingWindow(int windowWidth, int windowHeight) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = windowWidth;
        params.height = windowHeight;
        params.alpha = 1;
        params.dimAmount = 0.6f;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        getWindow().setAttributes(params);
    }

    protected void cancelSnackbar() {
        if(mSnackbar != null) {
            mSnackbar.dismiss();
            mSnackbar = null;
        }
    }

    protected void showSnackbar(@NonNull final String message, @NonNull final View view) {
        view.post(new Runnable() {
            @Override
            public void run() {
                cancelSnackbar();
                mSnackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
                mSnackbar.getView().setBackgroundColor(getResources().getColor(R.color.primary));
                mSnackbar.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        cancelSnackbar();
        super.onDestroy();
    }
}
