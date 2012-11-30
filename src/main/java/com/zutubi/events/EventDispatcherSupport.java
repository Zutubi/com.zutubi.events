package com.zutubi.events;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper base class for implementing dispatchers.
 */
public abstract class EventDispatcherSupport implements EventDispatcher
{
    private static final Logger LOG = Logger.getLogger(EventDispatcherSupport.class.getName());

    /**
     * Safely dispatches the given event to the given listeners, isolating any exceptions thrown by
     * a listener.  Also handles registering the exceptions against the event.
     *
     * @param event the event to dispatch
     * @param listeners listeners to dispatch the event to
     */
    protected void safeDispatch(final Event event, final List<EventListener> listeners)
    {
        for (EventListener listener: listeners)
        {
            try
            {
                listener.handleEvent(event);
            }
            catch (Exception e)
            {
                event.addException(e);
                LOG.log(Level.WARNING, "Exception generated by " + listener + ".handleEvent(" + event + ")", e);
            }
        }
    }
}