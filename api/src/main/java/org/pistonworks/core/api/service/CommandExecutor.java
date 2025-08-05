package org.pistonworks.core.api.service;

import org.pistonworks.core.api.model.Player;

/**
 * Interface for handling command execution.
 */
public interface CommandExecutor {

    /**
     * Executes the command.
     *
     * @param sender the player or console that executed the command
     * @param command the command that was executed
     * @param args the arguments passed to the command
     * @return true if the command was handled successfully, false otherwise
     */
    boolean execute(Player sender, String command, String[] args);
}
