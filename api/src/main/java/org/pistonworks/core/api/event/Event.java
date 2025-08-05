package org.pistonworks.core.api.event;

/**
 * Base class for all events in the Piston Core system.
 * This provides common functionality that all events share.
 */
public abstract class Event {

    private boolean cancelled = false;
    private final long timestamp;

    /**
     * Creates a new event with the current timestamp.
     */
    protected Event() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Gets the timestamp when this event was created.
     * @return the event creation timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Checks if this event has been cancelled.
     * @return true if the event is cancelled, false otherwise
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancelled state of this event.
     * @param cancelled true to cancel the event, false to uncancel it
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
