package org.pistonworks.core.common;

import org.pistonworks.core.api.model.PistonPlugin;
import org.pistonworks.core.api.model.PluginMetadata;
import org.pistonworks.core.api.service.PluginDiscoveryService;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Common implementation of PluginDiscoveryService that can be used across platforms
 */
public class PluginDiscoveryServiceImpl implements PluginDiscoveryService
{
    private final List<PistonPlugin> loadedPlugins = new ArrayList<>();
    private final File pluginsDirectory;
    private final File dataDirectory;

    public PluginDiscoveryServiceImpl(File pluginsDirectory, File dataDirectory)
    {
        this.pluginsDirectory = pluginsDirectory;
        this.dataDirectory = dataDirectory;
    }

    @Override
    public List<PluginMetadata> discoverPlugins(File pluginsDirectory)
    {
        List<PluginMetadata> plugins = new ArrayList<>();

        if (!pluginsDirectory.exists() || !pluginsDirectory.isDirectory())
        {
            return plugins;
        }

        File[] jarFiles = pluginsDirectory.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null) return plugins;

        for (File jarFile : jarFiles)
        {
            try
            {
                PluginMetadata metadata = loadMetadataFromJar(jarFile);
                if (metadata != null)
                {
                    plugins.add(metadata);
                }
            }
            catch (Exception e)
            {
                System.err.println("Failed to load plugin metadata from " + jarFile.getName() + ": " + e.getMessage());
            }
        }

        return plugins;
    }

    @Override
    public PistonPlugin loadPlugin(PluginMetadata metadata, File jarFile) throws Exception
    {
        // Create plugin data folder
        File pluginDataFolder = new File(dataDirectory, metadata.getName());
        if (!pluginDataFolder.exists())
        {
            pluginDataFolder.mkdirs();
        }

        // Load the plugin class
        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()});
        Class<?> mainClass = classLoader.loadClass(metadata.getMainClass());

        // Create the plugin instance
        PistonPlugin plugin = new PistonPlugin(metadata, mainClass, pluginDataFolder);

        // Try to instantiate the main class
        Object instance = mainClass.getDeclaredConstructor().newInstance();
        plugin.setInstance(instance);

        loadedPlugins.add(plugin);
        return plugin;
    }

    @Override
    public void autoDiscoverCurrentPlugin() throws Exception
    {
        // Look for piston-core.yml in the current classpath (bundled plugin)
        InputStream metadataStream = getClass().getClassLoader().getResourceAsStream("piston-core.yml");
        if (metadataStream == null)
        {
            throw new Exception("No piston-core.yml found in current plugin JAR");
        }

        // Parse the metadata
        Scanner scanner = new Scanner(metadataStream);
        StringBuilder content = new StringBuilder();
        while (scanner.hasNextLine())
        {
            content.append(scanner.nextLine()).append("\n");
        }
        scanner.close();
        metadataStream.close();

        PluginMetadata metadata = parseMetadata(content.toString());

        // Create plugin data folder
        File pluginDataFolder = new File(dataDirectory, metadata.getName());
        if (!pluginDataFolder.exists())
        {
            pluginDataFolder.mkdirs();
        }

        // Load the main class from current classpath
        Class<?> mainClass = Class.forName(metadata.getMainClass());

        // Create the plugin instance
        PistonPlugin plugin = new PistonPlugin(metadata, mainClass, pluginDataFolder);

        // Instantiate the main class
        Object instance = mainClass.getDeclaredConstructor().newInstance();
        plugin.setInstance(instance);
        plugin.setEnabled(true);

        loadedPlugins.add(plugin);

        // Call enable lifecycle if the instance has the method
        try
        {
            mainClass.getMethod("onEnable").invoke(instance);
        }
        catch (NoSuchMethodException e)
        {
            // onEnable method is optional
        }

        System.out.println("Auto-discovered and loaded plugin: " + metadata.getName() + " v" + metadata.getVersion());
    }

    @Override
    public PluginMetadata parseMetadata(String yamlContent) throws Exception
    {
        // Simple YAML parser for piston-core.yml
        // This is a basic implementation - in production you might want to use a proper YAML library
        PluginMetadata metadata = new PluginMetadata();

        String[] lines = yamlContent.split("\n");
        for (String line : lines)
        {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            if (line.startsWith("name:"))
            {
                metadata.setName(extractValue(line));
            }
            else if (line.startsWith("version:"))
            {
                metadata.setVersion(extractValue(line));
            }
            else if (line.startsWith("description:"))
            {
                metadata.setDescription(extractValue(line));
            }
            else if (line.startsWith("main:"))
            {
                metadata.setMainClass(extractValue(line));
            }
            else if (line.startsWith("api-version:"))
            {
                metadata.setApiVersion(extractValue(line));
            }
            else if (line.startsWith("authors:"))
            {
                metadata.setAuthors(parseStringList(lines, line));
            }
            else if (line.startsWith("dependencies:"))
            {
                metadata.setDependencies(parseStringList(lines, line));
            }
        }

        return metadata;
    }

    @Override
    public List<PistonPlugin> getLoadedPlugins()
    {
        return new ArrayList<>(loadedPlugins);
    }

    private PluginMetadata loadMetadataFromJar(File jarFile) throws Exception
    {
        try (JarFile jar = new JarFile(jarFile))
        {
            JarEntry entry = jar.getJarEntry("piston-core.yml");
            if (entry == null)
            {
                return null; // Not a Piston Core plugin
            }

            try (InputStream is = jar.getInputStream(entry))
            {
                Scanner scanner = new Scanner(is);
                StringBuilder content = new StringBuilder();
                while (scanner.hasNextLine())
                {
                    content.append(scanner.nextLine()).append("\n");
                }
                return parseMetadata(content.toString());
            }
        }
    }

    private String extractValue(String line)
    {
        int colonIndex = line.indexOf(':');
        if (colonIndex == -1) return "";

        String value = line.substring(colonIndex + 1).trim();
        // Remove quotes if present
        if (value.startsWith("\"") && value.endsWith("\""))
        {
            value = value.substring(1, value.length() - 1);
        }
        else if (value.startsWith("'") && value.endsWith("'"))
        {
            value = value.substring(1, value.length() - 1);
        }
        return value;
    }

    private List<String> parseStringList(String[] lines, String currentLine)
    {
        List<String> result = new ArrayList<>();

        // Check if it's inline format: "authors: [author1, author2]"
        if (currentLine.contains("[") && currentLine.contains("]"))
        {
            String listContent = currentLine.substring(currentLine.indexOf('[') + 1, currentLine.indexOf(']'));
            String[] items = listContent.split(",");
            for (String item : items)
            {
                result.add(item.trim().replaceAll("^\"|\"$|^'|'$", ""));
            }
        }

        return result;
    }
}
