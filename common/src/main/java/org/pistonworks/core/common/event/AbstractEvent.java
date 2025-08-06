package org.pistonworks.core.common.event;

import org.pistonworks.core.api.event.Event;

/**
 * Abstract base class for events providing common functionality.
 * This reduces boilerplate for concrete event implementations.
 */
public abstract class AbstractEvent implements Event {

    private final String eventName;
    private final long timestamp;

    protected AbstractEvent() {
        this.eventName = this.getClass().getSimpleName();
        this.timestamp = System.currentTimeMillis();
    }

    protected AbstractEvent(String eventName) {
        this.eventName = eventName;
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public String getEventName() {
        return eventName;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("%s{timestamp=%d}", eventName, timestamp);
    }
}
