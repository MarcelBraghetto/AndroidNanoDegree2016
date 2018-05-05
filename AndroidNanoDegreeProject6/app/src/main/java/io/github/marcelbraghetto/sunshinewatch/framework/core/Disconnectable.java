package io.github.marcelbraghetto.sunshinewatch.framework.core;

/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * Contract to advertise the availability of a 'disconnect' method which would typically
 * break any strong connections or clean up.
 */
public interface Disconnectable {
    /**
     * Disconnect from the given object, the action being based on the specific implementation.
     */
    void disconnect();
}
