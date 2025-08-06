package org.pistonworks.core.api.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods as event handlers.
 * Methods annotated with this should have exactly one parameter of an Event type.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler
{

    /**
     * The priority of this event handler.
     *
     * @return the priority
     */
    EventPriority priority() default EventPriority.NORMAL;

    /**
     * Whether this handler should receive cancelled events.
     *
     * @return true to receive cancelled events
     */
    boolean ignoreCancelled() default true;
}
