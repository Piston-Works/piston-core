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

**Important:** The `pluginManagement` block must be at the top of your `settings.gradle.kts` file, before any other configuration.

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

import org.bukkit.plugin.java.JavaPlugin;
import org.pistonworks.core.api.PistonCore;
import org.pistonworks.core.api.command.Command;
import org.pistonworks.core.api.command.CommandHandler;
import org.pistonworks.core.api.command.Arg;
import org.pistonworks.core.api.event.EventHandler;
import org.pistonworks.core.api.event.player.PlayerJoinEvent;
import org.pistonworks.core.api.model.CommandSender;
import org.pistonworks.core.api.model.Player;

public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Initialize Piston Core
        PistonCore.autoInitialize();
        
        // Register commands using annotations
        PistonCore.getCommandService().registerHandler(new MyCommands());
        
        // Register event listeners
        PistonCore.getEventService().registerListener(PlayerJoinEvent.class, this::onPlayerJoin);
        
        getLogger().info("MyPlugin enabled!");
    }
    
    // Event listener method
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("Welcome to the server, " + player.getName() + "!");
    }
    
    // Command handler class
    public static class MyCommands extends CommandHandler {
        
        @Command(value = "heal", description = "Heal yourself or another player", permission = "myplugin.heal")
        public void heal(CommandSender sender, 
                        @Arg(value = "player", optional = true) String playerName,
                        @Arg(value = "amount", optional = true, defaultValue = "20") int amount) {
            
            String target = playerName != null ? playerName : sender.getName();
            sender.sendMessage("Healing " + target + " for " + amount + " health!");
            // Implementation would go here
        }
        
        @Command(value = "broadcast", aliases = {"bc"}, description = "Broadcast a message")
        public void broadcast(CommandSender sender, @Arg("message") String message) {
            // Broadcast to all players
            sender.sendMessage("Broadcasting: " + message);
            // Implementation would go here
        }
    }
}
```

### 4. Platform-Specific Setup

The example above shows Spigot setup (extending `JavaPlugin`). For other platforms:

- **Fabric**: Extend `ModInitializer` 
- **Forge**: Use `@Mod` annotation
- **Velocity**: Extend velocity plugin class

The core Piston Core API calls remain exactly the same across all platforms!

## Key Features

- **Cross-Platform**: Write once, run on Spigot, Fabric, Forge, and more
- **Annotation-Based Commands**: Simple `@Command` annotations with automatic argument parsing
- **Type-Safe Events**: Strongly typed event system with lambda support
- **Zero Boilerplate**: Minimal setup code required
- **Auto-Discovery**: Automatic plugin initialization and registration

## Supported platforms

- [x] Spigot/any Spigot fork
- [ ] Fabric (planned)
- [ ] Forge (planned)
- [ ] NeoForge (planned)
- [ ] Velocity (planned)
- [ ] BungeeCord (planned)
- [ ] Folia (planned)

## What's not supported

- [ ] Client side mods
- [ ] Hybrid server software (e.g. Mohist, SpongeForge, etc.)

## Supported use cases

- ✅ Creating custom commands
- ✅ Listening for events
- ✅ Almost anything you could do with a Spigot plugin
- ❌ This is **not** a modding framework, so you cannot create custom blocks, items, entities, etc.

## Repositories

https://nexus.alecj.tk/#browse/browse:maven-releases:org%2Fpistonworks