package org.pistonworks.core.api.event.playerevent;

import org.pistonworks.core.api.model.Player;

/**
 * Event fired when a player joins the server.
 */
public class PlayerJoinEvent extends PlayerEvent {

    private String joinMessage;

    /**
     * Creates a new player join event.
     * @param player the player who joined
     * @param joinMessage the message to display when the player joins
     */
    public PlayerJoinEvent(Player player, String joinMessage) {
        super(player);
        this.joinMessage = joinMessage;
    }

    /**
     * Gets the join message for this event.
     * @return the join message
     */
    public String getJoinMessage() {
        return joinMessage;
    }

    /**
     * Sets the join message for this event.
     * @param joinMessage the new join message
     */
    public void setJoinMessage(String joinMessage) {
        this.joinMessage = joinMessage;
    }
}