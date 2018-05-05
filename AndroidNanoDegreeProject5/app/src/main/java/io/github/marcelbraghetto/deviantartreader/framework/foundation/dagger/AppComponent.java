package io.github.marcelbraghetto.deviantartreader.framework.foundation.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import io.github.marcelbraghetto.deviantartreader.features.collection.logic.providers.contracts.CollectionProvider;
import io.github.marcelbraghetto.deviantartreader.features.collection.ui.CollectionFragment;
import io.github.marcelbraghetto.deviantartreader.features.detail.ui.DetailActivity;
import io.github.marcelbraghetto.deviantartreader.features.info.ui.InfoFragment;
import io.github.marcelbraghetto.deviantartreader.features.home.ui.HomeActivity;
import io.github.marcelbraghetto.deviantartreader.features.application.MainApp;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.contracts.ArtworksProvider;
import io.github.marcelbraghetto.deviantartreader.framework.artworks.service.ArtworksDataService;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.device.contracts.DeviceProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.strings.contracts.StringsProvider;

/**
 * Created by Marcel Braghetto on 24/02/16.
 *
 * Application Dagger component for mapping and injection.
 */
@Singleton
@Component(modules = {
        AppDaggerModule.class
})
public interface AppComponent {
    void inject(MainApp mainApplication);
    void inject(ArtworksDataService service);
    void inject(HomeActivity activity);
    void inject(CollectionFragment fragment);
    void inject(DetailActivity activity);
    void inject(InfoFragment fragment);

    Context getApplicationContext();
    StringsProvider getAppStringsProvider();
    ArtworksProvider getArtworksProvider();
    DeviceProvider getDeviceProvider();
    EventBusProvider getEventBusProvider();
    CollectionProvider getCollectionProvider();
}
