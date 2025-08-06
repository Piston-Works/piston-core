package org.pistonworks.core.common.event.examples;

import org.pistonworks.core.api.event.EventHandler;
import org.pistonworks.core.api.event.EventPriority;
import org.pistonworks.core.api.event.playerevent.PlayerChatEvent;
import org.pistonworks.core.api.event.playerevent.PlayerJoinEvent;
import org.pistonworks.core.api.event.playerevent.PlayerQuitEvent;
import org.pistonworks.core.api.service.EventService;
import org.pistonworks.core.common.event.EventListeners;

/**
 * Example showing how clean and easy it is to use the modular event system.
 * This demonstrates both annotation-based and functional listener registration.
 */
public class EventSystemExamples {

    /**
     * Example of annotation-based event listeners (traditional approach)
     */
    public static class AnnotationBasedListener {

        @EventHandler(priority = EventPriority.HIGH)
        public void onPlayerJoin(PlayerJoinEvent event) {
            // Welcome new players with a custom message
            event.setJoinMessage("§6Welcome §e" + event.getPlayer().getName() + " §6to the server!");
        }

        @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
        public void onPlayerChat(PlayerChatEvent event) {
            String message = event.getMessage();

            // Block spam (example)
            if (message.length() > 100) {
                event.setCancelled(true);
                return;
            }

            // Format chat messages
            event.setFormat("§7[§bPlayer§7] §f%s: %s");
        }

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            event.setQuitMessage("§c" + event.getPlayer().getName() + " has left the server");
        }
    }

    /**
     * Example of functional event listeners (modern approach)
     */
    public static void registerFunctionalListeners(EventService eventService) {
        EventListeners.using(eventService)
            // Simple lambda listeners
            .listen(PlayerJoinEvent.class, event -> {
                System.out.println("Player joined: " + event.getPlayer().getName());
            })

            // More complex logic with method references
            .listen(PlayerChatEvent.class, EventSystemExamples::handleChatSecurity)

            // Chain multiple listeners easily
            .listen(PlayerQuitEvent.class, event -> {
                System.out.println("Player left: " + event.getPlayer().getName());
            });
    }

    /**
     * Example method that can be used as a method reference
     */
    private static void handleChatSecurity(PlayerChatEvent event) {
        String message = event.getMessage().toLowerCase();

        // Example security checks
        if (message.contains("hack") || message.contains("cheat")) {
            event.setCancelled(true);
            // Could also log this, notify admins, etc.
        }
    }

    /**
     * Example showing how easy it is to register listeners
     */
    public static void setupEventSystem(EventService eventService) {
        // Register annotation-based listener
        eventService.registerListener(new AnnotationBasedListener());

        // Register functional listeners
        registerFunctionalListeners(eventService);

        // Can also register individual listeners directly
        eventService.registerListener(PlayerJoinEvent.class, event -> {
            // Log join times for analytics
            System.out.printf("Player %s joined at %d%n",
                            event.getPlayer().getName(), event.getTimestamp());
        });
    }
}
