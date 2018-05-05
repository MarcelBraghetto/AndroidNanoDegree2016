package io.github.marcelbraghetto.dailydeviations.framework.foundation.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.github.marcelbraghetto.dailydeviations.features.application.MainApp;

/**
 * Created by Marcel Braghetto on 02/12/15.
 *
 * Base class for view model classes.
 */
public abstract class BaseViewModel<T> implements Destroyable {
    /**
     * This field can be accessed by the child class and
     * have commands sent via it.
     */
    protected T mActionDelegate;
    private final Class<T> mClazz;
    private final String mInstanceId;

    /**
     * Initialise the instance with the given class type
     * that represents the logic delegate.
     *
     * @param clazz type of the delegate contract.
     */
    public BaseViewModel(@NonNull Class<T> clazz) {
        mClazz = clazz;
        resetDelegate();
        mInstanceId = MainApp.getDagger().getAppStringsProvider().generateGUID();
    }

    /**
     * Connect the logic actionDelegate to the given actionDelegate
     * instance, via the weak wrapper so it won't hold
     * a strong reference to it and so it also won't
     * through NPE when calling methods on the actionDelegate
     * that is not connected to anything.
     *
     * @param actionDelegate to assign as the logic actionDelegate.
     */
    public void setActionDelegate(@Nullable T actionDelegate) {
        mActionDelegate = WeakWrapper.wrap(actionDelegate, mClazz);
    }

    @Override
    public void destroy() {
        resetDelegate();
    }

    @NonNull
    protected String getInstanceId() {
        return mInstanceId;
    }

    /**
     * Resetting the delegate basically just wraps it
     * with a weak wrapper with null as the target.
     */
    private void resetDelegate() {
        mActionDelegate = WeakWrapper.wrapEmpty(mClazz);
    }
}