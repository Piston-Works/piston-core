package org.pistonworks.core.common.event;

import org.pistonworks.core.api.event.Event;
import org.pistonworks.core.api.service.EventService;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

/**
 * Event bus utility that provides additional functionality for event management.
 * This demonstrates how easy it is to extend the event system with new features.
 */
public class EventBus {

    private final EventService eventService;
    private final List<EventFilter> filters = new CopyOnWriteArrayList<>();

    public EventBus(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Fires an event through the bus with filtering support.
     * @param event the event to fire
     * @param <T> the event type
     * @return the event (potentially modified or cancelled)
     */
    public <T extends Event> T fire(T event) {
        // Apply filters before firing
        for (EventFilter filter : filters) {
            if (!filter.shouldFire(event)) {
                return event; // Event was filtered out
            }
        }

        return eventService.fireEvent(event);
    }

    /**
     * Adds a filter that can prevent events from being fired.
     * @param filter the filter to add
     */
    public void addFilter(EventFilter filter) {
        filters.add(filter);
    }

    /**
     * Creates a filter for a specific event type.
     * @param eventClass the event class to filter
     * @param predicate the filter condition
     * @param <T> the event type
     */
    public <T extends Event> void addFilter(Class<T> eventClass, Predicate<T> predicate) {
        addFilter(event -> {
            if (eventClass.isInstance(event)) {
                @SuppressWarnings("unchecked")
                T typedEvent = (T) event;
                return predicate.test(typedEvent);
            }
            return true; // Allow other event types
        });
    }

    /**
     * Delegates to the underlying event service for registration.
     */
    public void registerListener(Object listener) {
        eventService.registerListener(listener);
    }

    /**
     * Functional interface for event filtering.
     */
    @FunctionalInterface
    public interface EventFilter {
        /**
         * Determines if an event should be fired.
         * @param event the event to check
         * @return true if the event should be fired, false to filter it out
         */
        boolean shouldFire(Event event);
    }
}
