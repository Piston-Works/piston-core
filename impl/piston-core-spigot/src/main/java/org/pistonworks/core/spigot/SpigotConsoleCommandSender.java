package org.pistonworks.core.spigot;

import org.bukkit.command.CommandSender;
import org.pistonworks.core.api.model.ConsoleCommandSender;

/**
 * Spigot implementation of ConsoleCommandSender
 */
public class SpigotConsoleCommandSender implements ConsoleCommandSender
{
    private final CommandSender bukkit;

    /**
     * Creates a new SpigotConsoleCommandSender wrapper.
     * 
     * @param bukkit The Bukkit CommandSender to wrap
     */
    public SpigotConsoleCommandSender(CommandSender bukkit)
    {
        this.bukkit = bukkit;
    }

    @Override
    public String getName()
    {
        return bukkit.getName();
    }

    @Override
    public void sendMessage(String message)
    {
        bukkit.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission)
    {
        return bukkit.hasPermission(permission);
    }

    @Override
    public boolean isConsole()
    {
        return !(bukkit instanceof org.bukkit.entity.Player);
    }
}
