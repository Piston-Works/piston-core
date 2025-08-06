package org.pistonworks.core.common.event;

import org.pistonworks.core.api.event.playerevent.PlayerChatEvent;
import org.pistonworks.core.api.event.playerevent.PlayerJoinEvent;
import org.pistonworks.core.api.event.playerevent.PlayerQuitEvent;
import org.pistonworks.core.api.model.Player;
import org.pistonworks.core.common.event.playerevent.PlayerChatEventImpl;
import org.pistonworks.core.common.event.playerevent.PlayerJoinEventImpl;
import org.pistonworks.core.common.event.playerevent.PlayerQuitEventImpl;

/**
 * Factory for creating event instances.
 * This keeps event creation logic centralized and makes it easy to swap implementations.
 */
public class EventFactory {

    /**
     * Creates a new PlayerJoinEvent.
     * @param player the player who joined
     * @param joinMessage the join message
     * @return the event instance
     */
    public static PlayerJoinEvent createPlayerJoinEvent(Player player, String joinMessage) {
        return new PlayerJoinEventImpl(player, joinMessage);
    }

    /**
     * Creates a new PlayerQuitEvent.
     * @param player the player who quit
     * @param quitMessage the quit message
     * @return the event instance
     */
    public static PlayerQuitEvent createPlayerQuitEvent(Player player, String quitMessage) {
        return new PlayerQuitEventImpl(player, quitMessage);
    }

    /**
     * Creates a new PlayerChatEvent.
     * @param player the player who sent the message
     * @param message the chat message
     * @param format the chat format
     * @return the event instance
     */
    public static PlayerChatEvent createPlayerChatEvent(Player player, String message, String format) {
        return new PlayerChatEventImpl(player, message, format);
    }
}
