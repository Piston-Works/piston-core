package org.pistonworks.core.api.model;

/**
 * Represents any entity that can send commands and receive messages.
 * This is the base interface for both players and console command senders.
 */
public interface CommandSender
{
    /**
     * Gets the name of this command sender.
     *
     * @return the sender's name
     */
    String getName();

    /**
     * Sends a message to this command sender.
     *
     * @param message the message to send
     */
    void sendMessage(String message);

    /**
     * Checks if this command sender has a specific permission.
     *
     * @param permission the permission to check
     * @return true if the sender has the permission, false otherwise
     */
    boolean hasPermission(String permission);
}
