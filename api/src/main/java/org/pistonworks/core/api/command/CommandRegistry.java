package org.pistonworks.core.api.command;

import org.pistonworks.core.api.model.CommandSender;

import java.util.List;

/**
 * Enhanced command registry that supports annotation-based command registration
 * with automatic argument parsing and tab completion.
 */
public interface CommandRegistry
{
    /**
     * Registers all commands from a CommandHandler instance.
     * Scans for @Command annotated methods and registers them automatically.
     *
     * @param handler the command handler instance
     */
    void registerCommands(CommandHandler handler);

    /**
     * Registers all commands from a CommandHandler class.
     * Creates an instance and registers all @Command annotated methods.
     *
     * @param handlerClass the command handler class
     */
    void registerCommands(Class<? extends CommandHandler> handlerClass);

    /**
     * Unregisters all commands from a CommandHandler instance.
     *
     * @param handler the command handler instance
     */
    void unregisterCommands(CommandHandler handler);

    /**
     * Unregisters all commands from a CommandHandler class.
     *
     * @param handlerClass the command handler class
     */
    void unregisterCommands(Class<? extends CommandHandler> handlerClass);

    /**
     * Registers a custom tab completer for a specific argument type.
     *
     * @param type the argument type (e.g., "player", "world")
     * @param completer the tab completer
     */
    void registerTabCompleter(String type, TabCompleter completer);

    /**
     * Executes a command with automatic argument parsing and validation.
     *
     * @param sender the command sender
     * @param commandLine the full command line
     * @return true if the command was executed successfully
     */
    boolean executeCommand(CommandSender sender, String commandLine);

    /**
     * Gets tab completions for a command.
     *
     * @param sender the command sender
     * @param commandLine the current command line being typed
     * @return list of possible completions
     */
    List<String> getTabCompletions(CommandSender sender, String commandLine);

    /**
     * Gets all registered command names.
     *
     * @return list of command names
     */
    List<String> getRegisteredCommands();

    /**
     * Gets command information for help display.
     *
     * @param commandName the command name
     * @return command info or null if not found
     */
    CommandInfo getCommandInfo(String commandName);
}
