package org.pistonworks.core.api.model;

import org.pistonworks.core.api.model.entity.Player;

import java.util.List;

public interface World
{
    /**
     * Gets the unique identifier for this world.
     *
     * @return the world's UUID
     */
    String getUniqueId();

    /**
     * Gets the name of this world.
     *
     * @return the world's name
     */
    String getName();

    /**
     * Gets the current time in this world.
     *
     * @return the world's time
     */
    long getTime();

    /**
     * Sets the time in this world.
     *
     * @param time the new time value
     */
    void setTime(long time);

    /**
     * Get all players currently in this world.
     *
     * @return a list of players in this world
     */
    List<Player> getPlayers();
}
