package org.pistonworks.core.spigot;

import org.pistonworks.core.api.service.PluginMetadataService;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Spigot implementation of PluginMetadataService that reads from piston-core.yml
 */
public class SpigotPluginMetadataService implements PluginMetadataService
{
    private final Map<String, Object> metadata;
    private final org.bukkit.plugin.Plugin plugin;

    /**
     * Creates a new SpigotPluginMetadataService instance.
     *
     * @param plugin The Bukkit plugin instance
     */
    public SpigotPluginMetadataService(org.bukkit.plugin.Plugin plugin)
    {
        this.plugin = plugin;
        this.metadata = loadMetadata();
    }

    private Map<String, Object> loadMetadata()
    {
        try
        {
            InputStream pistonYmlStream = plugin.getResource("piston-core.yml");
            if (pistonYmlStream == null)
            {
                plugin.getLogger().warning("piston-core.yml not found in plugin resources");
                return new HashMap<>();
            }

            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(pistonYmlStream);
            return config != null ? config : new HashMap<>();
        } catch (Exception e)
        {
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
    public List<String> getPluginAuthors()
    {
        Object authors = metadata.get("authors");
        if (authors == null)
        {
            return List.of("Unknown");
        }

        // Handle different formats that authors might be stored in
        if (authors instanceof List)
        {
            // If it's already a list, cast and convert to strings
            @SuppressWarnings("unchecked")
            List<Object> authorList = (List<Object>) authors;
            return authorList.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        } else if (authors instanceof String)
        {
            // If it's a string, split by common separators
            String authorsStr = authors.toString();
            if (authorsStr.contains(","))
            {
                return java.util.Arrays.stream(authorsStr.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
            } else
            {
                return List.of(authorsStr.trim());
            }
        } else
        {
            // Fallback for any other type
            return List.of(authors.toString());
        }
    }

    public String getPluginAuthorsString()
    {
        List<String> authors = getPluginAuthors();
        return String.join(", ", authors);
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
        if (value != null)
        {
            try
            {
                return (T) value;
            } catch (ClassCastException e)
            {
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
