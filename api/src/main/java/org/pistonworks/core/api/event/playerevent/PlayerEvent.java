package org.pistonworks.core.api.event.playerevent;

import org.pistonworks.core.api.event.Event;
import org.pistonworks.core.api.model.Player;

/**
 * Base class for all player-related events.
 */
public interface PlayerEvent extends Event {

    /**
     * Gets the player involved in this event.
     * @return the player
     */
    Player getPlayer();
}
