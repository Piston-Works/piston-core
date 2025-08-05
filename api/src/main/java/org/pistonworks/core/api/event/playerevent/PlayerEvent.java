package org.pistonworks.core.api.event.playerevent;

import org.pistonworks.core.api.event.Event;
import org.pistonworks.core.api.model.Player;

/**
 * Base class for all player-related events.
 */
public abstract class PlayerEvent extends Event {

    private final Player player;

    /**
     * Creates a new player event.
     * @param player the player involved in this event
     */
    protected PlayerEvent(Player player) {
        super();
        this.player = player;
    }

    /**
     * Gets the player involved in this event.
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }
}
