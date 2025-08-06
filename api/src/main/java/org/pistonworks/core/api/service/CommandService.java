package org.pistonworks.core.api.service;

import org.pistonworks.core.api.command.CommandHandler;
import org.pistonworks.core.api.command.CommandRegistry;
import org.pistonworks.core.api.command.TabCompleter;
import org.pistonworks.core.api.model.CommandSender;

import java.util.List;

/**
 * Enhanced command service interface for registering and managing commands.
 * Supports both legacy CommandExecutor registration and modern annotation-based CommandHandlers.
 */
public interface CommandService extends CommandRegistry
{

    /**
     * Registers a command with a simple executor.
     *
     * @param commandName the name of the command
     * @param executor    the executor to handle the command
     */
    void registerCommand(String commandName, CommandExecutor executor);

    /**
     * Registers a command with additional metadata.
     *
     * @param commandName the name of the command
     * @param executor    the executor to handle the command
     * @param description the command description
     * @param usage       the command usage string
     * @param aliases     alternative names for the command
     */
    void registerCommand(String commandName, CommandExecutor executor, String description, String usage, String... aliases);

    /**
     * Unregisters a command.
     *
     * @param commandName the name of the command to unregister
     * @return true if the command was successfully unregistered, false if it didn't exist
     */
    boolean unregisterCommand(String commandName);

    /**
     * Gets a list of all registered commands.
     *
     * @return list of command names
     */
    List<String> getRegisteredCommands();

    /**
     * Executes a command programmatically.
     *
     * @param sender      the command sender (player or console)
     * @param commandLine the full command line (command + arguments)
     * @return true if the command was executed successfully, false otherwise
     */
    boolean executeCommand(CommandSender sender, String commandLine);

    /**
     * Gets tab completions for a command at the current cursor position.
     *
     * @param sender      the command sender requesting completions
     * @param commandLine the current command line being typed
     * @return list of possible completions
     */
    List<String> getTabCompletions(CommandSender sender, String commandLine);

    /**
     * Registers a CommandHandler with annotation-based commands.
     * This is the preferred way to register commands in the new system.
     *
     * @param handler the command handler instance
     */
    @Override
    void registerCommands(CommandHandler handler);

    /**
     * Registers a CommandHandler class with annotation-based commands.
     * Creates an instance of the handler and registers all @Command methods.
     *
     * @param handlerClass the command handler class
     */
    @Override
    void registerCommands(Class<? extends CommandHandler> handlerClass);

    /**
     * Registers a custom tab completer for argument types.
     *
     * @param type      the argument type name
     * @param completer the tab completer
     */
    @Override
    void registerTabCompleter(String type, TabCompleter completer);
}
