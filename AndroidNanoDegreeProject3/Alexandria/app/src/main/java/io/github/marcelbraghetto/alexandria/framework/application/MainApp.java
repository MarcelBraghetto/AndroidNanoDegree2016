package io.github.marcelbraghetto.alexandria.framework.application;

import android.app.Application;
import android.support.annotation.NonNull;

import io.github.marcelbraghetto.alexandria.framework.dagger.AppComponent;
import io.github.marcelbraghetto.alexandria.framework.dagger.AppDaggerModule;
import io.github.marcelbraghetto.alexandria.framework.dagger.DaggerAppComponent;

/**
 * Created by Marcel Braghetto on 1/12/15.
 *
 * Main application class for the app.
 */
public class MainApp extends Application {
    private AppComponent mAppComponent;
    private static MainApp sSelf;

    @Override
    public void onCreate() {
        super.onCreate();
        sSelf = this;
        mAppComponent = buildDaggerComponent();
    }

    @NonNull
    protected AppComponent buildDaggerComponent() {
        return DaggerAppComponent
                .builder()
                .appDaggerModule(new AppDaggerModule(this))
                .build();
    }

    @NonNull
    public static AppComponent getDagger() {
        return sSelf.mAppComponent;
    }
}
