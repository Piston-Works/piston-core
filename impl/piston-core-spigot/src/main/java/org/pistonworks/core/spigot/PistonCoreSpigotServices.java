package org.pistonworks.core.spigot;

import org.bukkit.plugin.java.JavaPlugin;
import org.pistonworks.core.api.PistonCoreServices;
import org.pistonworks.core.api.service.CommandService;
import org.pistonworks.core.api.service.EventService;
import org.pistonworks.core.api.service.LifecycleService;
import org.pistonworks.core.api.service.PluginDiscoveryService;
import org.pistonworks.core.common.PluginDiscoveryServiceImpl;

import java.io.File;

public final class PistonCoreSpigotServices implements PistonCoreServices
{

    private final JavaPlugin plugin;
    private final SpigotPlugin spigotPlugin;
    private final CommandService commandService;
    private final EventService eventService;
    private final LifecycleService lifecycleService;
    private final PluginDiscoveryService pluginDiscoveryService;

    public PistonCoreSpigotServices(JavaPlugin plugin)
    {
        this.plugin = plugin;
        this.spigotPlugin = new SpigotPlugin(plugin);
        this.commandService = new SpigotCommandServiceImpl(plugin);
        this.eventService = new SpigotEventServiceImpl(spigotPlugin);
        this.lifecycleService = new SpigotLifecycleServiceImpl(plugin);

        // Initialize plugin discovery service for the current bundled plugin
        // We use the plugin's data folder as both plugins dir and data dir since we're only handling one plugin
        File pluginDataFolder = plugin.getDataFolder();
        this.pluginDiscoveryService = new PluginDiscoveryServiceImpl(pluginDataFolder, pluginDataFolder);
    }

    // Additional constructor for reflection-based initialization from API
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
}