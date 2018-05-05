package com.lilarcor.popularmovies.framework.foundation.eventbus;

import com.lilarcor.popularmovies.BuildConfig;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusEvent;
import com.lilarcor.popularmovies.framework.foundation.eventbus.contracts.EventBusSubscriber;
import com.lilarcor.popularmovies.framework.foundation.threadutils.contracts.ThreadUtilsProvider;
import com.lilarcor.popularmovies.testutils.RobolectricProperties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

/**
 * Created by Marcel Braghetto on 25/07/15.
 */
@Config(constants = BuildConfig.class, sdk = RobolectricProperties.EMULATE_SDK)
@RunWith(RobolectricGradleTestRunner.class)
public class DefaultEventBusProviderTest {
    @Mock ThreadUtilsProvider mThreadUtilsProvider;
    @Mock EventBusSubscriber mEventBusSubscriber;

    private DefaultEventBusProvider mProvider;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mProvider = new DefaultEventBusProvider(mThreadUtilsProvider);
    }

    @Test
    public void testSubscribe() {
        // Run
        mProvider.subscribe(mEventBusSubscriber);

        // Verify
        verify(mThreadUtilsProvider).runOnMainThread(any(Runnable.class));
    }

    @Test
    public void testUnsubscribe() {
        // Run
        mProvider.unsubscribe(mEventBusSubscriber);

        // Verify
        verify(mThreadUtilsProvider).runOnMainThread(any(Runnable.class));
    }

    @Test
    public void testPostEvent() {
        // Run
        mProvider.postEvent(new EventBusEvent() { });

        // Verify
        verify(mThreadUtilsProvider).runOnMainThread(any(Runnable.class));
    }
}