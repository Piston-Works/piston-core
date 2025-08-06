package org.pistonworks.core.api.service;

import org.pistonworks.core.api.plugin.PistonPlugin;

import java.util.List;

/**
 * Service for managing plugin instances
 */
public interface PluginDiscoveryService
{
    /**
     * Registers a plugin instance that has been discovered/loaded
     *
     * @param plugin The plugin instance to register
     */
    void discoverPlugin(PistonPlugin plugin);

    /**
     * Gets all currently registered plugins
     *
     * @return list of registered plugins
     */
    List<PistonPlugin> getLoadedPlugins();
}
