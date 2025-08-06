package org.pistonworks.core.api.event;

/**
 * Base interface for all events in the Piston Core system.
 * Events are immutable data objects that represent something that has happened or is about to happen.
 */
public interface Event {

    /**
     * Gets the name of this event type.
     * @return the event name
     */
    String getEventName();

    /**
     * Gets the timestamp when this event was created.
     * @return the creation timestamp in milliseconds
     */
    long getTimestamp();

    /**
     * Checks if this event can be cancelled.
     * @return true if the event is cancellable
     */
    default boolean isCancellable() {
        return this instanceof Cancellable;
    }
}
