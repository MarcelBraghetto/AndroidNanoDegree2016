package io.github.marcelbraghetto.sunshinewatch.features.home.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.github.marcelbraghetto.sunshinewatch.R;
import io.github.marcelbraghetto.sunshinewatch.features.forecast.ui.ForecastFragment;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity);

        if(savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.home_content_container, ForecastFragment.newInstance())
                    .commit();
        }
    }
}
