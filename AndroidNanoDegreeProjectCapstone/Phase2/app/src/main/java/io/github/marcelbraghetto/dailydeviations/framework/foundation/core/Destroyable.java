package io.github.marcelbraghetto.dailydeviations.framework.foundation.core;

/**
 * Created by Marcel Braghetto on 2/12/15.
 *
 * Contract to advertise the availability of a 'destroy' method which would typically
 * break any strong connections or clean up.
 */
public interface Destroyable {
    /**
     * Destroys the given object, the action being based on the specific implementation.
     */
    void destroy();
}
