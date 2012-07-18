package com.zutubi.events;

import com.zutubi.util.logging.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * The Asynchronous delegating listener, as the name suggests, is an event listener that
 * delegates the handling of the event to another event listener (the delegate) that is
 * executed on a separate thread.
 * <p/>
 * The use of this delegating listener ensures that events do not block the event dispatch
 * thread.  If this listener is single threaded, events are also guaranteed to be handled
 * in the order they were sent.
 */
public class AsynchronousDelegatingListener implements EventListener
{
    private static final Logger LOG = Logger.getLogger(AsynchronousDelegatingListener.class);

    private final EventListener delegate;
    private final String name;
    private final ExecutorService executor;

    public AsynchronousDelegatingListener(EventListener delegate, String name, ExecutorService executor)
    {
        this.delegate = delegate;
        this.name = name;
        this.executor = executor;
    }

    public AsynchronousDelegatingListener(EventListener delegate, final String name, final ThreadFactory threadFactory)
    {
        this(delegate, name, Executors.newSingleThreadExecutor(new ThreadFactory()
        {
            public Thread newThread(Runnable runnable)
            {
                Thread thread = threadFactory.newThread(runnable);
                thread.setName(name);
                return thread;
            }
        }));
    }

    public void handleEvent(final Event event)
    {
        // deletate the handling of the event to the delegate being executed on
        // a separate thread. The executor will queue the event handling until the
        // thread is available. See Executors.newSingleThreadExecutor for full
        // sematic definition.
        if (!executor.isShutdown())
        {
            executor.submit(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        delegate.handleEvent(event);
                    }
                    catch (Exception e)
                    {
                        // isolate the exceptions generated by the event handling.
                        event.addException(e);
                        LOG.warning("Exception generated by handleEvent(" + event + ") of '" + name + "'", e);
                    }
                }
            });
        }
    }

    public Class[] getHandledEvents()
    {
        return delegate.getHandledEvents();
    }

    public void stop(boolean force)
    {
        if (force)
        {
            executor.shutdownNow();
        }
        else
        {
            executor.shutdown();
            try
            {
                if (!executor.awaitTermination(600, TimeUnit.SECONDS))
                {
                    LOG.warning("Time out awaiting termination of asynchronous listener for '" + name + "'");
                }
            }
            catch (InterruptedException e)
            {
                LOG.warning(e);
            }
        }
    }
}
