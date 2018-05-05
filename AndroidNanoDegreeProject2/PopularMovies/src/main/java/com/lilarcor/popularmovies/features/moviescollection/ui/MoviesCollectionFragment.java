package com.lilarcor.popularmovies.features.moviescollection.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.features.application.MainApp;
import com.lilarcor.popularmovies.features.moviescollection.logic.MoviesCollectionController;
import com.lilarcor.popularmovies.features.moviescollection.logic.models.MoviesCollectionFilter;
import com.lilarcor.popularmovies.framework.ui.ViewUtils;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Marcel Braghetto on 11/07/15.
 *
 * Home screen of the app.
 */
public class MoviesCollectionFragment extends Fragment {
    @Bind(R.id.movie_collection_recycler_view) RecyclerView mRecyclerView;
    @Bind(R.id.movie_collection_swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;

    private Snackbar mSnackbar;
    private MoviesCollectionGridAdapter mAdapter;

    @Inject
    MoviesCollectionController mController;

    /**
     * The movies collection fragment needs to know what 'filter' to apply when
     * being created.
     *
     * @param filter to apply to the fragment.
     *
     * @return new instance of the fragment.
     */
    public static MoviesCollectionFragment newInstance(@NonNull MoviesCollectionFilter filter) {
        Bundle arguments = new Bundle();
        MoviesCollectionFragment fragment = new MoviesCollectionFragment();

        arguments.putSerializable(MoviesCollectionController.ARG_MOVIES_FILTER, filter);
        fragment.setArguments(arguments);

        return fragment;
    }

    private void initUI(View view) {
        ButterKnife.bind(this, view);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mController.swipeToRefreshInitiated();
            }
        });

        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(mRecyclerViewLayoutListener);
    }

    private ViewTreeObserver.OnGlobalLayoutListener mRecyclerViewLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if(mRecyclerView.getWidth() > 0 && mRecyclerView.getHeight() > 0) {
                screenReady(mRecyclerView.getWidth());
                ViewUtils.removeOnGlobalLayoutListener(mRecyclerView, mRecyclerViewLayoutListener);
            }
        }
    };

    private void screenReady(int recyclerViewWidth) {
        mController.initController(mControllerDelegate, recyclerViewWidth, getArguments());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainApp.getDagger().inject(this);
        initUI(view);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.movies_collection_fragment, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        cancelSnackbar();
        mRecyclerView.setAdapter(null);
        mAdapter.swapCursor(null);
        ButterKnife.unbind(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        if(mController != null) {
            mController.screenStarted();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if(mController != null) {
            mController.screenStopped();
        }
    }

    private void cancelSnackbar() {
        if(mSnackbar != null) {
            mSnackbar.dismiss();
            mSnackbar = null;
        }
    }

    //region Grid item delegate implementation
    private MoviesCollectionGridAdapter.GridItemDelegate mGridItemDelegate = new MoviesCollectionGridAdapter.GridItemDelegate() {
        @Override
        public void movieIdSelected(int movieId) {
            mController.movieIdSelected(movieId);
        }

        @Override
        public void starSelected(int movieId) {
            mController.toggleFavouriteSelected(movieId);
        }

        @Override
        public void requestMoreData(int fromRequestPage) {
            mController.requestMoreData(fromRequestPage);
        }
    };
    //endregion

    //region Controller delegate implementation
    private MoviesCollectionController.ControllerDelegate mControllerDelegate = new MoviesCollectionController.ControllerDelegate() {
        @Override
        public void initScreen(@NonNull final Uri dataSourceUri, @NonNull MoviesCollectionFilter filter, int numColumns, int desiredMovieImageHeightPx) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numColumns));
            mAdapter = new MoviesCollectionGridAdapter(filter, desiredMovieImageHeightPx, mGridItemDelegate);
            mRecyclerView.setAdapter(mAdapter);

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

        @Override
        public void hideSwipeIndicator() {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void disableSwipeRefresh() {
            mSwipeRefreshLayout.setEnabled(false);
        }

        @Override
        public void startActivityIntent(@NonNull Intent intent) {
            startActivity(intent);
        }

        @Override
        public void showSnackbar(@NonNull String text) {
            cancelSnackbar();
            mSnackbar = Snackbar.make(mSwipeRefreshLayout, text, Snackbar.LENGTH_SHORT);
            mSnackbar.show();
        }
    };
    //endregion
}
