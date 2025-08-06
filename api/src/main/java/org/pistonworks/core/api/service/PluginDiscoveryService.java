package org.pistonworks.core.api.service;

import org.pistonworks.core.api.model.PistonPlugin;
import org.pistonworks.core.api.model.PluginMetadata;

import java.io.File;
import java.util.List;

/**
 * Service for discovering and loading plugins automatically using piston-core.yml metadata
 */
public interface PluginDiscoveryService
{
    /**
     * Discovers all plugins in the plugins directory by scanning for piston-core.yml files
     *
     * @param pluginsDirectory the directory to scan for plugins
     * @return list of discovered plugin metadata
     */
    List<PluginMetadata> discoverPlugins(File pluginsDirectory);

    /**
     * Loads a plugin from its metadata and JAR file
     *
     * @param metadata the plugin metadata
     * @param jarFile the plugin JAR file
     * @return the loaded plugin instance
     * @throws Exception if the plugin cannot be loaded
     */
    PistonPlugin loadPlugin(PluginMetadata metadata, File jarFile) throws Exception;

    /**
     * Parses plugin metadata from a piston-core.yml file
     *
     * @param yamlContent the YAML content as a string
     * @return the parsed plugin metadata
     * @throws Exception if the YAML cannot be parsed
     */
    PluginMetadata parseMetadata(String yamlContent) throws Exception;

    /**
     * Gets all currently loaded plugins
     *
     * @return list of loaded plugins
     */
    List<PistonPlugin> getLoadedPlugins();

    /**
     * Auto-discovers and initializes the current plugin that contains Piston Core.
     * This is used when Piston Core is bundled within a plugin JAR.
     *
     * @throws Exception if the current plugin cannot be discovered or initialized
     */
    void autoDiscoverCurrentPlugin() throws Exception;
}
