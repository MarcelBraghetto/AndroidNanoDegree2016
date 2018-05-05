package io.github.marcelbraghetto.alexandria.features.mybooks.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.marcelbraghetto.alexandria.R;
import io.github.marcelbraghetto.alexandria.features.mybooks.logic.MyBooksController;
import io.github.marcelbraghetto.alexandria.framework.application.MainApp;
import io.github.marcelbraghetto.alexandria.framework.ui.BaseFragment;

/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * UI for the 'my books' collection fragment.
 */
public class MyBooksFragment extends BaseFragment {
    @Bind(R.id.my_books_recycler_view) RecyclerView mRecyclerView;
    @Bind(R.id.my_books_no_results_container) View mNoBooksView;
    @OnClick(R.id.my_books_action_button)
    void actionButtonClicked() {
        if(mController != null) {
            mController.actionButtonSelected();
        }
    }

    private View mRootView;
    private Snackbar mSnackbar;
    private SearchView mSearchView;

    @Inject MyBooksController mController;
    private MyBooksAdapter mAdapter;

    public static MyBooksFragment newInstance() {
        return  new MyBooksFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        MainApp.getDagger().inject(this);
    }

    private void initUI(View view) {
        ButterKnife.bind(this, view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new MyBooksAdapter(new MyBooksAdapter.Delegate() {
            @Override
            public void removeSelected(@NonNull String isbn) {
                if(mController != null) {
                    mController.removeSelected(isbn);
                }
            }

            @Override
            public void shareSelected(@NonNull String isbn) {
                if(mController != null) {
                    mController.shareSelected(isbn);
                }
            }

            @Override
            public void viewInBrowserSelected(@NonNull String isbn) {
                if(mController != null) {
                    mController.viewInBrowserSelected(isbn);
                }
            }

            @Override
            public void thumbnailSelected(@NonNull String isbn) {
                if(mController != null) {
                    mController.thumbnailSelected(isbn);
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        mController.initController(mControllerDelegate);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mRootView = null;
        mSearchView = null;
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
        mRootView = inflater.inflate(R.layout.my_books_fragment, container, false);
        return mRootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.my_books, menu);

        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_my_books_search));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(mController != null) {
                    mController.searchTextChanged(newText);
                }
                return false;
            }
        });
    }

    private void cancelSnackBar() {
        if(mSnackbar != null) {
            mSnackbar.dismiss();
            mSnackbar = null;
        }
    }

    private final MyBooksController.Delegate mControllerDelegate = new MyBooksController.Delegate() {
        @Override
        public void setScreenTitle(@NonNull String title) {
            setTitle(title);
        }

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
        public void showSnackbarMessage(@NonNull String message) {
            cancelSnackBar();
            mSnackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
            mSnackbar.show();
        }

        @Override
        public void startActivityIntent(@NonNull Intent intent) {
            getActivity().startActivity(intent);
        }

        @Override
        public void showNoBooksMessage() {
            mNoBooksView.setVisibility(View.VISIBLE);
        }

        @Override
        public void hideNoBooksMessage() {
            mNoBooksView.setVisibility(View.GONE);
        }
    };
}
