package org.pistonworks.core.api.event;

/**
 * Represents the priority of an event listener.
 * Higher priority listeners are called first.
 */
public enum EventPriority
{
    LOWEST(0),
    LOW(1),
    NORMAL(2),
    HIGH(3),
    HIGHEST(4),
    MONITOR(5); // For monitoring only, should not modify events

    private final int value;

    EventPriority(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
