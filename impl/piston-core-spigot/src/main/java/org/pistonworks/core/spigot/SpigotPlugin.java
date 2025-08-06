package org.pistonworks.core.spigot;

import org.bukkit.plugin.java.JavaPlugin;
import org.pistonworks.core.api.model.Plugin;

import java.io.File;

/**
 * Spigot implementation of the Piston Plugin interface.
 * Wraps a Bukkit JavaPlugin to provide the Piston API interface.
 */
public class SpigotPlugin implements Plugin {

    private final JavaPlugin bukkitPlugin;

    public SpigotPlugin(JavaPlugin bukkitPlugin) {
        this.bukkitPlugin = bukkitPlugin;
    }

    @Override
    public String getName() {
        return bukkitPlugin.getName();
    }

    @Override
    public String getVersion() {
        return bukkitPlugin.getDescription().getVersion();
    }

    @Override
    public String getDescription() {
        return bukkitPlugin.getDescription().getDescription();
    }

    @Override
    public Class<?> getMainClass() {
        return bukkitPlugin.getClass();
    }

    @Override
    public File getDataFolder() {
        return bukkitPlugin.getDataFolder();
    }

    @Override
    public boolean isEnabled() {
        return bukkitPlugin.isEnabled();
    }

    @Override
    public String[] getAuthors() {
        return bukkitPlugin.getDescription().getAuthors().toArray(new String[0]);
    }

    /**
     * Gets the underlying Bukkit JavaPlugin instance.
     * This is used for platform-specific operations.
     * @return the wrapped JavaPlugin
     */
    public JavaPlugin getBukkitPlugin() {
        return bukkitPlugin;
    }
}
