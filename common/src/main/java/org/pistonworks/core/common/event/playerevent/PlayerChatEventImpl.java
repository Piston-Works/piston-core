package org.pistonworks.core.common.event.playerevent;

import org.pistonworks.core.api.event.playerevent.PlayerChatEvent;
import org.pistonworks.core.api.model.Player;
import org.pistonworks.core.common.event.AbstractCancellableEvent;

/**
 * Concrete implementation of PlayerChatEvent.
 */
public class PlayerChatEventImpl extends AbstractCancellableEvent implements PlayerChatEvent {

    private final Player player;
    private String message;
    private String format;

    public PlayerChatEventImpl(Player player, String message, String format) {
        super();
        this.player = player;
        this.message = message;
        this.format = format;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return String.format("%s{player=%s, message=%s, cancelled=%b, timestamp=%d}",
                           getEventName(), player.getName(), message, isCancelled(), getTimestamp());
    }
}
