package org.pistonworks.core.spigot;

import org.bukkit.plugin.java.JavaPlugin;
import org.pistonworks.core.api.model.PluginContainer;

import java.io.File;

/**
 * Spigot implementation of the Piston PluginContainer interface.
 * Wraps a Bukkit JavaPlugin to provide the Piston API interface.
 */
public class SpigotPlugin implements PluginContainer
{

    private final JavaPlugin bukkitPlugin;

    /**
     * Creates a new SpigotPlugin wrapper.
     *
     * @param bukkitPlugin The JavaPlugin to wrap
     */
    public SpigotPlugin(JavaPlugin bukkitPlugin)
    {
        this.bukkitPlugin = bukkitPlugin;
    }

    @Override
    public String getName()
    {
        return bukkitPlugin.getName();
    }

    @Override
    public String getVersion()
    {
        return bukkitPlugin.getDescription().getVersion();
    }

    @Override
    public String getDescription()
    {
        return bukkitPlugin.getDescription().getDescription();
    }

    @Override
    public Class<?> getMainClass()
    {
        return bukkitPlugin.getClass();
    }

    @Override
    public File getDataFolder()
    {
        return bukkitPlugin.getDataFolder();
    }

    @Override
    public Object getInstance()
    {
        return bukkitPlugin;
    }

    /**
     * Gets the underlying Bukkit JavaPlugin instance.
     * This is used for platform-specific operations.
     *
     * @return the Bukkit JavaPlugin instance
     */
    public JavaPlugin getBukkitPlugin()
    {
        return bukkitPlugin;
    }

    /**
     * Checks if this plugin is currently enabled.
     *
     * @return true if the plugin is enabled, false otherwise
     */
    public boolean isEnabled()
    {
        return bukkitPlugin.isEnabled();
    }

    /**
     * Gets the authors of this plugin.
     *
     * @return array of author names
     */
    public String[] getAuthors()
    {
        return bukkitPlugin.getDescription().getAuthors().toArray(new String[0]);
    }
}
