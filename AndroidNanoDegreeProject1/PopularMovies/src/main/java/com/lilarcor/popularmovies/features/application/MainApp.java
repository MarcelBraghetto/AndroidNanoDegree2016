package com.lilarcor.popularmovies.features.application;

import android.app.Application;
import android.support.annotation.NonNull;

import com.lilarcor.popularmovies.features.application.logic.MainAppController;
import com.lilarcor.popularmovies.framework.foundation.dagger.AppComponent;
import com.lilarcor.popularmovies.framework.foundation.dagger.AppDaggerModule;
import com.lilarcor.popularmovies.framework.foundation.dagger.DaggerAppComponent;
import com.lilarcor.popularmovies.framework.foundation.network.dagger.NetworkRequestProviderModule;

import javax.inject.Inject;

/**
 * Created by Marcel Braghetto on 11/07/15.
 *
 * Core application class that performs some basic
 * framework level tasks, and that hosts the app
 * level controller logic.
 */
public class MainApp extends Application {
    private AppComponent mAppComponent;
    private static MainApp sSelf;

    @Inject
    MainAppController mController;

    @Override
    public void onCreate() {
        super.onCreate();

        sSelf = this;
        mAppComponent = buildDaggerComponent();

        initController();
    }

    @NonNull
    public static AppComponent getDagger() {
        return sSelf.mAppComponent;
    }

    /**
     * Build the Dagger dependency component. This
     * method can be overridden in child classes
     * of the MainApp class, to provide different
     * implementations of elements in the Dagger
     * mapping. For example, the network request
     * provider is mapped to a different implementation
     * for the Espresso suite of tests to provide
     * a 'canned' way of responding to specific
     * url requests during automated tests.
     *
     * @return constructed Dagger component.
     */
    @NonNull
    protected AppComponent buildDaggerComponent() {
        return DaggerAppComponent
                .builder()
                .appDaggerModule(new AppDaggerModule(this))
                .networkRequestProviderModule(new NetworkRequestProviderModule())
                .build();
    }

    /**
     * Perform a Dagger injection of the application
     * instance which will start the main controller
     * logic among. Can be overridden to perform
     * other tasks.
     */
    protected void initController() {
        mAppComponent.inject(this);
    }
}