package io.github.marcelbraghetto.dailydeviations.features.collection.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import io.github.marcelbraghetto.dailydeviations.R;
import io.github.marcelbraghetto.dailydeviations.databinding.CollectionFragmentBinding;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.features.collection.logic.CollectionArguments;
import io.github.marcelbraghetto.dailydeviations.features.collection.logic.CollectionDisplayMode;
import io.github.marcelbraghetto.dailydeviations.features.collection.logic.CollectionViewModel;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.models.Artwork;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.ui.BaseFragment;

/**
 * Created by Marcel Braghetto on 24/02/16.
 *
 * Fragment representing the 'collection' of artworks rendered in a grid formation.
 */
public class CollectionFragment extends BaseFragment {
    private static final int LOADER_ID = 0;

    private CollectionFragmentBinding mViews;
    private CollectionAdapter mAdapter;
    @Inject CollectionViewModel mViewModel;

    @NonNull
    public static CollectionFragment newInstance(@NonNull CollectionArguments extras) {
        CollectionFragment fragment = new CollectionFragment();
        fragment.setArguments(extras.toBundle());
        return fragment;
    }

    public CollectionFragment() {
        MainApp.getDagger().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new CollectionAdapter(getActivity());
        mAdapter.setGridItemDelegate(mAdapterDelegate);
        mViews.setViewModel(mViewModel);
        mViewModel.begin(getArguments(), mDelegate);
    }

    @Override
    public void onStart() {
        super.onStart();
        mViewModel.screenStarted();
    }

    @Override
    public void onStop() {
        super.onStop();
        mViewModel.screenStopped();
    }

    @Override
    public void onDestroyView() {
        mViews.collectionRecyclerView.setAdapter(null);
        mAdapter.swapCursor(null);
        getLoaderManager().destroyLoader(LOADER_ID);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mViewModel.destroy();
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViews = CollectionFragmentBinding.inflate(inflater, container, false);
        return mViews.getRoot();
    }

    private final CollectionViewModel.Actions mDelegate = new CollectionViewModel.Actions() {
        @Override
        public void setDataSource(@NonNull final Uri dataSourceUri) {
            mViews.collectionRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(getResources().getInteger(R.integer.collection_num_columns), StaggeredGridLayoutManager.VERTICAL));
            mViews.collectionRecyclerView.setAdapter(mAdapter);

            getLoaderManager().initLoader(LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
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
        public void setCollectionMode(@NonNull CollectionDisplayMode collectionMode) {
            // Apply a fade in effect and tell the adapter to changes its display mode.
            mViews.collectionSwipeRefreshLayout.clearAnimation();
            mViews.collectionSwipeRefreshLayout.setAlpha(0);
            mViews.collectionSwipeRefreshLayout.animate().alpha(1f).setStartDelay(300).setDuration(600).start();
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
        public void showSnackbar(@NonNull String message) {
            CollectionFragment.this.showSnackbar(message, mViews.collectionSwipeRefreshLayout);
        }
    };

    private final CollectionAdapter.GridItemDelegate mAdapterDelegate = new CollectionAdapter.GridItemDelegate() {
        @Override
        public void onArtworkSelected(@NonNull Artwork artwork, @Nullable Bundle sceneTransitionBundle) {
            mViewModel.artworkSelected(artwork, sceneTransitionBundle);
        }

        @Override
        public void onToggleFavourite(@NonNull Artwork artwork, boolean isFavourite) {
            mViewModel.favouriteButtonSelected(artwork, isFavourite);
        }

        @Override
        public void onCollectionChanged(int numItems) {
            mViewModel.collectionDataSourceChanged(numItems);
        }
    };
}
