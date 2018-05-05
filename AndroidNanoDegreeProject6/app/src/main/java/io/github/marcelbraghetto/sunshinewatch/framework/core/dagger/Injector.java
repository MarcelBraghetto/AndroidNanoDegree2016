package io.github.marcelbraghetto.sunshinewatch.framework.core.dagger;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Marcel Braghetto on 30/04/16.
 *
 * Dependency injector - lazily created to also allow Dagger to be available from any Android
 * component that has a context.
 */
public final class Injector {
    private static InjectorComponent sAppComponent;

    private Injector() { }

    /**
     * Obtain the shared instance of the injector for dependency injection.
     * @param context needed to resolve the injector.
     * @return shared instance of the dependency injector.
     */
    @NonNull
    public static synchronized InjectorComponent get(@NonNull Context context) {
        if(sAppComponent == null) {
            sAppComponent = DaggerInjectorComponent
                    .builder()
                    .injectorDaggerModule(new InjectorDaggerModule(context.getApplicationContext()))
                    .build();
        }

        return sAppComponent;
    }
}
