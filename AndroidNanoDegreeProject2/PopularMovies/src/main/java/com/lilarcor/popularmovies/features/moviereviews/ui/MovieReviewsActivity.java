package com.lilarcor.popularmovies.features.moviereviews.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.features.application.MainApp;
import com.lilarcor.popularmovies.features.moviereviews.logic.MovieReviewsController;

/**
 * Created by Marcel Braghetto on 9/08/15.
 *
 * Host activity for the movie reviews screen, which
 * will know how to present itself for tablets and
 * phones. On a tablet the activity will present itself
 * in a floating window, whereas on a phone it will be
 * a regular activity.
 */
public class MovieReviewsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Find out if we are on a large device, and if so, configure
        // the activity to run in a floating window.
        if(MainApp.getDagger().getDeviceInfoProvider().isLargeDevice()) {
            setupFloatingWindow();
        }

        setContentView(R.layout.movie_reviews_activity);

        if(savedInstanceState == null) {
            MovieReviewsFragment fragment = MovieReviewsFragment.newInstance(getIntent().getIntExtra(MovieReviewsController.ARG_MOVIE_ID, -1));

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.movie_reviews_fragment, fragment)
                    .commit();
        }
    }

    /**
     * Configure this activity to open itself as a floating window.
     */
    private void setupFloatingWindow() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = getResources().getDimensionPixelSize(R.dimen.movie_reviews_floating_window_width);
        params.height = getResources().getDimensionPixelSize(R.dimen.movie_reviews_floating_window_height);
        params.alpha = 1;
        params.dimAmount = 0.7f;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        getWindow().setAttributes(params);
    }
}