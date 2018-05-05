package io.github.marcelbraghetto.football.features.scores.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.marcelbraghetto.football.R;
import io.github.marcelbraghetto.football.features.scores.logic.ScoresController;
import io.github.marcelbraghetto.football.framework.application.MainApp;
import io.github.marcelbraghetto.football.framework.ui.BaseFragment;

/**
 * Created by Marcel Braghetto on 5/12/15.
 *
 * Fragment representing a list of football scores.
 */
public class ScoresFragment extends BaseFragment {
    private static final String EXTRA_YEAR = "SFY";
    private static final String EXTRA_MONTH = "SFM";
    private static final String EXTRA_DAY = "SFD";

    @Bind(R.id.scores_recycler_view) RecyclerView mRecyclerView;
    @Bind(R.id.scores_no_results_container) View mNoResultsView;
    @Bind(R.id.scores_fader_view) View mFaderView;

    @Inject ScoresController mController;

    private ScoresAdapter mAdapter;

    public static ScoresFragment newInstance(int year, int month, int day) {
        ScoresFragment fragment = new ScoresFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_YEAR, year);
        bundle.putInt(EXTRA_MONTH, month);
        bundle.putInt(EXTRA_DAY, day);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApp.getDagger().inject(this);
    }

    private void initUI(View view) {
        ButterKnife.bind(this, view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new ScoresAdapter(new ScoresAdapter.Delegate() {
            @Override
            public void shareSelected(@NonNull String text) {
                if(mController != null) {
                    mController.shareSelected(text);
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);

        int year = getArguments().getInt(EXTRA_YEAR);
        int month = getArguments().getInt(EXTRA_MONTH);
        int day = getArguments().getInt(EXTRA_DAY);

        mController.initController(mDelegate, year, month, day);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView.setAdapter(null);
        mAdapter.swapCursor(null);
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mController != null) {
            mController.disconnect();
            mController = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.scores_fragment, container, false);
    }

    private final ScoresController.Delegate mDelegate = new ScoresController.Delegate() {
        @Override
        public void setDataSource(@NonNull final Uri dataSourceUri) {
            getLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    return new CursorLoader(getActivity(), dataSourceUri, null, null, null, null);
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    mAdapter.swapCursor(data);
                    if(mController != null) {
                        mController.loaderManagerFinished(data);
                    }
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    mAdapter.swapCursor(null);
                }
            });
        }

        @Override
        public void showNoResultsMessage() {
            mNoResultsView.setVisibility(View.VISIBLE);
        }

        @Override
        public void hideNoResultsMessage() {
            mNoResultsView.setVisibility(View.GONE);
        }

        @Override
        public void hideFaderView() {
            if(mFaderView.getVisibility() == View.GONE) {
                return;
            }

            AnimationSet animation = new AnimationSet(true);
            animation.addAnimation(new AlphaAnimation(1f, 0f));
            animation.setDuration(300);
            animation.setFillAfter(true);
            animation.setFillEnabled(true);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mFaderView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mFaderView.startAnimation(animation);
        }

        @Override
        public void startActivityIntent(@NonNull Intent intent) {
            getActivity().startActivity(intent);
        }
    };
}
