package org.pistonworks.core.api;

import org.pistonworks.core.api.service.*;

/**
 * Main services interface that all platform implementations must provide.
 * This is the contract that defines what services are available across all platforms.
 */
public interface PistonCoreServices
{

    /**
     * Get the command service for registering and managing commands.
     *
     * @return the command service implementation
     */
    CommandService getCommandService();

    /**
     * Get the event service for registering and managing event listeners.
     *
     * @return the event service implementation
     */
    EventService getEventService();

    /**
     * Get the lifecycle service for managing plugin lifecycle events.
     *
     * @return the lifecycle service implementation
     */
    LifecycleService getLifecycleService();

    /**
     * Get the plugin discovery service for automatically loading plugins.
     *
     * @return the plugin discovery service implementation
     */
    PluginDiscoveryService getPluginDiscoveryService();

    /**
     * Get the logging service for logging messages.
     *
     * @return the logging service implementation
     */
    LoggingService getLoggingService();

    /**
     * Get the plugin metadata service for accessing piston-core.yml properties.
     *
     * @return the plugin metadata service implementation
     */
    PluginMetadataService getPluginMetadataService();
}
