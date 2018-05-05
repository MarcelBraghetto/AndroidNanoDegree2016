package io.github.marcelbraghetto.alexandria.framework.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Marcel Braghetto on 02/12/15.
 *
 * Base class for view controller classes.
 */
public abstract class BaseController<T> implements Disconnectable {
    /**
     * This field can be accessed by the child class and
     * have commands sent via it.
     */
    protected T mDelegate;
    private final Class<T> mClazz;

    /**
     * Initialise the instance with the given class type
     * that represents the logic delegate.
     *
     * @param clazz type of the delegate contract.
     */
    public BaseController(@NonNull Class<T> clazz) {
        mClazz = clazz;
        resetDelegate();
    }

    /**
     * Connect the logic delegate to the given delegate
     * instance, via the weak wrapper so it won't hold
     * a strong reference to it and so it also won't
     * through NPE when calling methods on the delegate
     * that is not connected to anything.
     *
     * @param delegate to assign as the logic delegate.
     */
    public void setDelegate(@Nullable T delegate) {
        mDelegate = WeakWrapper.wrap(delegate, mClazz);
    }

    @Override
    public void disconnect() {
        resetDelegate();
    }

    /**
     * Resetting the delegate basically just wraps it
     * with a weak wrapper with null as the target.
     */
    private void resetDelegate() {
        mDelegate = WeakWrapper.wrapEmpty(mClazz);
    }
}