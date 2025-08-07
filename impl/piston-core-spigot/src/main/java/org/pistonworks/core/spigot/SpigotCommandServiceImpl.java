package org.pistonworks.core.spigot;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.pistonworks.core.api.command.CommandHandler;
import org.pistonworks.core.api.command.TabCompleter;
import org.pistonworks.core.api.model.CommandSender;
import org.pistonworks.core.api.service.CommandService;

import java.lang.reflect.Field;
import java.util.*;

public final class SpigotCommandServiceImpl implements CommandService
{

    private final JavaPlugin plugin;
    private final Map<String, org.pistonworks.core.api.service.CommandExecutor> registeredCommands = new HashMap<>();
    private SimpleCommandMap commandMap;

    public SpigotCommandServiceImpl(JavaPlugin plugin)
    {
        this.plugin = plugin;
        initializeCommandMap();
    }

    private void initializeCommandMap()
    {
        try
        {
            Field commandMapField = plugin.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (SimpleCommandMap) commandMapField.get(plugin.getServer());
        } catch (Exception e)
        {
            plugin.getLogger().warning("Could not access command map: " + e.getMessage());
        }
    }

    @Override
    public void registerCommand(String commandName, org.pistonworks.core.api.service.CommandExecutor executor)
    {
        registerCommand(commandName, executor, "No description provided", "/" + commandName);
    }

    @Override
    public void registerCommand(String commandName, org.pistonworks.core.api.service.CommandExecutor executor,
                                String description, String usage, String... aliases)
    {
        // Store the executor for later use
        registeredCommands.put(commandName, executor);

        // Try to get existing plugin command first
        PluginCommand pluginCommand = plugin.getCommand(commandName);

        if (pluginCommand != null)
        {
            // Command exists in plugin.yml, use it
            pluginCommand.setExecutor(new SpigotCommandWrapper(executor));
            pluginCommand.setDescription(description);
            pluginCommand.setUsage(usage);
            if (aliases.length > 0)
            {
                pluginCommand.setAliases(List.of(aliases));
            }
        } else
        {
            // Create a new command dynamically
            try
            {
                Command command = new Command(commandName, description, usage, List.of(aliases))
                {
                    @Override
                    public boolean execute(org.bukkit.command.CommandSender sender, String commandLabel, String[] args)
                    {
                        SpigotCommandWrapper wrapper = new SpigotCommandWrapper(executor);
                        return wrapper.onCommand(sender, this, commandLabel, args);
                    }
                };

                if (commandMap != null)
                {
                    commandMap.register(plugin.getName(), command);
                }
            } catch (Exception e)
            {
                plugin.getLogger().warning("Failed to register command " + commandName + ": " + e.getMessage());
            }
        }
    }

