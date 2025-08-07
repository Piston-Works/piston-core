package org.pistonworks.core.api.command;

import org.pistonworks.core.api.model.CommandSender;

/**
 * Base class for command handlers. Extend this class and use @Command annotations
 * on methods to create commands with minimal boilerplate.
 * <p>
 * Example usage:
 * <pre>{@code
 * public class MyCommands extends CommandHandler {
 *
 *     @Command(value = "heal", description = "Heal a player", permission = "myplugin.heal")
 *     public void heal(CommandSender sender, @Arg("player") String playerName, @Arg(value = "amount", optional = true, defaultValue = "20") int amount) {
 *         // Command implementation
 *         sender.sendMessage("Healing " + playerName + " for " + amount + " health!");
 *     }
 *
 *     @Command("broadcast")
 *     public void broadcast(CommandSender sender, @Arg(value = "message", completions = {"Hello world!", "Server restarting"}) String message) {
 *         // Broadcast implementation
 *     }
 * }
 * }</pre>
 */
public abstract class CommandHandler
{
    /**
     * Called when a command execution fails due to invalid arguments, permissions, etc.
     * Override this method to customize error handling.
     *
     * @param sender  the command sender
     * @param command the command that failed
     * @param error   the error that occurred
     */
    protected void onCommandError(CommandSender sender, String command, CommandError error)
    {
        switch (error.getType())
        {
            case INVALID_ARGUMENTS:
                sender.sendMessage("§cInvalid arguments. Usage: " + error.getUsage());
                break;
            case NO_PERMISSION:
                sender.sendMessage("§cYou don't have permission to use this command.");
                break;
            case PLAYER_ONLY:
                sender.sendMessage("§cThis command can only be used by players.");
                break;
            case CONSOLE_ONLY:
                sender.sendMessage("§cThis command can only be used from console.");
                break;
            case EXECUTION_ERROR:
                sender.sendMessage("§cAn error occurred while executing the command: " + error.getMessage());
                break;
            default:
                sender.sendMessage("§cCommand failed: " + error.getMessage());
                break;
        }
    }

    /**
     * Called before any command in this handler is executed.
     * Return false to prevent the command from executing.
     *
     * @param sender  the command sender
     * @param command the command being executed
     * @param args    the command arguments
     * @return true to allow execution, false to prevent it
     */
    protected boolean onBeforeCommand(CommandSender sender, String command, String[] args)
    {
        return true;
    }

    /**
     * Called after a command in this handler is successfully executed.
     *
     * @param sender  the command sender
     * @param command the command that was executed
     * @param args    the command arguments
     */
    protected void onAfterCommand(CommandSender sender, String command, String[] args)
    {
        // Default implementation does nothing
    }
}
