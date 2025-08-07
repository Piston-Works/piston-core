# Piston Core

![Piston Core Logo](/images/piston-core-logo.png)

*In active development, not ready for use yet!*

Piston Core is a cross-platform API for building Minecraft server-side mods/plugins.

The goal is to provide a unified API that works across different server software, even modded software like Fabric and
Forge, for creating server-side plugins (and mods that act like plugins).

## Quick Start

### 1. Configure Plugin Resolution

First, create or update your `settings.gradle.kts` file to tell Gradle where to find the Piston Core plugin:

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://nexus.alecj.tk/repository/maven-releases/")
        }
    }
}

rootProject.name = "your-plugin-name"
```

**Important:** The `pluginManagement` block must be at the top of your `settings.gradle.kts` file, before any other
configuration.

### 2. Add the Gradle Plugin

```kotlin
plugins {
    id("org.pistonworks.core") version "0.3.0"
}

repositories {
    maven {
        url = uri("https://nexus.alecj.tk/repository/maven-releases/")
    }
    mavenCentral()
}

group = "com.example"
version = "1.0.0"
```

### 3. Create Your Plugin

```java
package com.example;

import org.pistonworks.core.api.PistonCore;
import org.pistonworks.core.api.command.Command;
import org.pistonworks.core.api.command.CommandHandler;
import org.pistonworks.core.api.command.Arg;
import org.pistonworks.core.api.event.EventListener;
import org.pistonworks.core.api.event.player.PlayerJoinEvent;
import org.pistonworks.core.api.model.CommandSender;
import org.pistonworks.core.api.plugin.PistonPlugin;

/**
 * Example plugin that demonstrates zero platform-specific code.
 * This plugin will work on ANY platform - Spigot, Fabric, Forge, Velocity, etc.
 */
public class MyPlugin extends PistonPlugin
{

    @Override
    public void onEnable()
    {
        // Register command handler - completely platform agnostic
        PistonCore.getCommandService().registerHandler(new MyCommands());

        // Register event listener - completely platform agnostic
        PistonCore.getEventService().registerListener(PlayerJoinEvent.class, new EventListener<PlayerJoinEvent>()
        {
            @Override
            public void handle(PlayerJoinEvent event)
            {
                event.getPlayer().sendMessage("Welcome to the server, " + event.getPlayer().getName() + "!");
            }
        });

        // Use platform-agnostic logging
        getLogger().info("MyPlugin has been enabled!");
    }

    @Override
    public void onDisable()
    {
        getLogger().info("MyPlugin has been disabled!");
    }

    /**
     * Command handler class using annotation-based commands
     */
    public static class MyCommands extends CommandHandler
    {

        @Command(value = "heal", description = "Heal yourself or another player", permission = "myplugin.heal")
        public void heal(CommandSender sender,
                         @Arg(value = "player", optional = true) String playerName,
                         @Arg(value = "amount", optional = true, defaultValue = "20") int amount)
        {

            String target = playerName != null ? playerName : sender.getName();
            sender.sendMessage("Healing " + target + " for " + amount + " health!");
            // Implementation would go here
        }

        @Command(value = "broadcast", aliases = {"bc"}, description = "Broadcast a message")
        public void broadcast(CommandSender sender, @Arg("message") String message)
        {
            // Broadcast to all players
            sender.sendMessage("Broadcasting: " + message);
            // Implementation would go here
        }
    }
}
```

### 4. Platform Setup

The beauty of Piston Core is that **your plugin code is 100% platform-independent**. The same JAR file will work on:

- **Spigot/Paper/Bukkit**: Drop into plugins folder
- **Fabric**: Install as a mod
- **Forge**: Install as a mod
- **Velocity**: Drop into plugins folder

No platform-specific code required! Piston Core handles all the platform differences for you.

## Key Features

### ✅ Zero Platform-Specific Code

Your plugins extend `PistonPlugin` instead of platform-specific base classes. No more `JavaPlugin`, `ModInitializer`, or
other platform dependencies in your code.

### ✅ Annotation-Based Commands

Create commands using simple annotations:

```java
@Command(value = "heal", description = "Heal a player", permission = "myplugin.heal")
public void heal(CommandSender sender, @Arg("player") String playerName) {
    sender.sendMessage("Healing " + playerName + "!");
}
```

### ✅ Unified Logging

Use the built-in logger with standard levels:

```java
getLogger().

debug("Debug message");

getLogger().

info("Info message");

getLogger().

warn("Warning message");

getLogger().

error("Error message");

getLogger().

critical("Critical message");
```

### ✅ Simple Event Handling

Listen to events across all platforms:

```java
PistonCore.getEventService().registerListener(PlayerJoinEvent.class, event -> {
    event.getPlayer().sendMessage("Welcome!");
});
```

### ✅ Automatic Initialization

No need to manually initialize Piston Core - it happens automatically when you extend `PistonPlugin`.
