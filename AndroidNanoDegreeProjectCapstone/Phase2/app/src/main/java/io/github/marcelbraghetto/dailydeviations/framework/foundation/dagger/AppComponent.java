package io.github.marcelbraghetto.dailydeviations.framework.foundation.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import io.github.marcelbraghetto.dailydeviations.features.about.ui.AboutFragment;
import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;
import io.github.marcelbraghetto.dailydeviations.features.collection.logic.providers.contracts.CollectionProvider;
import io.github.marcelbraghetto.dailydeviations.features.collection.ui.CollectionFragment;
import io.github.marcelbraghetto.dailydeviations.features.detail.ui.DetailActivity;
import io.github.marcelbraghetto.dailydeviations.features.home.ui.HomeActivity;
import io.github.marcelbraghetto.dailydeviations.features.home.ui.HomeNavHeaderView;
import io.github.marcelbraghetto.dailydeviations.features.info.ui.InfoFragment;
import io.github.marcelbraghetto.dailydeviations.features.settings.ui.SettingsFragment;
import io.github.marcelbraghetto.dailydeviations.features.wallpaper.providers.contracts.WallpaperProvider;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.contracts.ArtworksProvider;
import io.github.marcelbraghetto.dailydeviations.framework.artworks.service.ArtworksDataService;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.device.contracts.DeviceProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.eventbus.contracts.EventBusProvider;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.strings.contracts.StringsProvider;

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
    void inject(HomeNavHeaderView view);
    void inject(CollectionFragment fragment);
    void inject(DetailActivity activity);
    void inject(InfoFragment fragment);
    void inject(AboutFragment fragment);
    void inject(SettingsFragment fragment);

    Context getApplicationContext();
    StringsProvider getAppStringsProvider();
    DeviceProvider getDeviceProvider();
    EventBusProvider getEventBusProvider();
    CollectionProvider getCollectionProvider();
    ArtworksProvider getArtworksProvider();
    WallpaperProvider getWallpaperProvider();
}
