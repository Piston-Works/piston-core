package org.pistonworks.core.api.model;

import java.io.File;

/**
 * Represents a container for a plugin, holding its metadata and instance.
 */
public interface PluginContainer {

    /**
     * Gets the name of this plugin.
     *
     * @return the plugin's name
     */
    String getName();

    /**
     * Gets the version of this plugin.
     *
     * @return the plugin's version
     */
    String getVersion();

    /**
     * Gets the description of this plugin.
     *
     * @return the plugin's description
     */
    String getDescription();

    /**
     * Gets the main class of this plugin.
     *
     * @return the plugin's main class
     */
    Class<?> getMainClass();

    /**
     * Gets the data folder for this plugin.
     *
     * @return the plugin's data folder
     */
    File getDataFolder();

    /**
     * Gets the instance of the plugin.
     *
     * @return the plugin instance
     */
    Object getInstance();
}

