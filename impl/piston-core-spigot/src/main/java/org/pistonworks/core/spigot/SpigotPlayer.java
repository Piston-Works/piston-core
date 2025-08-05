package org.pistonworks.core.spigot;

import java.util.UUID;

/**
 * Spigot implementation of the Piston Player interface.
 * Wraps a Bukkit Player to provide the Piston API interface.
 */
public class SpigotPlayer implements org.pistonworks.core.api.model.Player {

    private final org.bukkit.entity.Player bukkitPlayer;

    public SpigotPlayer(org.bukkit.entity.Player bukkitPlayer) {
        this.bukkitPlayer = bukkitPlayer;
    }

    @Override
    public UUID getUniqueId() {
        return bukkitPlayer.getUniqueId();
    }

    @Override
    public String getName() {
        return bukkitPlayer.getName();
    }

    @Override
    public void sendMessage(String message) {
        bukkitPlayer.sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return bukkitPlayer.hasPermission(permission);
    }

    @Override
    public boolean isOnline() {
        return bukkitPlayer.isOnline();
    }

    @Override
    public void kick(String reason) {
        bukkitPlayer.kickPlayer(reason);
    }

    /**
     * Gets the underlying Bukkit player.
     * @return the Bukkit Player instance
     */
    public org.bukkit.entity.Player getBukkitPlayer() {
        return bukkitPlayer;
    }
}
