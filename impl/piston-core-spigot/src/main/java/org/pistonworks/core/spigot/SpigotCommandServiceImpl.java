package org.pistonworks.core.spigot;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.pistonworks.core.api.service.CommandService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SpigotCommandServiceImpl implements CommandService {

    private final JavaPlugin plugin;
    private final Map<String, org.pistonworks.core.api.service.CommandExecutor> registeredCommands = new HashMap<>();
    private SimpleCommandMap commandMap;

    public SpigotCommandServiceImpl(JavaPlugin plugin) {
        this.plugin = plugin;
        initializeCommandMap();
    }

    private void initializeCommandMap() {
        try {
            Field commandMapField = plugin.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (SimpleCommandMap) commandMapField.get(plugin.getServer());
        } catch (Exception e) {
            plugin.getLogger().warning("Could not access command map: " + e.getMessage());
        }
    }

    @Override
    public void registerCommand(String commandName, org.pistonworks.core.api.service.CommandExecutor executor) {
        registerCommand(commandName, executor, "No description provided", "/" + commandName);
    }

    @Override
    public void registerCommand(String commandName, org.pistonworks.core.api.service.CommandExecutor executor,
                               String description, String usage, String... aliases) {
        // Store the executor for later use
        registeredCommands.put(commandName, executor);

        // Try to get existing plugin command first
        PluginCommand pluginCommand = plugin.getCommand(commandName);

        if (pluginCommand != null) {
            // Command exists in plugin.yml, use it
            pluginCommand.setExecutor(new SpigotCommandWrapper(executor));
            pluginCommand.setDescription(description);
            pluginCommand.setUsage(usage);
            if (aliases.length > 0) {
                pluginCommand.setAliases(List.of(aliases));
            }
        } else {
            // Create a new command dynamically
            try {
                Command command = new Command(commandName, description, usage, List.of(aliases)) {
                    @Override
                    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                        SpigotCommandWrapper wrapper = new SpigotCommandWrapper(executor);
                        return wrapper.onCommand(sender, this, commandLabel, args);
                    }
                };

                if (commandMap != null) {
                    commandMap.register(plugin.getName(), command);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to register command " + commandName + ": " + e.getMessage());
            }
        }
    }

    @Override
    public boolean unregisterCommand(String commandName) {
        if (registeredCommands.containsKey(commandName)) {
            registeredCommands.remove(commandName);

            // Try to unregister from Bukkit
            PluginCommand pluginCommand = plugin.getCommand(commandName);
            if (pluginCommand != null) {
                pluginCommand.setExecutor(null);
                return true;
            }

            // Try to unregister from command map
            if (commandMap != null) {
                try {
                    Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
                    knownCommandsField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);

                    Command removed = knownCommands.remove(commandName);
                    return removed != null;
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to unregister command " + commandName + ": " + e.getMessage());
                }
            }
        }
        return false;
    }

    @Override
    public List<String> getRegisteredCommands() {
        return new ArrayList<>(registeredCommands.keySet());
    }

    @Override
    public boolean executeCommand(org.pistonworks.core.api.model.Player sender, String commandLine) {
        if (commandLine == null || commandLine.trim().isEmpty()) {
            return false;
        }

        String[] parts = commandLine.trim().split("\\s+");
        String commandName = parts[0].toLowerCase();
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);

        org.pistonworks.core.api.service.CommandExecutor executor = registeredCommands.get(commandName);
        if (executor != null) {
            return executor.execute(sender, commandName, args);
        }

        return false;
    }

    /**
     * Wrapper class to bridge Bukkit CommandExecutor to Piston CommandExecutor
     */
    private static class SpigotCommandWrapper implements CommandExecutor {
        private final org.pistonworks.core.api.service.CommandExecutor pistonExecutor;

        public SpigotCommandWrapper(org.pistonworks.core.api.service.CommandExecutor pistonExecutor) {
            this.pistonExecutor = pistonExecutor;
        }

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            org.pistonworks.core.api.model.Player pistonPlayer = null;

            if (sender instanceof Player) {
                pistonPlayer = new SpigotPlayer((Player) sender);
            } else {
                // For console senders, we could create a console player wrapper
                // For now, we'll create a null player and let the executor handle it
                pistonPlayer = new ConsolePlayer(sender);
            }

            return pistonExecutor.execute(pistonPlayer, command.getName(), args);
        }
    }

    /**
     * Console player implementation for non-player command senders
     */
    private static class ConsolePlayer implements org.pistonworks.core.api.model.Player {
        private final CommandSender sender;

        public ConsolePlayer(CommandSender sender) {
            this.sender = sender;
        }

        @Override
        public java.util.UUID getUniqueId() {
            return java.util.UUID.fromString("00000000-0000-0000-0000-000000000000");
        }

        @Override
        public String getName() {
            return sender.getName();
        }

        @Override
        public void sendMessage(String message) {
            sender.sendMessage(message);
        }

        @Override
        public boolean hasPermission(String permission) {
            return sender.hasPermission(permission);
        }

        @Override
        public boolean isOnline() {
            return true; // Console is always "online"
        }

        @Override
        public void kick(String reason) {
            // Console cannot be kicked
        }
    }
}