package io.github.marcelbraghetto.jokemachine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import io.github.marcelbraghetto.jokemachine.lib.android.api.JokesApiEvent;
import io.github.marcelbraghetto.jokemachine.lib.android.api.JokesApiManager;
import io.github.marcelbraghetto.jokemachine.lib.android.features.displayjoke.DisplayJokeActivity;
import io.github.marcelbraghetto.jokemachine.lib.android.features.settings.SettingsActivity;
import io.github.marcelbraghetto.jokemachine.lib.android.models.Joke;
import io.github.marcelbraghetto.jokemachine.lib.android.utils.IntentUtils;
import io.github.marcelbraghetto.jokemachine.lib.android.utils.ViewUtils;

/**
 * Base activity class shared between all product flavours and provides common feature
 * functionality.
 *
 * This activity uses the Event Bus to be notified when requests to fetch jokes succeed (or fail) -
 * decoupling the async task execution from the activity life cycle.
 *
 * An instance of this activity will have a unique identifier generated which is passed through to
 * the Joke manager, so the broadcast response events can be mapped against the identifier
 * therefore determining if this instance of the activity was the original 'owner' of the request
 * to fetch jokes.
 */
public abstract class BaseHomeActivity extends AppCompatActivity {
    @Bind(R.id.home_joke_button) View mJokeButton;
    @Bind(R.id.home_joke_loading_indicator) View mJokeLoadingIndicator;

    @SuppressWarnings("unused")
    @OnClick(R.id.home_joke_button)
    void jokeButtonClicked() {
        fetchRandomJoke();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.home_settings_button)
    void settingsButtonClicked() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    // The 'current' joke which is (hopefully) retrieved from the server every time the user taps
    // the button.
    protected Joke mCurrentJoke;

    // The instance id identifies the current class instance uniquely so we can match ownership to
    // remote operations that occur elsewhere and potentially on different life cycles.
    private final String mInstanceId = UUID.randomUUID().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.applyTranslucentStatusBar(getWindow());
        setContentView(R.layout.home_activity);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showJokeButton();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    /**
     * The event bus will listen for any events that will be broadcast related to the completion of
     * server requests for jokes with the given event properties.
     * @param event properties for the broadcast event.
     */
    @SuppressWarnings("unused")
    public void onEvent(@NonNull JokesApiEvent event) {
        // Only react to this event if we were the original 'owner' of it - ignore any others.
        if(event.getOwnerId().equals(mInstanceId)) {
            switch (event.getResult()) {
                case Failed:
                    jokeFailed();
                    break;
                case Success:
                    jokeLoaded(event.getJoke());
                    break;
            }
        }
    }

    /**
     * Display the current joke by launching the joke activity. Subclasses can override this method
     * to hijack the display operation - for example the free version will override this and
     * instead display an interstitial ad.
     */
    protected void displayCurrentJoke() {
        Intent intent = new Intent(this, DisplayJokeActivity.class);
        IntentUtils.putJoke(intent, mCurrentJoke);
        startActivity(intent);
    }

    /**
     * When a new joke has been fetched, check it then take action to display it to the user.
     * @param joke that was loaded.
     */
    private void jokeLoaded(Joke joke) {
        // Cache the joke and display it.
        mCurrentJoke = joke;
        displayCurrentJoke();
    }

    private void jokeFailed() {
        showJokeButton();
        Toast.makeText(this, getString(R.string.home_network_failure), Toast.LENGTH_SHORT).show();
    }

    /**
     * Start an operation to fetch a new joke on a background thread, handled by the Jokes Api
     * Manager.
     */
    private void fetchRandomJoke() {
        showLoadingIndicator();
        JokesApiManager.Instance.fetchJoke(mInstanceId);
    }

    /**
     * Display the loading indicator UI.
     */
    private void showLoadingIndicator() {
        mJokeButton.setVisibility(View.GONE);
        mJokeLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * Display the joke button UI.
     */
    private void showJokeButton() {
        mJokeButton.setVisibility(View.VISIBLE);
        mJokeLoadingIndicator.setVisibility(View.GONE);
    }
}
