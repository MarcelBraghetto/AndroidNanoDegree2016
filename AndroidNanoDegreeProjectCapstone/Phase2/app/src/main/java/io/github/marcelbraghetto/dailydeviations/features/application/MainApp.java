package io.github.marcelbraghetto.dailydeviations.features.application;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;
import android.webkit.WebView;

import javax.inject.Inject;

import io.github.marcelbraghetto.dailydeviations.features.application.logic.MainAppLogic;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.dagger.AppComponent;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.dagger.AppDaggerModule;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.dagger.DaggerAppComponent;
import io.github.marcelbraghetto.dailydeviations.framework.foundation.utils.BasicActivityLifecycleCallbacks;

/**
 * Created by Marcel Braghetto on 24/02/16.
 */
public class MainApp extends Application {
    private static MainApp sSelf;
    private AppComponent mAppComponent;
    @Inject MainAppLogic mLogic;

    @Override
    public void onCreate() {
        super.onCreate();
        sSelf = this;
        mAppComponent = buildDaggerComponent();
        getDagger().inject(this);
        mLogic.begin();

        registerActivityLifecycleCallbacks(new BasicActivityLifecycleCallbacks() {
            @Override
            public void onActivityStarted(Activity activity) {
                mLogic.activityStarted();
            }

            @Override
            public void onActivityStopped(Activity activity) {
                mLogic.activityStopped();
            }
        });

        warmupWebView();
    }

    /**
     * From Lollipop onward, constructing a WebView is very expensive the first time
     * so we are going to warm it up here before the user might try to open something
     * needing a web view and be presented with a jarring main thread freeze.
     */
    private void warmupWebView() {
        new WebView(getApplicationContext());
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
