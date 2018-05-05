package io.github.marcelbraghetto.alexandria.framework.dagger;

import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Component;
import io.github.marcelbraghetto.alexandria.features.about.ui.AboutFragment;
import io.github.marcelbraghetto.alexandria.features.addbook.ui.AddBookFragment;
import io.github.marcelbraghetto.alexandria.features.home.ui.HomeActivity;
import io.github.marcelbraghetto.alexandria.features.mybooks.ui.MyBooksFragment;
import io.github.marcelbraghetto.alexandria.features.scanner.ui.ScannerActivity;
import io.github.marcelbraghetto.alexandria.features.thumbnailviewer.ui.ThumbnailViewerActivity;
import io.github.marcelbraghetto.alexandria.framework.application.MainApp;
import io.github.marcelbraghetto.alexandria.framework.providers.strings.contracts.AppStringsProvider;

/**
 * Created by Marcel Braghetto on 1/12/15.
 *
 * Application Dagger component for mapping
 * and injection.
 */
@Singleton
@Component(modules = {
        AppDaggerModule.class
})
public interface AppComponent {
    /**
     * Inject the given application object.
     *
     * @param mainApplication to inject.
     */
    void inject(MainApp mainApplication);

    void inject(HomeActivity activity);

    void inject(ThumbnailViewerActivity activity);

    void inject(ScannerActivity activity);

    void inject(AddBookFragment fragment);

    void inject(MyBooksFragment fragment);

    void inject(AboutFragment fragment);

    @NonNull AppStringsProvider getAppStringsProvider();
}
