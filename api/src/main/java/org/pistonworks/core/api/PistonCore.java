package org.pistonworks.core.api;

import org.pistonworks.core.api.service.CommandService;
import org.pistonworks.core.api.service.EventService;
import org.pistonworks.core.api.service.LifecycleService;
import org.pistonworks.core.api.service.PluginDiscoveryService;

// The central access point for all Piston Core services.
public final class PistonCore
{

    private static PistonCoreServices services;
    private static boolean autoInitialized = false;

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
     */
    public static void autoInitialize()
    {
        if (!autoInitialized)
        {
            autoInitialized = true;

            // Auto-discover and initialize the current plugin
            try
            {
                getServices().getPluginDiscoveryService().autoDiscoverCurrentPlugin();
            }
            catch (Exception e)
            {
                System.err.println("Failed to auto-initialize plugin: " + e.getMessage());
            }
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

                // Auto-initialize the current plugin
                autoInitialize();

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