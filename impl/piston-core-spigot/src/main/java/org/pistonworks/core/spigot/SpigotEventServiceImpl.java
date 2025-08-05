package org.pistonworks.core.spigot;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.pistonworks.core.api.event.Event;
import org.pistonworks.core.api.service.EventService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Spigot implementation of the EventService.
 * Bridges Bukkit events to Piston events and manages event listeners.
 */
public class SpigotEventServiceImpl implements EventService, Listener {

    private final JavaPlugin plugin;
    private final List<Object> registeredListeners = new CopyOnWriteArrayList<>();
    private final Map<Class<? extends Event>, List<Consumer<? extends Event>>> functionalHandlers = new HashMap<>();

    public SpigotEventServiceImpl(JavaPlugin plugin) {
        this.plugin = plugin;
        // Register this class as a Bukkit listener to bridge events
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void registerListener(Object listener) {
        if (!registeredListeners.contains(listener)) {
            registeredListeners.add(listener);
        }
    }

    @Override
    public void unregisterListener(Object listener) {
        registeredListeners.remove(listener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> void registerHandler(Class<T> eventClass, Consumer<T> handler) {
        functionalHandlers.computeIfAbsent(eventClass, k -> new ArrayList<>()).add((Consumer<Event>) handler);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> T fireEvent(T event) {
        // Fire to functional handlers
        List<Consumer<? extends Event>> handlers = functionalHandlers.get(event.getClass());
        if (handlers != null) {
            for (Consumer<? extends Event> handler : handlers) {
                ((Consumer<T>) handler).accept(event);
                if (event.isCancelled()) {
                    break;
                }
            }
        }

        // Fire to registered listener objects
        for (Object listener : registeredListeners) {
            fireEventToListener(event, listener);
            if (event.isCancelled()) {
                break;
            }
        }

        return event;
    }

    @Override
    public List<Object> getRegisteredListeners() {
        return new ArrayList<>(registeredListeners);
    }

    @Override
    public boolean hasListeners(Class<? extends Event> eventClass) {
        return !registeredListeners.isEmpty() ||
               (functionalHandlers.containsKey(eventClass) && !functionalHandlers.get(eventClass).isEmpty());
    }

    private void fireEventToListener(Event event, Object listener) {
        Class<?> listenerClass = listener.getClass();
        for (Method method : listenerClass.getMethods()) {
            if (method.isAnnotationPresent(EventHandler.class) &&
                method.getParameterCount() == 1 &&
                method.getParameterTypes()[0].isAssignableFrom(event.getClass())) {
                try {
                    method.setAccessible(true);
                    method.invoke(listener, event);
                } catch (Exception e) {
                    plugin.getLogger().warning("Error firing event to listener: " + e.getMessage());
                }
            }
        }
    }

    // Bukkit event bridges - these convert Bukkit events to Piston events

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SpigotPlayer pistonPlayer = new SpigotPlayer(event.getPlayer());
        org.pistonworks.core.api.event.playerevent.PlayerJoinEvent pistonEvent =
            new org.pistonworks.core.api.event.playerevent.PlayerJoinEvent(pistonPlayer, event.getJoinMessage());
        fireEvent(pistonEvent);

        // Apply changes back to Bukkit event
        if (pistonEvent.isCancelled()) {
            // Bukkit PlayerJoinEvent is not cancellable, but we can kick the player
            event.getPlayer().kickPlayer("Join cancelled");
        }
        event.setJoinMessage(pistonEvent.getJoinMessage());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        SpigotPlayer pistonPlayer = new SpigotPlayer(event.getPlayer());
        org.pistonworks.core.api.event.playerevent.PlayerQuitEvent pistonEvent =
            new org.pistonworks.core.api.event.playerevent.PlayerQuitEvent(pistonPlayer, event.getQuitMessage());
        fireEvent(pistonEvent);

        // Apply changes back to Bukkit event
        event.setQuitMessage(pistonEvent.getQuitMessage());
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        SpigotPlayer pistonPlayer = new SpigotPlayer(event.getPlayer());
        org.pistonworks.core.api.event.playerevent.PlayerChatEvent pistonEvent =
            new org.pistonworks.core.api.event.playerevent.PlayerChatEvent(pistonPlayer, event.getMessage());
        fireEvent(pistonEvent);

        // Apply changes back to Bukkit event
        event.setCancelled(pistonEvent.isCancelled());
        event.setMessage(pistonEvent.getMessage());
    }
}
