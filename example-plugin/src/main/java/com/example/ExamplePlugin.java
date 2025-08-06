package com.example;

import org.pistonworks.core.api.command.Command;
import org.pistonworks.core.api.command.CommandContext;
import org.pistonworks.core.api.event.EventListener;
import org.pistonworks.core.api.event.player.PlayerJoinEvent;
import org.pistonworks.core.api.plugin.PistonPlugin;
import org.pistonworks.core.api.PistonCore;

/**
 * Example plugin that demonstrates zero platform-specific code.
 */
public class ExamplePlugin extends PistonPlugin
{

    @Override
    public void onEnable()
    {
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
