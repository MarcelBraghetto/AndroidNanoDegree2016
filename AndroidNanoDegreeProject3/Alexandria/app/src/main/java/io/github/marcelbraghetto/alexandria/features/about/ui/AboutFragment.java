package io.github.marcelbraghetto.alexandria.features.about.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.marcelbraghetto.alexandria.R;
import io.github.marcelbraghetto.alexandria.features.about.logic.AboutController;
import io.github.marcelbraghetto.alexandria.framework.application.MainApp;
import io.github.marcelbraghetto.alexandria.framework.ui.BaseFragment;

/**
 * Created by Marcel Braghetto on 4/12/15.
 *
 * Simple about content for the app.
 */
public class AboutFragment extends BaseFragment {
    @Bind(R.id.about_body_text) TextView mBodyTextView;

    @Inject AboutController mController;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApp.getDagger().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mController.initController(mControllerDelegate);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.about_fragment, container, false);
    }

    private final AboutController.Delegate mControllerDelegate = new AboutController.Delegate() {
        @Override
        public void setScreenTitle(@NonNull String title) {
            setTitle(title);
        }

        @Override
        public void setBodyText(@NonNull String text) {
            mBodyTextView.setText(text);
        }
    };
}
