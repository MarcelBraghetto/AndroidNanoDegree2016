package io.github.marcelbraghetto.deviantartreader.features.info.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.marcelbraghetto.deviantartreader.R;
import io.github.marcelbraghetto.deviantartreader.features.info.logic.InfoPresenter;
import io.github.marcelbraghetto.deviantartreader.features.application.MainApp;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.ui.BaseFragment;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.utils.StringUtils;

/**
 * Created by Marcel Braghetto on 14/03/16.
 *
 * Artwork info fragment display to render the extended details of a given artwork.
 */
public class InfoFragment extends BaseFragment {
    @SuppressWarnings("unused")
    @OnClick(R.id.info_rootview)
    void onRootViewClicked() {
        getActivity().finish();
    }

    @Bind(R.id.info_rootview) View mRootView;
    @Bind(R.id.info_cardview) View mCardView;
    @Bind(R.id.info_title) TextView mTitleTextView;
    @Bind(R.id.info_author_image) ImageView mAuthorImageView;
    @Bind(R.id.info_author_text) TextView mAuthorTextView;
    @Bind(R.id.info_description_webview) WebView mDescriptionWebView;

    @Inject InfoPresenter mPresenter;

    @NonNull
    public static InfoFragment newInstance(@NonNull Bundle bundle) {
        InfoFragment fragment = new InfoFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initUI(View view) {
        ButterKnife.bind(this, view);
        mRootView.setAlpha(0);

        // Due to a glitch with WebView flashing white with hardware acceleration turned on
        // this seems to correct the issue.
        mDescriptionWebView.setBackgroundColor(Color.argb(1, 0, 0, 0));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        MainApp.getDagger().inject(this);
        mPresenter.init(getArguments(), mDelegate);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.info_fragment, container, false);
    }

    private void populateWebView(String content) {
        mDescriptionWebView.loadData(content, null, "UTF-8");

        // We want to wait until the web view has finished rendering itself with the new content before
        // displaying the content to avoid a jarring size change on the screen.
        mDescriptionWebView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mDescriptionWebView.removeOnLayoutChangeListener(this);

                // Once our web view has changed its layout we can animate in the whole view.
                if(mRootView != null) {
                    mRootView.animate().alpha(1).setStartDelay(250).setDuration(400).start();
                }
            }
        });
    }

    private final InfoPresenter.Delegate mDelegate = new InfoPresenter.Delegate() {
        @Override
        public void setTitleText(@NonNull String titleText) {
            mTitleTextView.setText(titleText);
        }

        @Override
        public void setAuthorText(@NonNull String authorText) {
            mAuthorTextView.setText(authorText);
        }

        @Override
        public void setDescriptionText(@NonNull String htmlDescriptionText) {
            populateWebView(htmlDescriptionText);
        }

        @Override
        public void loadAuthorImage(@NonNull String url) {
            if(StringUtils.isEmpty(url)) {
                mAuthorImageView.setVisibility(View.GONE);
            } else {
                Glide.with(InfoFragment.this).load(url).into(mAuthorImageView);
            }
        }
    };
}
