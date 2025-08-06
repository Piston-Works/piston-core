package org.pistonworks.core.api.service;

import org.pistonworks.core.api.event.Event;
import org.pistonworks.core.api.event.EventListener;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service for managing event registration and firing.
 * This service provides a clean API for event handling while keeping implementation details
 * in the common and implementation layers.
 */
public interface EventService {

    /**
     * Registers an object containing event handler methods.
     * Methods annotated with @EventHandler will be automatically registered.
     * @param listener the listener object
     */
    void registerListener(Object listener);

    /**
     * Unregisters all event handlers from the given listener object.
     * @param listener the listener object to unregister
     */
    void unregisterListener(Object listener);

    /**
     * Registers a functional event listener for a specific event type.
     * @param eventClass the class of event to listen for
     * @param listener the listener function
     * @param <T> the event type
     */
    <T extends Event> void registerListener(Class<T> eventClass, EventListener<T> listener);

    /**
     * Fires an event synchronously to all registered listeners.
     * @param event the event to fire
     * @param <T> the event type
     * @return the event (potentially modified by listeners)
     */
    <T extends Event> T fireEvent(T event);

    /**
     * Fires an event asynchronously to all registered listeners.
     * @param event the event to fire
     * @param <T> the event type
     * @return a CompletableFuture containing the event result
     */
    <T extends Event> CompletableFuture<T> fireEventAsync(T event);

    /**
     * Gets the number of registered listeners for a specific event type.
     * @param eventClass the event class
     * @return the number of listeners
     */
    int getListenerCount(Class<? extends Event> eventClass);

    /**
     * Unregisters all event listeners.
     */
    void unregisterAll();
}
