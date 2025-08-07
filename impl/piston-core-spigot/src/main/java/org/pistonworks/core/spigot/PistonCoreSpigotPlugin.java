package org.pistonworks.core.spigot;

import org.bukkit.plugin.java.JavaPlugin;
import org.pistonworks.core.api.PistonCore;
import org.pistonworks.core.api.logging.Logger;
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
    private PistonCoreSpigotServices services;
    private SpigotPlugin spigotPlugin;
    private PistonPlugin userPlugin;
    private Logger logger;

    public static PistonCoreSpigotPlugin getInstance()
    {
        return instance;
    }

    @Override
    public void onEnable()
    {
        instance = this;

        // Initialize Piston Core services after plugin is enabled
        services = new PistonCoreSpigotServices(this);
        PistonCore.setServices(services);

        // Get Piston Core logger
        logger = PistonCore.getLoggingService().getLogger(PistonCoreSpigotPlugin.class);

        // Create SpigotPlugin wrapper
        spigotPlugin = new SpigotPlugin(this);

        // Load and initialize user plugin
        try
        {
            loadUserPlugin();
        } catch (Exception e)
        {
            logger.error("Failed to load user plugin: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        logger.info("Piston Core Spigot implementation enabled!");
    }

    @Override
    public void onDisable()
    {
        // Disable user plugin first
        if (userPlugin != null)
        {
            try
            {
                userPlugin.onDisable();
            } catch (Exception e)
            {
                if (logger != null)
                {
                    logger.warn("Error disabling user plugin: " + e.getMessage());
                } else
                {
                    getLogger().warning("Error disabling user plugin: " + e.getMessage());
                }
            }
        }

        instance = null;
        if (logger != null)
        {
            logger.info("Piston Core Spigot implementation disabled!");
        } else
        {
            getLogger().info("Piston Core Spigot implementation disabled!");
        }
    }

    private void loadUserPlugin() throws Exception
    {
        logger.info("Loading user plugin...");

        // Prevent loading the user plugin multiple times
        if (userPlugin != null)
        {
            logger.warn("User plugin already loaded, skipping duplicate load");
            return;
        }

        // Read piston-core.yml to get the main class
        InputStream pistonYmlStream = getResource("piston-core.yml");
        if (pistonYmlStream == null)
        {
            throw new IllegalStateException("piston-core.yml not found in plugin resources");
        }

        Yaml yaml = new Yaml();
        Map<String, Object> config = yaml.load(pistonYmlStream);

        String mainClassName = (String) config.get("main");
        if (mainClassName == null)
        {
            throw new IllegalStateException("main class not specified in piston-core.yml");
        }

        // Load and instantiate the user's plugin class
        Class<?> mainClass = Class.forName(mainClassName);
        if (!PistonPlugin.class.isAssignableFrom(mainClass))
        {
            throw new IllegalStateException("Main class " + mainClassName + " must implement PistonPlugin");
        }

        userPlugin = (PistonPlugin) mainClass.getDeclaredConstructor().newInstance();

        // Initialize the user plugin
        userPlugin.onEnable();

        logger.info("Loaded user plugin: " + mainClassName);
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