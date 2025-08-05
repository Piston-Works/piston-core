package org.pistonworks.core.api.event.playerevent;

import org.pistonworks.core.api.model.Player;

/**
 * Event fired when a player leaves the server.
 */
public class PlayerQuitEvent extends PlayerEvent {

    private String quitMessage;

    /**
     * Creates a new player quit event.
     * @param player the player who quit
     * @param quitMessage the message to display when the player quits
     */
    public PlayerQuitEvent(Player player, String quitMessage) {
        super(player);
        this.quitMessage = quitMessage;
    }

    /**
     * Gets the quit message for this event.
     * @return the quit message
     */
    public String getQuitMessage() {
        return quitMessage;
    }

    /**
     * Sets the quit message for this event.
     * @param quitMessage the new quit message
     */
    public void setQuitMessage(String quitMessage) {
        this.quitMessage = quitMessage;
    }
}
