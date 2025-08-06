package org.pistonworks.core.common.event.playerevent;

import org.pistonworks.core.api.event.playerevent.PlayerQuitEvent;
import org.pistonworks.core.api.model.Player;

/**
 * Concrete implementation of PlayerQuitEvent.
 */
public class PlayerQuitEventImpl extends AbstractPlayerEvent implements PlayerQuitEvent {

    private String quitMessage;

    public PlayerQuitEventImpl(Player player, String quitMessage) {
        super(player);
        this.quitMessage = quitMessage;
    }

    @Override
    public String getQuitMessage() {
        return quitMessage;
    }

    @Override
    public void setQuitMessage(String message) {
        this.quitMessage = message;
    }
}
