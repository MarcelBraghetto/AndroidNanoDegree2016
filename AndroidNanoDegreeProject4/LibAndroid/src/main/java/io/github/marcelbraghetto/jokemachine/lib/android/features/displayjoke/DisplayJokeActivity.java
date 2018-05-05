package io.github.marcelbraghetto.jokemachine.lib.android.features.displayjoke;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.ButterKnife;
import io.github.marcelbraghetto.jokemachine.lib.android.R;
import io.github.marcelbraghetto.jokemachine.lib.android.models.Joke;
import io.github.marcelbraghetto.jokemachine.lib.android.utils.IntentUtils;

/**
 * Created by Marcel Braghetto on 19/01/16.
 *
 * Activity to display a given joke which is expected to be passed in via the intent bundle extras.
 *
 * It is an expectation that a joke be provided as a parcelable in the intent bundle.
 */
public class DisplayJokeActivity extends AppCompatActivity {
    private TextView mQuestionTextView;
    private TextView mAnswerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Extract the given joke from the intent extras
        Joke joke = IntentUtils.getJoke(getIntent());

        // If this activity was started without a joke in the intent extras, then it is an illegal
        // use of the activity so we deliberately crash out.
        if(joke == null) {
            throw new UnsupportedOperationException("The Joke activity must be launched with a joke in the intent bundle");
        }

        setupFloatingWindow();

        setContentView(R.layout.display_joke_activity);

        mQuestionTextView = ButterKnife.findById(this, R.id.joke_question);
        mAnswerTextView = ButterKnife.findById(this, R.id.joke_answer);

        populateJoke(joke);
    }

    private void populateJoke(Joke joke) {
        mQuestionTextView.setText(joke.getQuestion());
        mAnswerTextView.setText(joke.getAnswer());
    }

    private void setupFloatingWindow() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = getResources().getDimensionPixelSize(R.dimen.display_joke_window_width);
        params.height = getResources().getDimensionPixelSize(R.dimen.display_joke_window_height);
        params.alpha = 1;
        params.dimAmount = 0.5f;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        getWindow().setAttributes(params);
    }
}
