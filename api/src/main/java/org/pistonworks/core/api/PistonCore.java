package org.pistonworks.core.api;

import org.pistonworks.core.api.plugin.PistonPlugin;
import org.pistonworks.core.api.service.*;

// The central access point for all Piston Core services.
public final class PistonCore
{

    private static PistonCoreServices services;

    public static CommandService getCommandService()
    {
        return getServices().getCommandService();
    }

    public static EventService getEventService()
    {
        return getServices().getEventService();
    }

    public static LifecycleService getLifecycleService()
    {
        return getServices().getLifecycleService();
    }

    public static PluginDiscoveryService getPluginDiscoveryService()
    {
        return getServices().getPluginDiscoveryService();
    }

    public static LoggingService getLoggingService()
    {
        return getServices().getLoggingService();
    }

    public static PluginMetadataService getPluginMetadataService()
    {
        return getServices().getPluginMetadataService();
    }

    private static PistonCoreServices getServices()
    {
        if (services == null)
        {
            // Try to auto-detect and initialize platform services
            initializePlatformServices();
        }

        if (services == null)
        {
            throw new IllegalStateException("Piston Core services not initialized! Make sure a platform implementation is available.");
        }

        return services;
    }

    /**
     * Manually set the services implementation. This should be called by the platform implementation.
     *
     * @param servicesImpl the platform-specific services implementation
     */
    public static void setServices(PistonCoreServices servicesImpl)
    {
        services = servicesImpl;
    }

    /**
     * Initialize the plugin automatically when Piston Core is first accessed.
     * This is called automatically when the plugin JAR is loaded.
     *
     * @param plugin The plugin instance to initialize.
     */
    public static void autoInitialize(PistonPlugin plugin)
    {
        // Auto-discover and initialize the current plugin
        try
        {
            getServices().getPluginDiscoveryService().discoverPlugin(plugin);
        } catch (Exception e)
        {
            System.err.println("Failed to auto-initialize plugin: " + e.getMessage());
        }
    }

    private static void initializePlatformServices()
    {
        // Try to detect and initialize Spigot services
        if (isBukkitPlatform())
        {
            try
            {
                Class<?> spigotServicesClass = Class.forName("org.pistonworks.core.spigot.PistonCoreSpigotServices");
                Class<?> pluginClass = Class.forName("org.pistonworks.core.spigot.PistonCoreSpigotPlugin");

                // Get the plugin instance using reflection
                Object pluginInstance = pluginClass.getMethod("getInstance").invoke(null);

                // Create services with the plugin instance
                services = (PistonCoreServices) spigotServicesClass
                        .getConstructor(Object.class)
                        .newInstance(pluginInstance);


            } catch (Exception e)
            {
                // Platform detection failed, services will remain null
                // The calling code will handle this appropriately
            }
        }
        // ... add other platforms here in the future ...
    }

    private static boolean isBukkitPlatform()
    {
        // Checks for the existence of a core Bukkit class.
        try
        {
            Class.forName("org.bukkit.Server");
            return true;
        } catch (ClassNotFoundException e)
        {
            return false;
        }
    }
}