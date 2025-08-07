package org.pistonworks.core.api.service;

import org.pistonworks.core.api.model.CommandSender;

import java.util.List;

/**
 * Interface for handling command execution and tab completion.
 */
public interface CommandExecutor
{

    /**
     * Executes the command.
     *
     * @param sender  the command sender (player or console) that executed the command
     * @param command the command that was executed
     * @param args    the arguments passed to the command
     * @return true if the command was handled successfully, false otherwise
     */
    boolean execute(CommandSender sender, String command, String[] args);

    /**
     * Provides tab completions for this command.
     * This method is called when a player presses Tab while typing the command.
     *
     * @param sender      the command sender requesting completions
     * @param commandLine the current command line being typed
     * @return list of possible completions, or empty list if no completions available
     */
    default List<String> getTabCompletions(CommandSender sender, String commandLine)
    {
        // Default implementation returns empty list (no completions)
        return List.of();
    }
}
