package org.pistonworks.core.api.event.playerevent;

import org.pistonworks.core.api.event.Cancellable;
import org.pistonworks.core.api.model.Orientation;
import org.pistonworks.core.api.model.Position;
import org.pistonworks.core.api.model.entity.Player;

/**
 * Represents an event triggered when a player moves (change in position or orientation).
 * This event is cancellable.
 */
public interface PlayerMoveEvent extends PlayerEvent, Cancellable
{
    /**
     * Gets the relevant player for this event.
     *
     * @return the player who is moving
     */
    Player getPlayer();

    /**
     * Gets the new position of the player.
     *
     * @return the player's new position
     */
    Position getNewPosition();

    /**
     * Gets the new orientation of the player.
     *
     * @return the player's new orientation
     */
    Orientation getNewOrientation();

    /**
     * Gets the old position of the player before the move.
     *
     * @return the player's old position
     */
    Position getOldPosition();

    /**
     * Gets the old orientation of the player before the move.
     *
     * @return the player's old orientation
     */
    Orientation getOldOrientation();
}
