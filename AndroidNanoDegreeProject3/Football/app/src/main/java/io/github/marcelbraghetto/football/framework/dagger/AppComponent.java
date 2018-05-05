package io.github.marcelbraghetto.football.framework.dagger;

import javax.inject.Singleton;

import dagger.Component;
import io.github.marcelbraghetto.football.features.about.ui.AboutFragment;
import io.github.marcelbraghetto.football.features.scores.ui.ScoresFragment;
import io.github.marcelbraghetto.football.features.scores.ui.ScoresHostFragment;
import io.github.marcelbraghetto.football.features.home.ui.HomeActivity;
import io.github.marcelbraghetto.football.framework.application.MainApp;
import io.github.marcelbraghetto.football.framework.providers.football.contracts.FootballProvider;
import io.github.marcelbraghetto.football.framework.providers.football.service.FootballDataService;
import io.github.marcelbraghetto.football.framework.providers.strings.contracts.AppStringsProvider;

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
    void inject(MainApp mainApplication);

    void inject(HomeActivity activity);

    void inject(ScoresHostFragment fragment);

    void inject(ScoresFragment fragment);

    void inject(AboutFragment fragment);

    void inject(FootballDataService service);

    FootballProvider getFootballProvider();

    AppStringsProvider getAppStringsProvider();
}
