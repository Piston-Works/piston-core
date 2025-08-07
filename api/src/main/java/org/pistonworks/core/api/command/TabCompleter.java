package org.pistonworks.core.api.command;

import org.pistonworks.core.api.model.CommandSender;

import java.util.List;

/**
 * Interface for providing tab completions for command arguments.
 */
@FunctionalInterface
public interface TabCompleter
{
    /**
     * Returns a list of possible completions for the given context.
     *
     * @param sender     the command sender requesting completions
     * @param command    the command being completed
     * @param args       the current arguments (the last one being partially typed)
     * @param currentArg the argument currently being completed
     * @return list of possible completions, or empty list if none
     */
    List<String> complete(CommandSender sender, String command, String[] args, String currentArg);
}
