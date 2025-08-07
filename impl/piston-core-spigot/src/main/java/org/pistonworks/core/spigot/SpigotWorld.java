package org.pistonworks.core.spigot;

import org.pistonworks.core.api.model.World;
import org.pistonworks.core.api.model.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Spigot implementation of the Piston World interface.
 * Wraps a Bukkit World to provide the Piston API interface.
 */
public class SpigotWorld implements World
{

    private final org.bukkit.World bukkitWorld;

    /**
     * Creates a new SpigotWorld wrapper.
     *
     * @param bukkitWorld The Bukkit World to wrap
     */
    public SpigotWorld(org.bukkit.World bukkitWorld)
    {
        this.bukkitWorld = bukkitWorld;
    }

    @Override
    public String getUniqueId()
    {
        return bukkitWorld.getUID().toString();
    }

    @Override
    public String getName()
    {
        return bukkitWorld.getName();
    }

    @Override
    public long getTime()
    {
        return bukkitWorld.getTime();
    }

    @Override
    public void setTime(long time)
    {
        bukkitWorld.setTime(time);
    }

    @Override
    public List<Player> getPlayers()
    {
        return bukkitWorld.getPlayers().stream()
                .map(SpigotPlayer::new)
                .collect(Collectors.toList());
    }

    /**
     * Gets the underlying Bukkit world.
     *
     * @return the Bukkit world instance
     */
    public org.bukkit.World getBukkitWorld()
    {
        return bukkitWorld;
    }
}
