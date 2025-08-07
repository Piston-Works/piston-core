package org.pistonworks.core.spigot;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pistonworks.core.api.command.CommandHandler;
import org.pistonworks.core.api.command.TabCompleter;
import org.pistonworks.core.api.model.CommandSender;
import org.pistonworks.core.api.service.CommandService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spigot implementation of the CommandService interface.
 * Handles command registration, execution, and tab completion for Spigot/Bukkit servers.
 */
public final class SpigotCommandServiceImpl implements CommandService
{

    private final JavaPlugin plugin;
    private final Map<String, org.pistonworks.core.api.service.CommandExecutor> registeredCommands = new HashMap<>();
    private final Map<String, CommandMethodInfo> commandMethods = new HashMap<>();
    private SimpleCommandMap commandMap;

    /**
     * Creates a new SpigotCommandServiceImpl instance.
     *
     * @param plugin The JavaPlugin instance to register commands with
     */
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

        // Try to get the existing plugin command first
        PluginCommand pluginCommand = plugin.getCommand(commandName);

        if (pluginCommand != null)
        {
            // Command exists in plugin.yml, use it
            SpigotCommandWrapper wrapper = new SpigotCommandWrapper(executor, commandName, this);
            pluginCommand.setExecutor(wrapper);
            pluginCommand.setTabCompleter(wrapper); // THIS WAS MISSING - set the tab completer
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
                    public boolean execute(@NotNull org.bukkit.command.CommandSender sender, @NotNull String commandLabel, @NotNull String[] args)
                    {
                        SpigotCommandWrapper wrapper = new SpigotCommandWrapper(executor, commandName, SpigotCommandServiceImpl.this);
                        return wrapper.onCommand(sender, this, commandLabel, args);
                    }

                    @Override
                    public @NotNull List<String> tabComplete(@NotNull org.bukkit.command.CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException
                    {
                        SpigotCommandWrapper wrapper = new SpigotCommandWrapper(executor, commandName, SpigotCommandServiceImpl.this);
                        List<String> result = wrapper.onTabComplete(sender, this, alias, args);
                        return result != null ? result : new ArrayList<>();
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

            // Try to unregister from the command map
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
            // Try to find the original command method for tab completion
            return getCommandTabCompletions(commandName, parts);
        }

        return new ArrayList<>();
    }

    private List<String> getCommandTabCompletions(String commandName, String[] parts)
    {
        plugin.getLogger().info("getCommandTabCompletions called for command: " + commandName + " with parts: " + java.util.Arrays.toString(parts));

        CommandMethodInfo methodInfo = commandMethods.get(commandName);
        if (methodInfo == null)
        {
            plugin.getLogger().info("No method info found for command: " + commandName);
            return new ArrayList<>();
        }

        plugin.getLogger().info("Found method info for command: " + commandName + ", method: " + methodInfo.method.getName());

        // Calculate the correct argument index
        // parts[0] is the command name, parts[1] onwards are arguments
        // For tab completion, we want to complete the argument at the current position
        int argIndex = parts.length - 2; // -1 for command name, -1 because we're completing the current arg
        if (argIndex < 0) argIndex = 0; // First argument

        plugin.getLogger().info("Calculated argIndex: " + argIndex);

        // Get method parameters (skip the first parameter which is CommandSender)
        java.lang.reflect.Parameter[] parameters = methodInfo.method.getParameters();

        plugin.getLogger().info("Method has " + parameters.length + " parameters");

        // The parameter index in the method (accounting for CommandSender at index 0)
        int paramIndex = argIndex + 1;

        plugin.getLogger().info("Parameter index: " + paramIndex);

        if (paramIndex >= parameters.length)
        {
            plugin.getLogger().info("No more parameters to complete (paramIndex >= parameters.length)");
            return new ArrayList<>(); // No more parameters to complete
        }

        java.lang.reflect.Parameter param = parameters[paramIndex];
        plugin.getLogger().info("Checking parameter: " + param.getName() + " of type: " + param.getType().getSimpleName());

        // Check for @Arg annotation
        if (param.isAnnotationPresent(org.pistonworks.core.api.command.Arg.class))
        {
            org.pistonworks.core.api.command.Arg argAnnotation = param.getAnnotation(org.pistonworks.core.api.command.Arg.class);
            plugin.getLogger().info("Found @Arg annotation with completionType: " + argAnnotation.completionType());

            // Use custom completions if provided
            if (argAnnotation.completions().length > 0)
            {
                plugin.getLogger().info("Using custom completions: " + java.util.Arrays.toString(argAnnotation.completions()));
                return List.of(argAnnotation.completions());
            }

            // Use the completion type
            List<String> completions = getCompletionsForType(argAnnotation.completionType());
            plugin.getLogger().info("Generated completions for type " + argAnnotation.completionType() + ": " + completions);
            return completions;
        }

        plugin.getLogger().info("No @Arg annotation found, using type-based completion");
        // Fallback to type-based completion if no @Arg annotation
        return getCompletionsForParameterType(param.getType());
    }

    private List<String> getCompletionsForType(org.pistonworks.core.api.command.Arg.CompletionType completionType)
    {
        return switch (completionType)
        {
            case PLAYER -> getAllPlayerNames(); // All players, not just online
            case ONLINE_PLAYER -> getOnlinePlayerNames(); // Only online players
            case WORLD -> getWorldNames();
            case BOOLEAN -> List.of("true", "false");
            case INTEGER -> new ArrayList<>(); // No completion, but validates as integer
            case DOUBLE -> new ArrayList<>(); // No completion, but validates as double
            case CUSTOM -> new ArrayList<>(); // Should use the completion array (handled elsewhere)
            case METHOD -> getMethodBasedCompletions(); // Use @TabCompletion annotated method
            default -> new ArrayList<>();
        };
    }

    @SuppressWarnings("unused")
    private List<String> getCompletionsForParameterType(Class<?> paramType)
    {
        // For parameters without @Arg annotation, provide completions based on parameter type
        if (paramType == org.pistonworks.core.api.model.World.class)
        {
            return getWorldNames();
        }
        else if (paramType == org.pistonworks.core.api.model.entity.Player.class)
        {
            return getOnlinePlayerNames();
        }
        else if (paramType == boolean.class || paramType == Boolean.class)
        {
            return List.of("true", "false");
        }

        // For other types, provide no completions
        return new ArrayList<>();
    }

    private List<String> getAllPlayerNames()
    {
        // TODO: Implement to return all player names (online + offline)
        // For now, return online players as a fallback
        return getOnlinePlayerNames();
    }

    private List<String> getOnlinePlayerNames()
    {
        List<String> playerNames = new ArrayList<>();
        for (org.bukkit.entity.Player player : plugin.getServer().getOnlinePlayers())
        {
            playerNames.add(player.getName());
        }
        return playerNames;
    }

    private List<String> getMethodBasedCompletions()
    {
        // TODO: Implement @TabCompletion method support
        // This should find methods annotated with @TabCompletion and call them
        return new ArrayList<>();
    }

    private List<String> getWorldNames()
    {
        List<String> worldNames = new ArrayList<>();
        for (org.bukkit.World world : plugin.getServer().getWorlds())
        {
            worldNames.add(world.getName());
        }
        return worldNames;
    }

    /**
     * Converts a string argument to the expected parameter type
     */
    private Object convertArgumentToType(String arg, Class<?> paramType, java.lang.reflect.Parameter parameter)
    {
        try
        {
            // Check if parameter has @Arg annotation with completion type
            if (parameter.isAnnotationPresent(org.pistonworks.core.api.command.Arg.class))
            {
                org.pistonworks.core.api.command.Arg argAnnotation = parameter.getAnnotation(org.pistonworks.core.api.command.Arg.class);
                Object converted = convertByCompletionType(arg, argAnnotation.completionType(), paramType);
                if (converted != null)
                {
                    return converted;
                }
            }

            // Handle basic types
            if (paramType == String.class)
            {
                return arg;
            }
            else if (paramType == int.class || paramType == Integer.class)
            {
                return Integer.parseInt(arg);
            }
            else if (paramType == double.class || paramType == Double.class)
            {
                return Double.parseDouble(arg);
            }
            else if (paramType == boolean.class || paramType == Boolean.class)
            {
                return Boolean.parseBoolean(arg);
            }
            // Handle Piston Core object types
            else if (paramType == org.pistonworks.core.api.model.World.class)
            {
                return convertStringToWorld(arg);
            }
            else if (paramType == org.pistonworks.core.api.model.entity.Player.class)
            {
                return convertStringToPlayer(arg);
            }
            else
            {
                // For unknown types, try to return the string and let reflection handle it
                return arg;
            }
        }
        catch (NumberFormatException e)
        {
            plugin.getLogger().warning("Failed to convert argument '" + arg + "' to " + paramType.getSimpleName() + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Converts argument based on completion type from @Arg annotation
     */
    private Object convertByCompletionType(String arg, org.pistonworks.core.api.command.Arg.CompletionType completionType, Class<?> expectedType)
    {
        return switch (completionType)
        {
            case WORLD -> convertStringToWorld(arg);
            case PLAYER, ONLINE_PLAYER -> convertStringToPlayer(arg);
            case BOOLEAN -> Boolean.parseBoolean(arg);
            case INTEGER ->
            {
                try
                {
                    yield Integer.parseInt(arg);
                }
                catch (NumberFormatException e)
                {
                    yield null;
                }
            }
            case DOUBLE ->
            {
                try
                {
                    yield Double.parseDouble(arg);
                }
                catch (NumberFormatException e)
                {
                    yield null;
                }
            }
            case CUSTOM -> arg; // Return as string for custom types
            case METHOD -> arg; // Let the method-based completion handle it
            default -> null;
        };
    }

    /**
     * Converts a world name string to a Piston World object
     */
    private org.pistonworks.core.api.model.World convertStringToWorld(String worldName)
    {
        org.bukkit.World bukkitWorld = plugin.getServer().getWorld(worldName);
        if (bukkitWorld != null)
        {
            return new SpigotWorld(bukkitWorld);
        }
        return null;
    }

    /**
     * Converts a player name string to a Piston Player object
     */
    private org.pistonworks.core.api.model.entity.Player convertStringToPlayer(String playerName)
    {
        org.bukkit.entity.Player bukkitPlayer = plugin.getServer().getPlayer(playerName);
        if (bukkitPlayer != null)
        {
            return new SpigotPlayer(bukkitPlayer);
        }
        return null;
    }

    /**
     * Checks if a parameter is optional
     */
    private boolean isOptionalParameter(java.lang.reflect.Parameter parameter)
    {
        return parameter.isAnnotationPresent(org.pistonworks.core.api.command.Arg.class) &&
               parameter.getAnnotation(org.pistonworks.core.api.command.Arg.class).optional();
    }

    /**
     * Gets default value for a type
     */
    private Object getDefaultValueForType(Class<?> paramType)
    {
        if (paramType == String.class)
        {
            return null;
        }
        else if (paramType == int.class)
        {
            return 0;
        }
        else if (paramType == Integer.class)
        {
            return null;
        }
        else if (paramType == double.class)
        {
            return 0.0;
        }
        else if (paramType == Double.class)
        {
            return null;
        }
        else if (paramType == boolean.class)
        {
            return false;
        }
        else if (paramType == Boolean.class)
        {
            return null;
        }
        else
        {
            return null; // For object types
        }
    }

    // Implementation of CommandRegistry methods required by CommandService
    @Override
    public void registerCommands(CommandHandler handler)
    {
        // Use reflection to find @Command annotated methods
        Class<?> clazz = handler.getClass();
        for (java.lang.reflect.Method method : clazz.getDeclaredMethods())
        {
            if (method.isAnnotationPresent(org.pistonworks.core.api.command.Command.class))
            {
                org.pistonworks.core.api.command.Command cmdAnnotation = method.getAnnotation(org.pistonworks.core.api.command.Command.class);

                // Get command name - use annotation value/name or method name
                String commandName = cmdAnnotation.value().isEmpty() ?
                        (cmdAnnotation.name().isEmpty() ? method.getName() : cmdAnnotation.name()) :
                        cmdAnnotation.value();

                // Store method metadata for tab completion
                commandMethods.put(commandName, new CommandMethodInfo(handler, method));

                // Create a command executor that invokes the annotated method
                org.pistonworks.core.api.service.CommandExecutor executor = (sender, command, args) ->
                {
                    try
                    {
                        // Get method parameters
                        Class<?>[] paramTypes = method.getParameterTypes();
                        java.lang.reflect.Parameter[] parameters = method.getParameters();

                        // Check if we have enough arguments for required parameters
                        int requiredArgCount = 0;
                        for (int i = 1; i < parameters.length; i++) // Skip CommandSender parameter
                        {
                            java.lang.reflect.Parameter param = parameters[i];
                            boolean isOptional = param.isAnnotationPresent(org.pistonworks.core.api.command.Arg.class) &&
                                                param.getAnnotation(org.pistonworks.core.api.command.Arg.class).optional();

                            if (!isOptional)
                            {
                                requiredArgCount++;
                            }
                        }

                        // If we don't have enough arguments for required parameters, show usage
                        if (args.length < requiredArgCount)
                        {
                            sender.sendMessage("Usage: " + cmdAnnotation.usage());
                            return false;
                        }

                        Object[] methodArgs = new Object[paramTypes.length];

                        // The first parameter should always be CommandSender
                        if (paramTypes.length > 0 && CommandSender.class.isAssignableFrom(paramTypes[0]))
                        {
                            methodArgs[0] = sender;

                            // Fill remaining parameters from command arguments
                            for (int i = 1; i < paramTypes.length; i++)
                            {
                                int argIndex = i - 1; // Convert parameter index to argument index

                                if (argIndex < args.length)
                                {
                                    // We have an argument for this parameter
                                    methodArgs[i] = convertArgumentToType(args[argIndex], paramTypes[i], parameters[i]);
                                    if (methodArgs[i] == null && !isOptionalParameter(parameters[i]))
                                    {
                                        // Conversion failed for required parameter
                                        return false;
                                    }
                                } else
                                {
                                    // No argument provided for this parameter
                                    java.lang.reflect.Parameter param = parameters[i];
                                    boolean isOptional = param.isAnnotationPresent(org.pistonworks.core.api.command.Arg.class) &&
                                                        param.getAnnotation(org.pistonworks.core.api.command.Arg.class).optional();

                                    if (isOptional)
                                    {
                                        // Set default value for optional parameter
                                        methodArgs[i] = null;
                                        // Add more default value handling as needed
                                    } else
                                    {
                                        // This should have been caught by the earlier check, but just in case
                                        sender.sendMessage("Usage: " + cmdAnnotation.usage());
                                        return false;
                                    }
                                }
                            }

                            method.setAccessible(true);
                            method.invoke(handler, methodArgs);
                            return true;
                        }
                    } catch (Exception e)
                    {
                        plugin.getLogger().severe("Error executing command " + commandName + ": " + e.getMessage());
                        plugin.getLogger().severe("Stack trace: " + java.util.Arrays.toString(e.getStackTrace()));
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
        try
        {
            // Create an instance of the handler class and register its commands
            CommandHandler handler = handlerClass.getDeclaredConstructor().newInstance();
            registerCommands(handler);
        } catch (Exception e)
        {
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
    private class SpigotCommandWrapper implements CommandExecutor, org.bukkit.command.TabCompleter
    {
        private final org.pistonworks.core.api.service.CommandExecutor pistonExecutor;
        private final String commandName;

        public SpigotCommandWrapper(org.pistonworks.core.api.service.CommandExecutor pistonExecutor, String commandName, SpigotCommandServiceImpl commandService)
        {
            this.pistonExecutor = pistonExecutor;
            this.commandName = commandName;
        }

        @Override
        public boolean onCommand(@NotNull org.bukkit.command.CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
        {
            CommandSender pistonSender = convertSender(sender);
            return pistonExecutor.execute(pistonSender, command.getName(), args);
        }

        @Override
        public @Nullable List<String> onTabComplete(@NotNull org.bukkit.command.CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args)
        {
            // Debug logging
            plugin.getLogger().info("Tab completion called for command: " + command.getName() + " with " + args.length + " args: " + java.util.Arrays.toString(args));

            // Build the command line for tab completion
            String commandLine = command.getName();
            if (args.length > 0) {
                commandLine += " " + String.join(" ", args);
            }

            plugin.getLogger().info("Command line: " + commandLine);

            CommandSender pistonSender = convertSender(sender);

            // First try to get completions from the CommandExecutor itself
            List<String> executorCompletions = pistonExecutor.getTabCompletions(pistonSender, commandLine);
            if (!executorCompletions.isEmpty()) {
                plugin.getLogger().info("Using executor completions: " + executorCompletions);
                return executorCompletions;
            }

            // Fallback to the annotation-based tab completion system
            String[] parts = new String[args.length + 1];
            parts[0] = command.getName();
            System.arraycopy(args, 0, parts, 1, args.length);

            List<String> completions = getCommandTabCompletions(command.getName(), parts);
            plugin.getLogger().info("Using annotation-based completions: " + completions);
            return completions;
        }

        private CommandSender convertSender(org.bukkit.command.CommandSender bukkitSender) {
            // Convert Bukkit sender to Piston sender
            // This is a simplified conversion - you may need proper wrapper classes
            return new CommandSender() {
                @Override
                public void sendMessage(String message) {
                    bukkitSender.sendMessage(message);
                }

                @Override
                public boolean hasPermission(String permission) {
                    return bukkitSender.hasPermission(permission);
                }

                @Override
                public String getName() {
                    return bukkitSender.getName();
                }
            };
        }
    }

    /**
     * Helper class to store command method information for tab completion
     */
    private static class CommandMethodInfo
    {
        final CommandHandler handler;
        final java.lang.reflect.Method method;

        CommandMethodInfo(CommandHandler handler, java.lang.reflect.Method method)
        {
            this.handler = handler;
            this.method = method;
        }
    }
}
