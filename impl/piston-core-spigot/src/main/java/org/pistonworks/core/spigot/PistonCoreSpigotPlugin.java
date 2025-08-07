package org.pistonworks.core.spigot;

import org.bukkit.plugin.java.JavaPlugin;
import org.pistonworks.core.api.PistonCore;
import org.pistonworks.core.api.plugin.PistonPlugin;
import org.pistonworks.core.api.service.CommandService;
import org.pistonworks.core.api.service.EventService;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public final class PistonCoreSpigotPlugin extends JavaPlugin
{

    // A static reference to the main plugin instance. This is a common pattern for easy access.
    private static PistonCoreSpigotPlugin instance;
    private final PistonCoreSpigotServices services = new PistonCoreSpigotServices(this);
    private SpigotPlugin spigotPlugin;
    private PistonPlugin userPlugin;

    public static PistonCoreSpigotPlugin getInstance()
    {
        return instance;
    }

    @Override
    public void onEnable()
    {
        instance = this;

        // Initialize Piston Core services first
        PistonCore.setServices(services);

        // Create SpigotPlugin wrapper
        spigotPlugin = new SpigotPlugin(this);

        // Load and initialize user plugin
        try {
            loadUserPlugin();
        } catch (Exception e) {
            getLogger().severe("Failed to load user plugin: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Piston Core Spigot implementation enabled!");
    }

    @Override
    public void onDisable()
    {
        // Disable user plugin first
        if (userPlugin != null) {
            try {
                userPlugin.onDisable();
            } catch (Exception e) {
                getLogger().warning("Error disabling user plugin: " + e.getMessage());
            }
        }

        instance = null;
        getLogger().info("Piston Core Spigot implementation disabled!");
    }

    private void loadUserPlugin() throws Exception {
        // Read piston-core.yml to get the main class
        InputStream pistonYmlStream = getResource("piston-core.yml");
        if (pistonYmlStream == null) {
            throw new IllegalStateException("piston-core.yml not found in plugin resources");
        }

        Yaml yaml = new Yaml();
        Map<String, Object> config = yaml.load(pistonYmlStream);

        String mainClassName = (String) config.get("main");
        if (mainClassName == null) {
            throw new IllegalStateException("main class not specified in piston-core.yml");
        }

        // Load and instantiate the user's plugin class
        Class<?> mainClass = Class.forName(mainClassName);
        if (!PistonPlugin.class.isAssignableFrom(mainClass)) {
            throw new IllegalStateException("Main class " + mainClassName + " must implement PistonPlugin");
        }

        userPlugin = (PistonPlugin) mainClass.getDeclaredConstructor().newInstance();

        // Initialize the user plugin
        userPlugin.onEnable();

        getLogger().info("Loaded user plugin: " + mainClassName);
    }

    public CommandService getCommandService()
    {
        return services.getCommandService();
    }

    public EventService getEventService()
    {
        return services.getEventService();
    }
}