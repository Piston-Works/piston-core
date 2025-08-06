package org.pistonworks.core.common.event;

/**
 * Builder utility for creating new event types with less boilerplate.
 * This makes it easy to add new events without creating separate classes for simple events.
 */
public class SimpleEvent extends AbstractEvent
{

    private final String eventName;
    private Object data;

    private SimpleEvent(String eventName)
    {
        this.eventName = eventName;
    }

    /**
     * Creates a new simple event builder.
     *
     * @param eventName the name of the event
     * @return a new builder instance
     */
    public static SimpleEvent create(String eventName)
    {
        return new SimpleEvent(eventName);
    }

    /**
     * Attaches data to this event.
     *
     * @param data the data to attach
     * @return this event for chaining
     */
    public SimpleEvent withData(Object data)
    {
        this.data = data;
        return this;
    }

    /**
     * Gets the data attached to this event.
     *
     * @param <T> the expected data type
     * @return the attached data
     */
    @SuppressWarnings("unchecked")
    public <T> T getData()
    {
        return (T) data;
    }

    @Override
    public String getEventName()
    {
        return eventName;
    }

    /**
     * Creates a cancellable version of this event.
     *
     * @return a cancellable simple event
     */
    public CancellableSimpleEvent cancellable()
    {
        return new CancellableSimpleEvent(eventName, data);
    }

    /**
     * Cancellable version of SimpleEvent
     */
    public static class CancellableSimpleEvent extends AbstractCancellableEvent
    {
        private final String eventName;
        private Object data;

        private CancellableSimpleEvent(String eventName, Object data)
        {
            this.eventName = eventName;
            this.data = data;
        }

        @Override
        public String getEventName()
        {
            return eventName;
        }

        public CancellableSimpleEvent withData(Object data)
        {
            this.data = data;
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> T getData()
        {
            return (T) data;
        }
    }
}
