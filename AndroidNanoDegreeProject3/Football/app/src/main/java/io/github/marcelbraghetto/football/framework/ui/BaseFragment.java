package io.github.marcelbraghetto.football.framework.ui;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Marcel Braghetto on 4/12/15.
 *
 * Base fragment shared between all app fragments.
 */
public class BaseFragment extends Fragment {
    /**
     * Set the screen title for the hosting activity
     * of this fragment instance.
     * @param title to set.
     */
    protected void setTitle(@NonNull String title) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setTitle(title);
    }
}
