package com.example;

import org.bukkit.plugin.java.JavaPlugin;
import org.pistonworks.core.api.PistonCore;
import org.pistonworks.core.api.command.Command;
import org.pistonworks.core.api.command.CommandContext;
import org.pistonworks.core.api.event.EventListener;
import org.pistonworks.core.api.event.player.PlayerJoinEvent;

/**
 * Example plugin that demonstrates zero platform-specific code.
 * Note: The only platform-specific part is extending JavaPlugin for Spigot.
 * Other platforms would extend different base classes (e.g., ModInitializer for Fabric).
 */
public class ExamplePlugin extends JavaPlugin
{

    @Override
    public void onEnable()
    {
        // Initialize Piston Core - this will auto-discover this plugin
        PistonCore.autoInitialize();

        // Register a command - completely platform agnostic
        PistonCore.getCommandService().registerCommand(new Command()
        {
            @Override
            public String getName()
            {
                return "hello";
            }

            @Override
            public String getDescription()
            {
                return "Says hello to the player";
            }

            @Override
            public void execute(CommandContext context)
            {
                context.getSender().sendMessage("Hello, " + context.getSender().getName() + "!");
            }
        });

        // Register an event listener - completely platform agnostic
        PistonCore.getEventService().registerListener(new EventListener<PlayerJoinEvent>()
        {
            @Override
            public void onEvent(PlayerJoinEvent event)
            {
                event.getPlayer().sendMessage("Welcome to the server!");
            }

            @Override
            public Class<PlayerJoinEvent> getEventType()
            {
                return PlayerJoinEvent.class;
            }
        });

        getLogger().info("ExamplePlugin has been enabled!");
    }

    @Override
    public void onDisable()
    {
        getLogger().info("ExamplePlugin has been disabled!");
    }
}
