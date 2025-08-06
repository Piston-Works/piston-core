package org.pistonworks.core.api.model;

/**
 * Represents a console command sender.
 * This interface extends CommandSender to provide console-specific functionality.
 */
public interface ConsoleCommandSender extends CommandSender
{
    /**
     * Checks if this command sender is the server console.
     *
     * @return true if this is the server console, false otherwise
     */
    boolean isConsole();
}
