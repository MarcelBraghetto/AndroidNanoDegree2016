package io.github.marcelbraghetto.alexandria.features.about.logic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.InputStream;

import javax.inject.Inject;

import io.github.marcelbraghetto.alexandria.R;
import io.github.marcelbraghetto.alexandria.framework.core.BaseController;
import io.github.marcelbraghetto.alexandria.framework.providers.strings.contracts.AppStringsProvider;

/**
 * Created by Marcel Braghetto on 4/12/15.
 *
 * Logic behaviour for the 'about' feature.
 */
public class AboutController extends BaseController<AboutController.Delegate> {
    private final Context mApplicationContext;
    private final AppStringsProvider mAppStringsProvider;

    @Inject
    public AboutController(@NonNull Context applicationContext,
                           @NonNull AppStringsProvider appStringsProvider) {

        super(Delegate.class);

        mApplicationContext = applicationContext;
        mAppStringsProvider = appStringsProvider;
    }

    public void initController(@Nullable Delegate delegate) {
        setDelegate(delegate);
        mDelegate.setScreenTitle(mAppStringsProvider.getString(R.string.about_screen_title));
        loadBodyText();
    }

    private void loadBodyText() {
        try {
            InputStream inputStream = mApplicationContext.getResources().openRawResource(R.raw.about);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            String bodyText = new String(bytes);
            mDelegate.setBodyText(bodyText);
            inputStream.close();
        } catch (Exception e) {
            // Really? Can't open a baked in raw resource??
        }
    }

    public interface Delegate {
        void setScreenTitle(@NonNull String title);

        void setBodyText(@NonNull String text);
    }
}
