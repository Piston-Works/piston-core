package org.pistonworks.core.api.event;

/**
 * Interface for events that can be cancelled.
 * When an event is cancelled, the action it represents should not occur.
 */
public interface Cancellable {

    /**
     * Gets the cancellation state of this event.
     * @return true if this event is cancelled
     */
    boolean isCancelled();

    /**
     * Sets the cancellation state of this event.
     * @param cancelled true if you wish to cancel this event
     */
    void setCancelled(boolean cancelled);
}
