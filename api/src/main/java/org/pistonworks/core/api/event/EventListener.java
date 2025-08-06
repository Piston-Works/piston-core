package org.pistonworks.core.api.event;

/**
 * Functional interface for handling events.
 *
 * @param <T> the type of event this listener handles
 */
@FunctionalInterface
public interface EventListener<T extends Event>
{

    /**
     * Called when an event occurs.
     *
     * @param event the event that occurred
     */
    void handle(T event);
}
