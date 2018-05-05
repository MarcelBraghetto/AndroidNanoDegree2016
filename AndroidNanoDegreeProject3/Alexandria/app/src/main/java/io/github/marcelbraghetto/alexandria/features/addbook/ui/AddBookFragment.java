package io.github.marcelbraghetto.alexandria.features.addbook.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.github.marcelbraghetto.alexandria.R;
import io.github.marcelbraghetto.alexandria.features.addbook.logic.AddBookController;
import io.github.marcelbraghetto.alexandria.features.scanner.ui.ScannerActivity;
import io.github.marcelbraghetto.alexandria.framework.application.MainApp;
import io.github.marcelbraghetto.alexandria.framework.providers.books.models.Book;
import io.github.marcelbraghetto.alexandria.framework.ui.BaseFragment;
import io.github.marcelbraghetto.alexandria.framework.utils.ViewUtils;
import io.github.marcelbraghetto.alexandria.widgets.BookDetailsView;

/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * UI for the add books feature.
 */
public class AddBookFragment extends BaseFragment {
    @Bind(R.id.add_book_search_status) TextView mSearchStatusTextView;
    @Bind(R.id.add_book_search_result_container) View mBookDetailsContainer;
    @Bind(R.id.add_book_search_result) BookDetailsView mBookDetailsView;
    @Bind(R.id.add_book_search_edit_text) EditText mSearchEditTextView;

    @OnTextChanged(R.id.add_book_search_edit_text)
    void editTextChanged(CharSequence text) {
        if(mController != null) {
            mController.searchTextChanged(String.valueOf(text));
        }
    }
    @Bind(R.id.add_book_scan_container) View mScanContainer;

    @Bind(R.id.add_book_scan_button) Button mScanButton;
    @OnClick(R.id.add_book_scan_button)
    void scanButtonClicked() {
        if(mController != null) {
            mController.scanButtonSelected();
        }
    }

    private View mRootView;
    private Snackbar mSnackbar;

    @Inject AddBookController mController;

    @NonNull
    public static AddBookFragment newInstance() {
        return new AddBookFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApp.getDagger().inject(this);
    }

    private void initUI(View view) {
        ButterKnife.bind(this, view);

        mBookDetailsView.setActionDelegate(new BookDetailsView.ActionDelegate() {
            @Override
            public void actionButtonSelected() {
                if (mController != null) {
                    mController.bookDetailsActionButtonSelected();
                }
            }

            @Override
            public void thumbnailSelected() {
                if (mController != null) {
                    mController.bookDetailsThumbnailSelected();
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        mController.initController(mControllerDelegate);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.add_book_fragment, container, false);
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelSnackBar();
        ButterKnife.unbind(this);
        mRootView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mController != null) {
            mController.disconnect();
            mController = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ScannerActivity.SCANNER_REQUEST_CODE && resultCode == CommonStatusCodes.SUCCESS && data != null && mController != null) {
            Barcode barcode = data.getParcelableExtra(ScannerActivity.BARCODE_RESULT);
            mController.barCodeScanned(barcode.displayValue);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void cancelSnackBar() {
        if(mSnackbar != null) {
            mSnackbar.dismiss();
            mSnackbar = null;
        }
    }

    private final AddBookController.Delegate mControllerDelegate = new AddBookController.Delegate() {
        @Override
        public void setScreenTitle(@NonNull String title) {
            setTitle(title);
        }

        @Override
        public void showSnackBarMessage(@NonNull String message) {
            cancelSnackBar();
            mSnackbar = Snackbar.make(mRootView, message, Snackbar.LENGTH_LONG);
            mSnackbar.show();
        }

        @Override
        public void showSearchStatusText(@NonNull String text) {
            mSearchStatusTextView.setVisibility(View.VISIBLE);
            mSearchStatusTextView.setText(text);
        }

        @Override
        public void hideSearchStatusText() {
            mSearchStatusTextView.setVisibility(View.GONE);
        }

        @Override
        public void hideBookDetails() {
            mBookDetailsContainer.setVisibility(View.GONE);
        }

        @Override
        public void showBookDetails(@NonNull Book book, @NonNull String actionLabel) {
            mBookDetailsContainer.setVisibility(View.VISIBLE);
            mBookDetailsView.populate(book, actionLabel);
        }

        @Override
        public void hideKeyboard() {
            ViewUtils.hideKeyboard(getActivity());
        }

        @Override
        public void clearSearchText() {
            mSearchEditTextView.setText("");
        }

        @Override
        public void setSearchText(@NonNull String text) {
            mSearchEditTextView.setText(text);
        }

        @Override
        public void startActivityIntentForResult(@NonNull Intent intent, int requestCode) {
            startActivityForResult(intent, requestCode);
        }

        @Override
        public void hideScanButton() {
            mScanContainer.setVisibility(View.GONE);
        }

        @Override
        public void startActivityIntent(@NonNull Intent intent) {
            startActivity(intent);
        }
    };
}
