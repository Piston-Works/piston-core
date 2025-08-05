package org.pistonworks.core.api.model;

import java.util.UUID;

/**
 * Represents a player in the game, abstracting platform-specific player implementations.
 */
public interface Player {

    /**
     * Gets the unique identifier for this player.
     * @return the player's UUID
     */
    UUID getUniqueId();

    /**
     * Gets the display name of this player.
     * @return the player's name
     */
    String getName();

    /**
     * Sends a message to this player.
     * @param message the message to send
     */
    void sendMessage(String message);

    /**
     * Checks if this player has a specific permission.
     * @param permission the permission to check
     * @return true if the player has the permission, false otherwise
     */
    boolean hasPermission(String permission);

    /**
     * Checks if this player is currently online.
     * @return true if the player is online, false otherwise
     */
    boolean isOnline();

    /**
     * Kicks the player from the server.
     * @param reason the reason for kicking the player
     */
    void kick(String reason);
}
