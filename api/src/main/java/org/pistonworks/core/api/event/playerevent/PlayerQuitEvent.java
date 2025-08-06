package org.pistonworks.core.api.event.playerevent;

import org.pistonworks.core.api.model.Player;

/**
 * Called when a player leaves the server.
 */
public interface PlayerQuitEvent extends PlayerEvent {

    /**
     * Gets the quit message that will be displayed.
     * @return the quit message, or null if no message
     */
    String getQuitMessage();

    /**
     * Sets the quit message to be displayed.
     * @param message the quit message, or null for no message
     */
    void setQuitMessage(String message);
}
