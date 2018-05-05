package com.lilarcor.popularmovies.framework.foundation.network.dagger;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Marcel Braghetto on 30/07/15.
 *
 * Dagger component specifically for the network
 * request provider. This is primarily to allow
 * the network request provider implementation
 * to be swapped out for Espresso tests.
 */
@Singleton
@Component(modules = {
        NetworkRequestProviderModule.class
})
public interface NetworkRequestProviderComponent { }