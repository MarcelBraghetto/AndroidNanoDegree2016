package io.github.marcelbraghetto.sunshinewatch.framework.core.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import io.github.marcelbraghetto.sunshinewatch.features.application.MainApp;
import io.github.marcelbraghetto.sunshinewatch.features.forecast.ui.ForecastFragment;
import io.github.marcelbraghetto.sunshinewatch.features.home.ui.HomeActivity;
import io.github.marcelbraghetto.sunshinewatch.framework.strings.contracts.StringsProvider;
import io.github.marcelbraghetto.sunshinewatch.weather.sync.SunshineSyncAdapter;

/**
 * Created by Marcel Braghetto on 24/02/16.
 *
 * Application Dagger component for mapping and injection.
 */
@Singleton
@Component(modules = {
        InjectorDaggerModule.class
})
public interface InjectorComponent {
    void inject(MainApp mainApplication);
    void inject(HomeActivity activity);
    void inject(ForecastFragment fragment);
    void inject(SunshineSyncAdapter syncAdapter);

    Context getApplicationContext();
    StringsProvider getStringsProvider();
}
