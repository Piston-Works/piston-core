package org.pistonworks.core.common.event.playerevent;

import org.pistonworks.core.api.event.playerevent.PlayerJoinEvent;
import org.pistonworks.core.api.model.Player;

/**
 * Concrete implementation of PlayerJoinEvent.
 */
public class PlayerJoinEventImpl extends AbstractPlayerEvent implements PlayerJoinEvent {

    private String joinMessage;

    public PlayerJoinEventImpl(Player player, String joinMessage) {
        super(player);
        this.joinMessage = joinMessage;
    }

    @Override
    public String getJoinMessage() {
        return joinMessage;
    }

    @Override
    public void setJoinMessage(String message) {
        this.joinMessage = message;
    }
}
