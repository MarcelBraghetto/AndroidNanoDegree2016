package io.github.marcelbraghetto.alexandria.features.home.logic;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import javax.inject.Inject;

import io.github.marcelbraghetto.alexandria.R;
import io.github.marcelbraghetto.alexandria.features.about.ui.AboutFragment;
import io.github.marcelbraghetto.alexandria.features.addbook.ui.AddBookFragment;
import io.github.marcelbraghetto.alexandria.features.mybooks.ui.MyBooksFragment;
import io.github.marcelbraghetto.alexandria.framework.core.BaseController;
import io.github.marcelbraghetto.alexandria.framework.providers.books.contracts.BooksProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.alexandria.framework.providers.eventbus.contracts.EventBusSubscriber;
import io.github.marcelbraghetto.alexandria.framework.providers.eventbus.events.AddBookActionEvent;

/**
 * Created by Marcel Braghetto on 1/12/15.
 *
 * Controller logic for the home activity which is
 * responsible for the nav menu and content
 * population.
 */
public class HomeController extends BaseController<HomeController.Delegate> implements EventBusSubscriber {
    private final EventBusProvider mEventBusProvider;
    private final BooksProvider mBooksProvider;

    @IdRes private int mSelectedMenuItemId;

    //region Public methods
    @Inject
    public HomeController(@NonNull EventBusProvider eventBusProvider,
                          @NonNull BooksProvider booksProvider) {

        super(Delegate.class);

        mEventBusProvider = eventBusProvider;
        mBooksProvider = booksProvider;
    }

    public void initController(@Nullable Delegate delegate, @Nullable Bundle savedInstanceState) {
        setDelegate(delegate);

        if(savedInstanceState == null) {
            showInitialContent(false);
        }
    }

    /**
     * User selects a different menu item from
     * the navigation drawer.
     * @param itemId of the selected menu item.
     */
    public void menuItemSelected(@IdRes int itemId) {
        mDelegate.closeNavigationMenu();
        mSelectedMenuItemId = itemId;

        switch (itemId) {
            case R.id.nav_menu_my_books:
                mDelegate.replaceContent(MyBooksFragment.newInstance(), true);
                break;
            case R.id.nav_menu_add_book:
                mDelegate.replaceContent(AddBookFragment.newInstance(), true);
                break;
            case R.id.nav_menu_about:
                mDelegate.replaceContent(AboutFragment.newInstance(), true);
                break;
        }
    }

    /**
     * User selected the back key, determine
     * whether to show them the initial
     * content fragment or exit the app.
     */
    public void backPressed() {
        if(mSelectedMenuItemId != R.id.nav_menu_my_books) {
            mSelectedMenuItemId = R.id.nav_menu_my_books;
            mDelegate.highlightMenuItem(mSelectedMenuItemId);
            mDelegate.replaceContent(MyBooksFragment.newInstance(), true);
            return;
        }

        mDelegate.finishActivity();
    }

    /**
     * Screen has resumed.
     */
    public void screenResumed() {
        subscribeToEventBus();
    }

    /**
     * Screen has paused.
     */
    public void screenPaused() {
        unsubscribeFromEventBus();
    }
    //endregion

    //region Event bus
    @SuppressWarnings("unused")
    public void onEventMainThread(@NonNull AddBookActionEvent event) {
        menuItemSelected(R.id.nav_menu_add_book);
        mDelegate.highlightMenuItem(R.id.nav_menu_add_book);
    }

    @Override
    public void subscribeToEventBus() {
        mEventBusProvider.subscribe(this);
    }

    @Override
    public void unsubscribeFromEventBus() {
        mEventBusProvider.unsubscribe(this);
    }
    //endregion

    //region Private methods
    private void showInitialContent(boolean animated) {
        if(mBooksProvider.hasSavedBooks()) {
            mSelectedMenuItemId = R.id.nav_menu_my_books;
            mDelegate.replaceContent(MyBooksFragment.newInstance(), animated);
        } else {
            mSelectedMenuItemId = R.id.nav_menu_add_book;
            mDelegate.replaceContent(AddBookFragment.newInstance(), animated);
        }

        mDelegate.highlightMenuItem(mSelectedMenuItemId);
    }
    //endregion

    //region Controller delegate
    public interface Delegate {
        /**
         * Replace the currently displayed content
         * with the given fragment.
         * @param fragment to display
         */
        void replaceContent(@NonNull Fragment fragment, boolean animated);

        /**
         * Request the navigation menu 'highlight'
         * the given item id.
         * @param itemId to highlight.
         */
        void highlightMenuItem(@IdRes int itemId);

        /**
         * Close the sliding navigation menu.
         */
        void closeNavigationMenu();

        /**
         * Start the given intent.
         * @param intent to start.
         */
        void startActivityIntent(@NonNull Intent intent);

        /**
         * Tell the current activity to finish itself.
         */
        void finishActivity();
    }
    //endregion
}
