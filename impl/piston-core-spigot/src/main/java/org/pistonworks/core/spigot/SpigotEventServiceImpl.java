package org.pistonworks.core.spigot;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.pistonworks.core.api.event.playerevent.PlayerChatEvent;
import org.pistonworks.core.api.model.Orientation;
import org.pistonworks.core.api.model.Position;
import org.pistonworks.core.common.event.EventFactory;
import org.pistonworks.core.common.event.EventServiceImpl;
import org.pistonworks.core.common.event.RegisteredListener;

/**
 * Minimal Spigot event service implementation.
 * This class only handles platform-specific event bridging - all logic is in common/api.
 */
public class SpigotEventServiceImpl extends EventServiceImpl implements Listener
{

    private final SpigotPlugin plugin;

    /**
     * Creates a new SpigotEventServiceImpl instance.
     * 
     * @param plugin The SpigotPlugin instance
     */
    public SpigotEventServiceImpl(SpigotPlugin plugin)
    {
        this.plugin = plugin;
        // Register this as a Bukkit listener to bridge platform events
        plugin.getBukkitPlugin().getServer().getPluginManager().registerEvents(this, plugin.getBukkitPlugin());
    }

    // ===== BUKKIT EVENT BRIDGES =====
    // These methods simply convert Bukkit events to Piston Core events

    /**
     * Handles Bukkit PlayerJoinEvent and bridges it to Piston Core.
     *
     * @param bukkitEvent The Bukkit PlayerJoinEvent
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent bukkitEvent)
    {
        SpigotPlayer player = new SpigotPlayer(bukkitEvent.getPlayer());
        org.pistonworks.core.api.event.playerevent.PlayerJoinEvent pistonEvent =
                EventFactory.createPlayerJoinEvent(player, bukkitEvent.getJoinMessage());

        fireEvent(pistonEvent);

        // Apply any changes back to Bukkit event
        bukkitEvent.setJoinMessage(pistonEvent.getJoinMessage());
    }

    /**
     * Handles Bukkit PlayerQuitEvent and bridges it to Piston Core.
     *
     * @param bukkitEvent The Bukkit PlayerQuitEvent
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent bukkitEvent)
    {
        SpigotPlayer player = new SpigotPlayer(bukkitEvent.getPlayer());
        org.pistonworks.core.api.event.playerevent.PlayerQuitEvent pistonEvent =
                EventFactory.createPlayerQuitEvent(player, bukkitEvent.getQuitMessage());

        fireEvent(pistonEvent);

        // Apply any changes back to Bukkit event
        bukkitEvent.setQuitMessage(pistonEvent.getQuitMessage());
    }

    /**
     * Handles Bukkit AsyncPlayerChatEvent and bridges it to Piston Core.
     *
     * @param bukkitEvent The Bukkit AsyncPlayerChatEvent
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent bukkitEvent)
    {
        SpigotPlayer player = new SpigotPlayer(bukkitEvent.getPlayer());
        PlayerChatEvent pistonEvent = EventFactory.createPlayerChatEvent(
                player, bukkitEvent.getMessage(), bukkitEvent.getFormat());

        fireEvent(pistonEvent);

        // Apply any changes back to Bukkit event
        bukkitEvent.setMessage(pistonEvent.getMessage());
        bukkitEvent.setFormat(pistonEvent.getFormat());
        bukkitEvent.setCancelled(pistonEvent.isCancelled());
    }

    /**
     * Handles Bukkit PlayerMoveEvent and bridges it to Piston Core.
     *
     * @param bukkitEvent The Bukkit PlayerMoveEvent
     */
    @EventHandler
    public void onPlayerMove(org.bukkit.event.player.PlayerMoveEvent bukkitEvent)
    {
        // Null safety checks
        if (bukkitEvent.getTo() == null)
        {
            return;
        }

        SpigotPlayer player = new SpigotPlayer(bukkitEvent.getPlayer());
        Position newPosition = convertLocationToPosition(bukkitEvent.getTo());
        Position oldPosition = convertLocationToPosition(bukkitEvent.getFrom());
        Orientation newOrientation = convertLocationToOrientation(bukkitEvent.getTo());
        Orientation oldOrientation = convertLocationToOrientation(bukkitEvent.getFrom());

        org.pistonworks.core.api.event.playerevent.PlayerMoveEvent pistonEvent =
                EventFactory.createPlayerMoveEvent(player, newPosition, oldPosition, newOrientation, oldOrientation);

        fireEvent(pistonEvent);

        // Apply any changes back to Bukkit event
        if (pistonEvent.isCancelled())
        {
            bukkitEvent.setCancelled(true);
        }
    }

    // ===== HELPER METHODS =====

    /**
     * Converts a Bukkit Location to a Piston Core Position.
     */
    private Position convertLocationToPosition(Location location)
    {
        SpigotWorld world = new SpigotWorld(location.getWorld());
        return new Position(world, location.getX(), location.getY(), location.getZ());
    }

    /**
     * Converts a Bukkit Location to a Piston Core Orientation.
     */
    private Orientation convertLocationToOrientation(Location location)
    {
        return new Orientation(location.getYaw(), location.getPitch());
    }

    @Override
    protected void handleListenerException(RegisteredListener listener,
                                           org.pistonworks.core.api.event.Event event, Exception exception)
    {
        // Use Bukkit's logging system
        plugin.getBukkitPlugin().getLogger().severe(String.format("Error in event listener %s handling event %s: %s",
                listener.getClass().getSimpleName(), event.getEventName(), exception.getMessage()));
        exception.printStackTrace();
    }
}
