package org.pistonworks.core.spigot;

import org.pistonworks.core.api.service.PluginMetadataService;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Spigot implementation of PluginMetadataService that reads from piston-core.yml
 */
public class SpigotPluginMetadataService implements PluginMetadataService
{
    private final Map<String, Object> metadata;
    private final org.bukkit.plugin.Plugin plugin;

    public SpigotPluginMetadataService(org.bukkit.plugin.Plugin plugin)
    {
        this.plugin = plugin;
        this.metadata = loadMetadata();
    }

    private Map<String, Object> loadMetadata()
    {
        try {
            InputStream pistonYmlStream = plugin.getResource("piston-core.yml");
            if (pistonYmlStream == null) {
                plugin.getLogger().warning("piston-core.yml not found in plugin resources");
                return new HashMap<>();
            }

            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(pistonYmlStream);
            return config != null ? config : new HashMap<>();
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load piston-core.yml: " + e.getMessage());
            return new HashMap<>();
        }
    }

    @Override
    public String getPluginName()
    {
        Object name = metadata.get("name");
        return name != null ? name.toString() : "Unknown";
    }

    @Override
    public String getPluginVersion()
    {
        Object version = metadata.get("version");
        return version != null ? version.toString() : "Unknown";
    }

    @Override
    public String getPluginDescription()
    {
        Object description = metadata.get("description");
        return description != null ? description.toString() : "No description";
    }

    @Override
    public String getPluginMainClass()
    {
        Object main = metadata.get("main");
        return main != null ? main.toString() : "Unknown";
    }

    @Override
    public String getPluginAuthors()
    {
        Object authors = metadata.get("authors");
        return authors != null ? authors.toString() : "Unknown";
    }

    @Override
    public Optional<Object> getProperty(String key)
    {
        return Optional.ofNullable(metadata.get(key));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key, T defaultValue)
    {
        Object value = metadata.get(key);
        if (value != null) {
            try {
                return (T) value;
            } catch (ClassCastException e) {
                plugin.getLogger().warning("Property '" + key + "' cannot be cast to expected type, returning default value");
            }
        }
        return defaultValue;
    }

    @Override
    public Map<String, Object> getAllProperties()
    {
        return new HashMap<>(metadata);
    }

    @Override
    public boolean hasProperty(String key)
    {
        return metadata.containsKey(key);
    }
}
