package org.pistonworks.core.api.service;

import org.pistonworks.core.api.event.Event;
import java.util.List;
import java.util.function.Consumer;

/**
 * Enhanced event service interface for registering and managing event listeners.
 */
public interface EventService {

    /**
     * Registers an event listener object.
     * @param listener the listener object containing event handler methods
     */
    void registerListener(Object listener);

    /**
     * Unregisters an event listener object.
     * @param listener the listener object to unregister
     */
    void unregisterListener(Object listener);

    /**
     * Registers a functional event handler for a specific event type.
     * @param eventClass the class of the event to listen for
     * @param handler the handler function to execute when the event occurs
     * @param <T> the type of event
     */
    <T extends Event> void registerHandler(Class<T> eventClass, Consumer<T> handler);

    /**
     * Fires an event to all registered listeners.
     * @param event the event to fire
     * @param <T> the type of event
     * @return the event after it has been processed by all listeners
     */
    <T extends Event> T fireEvent(T event);

    /**
     * Gets a list of all registered listeners.
     * @return list of registered listener objects
     */
    List<Object> getRegisteredListeners();

    /**
     * Checks if there are any listeners registered for a specific event type.
     * @param eventClass the event class to check
     * @return true if there are listeners for this event type, false otherwise
     */
    boolean hasListeners(Class<? extends Event> eventClass);
}
