package org.pistonworks.core.spigot;

import org.bukkit.plugin.java.JavaPlugin;
import org.pistonworks.core.api.service.CommandService;
import org.pistonworks.core.api.service.EventService;
import org.pistonworks.core.api.service.LifecycleService;

public final class PistonCoreSpigotPlugin extends JavaPlugin {

    // A static reference to the main plugin instance. This is a common pattern for easy access.
    private static PistonCoreSpigotPlugin instance;
    private final PistonCoreSpigotServices services = new PistonCoreSpigotServices(this);
    private SpigotPlugin spigotPlugin;

    @Override
    public void onEnable() {
        instance = this;

        // Create SpigotPlugin
        spigotPlugin = new SpigotPlugin(this);

        // Call the lifecycle hook to let the core know it's loaded.
        services.getLifecycleService().onEnable(spigotPlugin);
        getLogger().info("Piston Core Spigot implementation enabled!");
    }

    @Override
    public void onDisable() {
        // Call the lifecycle hook to let the core know it's being unloaded.
        services.getLifecycleService().onDisable(spigotPlugin);
        instance = null;
        getLogger().info("Piston Core Spigot implementation disabled!");
    }

    public static PistonCoreSpigotPlugin getInstance() {
        return instance;
    }

    public CommandService getCommandService() {
        return services.getCommandService();
    }

    public EventService getEventService() {
        return services.getEventService();
    }
}