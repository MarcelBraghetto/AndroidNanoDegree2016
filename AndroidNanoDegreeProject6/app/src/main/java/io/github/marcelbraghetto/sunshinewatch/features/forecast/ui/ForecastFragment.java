package io.github.marcelbraghetto.sunshinewatch.features.forecast.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.marcelbraghetto.sunshinewatch.R;
import io.github.marcelbraghetto.sunshinewatch.features.application.MainApp;
import io.github.marcelbraghetto.sunshinewatch.features.forecast.logic.ForecastPresenter;

public class ForecastFragment extends Fragment {
    @Bind(R.id.recyclerview_forecast) RecyclerView mRecyclerView;
    @Bind(R.id.recyclerview_forecast_empty) View mEmptyView;

    @Inject ForecastPresenter mPresenter;
    private ForecastAdapter mAdapter;

    @NonNull
    public static ForecastFragment newInstance() {
        return new ForecastFragment();
    }

    private void initUI(View view) {
        ButterKnife.bind(this, view);
        mAdapter = new ForecastAdapter(getActivity(), mEmptyView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.forecast_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);
        MainApp.getInjector().inject(this);
        mPresenter.connect(mDelegate);
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

        if(mPresenter != null) {
            mPresenter.disconnect();
            mPresenter = null;
        }
    }

    private final ForecastPresenter.Delegate mDelegate = new ForecastPresenter.Delegate() {
        @Override
        public void setDataSource(@NonNull final Uri dataSourceUri) {
            getLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    return new CursorLoader(getActivity(), dataSourceUri, null, null, null, null);
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    mAdapter.swapCursor(data);
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                    mAdapter.swapCursor(null);
                }
            });
        }
    };
}