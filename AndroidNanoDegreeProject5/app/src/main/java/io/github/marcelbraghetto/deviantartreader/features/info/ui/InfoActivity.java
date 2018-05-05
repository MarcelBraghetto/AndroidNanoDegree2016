package io.github.marcelbraghetto.deviantartreader.features.info.ui;

import android.os.Bundle;

import io.github.marcelbraghetto.deviantartreader.R;
import io.github.marcelbraghetto.deviantartreader.features.application.MainApp;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.device.contracts.DeviceProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.ui.BaseActivity;

/**
 * Created by Marcel Braghetto on 15/03/16.
 *
 * Host activity for the info window for an artwork.
 */
public class InfoActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure the size of the floating window based on the size of the host window.
        DeviceProvider deviceProvider = MainApp.getDagger().getDeviceProvider();
        int contentWidth = Math.min((int) (deviceProvider.getCurrentWindowWidth() * 0.8), getResources().getDimensionPixelSize(R.dimen.info_window_max_width));
        int contentHeight = (int) (deviceProvider.getCurrentWindowHeight() * 0.8);
        setupFloatingWindow(contentWidth, contentHeight);

        setContentView(R.layout.info_activity);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.info_content_container, InfoFragment.newInstance(getIntent().getExtras())).commit();
        }
    }
}
