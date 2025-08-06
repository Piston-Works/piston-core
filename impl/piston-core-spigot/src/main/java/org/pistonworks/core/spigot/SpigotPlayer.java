package org.pistonworks.core.spigot;

import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.pistonworks.core.api.model.entity.Player;
import org.pistonworks.core.api.model.Position;
import org.pistonworks.core.api.model.World;

import java.util.UUID;

/**
 * Spigot implementation of the Piston Player interface.
 * Wraps a Bukkit Player to provide the Piston API interface.
 */
public class SpigotPlayer implements Player
{

    private final org.bukkit.entity.Player bukkitPlayer;

    public SpigotPlayer(org.bukkit.entity.Player bukkitPlayer)
    {
        this.bukkitPlayer = bukkitPlayer;
    }

    @Override
    public UUID getUniqueId()
    {
        return bukkitPlayer.getUniqueId();
    }

    @Override
    public String getName()
    {
        return bukkitPlayer.getName();
    }

    @Override
    public void sendMessage(String message)
    {
        bukkitPlayer.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission)
    {
        return bukkitPlayer.hasPermission(permission);
    }

    @Override
    public boolean isOnline()
    {
        return bukkitPlayer.isOnline();
    }

    @Override
    public void kick(String reason)
    {
        bukkitPlayer.kickPlayer(reason);
    }

    @Override
    public double getWalkSpeed()
    {
        return bukkitPlayer.getWalkSpeed();
    }

    @Override
    public void setWalkSpeed(double speed)
    {
        bukkitPlayer.setWalkSpeed((float) speed);
    }

    @Override
    public int getExperience()
    {
        return bukkitPlayer.getTotalExperience();
    }

    @Override
    public void setExperience(int experience)
    {
        bukkitPlayer.setTotalExperience(experience);
    }

    @Override
    public Position getPosition()
    {
        org.bukkit.Location loc = bukkitPlayer.getLocation();
        return new Position(null, loc.getX(), loc.getY(), loc.getZ()); // TODO: Implement world conversion
    }

    @Override
    public void setPosition(Position position)
    {
        org.bukkit.Location loc = bukkitPlayer.getLocation();
        loc.setX(position.x());
        loc.setY(position.y());
        loc.setZ(position.z());
        bukkitPlayer.teleport(loc);
    }

    @Override
    public org.pistonworks.core.api.model.Orientation getOrientation()
    {
        org.bukkit.Location loc = bukkitPlayer.getLocation();
        return new org.pistonworks.core.api.model.Orientation(loc.getYaw(), loc.getPitch());
    }

    @Override
    public void setOrientation(org.pistonworks.core.api.model.Orientation orientation)
    {
        org.bukkit.Location loc = bukkitPlayer.getLocation();
        loc.setYaw((float) orientation.yaw());
        loc.setPitch((float) orientation.pitch());
        bukkitPlayer.teleport(loc);
    }

    @Override
    public double getHealth()
    {
        return bukkitPlayer.getHealth();
    }

    @Override
    public void setHealth(double health)
    {
        bukkitPlayer.setHealth(health);
    }

    @Override
    public void setMaxHealth(double maxHealth)
    {
        AttributeInstance maxHealthAttribute = ((Attributable) bukkitPlayer).getAttribute(Attribute.MAX_HEALTH);
        maxHealthAttribute.setBaseValue(maxHealth);
    }

    @Override
    public double getMaxHealth()
    {
        AttributeInstance maxHealthAttribute = ((Attributable) bukkitPlayer).getAttribute(Attribute.MAX_HEALTH);
        return maxHealthAttribute.getBaseValue();
    }

    public World getWorld()
    {
        // TODO: Implement SpigotWorld wrapper
        return null;
    }

    /**
     * Gets the underlying Bukkit player.
     *
     * @return the Bukkit Player instance
     */
    public org.bukkit.entity.Player getBukkitPlayer()
    {
        return bukkitPlayer;
    }
}
