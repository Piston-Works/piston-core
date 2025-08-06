package org.pistonworks.core.common.event;

import org.pistonworks.core.api.event.Event;
import org.pistonworks.core.api.event.EventListener;

/**
 * Utility class for creating event listeners with fluent API.
 * This makes it easy to register listeners without creating separate classes.
 */
public class EventListeners
{

    /**
     * Creates a builder for registering event listeners.
     *
     * @param eventService the event service to register with
     * @return a new builder instance
     */
    public static Builder using(org.pistonworks.core.api.service.EventService eventService)
    {
        return new Builder(eventService);
    }

    public static class Builder
    {
        private final org.pistonworks.core.api.service.EventService eventService;

        private Builder(org.pistonworks.core.api.service.EventService eventService)
        {
            this.eventService = eventService;
        }

        /**
         * Registers a listener for the specified event type.
         *
         * @param eventClass the event class
         * @param listener   the listener function
         * @param <T>        the event type
         * @return this builder for chaining
         */
        public <T extends Event> Builder listen(Class<T> eventClass, EventListener<T> listener)
        {
            eventService.registerListener(eventClass, listener);
            return this;
        }

        /**
         * Registers multiple listeners at once using method references.
         *
         * @param listenerObject object containing @EventHandler methods
         * @return this builder for chaining
         */
        public Builder listen(Object listenerObject)
        {
            eventService.registerListener(listenerObject);
            return this;
        }
    }
}
