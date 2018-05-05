package com.lilarcor.popularmovies.features.moviedetails.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lilarcor.popularmovies.R;
import com.lilarcor.popularmovies.features.moviedetails.logic.MovieDetailsController;

/**
 * Created by Marcel Braghetto on 2/08/15.
 */
public class MovieDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.movie_details_activity);

        if(savedInstanceState == null) {
            MovieDetailsFragment fragment = MovieDetailsFragment.newInstance(getIntent().getIntExtra(MovieDetailsController.ARG_MOVIE_ID, -1));

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.movie_details_fragment, fragment)
                    .commit();
        }
    }
}
