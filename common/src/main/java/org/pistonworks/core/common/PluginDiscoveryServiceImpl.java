package org.pistonworks.core.common;

import org.pistonworks.core.api.plugin.PistonPlugin;
import org.pistonworks.core.api.service.PluginDiscoveryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Common implementation of PluginDiscoveryService that can be used across platforms
 */
public class PluginDiscoveryServiceImpl implements PluginDiscoveryService
{
    private final List<PistonPlugin> loadedPlugins = new ArrayList<>();

    @Override
    public void discoverPlugin(PistonPlugin plugin) {
        if (!loadedPlugins.contains(plugin)) {
            loadedPlugins.add(plugin);
        }
    }

    @Override
    public List<PistonPlugin> getLoadedPlugins()
    {
        return new ArrayList<>(loadedPlugins);
    }
}
