package io.github.marcelbraghetto.jokemachine;

import android.os.Bundle;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by Marcel Braghetto on 19/01/16.
 *
 * The free version of the home activity includes the integration of the interstitial fullscreen
 * ads between loading and displaying a joke.
 */
public final class HomeActivity extends BaseHomeActivity {
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We will be reusing an interstitial ad instance to rotate through ads.
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.home_fullscreen_ad_unit_id));

        // When the user closes the fullscreen ad we will navigate through to the joke display
        // activity.
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                handleAdClosed();
            }
        });

        // Start loading an ad so it is ready by the time the user has retrieved a joke to display
        // (maybe!)
        requestNewInterstitial();
    }

    /**
     * When a joke has 'loaded', we have the opportunity to hijack the navigation behaviour. By
     * default, the super class will simply navigate to the joke display activity, however in the
     * free version, if there is an interstitial ad ready to be displayed, it will be displayed
     * instead of navigating. When the user closes the ad, the callback will trigger the regular
     * navigation behaviour to present the joke to the user in the joke activity.
     */
    @Override
    protected void displayCurrentJoke() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            super.displayCurrentJoke();
        }
    }

    /**
     * When a fullscreen ad has been closed by the user start a request to get another ad, and call
     * the super class to show the joke that was last loaded.
     */
    private void handleAdClosed() {
        requestNewInterstitial();
        super.displayCurrentJoke();
    }

    /**
     * Ask the interstitial ad instance to fetch a fresh ad ready to be displayed.
     */
    private void requestNewInterstitial() {
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }
}
