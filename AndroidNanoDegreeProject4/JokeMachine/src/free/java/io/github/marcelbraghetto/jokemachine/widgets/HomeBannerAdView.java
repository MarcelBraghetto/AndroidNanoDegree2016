package io.github.marcelbraghetto.jokemachine.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import io.github.marcelbraghetto.jokemachine.R;

/**
 * Created by Marcel Braghetto on 6/01/16.
 *
 * This custom view is specific to the free flavour of the app and encapsulates the Google Ads
 * 'AdView' widget, such that we can drop an instance of this class in an XML layout and it will be
 * fully configured to run itself.
 */
public class HomeBannerAdView extends FrameLayout {
    public HomeBannerAdView(Context context) {
        super(context);
        init();
    }

    public HomeBannerAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HomeBannerAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.home_banner_ad_view, this);

        AdView adView = (AdView) findViewById(R.id.home_banner_ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);
    }
}
