package org.pistonworks.core.api.service;

import org.pistonworks.core.api.model.Player;
import java.util.List;

/**
 * Enhanced command service interface for registering and managing commands.
 */
public interface CommandService {

    /**
     * Registers a command with a simple executor.
     * @param commandName the name of the command
     * @param executor the executor to handle the command
     */
    void registerCommand(String commandName, CommandExecutor executor);

    /**
     * Registers a command with additional metadata.
     * @param commandName the name of the command
     * @param executor the executor to handle the command
     * @param description the command description
     * @param usage the command usage string
     * @param aliases alternative names for the command
     */
    void registerCommand(String commandName, CommandExecutor executor, String description, String usage, String... aliases);

    /**
     * Unregisters a command.
     * @param commandName the name of the command to unregister
     * @return true if the command was successfully unregistered, false if it didn't exist
     */
    boolean unregisterCommand(String commandName);

    /**
     * Gets a list of all registered commands.
     * @return list of command names
     */
    List<String> getRegisteredCommands();

    /**
     * Executes a command programmatically.
     * @param sender the command sender
     * @param commandLine the full command line (command + arguments)
     * @return true if the command was executed successfully, false otherwise
     */
    boolean executeCommand(Player sender, String commandLine);
}
