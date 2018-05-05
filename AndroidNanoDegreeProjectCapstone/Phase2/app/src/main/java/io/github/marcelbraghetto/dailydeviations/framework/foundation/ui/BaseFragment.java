package io.github.marcelbraghetto.dailydeviations.framework.foundation.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import io.github.marcelbraghetto.dailydeviations.R;

/**
 * Created by Marcel Braghetto on 4/12/15.
 *
 * Base fragment shared between all app fragments.
 */
public class BaseFragment extends Fragment {
    private Snackbar mSnackbar;

    /**
     * Set the screen title for the hosting activity
     * of this fragment instance.
     * @param title to set.
     */
    protected void setTitle(@NonNull String title) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if(actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    protected void showSnackbar(@NonNull final String message, @NonNull final View view) {
        view.post(new Runnable() {
            @Override
            public void run() {
                cancelSnackbar();
                mSnackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
                mSnackbar.getView().setBackgroundColor(getResources().getColor(R.color.primary));
                mSnackbar.show();
            }
        });
    }

    protected void cancelSnackbar() {
        if(mSnackbar != null) {
            mSnackbar.dismiss();
            mSnackbar = null;
        }
    }

    @Override
    public void onDestroyView() {
        cancelSnackbar();
        super.onDestroyView();
    }
}
