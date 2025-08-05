package org.pistonworks.core.api;

import org.pistonworks.core.api.service.CommandService;
import org.pistonworks.core.api.service.EventService;
import org.pistonworks.core.api.service.LifecycleService;

/**
 * Main services interface that all platform implementations must provide.
 * This is the contract that defines what services are available across all platforms.
 */
public interface PistonCoreServices {

    /**
     * Get the command service for registering and managing commands.
     * @return the command service implementation
     */
    CommandService getCommandService();

    /**
     * Get the event service for registering and managing event listeners.
     * @return the event service implementation
     */
    EventService getEventService();

    /**
     * Get the lifecycle service for managing plugin lifecycle events.
     * @return the lifecycle service implementation
     */
    LifecycleService getLifecycleService();
}
