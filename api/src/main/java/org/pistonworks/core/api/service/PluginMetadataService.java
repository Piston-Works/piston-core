package org.pistonworks.core.api.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for accessing plugin metadata from piston-core.yml
 */
public interface PluginMetadataService
{
    /**
     * Gets the plugin name from piston-core.yml
     *
     * @return the plugin name
     */
    String getPluginName();

    /**
     * Gets the plugin version from piston-core.yml
     *
     * @return the plugin version
     */
    String getPluginVersion();

    /**
     * Gets the plugin description from piston-core.yml
     *
     * @return the plugin description
     */
    String getPluginDescription();

    /**
     * Gets the plugin main class from piston-core.yml
     *
     * @return the main class name
     */
    String getPluginMainClass();

    /**
     * Gets the plugin authors from piston-core.yml
     *
     * @return the plugin authors as a list
     */
    List<String> getPluginAuthors();

    /**
     * Gets the plugin authors from piston-core.yml as a formatted string
     *
     * @return the plugin authors formatted as a comma-separated string
     */
    String getPluginAuthorsString();

    /**
     * Gets a specific property from piston-core.yml
     *
     * @param key the property key
     * @return the property value, or empty if not found
     */
    Optional<Object> getProperty(String key);

    /**
     * Gets a specific property from piston-core.yml with a default value
     *
     * @param key          the property key
     * @param defaultValue the default value if not found
     * @param <T>          the type of the value
     * @return the property value or default value
     */
    <T> T getProperty(String key, T defaultValue);

    /**
     * Gets all metadata properties
     *
     * @return a map of all properties from piston-core.yml
     */
    Map<String, Object> getAllProperties();

    /**
     * Checks if a property exists in piston-core.yml
     *
     * @param key the property key
     * @return true if the property exists
     */
    boolean hasProperty(String key);
}
