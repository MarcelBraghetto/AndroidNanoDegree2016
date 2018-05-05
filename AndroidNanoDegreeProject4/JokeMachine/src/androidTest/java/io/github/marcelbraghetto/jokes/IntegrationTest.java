package io.github.marcelbraghetto.jokes;

import android.test.AndroidTestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import io.github.marcelbraghetto.jokemachine.lib.android.api.JokesApiEvent;
import io.github.marcelbraghetto.jokemachine.lib.android.api.JokesApiManager;

/**
 * Created by Marcel Braghetto on 24/01/16.
 *
 * Example integration test which will expect the local Google App Engine server to be running
 * before being executed.
 *
 * This test has been written to address the requirements of the assignment project to write a test
 * that performs an actual end to end connection to a real Google App Engine server (in our case
 * hosted locally).
 *
 * The negative condition is not covered in this test suite (a failed connection etc) only the
 * positive successful response.
 */
public class IntegrationTest extends AndroidTestCase {
    private CountDownLatch mLatch;
    private JokesApiEvent mLastApiEvent;

    /**
     * Before each test case, register ourselves with the event bus and create a new count down
     * latch to cope with async task and event bus communication.
     */
    public void setUp() {
        mLatch = new CountDownLatch(1);
        EventBus.getDefault().register(this);
    }

    /**
     * Make sure we unregister from the event bus after each test case.
     */
    public void tearDown() {
        EventBus.getDefault().unregister(this);
    }

    /**
     * Test the ability to successfully retrieve a joke from our local Google App Engine server.
     *
     * This test requires that the local server is running already.
     *
     * Due to the implementation using the Event Bus and hiding the actual AsyncTask implementation
     * behind the JokesApiManager, we need to use a CountDownLatch to wait for the async operation
     * to complete, and listen on the event bus for the completion events.
     *
     * @throws InterruptedException
     */
    public void testFetchJokeFromLocalServer() throws InterruptedException {
        // Run
        JokesApiManager.Instance.fetchJoke("test owner");

        // Allow our countdown latch to wait for up to 5 seconds.
        mLatch.await(5, TimeUnit.SECONDS);

        // Verify

        // The owner of the event should be the same as when we fetched the joke
        assertEquals(mLastApiEvent.getOwnerId(), "test owner");

        // The result state in the event should be 'success'
        assertEquals(mLastApiEvent.getResult(), JokesApiEvent.Result.Success);

        // The joke instance itself in the event should not be null
        assertNotNull(mLastApiEvent.getJoke());

        // The question and answer within the joke should be a non zero length string
        assertTrue(mLastApiEvent.getJoke().getQuestion().length() > 0);
        assertTrue(mLastApiEvent.getJoke().getAnswer().length() > 0);
    }

    /**
     * This event will be received after the async task has completed and will contain the result
     * of the Api request that was made.
     * @param event holding the result of the Api request.
     */
    @SuppressWarnings("unused")
    public void onEvent(JokesApiEvent event) {
        // We need to store the result in this test class so it is available to test cases as they
        // run. We also need to count down the latch so it knows that the await sequence should end
        // and allow the test cases to proceed to their verification clauses.
        mLastApiEvent = event;
        mLatch.countDown();
    }
}
