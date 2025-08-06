package org.pistonworks.core.api;

import org.pistonworks.core.api.service.CommandService;
import org.pistonworks.core.api.service.EventService;
import org.pistonworks.core.api.service.LifecycleService;

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