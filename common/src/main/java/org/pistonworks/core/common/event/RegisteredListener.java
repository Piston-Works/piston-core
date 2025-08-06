package org.pistonworks.core.common.event;

import org.pistonworks.core.api.event.Event;
import org.pistonworks.core.api.event.EventPriority;

import java.lang.reflect.Method;

/**
 * Represents a registered event listener with metadata.
 */
public class RegisteredListener
{

    private final Object listener;
    private final Method method;
    private final EventPriority priority;
    private final boolean ignoreCancelled;

    public RegisteredListener(Object listener, Method method, EventPriority priority, boolean ignoreCancelled)
    {
        this.listener = listener;
        this.method = method;
        this.priority = priority;
        this.ignoreCancelled = ignoreCancelled;
        this.method.setAccessible(true);
    }

    public void call(Event event) throws Exception
    {
        method.invoke(listener, event);
    }

    public EventPriority getPriority()
    {
        return priority;
    }

    public boolean isIgnoreCancelled()
    {
        return ignoreCancelled;
    }

    public Object getListener()
    {
        return listener;
    }

    public Method getMethod()
    {
        return method;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof RegisteredListener)) return false;
        RegisteredListener other = (RegisteredListener) obj;
        return listener.equals(other.listener) && method.equals(other.method);
    }

    @Override
    public int hashCode()
    {
        return listener.hashCode() * 31 + method.hashCode();
    }
}
