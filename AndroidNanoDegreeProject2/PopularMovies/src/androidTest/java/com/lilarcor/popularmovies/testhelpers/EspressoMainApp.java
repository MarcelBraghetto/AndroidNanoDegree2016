package com.lilarcor.popularmovies.testhelpers;

import android.support.annotation.NonNull;

import com.lilarcor.popularmovies.features.application.MainApp;
import com.lilarcor.popularmovies.framework.foundation.dagger.AppComponent;
import com.lilarcor.popularmovies.framework.foundation.dagger.AppDaggerModule;
import com.lilarcor.popularmovies.framework.foundation.dagger.DaggerAppComponent;
import com.lilarcor.popularmovies.testhelpers.framework.foundation.network.dagger.EspressoNetworkRequestModule;

/**
 * Created by Marcel Braghetto on 30/07/15.
 *
 * This is a custom Android application which is used by
 * the custom Espresso test runner. The primary reason for
 * this is to override the default Dagger configuration so
 * we can replace any Dagger provider mappings with test
 * versions. For example, instead of using the default
 * network request provider implementation which will
 * actually go to the server to fetch data, we are using
 * a custom Espresso network request provider, which
 * mocks networking responses.
 */
public class EspressoMainApp extends MainApp {
    @Override
    @NonNull
    protected AppComponent buildDaggerComponent() {
        return DaggerAppComponent
                .builder()
                .appDaggerModule(new AppDaggerModule(this))
                .networkRequestProviderModule(new EspressoNetworkRequestModule())
                .build();
    }

    /**
     * Before we allow the main app to start its
     * controller, we want to clear any saved
     * data so each Espresso session starts in
     * a clean state.
     */
    @Override
    protected void initController() {
        EspressoHelper.resetApplicationData();
        super.initController();
    }
}