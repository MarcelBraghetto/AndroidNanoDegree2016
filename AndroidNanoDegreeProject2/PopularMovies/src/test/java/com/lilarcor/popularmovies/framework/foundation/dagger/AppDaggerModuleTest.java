package com.lilarcor.popularmovies.framework.foundation.dagger;

import android.content.Context;

import com.lilarcor.popularmovies.BuildConfig;
import com.lilarcor.popularmovies.features.application.MainApp;
import com.lilarcor.popularmovies.framework.foundation.device.DefaultDeviceInfoProvider;
import com.lilarcor.popularmovies.framework.foundation.device.contracts.DeviceInfoProvider;
import com.lilarcor.popularmovies.framework.foundation.eventbus.DefaultEventBusProvider;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusProvider;
import com.lilarcor.popularmovies.framework.foundation.network.DefaultNetworkRequestProvider;
import com.lilarcor.popularmovies.framework.foundation.network.contracts.NetworkRequestProvider;
import com.lilarcor.popularmovies.framework.foundation.threadutils.DefaultThreadUtilsProvider;
import com.lilarcor.popularmovies.framework.foundation.threadutils.contracts.ThreadUtilsProvider;
import com.lilarcor.popularmovies.framework.movies.provider.DefaultMoviesProvider;
import com.lilarcor.popularmovies.framework.movies.provider.contracts.MoviesProvider;
import com.lilarcor.popularmovies.testutils.RobolectricProperties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Marcel Braghetto on 25/07/15.
 *
 * Validation of the Dagger dependency graph, checking that implementations are
 * mapped to the correct interfaces and that singletons and instances are marked
 * correctly.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class AppDaggerModuleTest {
    /**
     * Verify application context provider mapping.
     */
    @Test
    public void testApplicationContextProviderMapping() {
        // Run
        Context context = MainApp.getDagger().getApplicationContext();

        // Verify
        assertThat(context, is(RuntimeEnvironment.application.getApplicationContext()));
    }

    /**
     * Verify application context is a singleton.
     */
    @Test
    public void testApplicationContextSingleton() {
        // Run
        Context a = MainApp.getDagger().getApplicationContext();
        Context b = MainApp.getDagger().getApplicationContext();

        // Verify
        assertThat(a, is(b));
    }

    /**
     * Verify network request provider mapping.
     */
    @Test
    public void testNetworkRequestProviderMapping() {
        // Run
        NetworkRequestProvider provider = MainApp.getDagger().getNetworkRequestProvider();

        // Verify
        assertThat(provider, instanceOf(DefaultNetworkRequestProvider.class));
    }

    /**
     * Verify network request provider is a singleton.
     */
    @Test
    public void testNetworkRequestProviderSingleton() {
        // Run
        NetworkRequestProvider a = MainApp.getDagger().getNetworkRequestProvider();
        NetworkRequestProvider b = MainApp.getDagger().getNetworkRequestProvider();

        // Verify
        assertThat(a, is(b));
    }

    /**
     * Verify event bus provider mapping.
     */
    @Test
    public void testEventBusProviderMapping() {
        // Run
        EventBusProvider provider = MainApp.getDagger().getEventBusProvider();

        // Verify
        assertThat(provider, instanceOf(DefaultEventBusProvider.class));
    }

    /**
     * Verify event bus provider is a singleton.
     */
    @Test
    public void testEventBusProviderSingleton() {
        // Run
        EventBusProvider a = MainApp.getDagger().getEventBusProvider();
        EventBusProvider b = MainApp.getDagger().getEventBusProvider();

        // Verify
        assertThat(a, is(b));
    }

    /**
     * Verify thread utils provider mapping.
     */
    @Test
    public void testThreadUtilsProviderMapping() {
        // Run
        ThreadUtilsProvider provider = MainApp.getDagger().getThreadUtilsProvider();

        // Verify
        assertThat(provider, instanceOf(DefaultThreadUtilsProvider.class));
    }

    /**
     * Verify thread utils provider is a singleton.
     */
    @Test
    public void testThreadUtilsProviderSingleton() {
        // Run
        ThreadUtilsProvider a = MainApp.getDagger().getThreadUtilsProvider();
        ThreadUtilsProvider b = MainApp.getDagger().getThreadUtilsProvider();

        // Verify
        assertThat(a, is(b));
    }

    /**
     * Verify device info provider mapping.
     */
    @Test
    public void testDeviceInfoProviderMapping() {
        // Run
        DeviceInfoProvider provider = MainApp.getDagger().getDeviceInfoProvider();

        // Verify
        assertThat(provider, instanceOf(DefaultDeviceInfoProvider.class));
    }

    /**
     * Verify device info provider is a singleton.
     */
    @Test
    public void testDeviceInfoProviderSingleton() {
        // Run
        DeviceInfoProvider a = MainApp.getDagger().getDeviceInfoProvider();
        DeviceInfoProvider b = MainApp.getDagger().getDeviceInfoProvider();

        // Verify
        assertThat(a, is(b));
    }

    /**
     * Verify movies provider mapping.
     */
    @Test
    public void testMoviesProviderMapping() {
        // Run
        MoviesProvider provider = MainApp.getDagger().getMoviesProvider();

        // Verify
        assertThat(provider, instanceOf(DefaultMoviesProvider.class));
    }

    /**
     * Verify movies provider is an instance.
     */
    @Test
    public void testMoviesProviderIsInstance() {
        // Run
        MoviesProvider a = MainApp.getDagger().getMoviesProvider();
        MoviesProvider b = MainApp.getDagger().getMoviesProvider();

        // Verify
        assertThat(a, not(b));
    }

}