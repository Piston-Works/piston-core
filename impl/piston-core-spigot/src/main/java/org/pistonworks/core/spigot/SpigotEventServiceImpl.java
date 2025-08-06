package org.pistonworks.core.spigot;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.pistonworks.core.api.event.playerevent.PlayerChatEvent;
import org.pistonworks.core.common.event.EventFactory;
import org.pistonworks.core.common.event.EventServiceImpl;
import org.pistonworks.core.common.event.RegisteredListener;

/**
 * Minimal Spigot event service implementation.
 * This class only handles platform-specific event bridging - all logic is in common/api.
 */
public class SpigotEventServiceImpl extends EventServiceImpl implements Listener {
    
    private final SpigotPlugin plugin;
    
    public SpigotEventServiceImpl(SpigotPlugin plugin) {
        this.plugin = plugin;
        // Register this as a Bukkit listener to bridge platform events
        plugin.getBukkitPlugin().getServer().getPluginManager().registerEvents(this, plugin.getBukkitPlugin());
    }
    
    // ===== BUKKIT EVENT BRIDGES =====
    // These methods simply convert Bukkit events to Piston Core events
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent bukkitEvent) {
        SpigotPlayer player = new SpigotPlayer(bukkitEvent.getPlayer());
        org.pistonworks.core.api.event.playerevent.PlayerJoinEvent pistonEvent = 
            EventFactory.createPlayerJoinEvent(player, bukkitEvent.getJoinMessage());
        
        fireEvent(pistonEvent);
        
        // Apply any changes back to Bukkit event
        bukkitEvent.setJoinMessage(pistonEvent.getJoinMessage());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent bukkitEvent) {
        SpigotPlayer player = new SpigotPlayer(bukkitEvent.getPlayer());
        org.pistonworks.core.api.event.playerevent.PlayerQuitEvent pistonEvent = 
            EventFactory.createPlayerQuitEvent(player, bukkitEvent.getQuitMessage());
        
        fireEvent(pistonEvent);
        
        // Apply any changes back to Bukkit event
        bukkitEvent.setQuitMessage(pistonEvent.getQuitMessage());
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent bukkitEvent) {
        SpigotPlayer player = new SpigotPlayer(bukkitEvent.getPlayer());
        PlayerChatEvent pistonEvent = EventFactory.createPlayerChatEvent(
            player, bukkitEvent.getMessage(), bukkitEvent.getFormat());
        
        fireEvent(pistonEvent);
        
        // Apply any changes back to Bukkit event
        bukkitEvent.setMessage(pistonEvent.getMessage());
        bukkitEvent.setFormat(pistonEvent.getFormat());
        bukkitEvent.setCancelled(pistonEvent.isCancelled());
    }
    
    @Override
    protected void handleListenerException(RegisteredListener listener, 
                                         org.pistonworks.core.api.event.Event event, Exception exception) {
        // Use Bukkit's logging system
        plugin.getBukkitPlugin().getLogger().severe(String.format("Error in event listener %s handling event %s: %s",
                                               listener.getClass().getSimpleName(), event.getEventName(), exception.getMessage()));
        exception.printStackTrace();
    }
}
