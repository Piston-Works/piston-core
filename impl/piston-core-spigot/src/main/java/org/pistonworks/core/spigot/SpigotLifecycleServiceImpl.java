package org.pistonworks.core.spigot;

import org.bukkit.plugin.java.JavaPlugin;
import org.pistonworks.core.api.plugin.PistonPlugin;
import org.pistonworks.core.api.service.LifecycleService;

/**
 * Spigot implementation of the LifecycleService.
 * Manages plugin lifecycle events for the Spigot platform.
 */
public class SpigotLifecycleServiceImpl implements LifecycleService
{

    private final JavaPlugin plugin;

    public SpigotLifecycleServiceImpl(JavaPlugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onEnable(PistonPlugin plugin)
    {
        this.plugin.getLogger().info("Plugin enabled: " + plugin.getClass().getSimpleName());
        // Additional enable logic can be added here
    }

    @Override
    public void onDisable(PistonPlugin plugin)
    {
        this.plugin.getLogger().info("Plugin disabled: " + plugin.getClass().getSimpleName());
        // Additional disable logic can be added here
    }

    /**
     * Called when the Piston Core system is loaded.
     */
    public void onLoad()
    {
        plugin.getLogger().info("Piston Core system loaded on Spigot");
    }

    /**
     * Called when the Piston Core system is unloaded.
     */
    public void onUnload()
    {
        plugin.getLogger().info("Piston Core system unloaded from Spigot");
    }
}
