package com.lilarcor.popularmovies.features.moviereviews.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.features.application.MainApp;
import com.lilarcor.popularmovies.features.moviereviews.logic.MovieReviewsController;
import com.lilarcor.popularmovies.framework.ui.DividerDecorationItem;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Marcel Braghetto on 9/08/15.
 *
 * Screen that displays all the user reviews for
 * a given movie id.
 */
public class MovieReviewsFragment extends Fragment {
    @Bind(R.id.movie_reviews_toolbar) Toolbar mToolbar;
    @Bind(R.id.movie_reviews_recycler_view) RecyclerView mRecyclerView;

    @Inject MovieReviewsController mController;
    private MovieReviewsAdapter mAdapter;
    private boolean mShowCloseButton;

    public static MovieReviewsFragment newInstance(int movieId) {
        MovieReviewsFragment fragment = new MovieReviewsFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(MovieReviewsController.ARG_MOVIE_ID, movieId);

        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mController.initController(mControllerDelegate, getArguments());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainApp.getDagger().inject(this);

        ButterKnife.bind(this, view);

        AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
        parentActivity.setSupportActionBar(mToolbar);
        ActionBar actionBar = parentActivity.getSupportActionBar();

        if(actionBar != null && !MainApp.getDagger().getDeviceInfoProvider().isLargeDevice()) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.movie_reviews_fragment, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home || id == R.id.movie_reviews_close) {
            getActivity().finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(mShowCloseButton) {
            inflater.inflate(R.menu.movie_reviews_menu, menu);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    private MovieReviewsController.ControllerDelegate mControllerDelegate = new MovieReviewsController.ControllerDelegate() {
        @Override
        public void setActionBarTitle(@NonNull String title) {
            AppCompatActivity parentActivity = (AppCompatActivity) getActivity();
            parentActivity.setTitle(title);
        }

        @Override
        public void showCloseButton() {
            mShowCloseButton = true;
            getActivity().supportInvalidateOptionsMenu();
        }

        @Override
        public void populateReviews(@NonNull final Uri dataSourceUri) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mRecyclerView.addItemDecoration(new DividerDecorationItem(getResources()));
            mAdapter = new MovieReviewsAdapter();
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
    };
}
