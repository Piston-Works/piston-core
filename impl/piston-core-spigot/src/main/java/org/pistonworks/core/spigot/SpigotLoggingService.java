package org.pistonworks.core.spigot;

import org.pistonworks.core.api.logging.Logger;
import org.pistonworks.core.api.service.LoggingService;

import java.util.HashMap;
import java.util.Map;

public class SpigotLoggingService implements LoggingService
{

    private final Map<Class<?>, Logger> loggers = new HashMap<>();
    private final org.bukkit.plugin.Plugin plugin;

    public SpigotLoggingService(org.bukkit.plugin.Plugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public Logger getLogger(Class<?> clazz)
    {
        return loggers.computeIfAbsent(clazz, c -> new SpigotLogger(plugin.getLogger()));
    }

    private static class SpigotLogger implements Logger
    {
        private final java.util.logging.Logger logger;

        public SpigotLogger(java.util.logging.Logger logger)
        {
            this.logger = logger;
        }

        @Override
        public void debug(String message)
        {
            logger.fine(message);
        }

        @Override
        public void info(String message)
        {
            logger.info(message);
        }

        @Override
        public void warn(String message)
        {
            logger.warning(message);
        }

        @Override
        public void error(String message)
        {
            logger.severe(message);
        }

        @Override
        public void critical(String message)
        {
            logger.severe(message);
        }
    }
}
