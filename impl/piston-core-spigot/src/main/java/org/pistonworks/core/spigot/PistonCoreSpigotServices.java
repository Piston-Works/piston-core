package org.pistonworks.core.spigot;

import org.bukkit.plugin.java.JavaPlugin;
import org.pistonworks.core.api.PistonCoreServices;
import org.pistonworks.core.api.service.*;
import org.pistonworks.core.common.PluginDiscoveryServiceImpl;

/**
 * Spigot implementation of PistonCoreServices.
 * Provides all core services for the Spigot platform implementation.
 */
public final class PistonCoreSpigotServices implements PistonCoreServices
{

    private final JavaPlugin plugin;
    private final SpigotPlugin spigotPlugin;
    private final CommandService commandService;
    private final EventService eventService;
    private final LifecycleService lifecycleService;
    private final PluginDiscoveryService pluginDiscoveryService;
    private final LoggingService loggingService;
    private final PluginMetadataService pluginMetadataService;

    /**
     * Creates a new PistonCoreSpigotServices instance.
     *
     * @param plugin The JavaPlugin instance
     */
    public PistonCoreSpigotServices(JavaPlugin plugin)
    {
        this.plugin = plugin;
        this.spigotPlugin = new SpigotPlugin(plugin);
        this.commandService = new SpigotCommandServiceImpl(plugin);
        this.eventService = new SpigotEventServiceImpl(spigotPlugin);
        this.lifecycleService = new SpigotLifecycleServiceImpl(plugin);
        this.loggingService = new SpigotLoggingService(plugin);
        this.pluginMetadataService = new SpigotPluginMetadataService(plugin);

        // Initialize plugin discovery service - no longer needs directory parameters
        // since it just manages plugin instances, not file loading
        this.pluginDiscoveryService = new PluginDiscoveryServiceImpl();
    }

    /**
     * Additional constructor for reflection-based initialization from API.
     *
     * @param plugin The plugin instance (must be a JavaPlugin)
     */
    public PistonCoreSpigotServices(Object plugin)
    {
        this((JavaPlugin) plugin);
    }

    @Override
    public CommandService getCommandService()
    {
        return this.commandService;
    }

    @Override
    public EventService getEventService()
    {
        return this.eventService;
    }

    @Override
    public LifecycleService getLifecycleService()
    {
        return this.lifecycleService;
    }

    @Override
    public PluginDiscoveryService getPluginDiscoveryService()
    {
        return this.pluginDiscoveryService;
    }

    @Override
    public LoggingService getLoggingService()
    {
        return this.loggingService;
    }

    @Override
    public PluginMetadataService getPluginMetadataService()
    {
        return this.pluginMetadataService;
    }
}