package org.pistonworks.core.api.event.playerevent;

import org.pistonworks.core.api.event.Cancellable;
import org.pistonworks.core.api.model.Player;

/**
 * Called when a player sends a chat message.
 * This event is cancellable.
 */
public interface PlayerChatEvent extends PlayerEvent, Cancellable {

    /**
     * Gets the message the player is sending.
     * @return the chat message
     */
    String getMessage();

    /**
     * Sets the message the player is sending.
     * @param message the new chat message
     */
    void setMessage(String message);

    /**
     * Gets the format string used for this chat message.
     * @return the format string (e.g., "&lt;%s&gt; %s")
     */
    String getFormat();

    /**
     * Sets the format string used for this chat message.
     * @param format the format string
     */
    void setFormat(String format);
}
