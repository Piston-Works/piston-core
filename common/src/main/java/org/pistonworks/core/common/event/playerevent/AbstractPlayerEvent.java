package org.pistonworks.core.common.event.playerevent;

import org.pistonworks.core.api.event.playerevent.PlayerEvent;
import org.pistonworks.core.api.model.entity.Player;
import org.pistonworks.core.common.event.AbstractEvent;

/**
 * Abstract base class for player events providing common functionality.
 */
public abstract class AbstractPlayerEvent extends AbstractEvent implements PlayerEvent
{

    private final Player player;

    protected AbstractPlayerEvent(Player player)
    {
        this.player = player;
    }

    protected AbstractPlayerEvent(Player player, String eventName)
    {
        super(eventName);
        this.player = player;
    }

    @Override
    public Player getPlayer()
    {
        return player;
    }

    @Override
    public String toString()
    {
        return String.format("%s{player=%s, timestamp=%d}",
                getEventName(), player.getName(), getTimestamp());
    }
}
