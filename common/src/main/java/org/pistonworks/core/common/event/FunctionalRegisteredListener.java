package org.pistonworks.core.common.event;

import org.pistonworks.core.api.event.Event;
import org.pistonworks.core.api.event.EventListener;
import org.pistonworks.core.api.event.EventPriority;

/**
 * Wrapper for functional event listeners.
 */
public class FunctionalRegisteredListener<T extends Event> extends RegisteredListener
{

    private final EventListener<T> listener;

    public FunctionalRegisteredListener(EventListener<T> listener, EventPriority priority)
    {
        super(listener, null, priority, true);
        this.listener = listener;
    }

    @Override
    public void call(Event event)
    {
        @SuppressWarnings("unchecked")
        T typedEvent = (T) event;
        listener.handle(typedEvent);
    }
}
