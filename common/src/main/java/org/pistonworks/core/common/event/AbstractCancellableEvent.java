package org.pistonworks.core.common.event;

import org.pistonworks.core.api.event.Cancellable;

/**
 * Abstract base class for cancellable events.
 */
public abstract class AbstractCancellableEvent extends AbstractEvent implements Cancellable
{

    private boolean cancelled = false;

    protected AbstractCancellableEvent()
    {
        super();
    }

    protected AbstractCancellableEvent(String eventName)
    {
        super(eventName);
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    @Override
    public String toString()
    {
        return String.format("%s{timestamp=%d, cancelled=%b}", getEventName(), getTimestamp(), cancelled);
    }
}
