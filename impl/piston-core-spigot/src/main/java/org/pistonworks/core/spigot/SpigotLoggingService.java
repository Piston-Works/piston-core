package org.pistonworks.core.spigot;

import org.pistonworks.core.api.logging.Logger;
import org.pistonworks.core.api.service.LoggingService;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Spigot implementation of the LoggingService.
 * Provides logging functionality using Bukkit's logging system.
 */
public class SpigotLoggingService implements LoggingService
{

    private final Map<Class<?>, Logger> loggers = new HashMap<>();
    private final org.bukkit.plugin.Plugin plugin;

    /**
     * Creates a new SpigotLoggingService instance.
     *
     * @param plugin The Bukkit plugin instance
     */
    public SpigotLoggingService(org.bukkit.plugin.Plugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public Logger getLogger(Class<?> clazz)
    {
        return loggers.computeIfAbsent(clazz, c ->
        {
            // Determine if this is a Piston Core class or user plugin class
            boolean isPistonCore = c.getPackage() != null && c.getPackage().getName().startsWith("org.pistonworks.core");

            if (isPistonCore)
            {
                // For Piston Core classes, create a custom logger with "Piston Core" name
                java.util.logging.Logger coreLogger = java.util.logging.Logger.getLogger("Piston Core");
                return new SpigotLogger(coreLogger, true);
            } else
            {
                // For user plugin classes, use the plugin's logger directly
                return new SpigotLogger(plugin.getLogger(), false);
            }
        });
    }

    private static class SpigotLogger implements Logger
    {
        private final java.util.logging.Logger logger;
        private final boolean isPistonCore;

        public SpigotLogger(java.util.logging.Logger logger, boolean isPistonCore)
        {
            this.logger = logger;
            this.isPistonCore = isPistonCore;
        }

        private void log(Level level, String message)
        {
            if (isPistonCore)
            {
                // For Piston Core, we need to log to the server console directly to avoid double prefixes
                // Use the server's main logger instead of a custom one
                java.util.logging.Logger serverLogger = java.util.logging.Logger.getLogger("Minecraft");
                serverLogger.log(level, "[Piston Core] " + message);
            } else
            {
                // For user plugins, let Bukkit handle the prefixing
                logger.log(level, message);
            }
        }

        @Override
        public void debug(String message)
        {
            log(Level.FINE, message);
        }

        @Override
        public void info(String message)
        {
            log(Level.INFO, message);
        }

        @Override
        public void warn(String message)
        {
            log(Level.WARNING, message);
        }

        @Override
        public void error(String message)
        {
            log(Level.SEVERE, message);
        }

        @Override
        public void critical(String message)
        {
            log(Level.SEVERE, message);
        }
    }
}
