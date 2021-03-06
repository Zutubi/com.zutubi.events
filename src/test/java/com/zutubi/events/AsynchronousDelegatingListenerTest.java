package com.zutubi.events;

import junit.framework.TestCase;

import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class AsynchronousDelegatingListenerTest extends TestCase
{
    private Semaphore eventSemaphore = new Semaphore(0);
    private Semaphore doneSemaphore = new Semaphore(0);

    public void testEventsExecutedOnSeparateThread() throws InterruptedException
    {
        WaitListener delegate = new WaitListener();
        AsynchronousDelegatingListener l = new AsynchronousDelegatingListener(delegate, "test", Executors.defaultThreadFactory());

        l.handleEvent(new Event(this));
        // the listener thread is now waiting for the semaphore to release.
        // we can only release it if it is indeed in a separate thread.
        eventSemaphore.release();

        assertTrue(doneSemaphore.tryAcquire(10, TimeUnit.SECONDS));
        assertTrue(delegate.acquired);
    }

    private class WaitListener extends AllEventListener
    {
        private boolean acquired;

        public void handleEvent(Event evt)
        {
            try
            {
                acquired = eventSemaphore.tryAcquire(10, TimeUnit.SECONDS);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            doneSemaphore.release();
        }
    }
}
