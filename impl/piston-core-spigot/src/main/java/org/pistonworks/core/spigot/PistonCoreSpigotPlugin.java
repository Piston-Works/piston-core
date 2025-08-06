package org.pistonworks.core.spigot;

import org.bukkit.plugin.java.JavaPlugin;
import org.pistonworks.core.api.PistonCore;
import org.pistonworks.core.api.service.CommandService;
import org.pistonworks.core.api.service.EventService;

public final class PistonCoreSpigotPlugin extends JavaPlugin
{

    // A static reference to the main plugin instance. This is a common pattern for easy access.
    private static PistonCoreSpigotPlugin instance;
    private final PistonCoreSpigotServices services = new PistonCoreSpigotServices(this);
    private SpigotPlugin spigotPlugin;

    public static PistonCoreSpigotPlugin getInstance()
    {
        return instance;
    }

    @Override
    public void onEnable()
    {
        instance = this;

        // Initialize Piston Core services first
        PistonCore.setServices(services);

        // Create SpigotPlugin wrapper
        spigotPlugin = new SpigotPlugin(this);

        getLogger().info("Piston Core Spigot implementation enabled!");
    }

    @Override
    public void onDisable()
    {
        // No need to call lifecycle service here - individual plugins handle their own lifecycle
        instance = null;
        getLogger().info("Piston Core Spigot implementation disabled!");
    }

    public CommandService getCommandService()
    {
        return services.getCommandService();
    }

    public EventService getEventService()
    {
        return services.getEventService();
    }
}