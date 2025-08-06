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

import org.pistonworks.core.api.PistonCore;
import org.pistonworks.core.api.command.Command;
import org.pistonworks.core.api.command.CommandContext;
import org.pistonworks.core.api.event.EventListener;
import org.pistonworks.core.api.event.player.PlayerJoinEvent;
import org.pistonworks.core.api.plugin.PistonPlugin;

/**
 * Example plugin that demonstrates zero platform-specific code.
 * This plugin will work on ANY platform - Spigot, Fabric, Forge, Velocity, etc.
 */
public class MyPlugin extends PistonPlugin {

    @Override
    public void onEnable() {
        // Register commands using the platform-agnostic API
        PistonCore.getCommandService().registerCommand(new Command() {
            @Override
            public String getName() {
                return "heal";
            }

            @Override
            public String getDescription() {
                return "Heal yourself or another player";
            }

            @Override
            public void execute(CommandContext context) {
                context.getSender().sendMessage("You have been healed!");
                // Implementation would go here
            }
        });

        // Register event listeners - completely platform agnostic
        PistonCore.getEventService().registerListener(new EventListener<PlayerJoinEvent>() {
            @Override
            public void onEvent(PlayerJoinEvent event) {
                event.getPlayer().sendMessage("Welcome to the server, " + event.getPlayer().getName() + "!");
            }

            @Override
            public Class<PlayerJoinEvent> getEventType() {
                return PlayerJoinEvent.class;
            }
        });

        // Use platform-agnostic logging
        getLogger().info("MyPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MyPlugin has been disabled!");
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
Your plugins extend `PistonPlugin` instead of platform-specific base classes. No more `JavaPlugin`, `ModInitializer`, or other platform dependencies in your code.

### ✅ Unified Logging
Use the built-in logger with standard levels:
```java
getLogger().debug("Debug message");
getLogger().info("Info message");  
getLogger().warn("Warning message");
getLogger().error("Error message");
getLogger().critical("Critical message");
```

### ✅ Cross-Platform Commands
Register commands that work everywhere:
```java
PistonCore.getCommandService().registerCommand(myCommand);
```

### ✅ Universal Events
Listen to events across all platforms:
```java
PistonCore.getEventService().registerListener(myListener);
```

### ✅ Automatic Initialization
No need to manually initialize Piston Core - it happens automatically when you extend `PistonPlugin`.
