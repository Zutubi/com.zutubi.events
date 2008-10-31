package com.zutubi.events;

import java.util.List;

/**
 * <class-comment/>
 */
public interface EventDispatcher
{
    void dispatch(Event evt, List<EventListener> listeners);
}
