package io.github.marcelbraghetto.deviantartreader.framework.foundation.core;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by Marcel Braghetto on 02/12/15.
 *
 * This class is used to wrap a weak reference
 * around an interface to allow calls to be
 * made on it regardless of whether it is null
 * or not. This avoids a massive amount of
 * null checking when trying to call methods
 * on interfaces.
 *
 * This wrapper is only used in special conditions
 * where it is acceptable for it to swallow
 * some specific exceptions (such as null pointer
 * exceptions).
 */
public final class WeakWrapper {
    private WeakWrapper() { }

    /**
     * Wrap the given object with a weak wrapper that
     * is configured to have its target pointing at null.
     * This means the wrapped object can still continue
     * to call method invocations, but they will route
     * to nowhere. Typically used to 'disconnect' a wrapped
     * object from its target.
     *
     * @param clazz type to wrap.
     * @param <T> class type.
     *
     * @return wrapped object with the given class type.
     */
    public static <T> T wrapEmpty(Class<T> clazz) {
        return wrap(null, clazz);
    }

    /**
     * Wrap the given object with a weak wrapper, so from
     * that point onward it is safe to call methods on it
     * without performing null checks. However, if the user
     * of the wrapped object wants to ensure a full disconnect
     * from a wrapped object, the 'wrapEmpty' method should
     * be used to wrap the object with an empty target.
     *
     * @param target to apply the weak wrapper to.
     * @param clazz the class type of the target.
     * @param <T> the type of class.
     *
     * @return a wrapped target object.
     */
    public static <T> T wrap(T target, Class<T> clazz) {
        WeakWrapperHandler<T> handler = new WeakWrapperHandler<T>(target);
        Object proxy = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, handler);
        return clazz.cast(proxy);
    }

    /**
     * Used for handling the invocation of methods on a wrapped
     * object, by tracking the object via a weak reference and
     * only invoking a method if the reference is still alive.
     *
     * @param <T> the type of the object being wrapped.
     */
    private static class WeakWrapperHandler<T> implements InvocationHandler {
        private final WeakReference<T> mTargetReference;

        public WeakWrapperHandler(T target) {
            mTargetReference = new WeakReference<T>(target);
        }

        /**
         * Attempt to invoke the given method on the target object
         * which may or may not be still 'alive'. In addition, if
         * one of the caught exceptions occur, we are OK for it to
         * be swallowed because they are the type of exception we
         * could expect when running code on something that is
         * not in a stable state any more.
         *
         * @param proxy object being tracked.
         * @param method to invoke on the object.
         * @param args method arguments (if any) to pass in.
         *
         * @return method call result.
         */
        public Object invoke(Object proxy, Method method, Object[] args) {
            T target = mTargetReference.get();

            // Only attempt a call on the object if it isn't null
            if(target != null) {
                // Try to invoke the method, this may still fail
                // if the object state is not stable ...
                try {
                    return method.invoke(target, args);
                } catch(IllegalAccessException | NullPointerException | InvocationTargetException e) {
                    Log.d("WeakProxy", "Exception ignored");
                }
            }

            return null;
        }
    }
}