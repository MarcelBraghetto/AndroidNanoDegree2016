package io.github.marcelbraghetto.deviantartreader.features.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import io.github.marcelbraghetto.deviantartreader.features.application.logic.MainAppPresenter;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.dagger.AppComponent;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.dagger.AppDaggerModule;
import io.github.marcelbraghetto.deviantartreader.framework.foundation.dagger.DaggerAppComponent;

/**
 * Created by Marcel Braghetto on 24/02/16.
 */
public class MainApp extends Application {
    private static MainApp sSelf;

    private AppComponent mAppComponent;
    @Inject MainAppPresenter mPresenter;

    @Override
    public void onCreate() {
        super.onCreate();
        sSelf = this;
        mAppComponent = buildDaggerComponent();
        getDagger().inject(this);
        mPresenter.init();

        // We want to know when activities are started and stopped.
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                mPresenter.activityStarted();
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                mPresenter.activityStopped();
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
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
