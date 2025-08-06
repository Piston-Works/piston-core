package org.pistonworks.core.api.model.entity;

import org.pistonworks.core.api.model.CommandSender;

/**
 * Represents a player in the game, abstracting platform-specific player implementations.
 */
public interface Player extends Entity, CommandSender
{
    /**
     * Gets the unique identifier of this player.
     *
     * @return the player's unique ID
     */
    java.util.UUID getUniqueId();

    /**
     * Checks if this player is currently online.
     *
     * @return true if the player is online, false otherwise
     */
    boolean isOnline();

    /**
     * Kicks the player from the server.
     *
     * @param reason the reason for kicking the player
     */
    void kick(String reason);

    /**
     * Gets the current walk speed of the player.
     *
     * @return the player's walk speed
     */
    double getWalkSpeed();

    /**
     * Sets the walk speed of the player.
     *
     * @param speed the new walk speed
     */
    void setWalkSpeed(double speed);

    /**
     * Gets the current experience points of the player.
     *
     * @return the player's experience points
     */
    int getExperience();

    /**
     * Sets the experience points of the player.
     *
     * @param experience the new experience points
     */
    void setExperience(int experience);
}
