package io.github.marcelbraghetto.deviantartreader.features.collection.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.marcelbraghetto.deviantartreader.R;
import io.github.marcelbraghetto.deviantartreader.features.collection.logic.CollectionArguments;
import io.github.marcelbraghetto.deviantartreader.features.collection.logic.CollectionMode;
import io.github.marcelbraghetto.deviantartreader.features.collection.logic.CollectionPresenter;
import io.github.marcelbraghetto.deviantartreader.features.application.MainApp;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.ui.BaseFragment;

/**
 * Created by Marcel Braghetto on 24/02/16.
 *
 * Fragment representing the 'collection' of artworks rendered in a grid formation.
 */
public class CollectionFragment extends BaseFragment {
    @Bind(R.id.collection_recycler_view) RecyclerView mRecyclerView;
    @Bind(R.id.collection_swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.collection_error_container) View mErrorMessageContainer;
    @Bind(R.id.collection_error_message) TextView mErrorMessageView;

    private Snackbar mSnackbar;
    private StaggeredGridLayoutManager mLayoutManager;

    @Inject CollectionPresenter mPresenter;
    private CollectionAdapter mAdapter;
    private boolean mSwipeToRefreshDisabled;
    private int mNumColumns;

    @NonNull
    public static CollectionFragment newInstance(@NonNull CollectionArguments extras) {
        CollectionFragment fragment = new CollectionFragment();
        fragment.setArguments(extras.toBundle());
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApp.getDagger().inject(this);
    }

    private void initUI(View view) {
        ButterKnife.bind(this, view);
        mErrorMessageContainer.setVisibility(View.GONE);
        mNumColumns = getResources().getInteger(R.integer.collection_num_columns);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.primary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.swipeToRefreshInitiated();
            }
        });

        // There is an annoying default behaviour for a swipe layout where it intercepts
        // scroll events over the recycler view even if the recycler view is not at its own
        // top edge. This causes the swipe to start to happen incorrectly. While not perfect,
        // this scroll listener attempts to avoid this problem.
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // If we aren't meant to swipe in any scenario, then just bail (for example if
                // the user is on the favourites view).
                if(mSwipeToRefreshDisabled) {
                    return;
                }

                // If the layout manager is healthy, check if the first completely visible item is
                // at position 0.
                if(mLayoutManager != null) {
                    int[] into = new int[mNumColumns];
                    int firstVisibleItem = mLayoutManager.findFirstCompletelyVisibleItemPositions(into)[0];
                    mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0);
                } else {
                    mSwipeRefreshLayout.setEnabled(true);
                }
            }
        });

        mAdapter = new CollectionAdapter(getActivity(), mAdapterDelegate);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        mPresenter.init(getArguments(), mDelegate);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mPresenter != null) {
            mPresenter.screenStarted();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mPresenter != null) {
            mPresenter.screenStopped();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelSnackbar();
        mRecyclerView.setAdapter(null);
        mAdapter.swapCursor(null);
        ButterKnife.unbind(this);
        mLayoutManager = null;
    }

    private void cancelSnackbar() {
        if(mSnackbar != null) {
            mSnackbar.dismiss();
            mSnackbar = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mPresenter != null) {
            mPresenter.disconnect();
            mPresenter = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.collection_fragment, container, false);
    }

    private final CollectionPresenter.Delegate mDelegate = new CollectionPresenter.Delegate() {
        @Override
        public void setDataSource(@NonNull final Uri dataSourceUri) {
            mLayoutManager = new StaggeredGridLayoutManager(mNumColumns, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLayoutManager);
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
        public void setCollectionMode(@NonNull CollectionMode collectionMode) {
            // Apply a fade in effect for the whole view and tell the adapter to
            // changes its display mode.
            mSwipeRefreshLayout.clearAnimation();
            mSwipeRefreshLayout.setAlpha(0);
            mSwipeRefreshLayout.animate().alpha(1f).setStartDelay(300).setDuration(600).start();
            mAdapter.setCollectionMode(collectionMode);
        }

        @Override
        public void startActivity(@NonNull Intent intent) {
            getActivity().startActivity(intent);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void startActivityWithSceneTransition(@NonNull Intent intent, @NonNull Bundle sceneTransitionBundle) {
            getActivity().startActivity(intent, sceneTransitionBundle);
        }

        @Override
        public void hideLoadingIndicator() {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void showInfoMessage(@NonNull String message) {
            cancelSnackbar();
            mSnackbar = Snackbar.make(mSwipeRefreshLayout, message, Snackbar.LENGTH_SHORT);
            mSnackbar.getView().setBackgroundColor(getResources().getColor(R.color.primary));
            mSnackbar.show();
        }

        @Override
        public void disableSwipeToRefresh() {
            mSwipeToRefreshDisabled = true;
            mSwipeRefreshLayout.setEnabled(false);
        }

        @Override
        public void hideEmptyCollectionMessage() {
            mErrorMessageContainer.setVisibility(View.GONE);
        }

        @Override
        public void showEmptyCollectionMessage(@NonNull String message) {
            mErrorMessageView.setText(message);
            mErrorMessageContainer.setVisibility(View.VISIBLE);
        }
    };

    private final CollectionAdapter.GridItemDelegate mAdapterDelegate = new CollectionAdapter.GridItemDelegate() {
        @Override
        public void onArtworkSelected(@NonNull Artwork artwork, @Nullable Bundle sceneTransitionBundle) {
            mPresenter.artworkSelected(artwork, sceneTransitionBundle);
        }

        @Override
        public void onToggleFavourite(@NonNull Artwork artwork, boolean isFavourite) {
            mPresenter.favouriteButtonSelected(artwork, isFavourite);
        }

        @Override
        public void onCollectionChanged(int numItems) {
            mPresenter.collectionDataSourceChanged(numItems);
        }
    };
}
