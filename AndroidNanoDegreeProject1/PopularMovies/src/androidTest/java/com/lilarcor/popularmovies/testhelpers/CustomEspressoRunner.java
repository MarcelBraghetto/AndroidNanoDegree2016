package com.lilarcor.popularmovies.testhelpers;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnitRunner;

/**
 * Created by Marcel Braghetto on 30/07/15.
 *
 * We need to use a custom test runner when launching the
 * Espresso tests so that the main application class can
 * be redefined and pointed at an Espresso specific version.
 *
 * For our test suite, the 'EspressoMainApp' class will be
 * launched as the Android application, instead of the
 * default 'MainApp' class.
 *
 * This lets us override methods and properties that might
 * be needed specifically for testing (such as a custom
 * network request provider).
 */
@SuppressWarnings("unused")
public class CustomEspressoRunner extends AndroidJUnitRunner {
    @Override
    public Application newApplication(@NonNull ClassLoader classLoader, String className, Context context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(classLoader, EspressoMainApp.class.getName(), context);
    }
}