package org.pistonworks.core.api.event.playerevent;

import org.pistonworks.core.api.model.Player;

/**
 * Event fired when a player sends a chat message.
 */
public class PlayerChatEvent extends PlayerEvent {

    private String message;
    private String format;

    /**
     * Creates a new player chat event.
     * @param player the player who sent the message
     * @param message the chat message
     * @param format the format string for the message
     */
    public PlayerChatEvent(Player player, String message) {
        super(player);
        this.message = message;
    }

    /**
     * Gets the chat message.
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the chat message.
     * @param message the new message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the format string for the message.
     * @return the format string
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets the format string for the message.
     * @param format the new format string
     */
    public void setFormat(String format) {
        this.format = format;
    }
}