    @Override
    public boolean unregisterCommand(String commandName)
    {
        if (registeredCommands.containsKey(commandName))
        {
            registeredCommands.remove(commandName);

            // Try to unregister from Bukkit
            PluginCommand pluginCommand = plugin.getCommand(commandName);
            if (pluginCommand != null)
            {
                pluginCommand.setExecutor(null);
                return true;
            }

            // Try to unregister from command map
            if (commandMap != null)
            {
                try
                {
                    Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
                    knownCommandsField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);

                    Command removed = knownCommands.remove(commandName);
                    return removed != null;
                } catch (Exception e)
                {
                    plugin.getLogger().warning("Failed to unregister command " + commandName + ": " + e.getMessage());
                }
            }
        }
        return false;
    }

    @Override
    public List<String> getRegisteredCommands()
    {
        return new ArrayList<>(registeredCommands.keySet());
    }

    @Override
    public boolean executeCommand(CommandSender sender, String commandLine)
    {
        if (commandLine == null || commandLine.trim().isEmpty())
        {
            return false;
        }

        String[] parts = commandLine.trim().split("\\s+");
        String commandName = parts[0].toLowerCase();
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);

        org.pistonworks.core.api.service.CommandExecutor executor = registeredCommands.get(commandName);
        if (executor != null)
        {
            return executor.execute(sender, commandName, args);
        }

        return false;
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String commandLine)
    {
        if (commandLine == null || commandLine.trim().isEmpty())
        {
            return new ArrayList<>();
        }

        String[] parts = commandLine.split("\\s+", -1);
        if (parts.length == 0) return new ArrayList<>();

        String commandName = parts[0].toLowerCase();
        if (commandName.startsWith("/"))
        {
            commandName = commandName.substring(1);
        }

        org.pistonworks.core.api.service.CommandExecutor executor = registeredCommands.get(commandName);
        if (executor != null)
        {
            String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[]{""};
            // For now, return empty list since CommandExecutor doesn't have getTabCompletions method
            // This would need to be implemented properly when CommandExecutor interface is updated
            return new ArrayList<>();
        }

        return new ArrayList<>();
    }

    // Implementation of CommandRegistry methods required by CommandService
    @Override
    public void registerCommands(CommandHandler handler)
    {
        // Use reflection to find @Command annotated methods
        Class<?> clazz = handler.getClass();
        for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(org.pistonworks.core.api.command.Command.class)) {
                org.pistonworks.core.api.command.Command cmdAnnotation = method.getAnnotation(org.pistonworks.core.api.command.Command.class);

                // Get command name - use annotation value/name or method name
                String commandName = cmdAnnotation.value().isEmpty() ?
                    (cmdAnnotation.name().isEmpty() ? method.getName() : cmdAnnotation.name()) :
                    cmdAnnotation.value();

                // Create a command executor that invokes the annotated method
                org.pistonworks.core.api.service.CommandExecutor executor = (sender, command, args) -> {
                    try {
                        // Get method parameters
                        Class<?>[] paramTypes = method.getParameterTypes();
                        Object[] methodArgs = new Object[paramTypes.length];

                        // First parameter should always be CommandSender
                        if (paramTypes.length > 0 && CommandSender.class.isAssignableFrom(paramTypes[0])) {
                            methodArgs[0] = sender;

                            // Fill remaining parameters from command arguments
                            for (int i = 1; i < paramTypes.length && i - 1 < args.length; i++) {
                                if (paramTypes[i] == String.class) {
                                    methodArgs[i] = args[i - 1];
                                } else if (paramTypes[i] == int.class || paramTypes[i] == Integer.class) {
                                    try {
                                        methodArgs[i] = Integer.parseInt(args[i - 1]);
                                    } catch (NumberFormatException e) {
                                        sender.sendMessage("Invalid number: " + args[i - 1]);
                                        return false;
                                    }
                                } else {
                                    // For now, convert everything else to String
                                    methodArgs[i] = args[i - 1];
                                }
                            }

                            method.setAccessible(true);
                            method.invoke(handler, methodArgs);
                            return true;
                        }
                    } catch (Exception e) {
                        plugin.getLogger().severe("Error executing command " + commandName + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                    return false;
                };

                // Register the command with metadata from annotation
                String[] aliases = cmdAnnotation.aliases();
                registerCommand(commandName, executor, cmdAnnotation.description(), cmdAnnotation.usage(), aliases);
            }
        }
    }

    @Override
    public void registerCommands(Class<? extends CommandHandler> handlerClass)
    {
        try {
            // Create an instance of the handler class and register its commands
            CommandHandler handler = handlerClass.getDeclaredConstructor().newInstance();
            registerCommands(handler);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to create instance of command handler " + handlerClass.getName() + ": " + e.getMessage());
            throw new RuntimeException("Failed to register commands from class " + handlerClass.getName(), e);
        }
    }

    @Override
    public void unregisterCommands(CommandHandler handler)
    {
        // This would delegate to a CommandRegistry implementation
        throw new UnsupportedOperationException("CommandHandler unregistration not yet implemented in Spigot implementation");
    }

    @Override
    public void unregisterCommands(Class<? extends CommandHandler> handlerClass)
    {
        // This would delegate to a CommandRegistry implementation
        throw new UnsupportedOperationException("CommandHandler unregistration not yet implemented in Spigot implementation");
    }

    @Override
    public void registerTabCompleter(String type, TabCompleter completer)
    {
        // This would delegate to a CommandRegistry implementation
        throw new UnsupportedOperationException("TabCompleter registration not yet implemented in Spigot implementation");
    }

    @Override
    public org.pistonworks.core.api.command.CommandInfo getCommandInfo(String commandName)
    {
        // This would delegate to a CommandRegistry implementation
        throw new UnsupportedOperationException("CommandInfo retrieval not yet implemented in Spigot implementation");
    }

    /**
     * Wrapper class to bridge Bukkit CommandExecutor to Piston CommandExecutor
     */
    private static class SpigotCommandWrapper implements CommandExecutor
    {
        private final org.pistonworks.core.api.service.CommandExecutor pistonExecutor;

        public SpigotCommandWrapper(org.pistonworks.core.api.service.CommandExecutor pistonExecutor)
        {
            this.pistonExecutor = pistonExecutor;
        }

        @Override
        public boolean onCommand(org.bukkit.command.CommandSender sender, Command command, String label, String[] args)
        {
            org.pistonworks.core.api.model.CommandSender pistonSender;

            if (sender instanceof Player)
            {
                pistonSender = new SpigotPlayer((Player) sender);
            } else
            {
                pistonSender = new SpigotConsoleCommandSender(sender);
            }

            return pistonExecutor.execute(pistonSender, command.getName(), args);
        }
    }
}

