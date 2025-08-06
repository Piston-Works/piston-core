package org.pistonworks.core.common.event.playerevent;

import org.pistonworks.core.api.event.playerevent.PlayerMoveEvent;
import org.pistonworks.core.api.model.Orientation;
import org.pistonworks.core.api.model.Position;
import org.pistonworks.core.api.model.entity.Player;
import org.pistonworks.core.common.event.AbstractCancellableEvent;

public class PlayerMoveEventImpl extends AbstractCancellableEvent implements PlayerMoveEvent
{
    private final Player player;
    private final Position newPosition;
    private final Orientation newOrientation;
    private final Position oldPosition;
    private final Orientation oldOrientation;

    public PlayerMoveEventImpl(Player player, Position newPosition, Orientation newOrientation, Position oldPosition, Orientation oldOrientation)
    {
        this.player = player;
        this.newPosition = newPosition;
        this.newOrientation = newOrientation;
        this.oldPosition = oldPosition;
        this.oldOrientation = oldOrientation;
    }

    @Override
    public Player getPlayer()
    {
        return player;
    }

    @Override
    public Position getNewPosition()
    {
        return newPosition;
    }

    @Override
    public Orientation getNewOrientation()
    {
        return newOrientation;
    }

    @Override
    public Position getOldPosition()
    {
        return oldPosition;
    }

    @Override
    public Orientation getOldOrientation()
    {
        return oldOrientation;
    }
}
