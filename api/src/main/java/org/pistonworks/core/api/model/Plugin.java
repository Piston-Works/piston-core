package org.pistonworks.core.api.model;

import java.io.File;

/**
 * Represents a plugin in the system, abstracting platform-specific plugin implementations.
 */
public interface Plugin
{

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
     * Checks if this plugin is currently enabled.
     *
     * @return true if the plugin is enabled, false otherwise
     */
    boolean isEnabled();

    /**
     * Gets the authors of this plugin.
     *
     * @return array of author names
     */
    String[] getAuthors();
}
