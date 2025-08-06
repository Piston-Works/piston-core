package org.pistonworks.core.api.command;

import org.pistonworks.core.api.model.CommandSender;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Default implementation of CommandRegistry that provides annotation-based command registration
 * with automatic argument parsing and tab completion.
 */
public class DefaultCommandRegistry implements CommandRegistry
{
    private final Map<String, RegisteredCommand> commands = new ConcurrentHashMap<>();
    private final Map<String, TabCompleter> tabCompleters = new ConcurrentHashMap<>();
    private final Map<CommandHandler, Set<String>> handlerCommands = new ConcurrentHashMap<>();

    public DefaultCommandRegistry()
    {
        // Register built-in tab completers
        registerTabCompleter("boolean", (sender, command, args, currentArg) ->
            Arrays.asList("true", "false").stream()
                .filter(s -> s.toLowerCase().startsWith(currentArg.toLowerCase()))
                .collect(Collectors.toList()));
    }

    @Override
    public void registerCommands(CommandHandler handler)
    {
        Class<?> clazz = handler.getClass();
        Set<String> registeredCommands = new HashSet<>();

        for (Method method : clazz.getDeclaredMethods())
        {
            if (method.isAnnotationPresent(Command.class))
            {
                Command cmdAnnotation = method.getAnnotation(Command.class);
                String commandName = getCommandName(cmdAnnotation, method.getName());

                RegisteredCommand registeredCommand = createRegisteredCommand(handler, method, cmdAnnotation);
                commands.put(commandName.toLowerCase(), registeredCommand);
                registeredCommands.add(commandName.toLowerCase());

                // Register aliases
                for (String alias : cmdAnnotation.aliases())
                {
                    commands.put(alias.toLowerCase(), registeredCommand);
                    registeredCommands.add(alias.toLowerCase());
                }
            }
        }

        handlerCommands.put(handler, registeredCommands);
    }

    @Override
    public void registerCommands(Class<? extends CommandHandler> handlerClass)
    {
        try
        {
            CommandHandler handler = handlerClass.getDeclaredConstructor().newInstance();
            registerCommands(handler);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to instantiate command handler: " + handlerClass.getName(), e);
        }
    }

    @Override
    public void unregisterCommands(CommandHandler handler)
    {
        Set<String> commandNames = handlerCommands.remove(handler);
        if (commandNames != null)
        {
            commandNames.forEach(commands::remove);
        }
    }

    @Override
    public void unregisterCommands(Class<? extends CommandHandler> handlerClass)
    {
        handlerCommands.entrySet().removeIf(entry -> {
            if (entry.getKey().getClass().equals(handlerClass))
            {
                entry.getValue().forEach(commands::remove);
                return true;
            }
            return false;
        });
    }

    @Override
    public void registerTabCompleter(String type, TabCompleter completer)
    {
        tabCompleters.put(type.toLowerCase(), completer);
    }

    @Override
    public boolean executeCommand(CommandSender sender, String commandLine)
    {
        String[] parts = commandLine.trim().split("\\s+");
        if (parts.length == 0) return false;

        String commandName = parts[0].toLowerCase();
        if (commandName.startsWith("/")) {
            commandName = commandName.substring(1);
        }

        RegisteredCommand registeredCommand = commands.get(commandName);
        if (registeredCommand == null) return false;

        String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];

        try
        {
            return registeredCommand.execute(sender, commandName, args);
        }
        catch (Exception e)
        {
            registeredCommand.handler.onCommandError(sender, commandName,
                new CommandError(CommandError.ErrorType.EXECUTION_ERROR, e.getMessage()));
            return false;
        }
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String commandLine)
    {
        String[] parts = commandLine.split("\\s+", -1); // -1 to preserve trailing empty strings
        if (parts.length == 0) return Collections.emptyList();

        String commandName = parts[0].toLowerCase();
        if (commandName.startsWith("/")) {
            commandName = commandName.substring(1);
        }

        // If we're still typing the command name
        if (parts.length == 1 && !commandLine.endsWith(" "))
        {
            return commands.keySet().stream()
                .filter(cmd -> cmd.startsWith(commandName))
                .sorted()
                .collect(Collectors.toList());
        }

        RegisteredCommand registeredCommand = commands.get(commandName);
        if (registeredCommand == null) return Collections.emptyList();

        String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[]{""};
        return registeredCommand.getTabCompletions(sender, commandName, args);
    }

    @Override
    public List<String> getRegisteredCommands()
    {
        return new ArrayList<>(commands.keySet());
    }

    @Override
    public CommandInfo getCommandInfo(String commandName)
    {
        RegisteredCommand registeredCommand = commands.get(commandName.toLowerCase());
        return registeredCommand != null ? registeredCommand.getCommandInfo() : null;
    }

    private RegisteredCommand createRegisteredCommand(CommandHandler handler, Method method, Command cmdAnnotation)
    {
        method.setAccessible(true);

        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0 || !CommandSender.class.isAssignableFrom(parameters[0].getType()))
        {
            throw new IllegalArgumentException("Command method must have CommandSender as first parameter: " + method.getName());
        }

        List<CommandInfo.ArgumentInfo> arguments = new ArrayList<>();
        for (int i = 1; i < parameters.length; i++)
        {
            Parameter param = parameters[i];
            Arg argAnnotation = param.getAnnotation(Arg.class);
            if (argAnnotation == null)
            {
                throw new IllegalArgumentException("All command parameters (except CommandSender) must be annotated with @Arg: " + param.getName());
            }

            arguments.add(new CommandInfo.ArgumentInfo(
                argAnnotation.value(),
                param.getType(),
                argAnnotation.optional(),
                argAnnotation.defaultValue(),
                argAnnotation.completionType(),
                Arrays.asList(argAnnotation.completions()),
                argAnnotation.completionMethod()
            ));
        }

        // Scan for @TabCompletion methods in the handler
        Map<String, Method> completionMethods = new HashMap<>();
        for (Method m : handler.getClass().getDeclaredMethods())
        {
            if (m.isAnnotationPresent(TabCompletion.class))
            {
                TabCompletion tabAnnotation = m.getAnnotation(TabCompletion.class);
                m.setAccessible(true);
                completionMethods.put(tabAnnotation.value(), m);
            }
        }

        return new RegisteredCommand(handler, method, cmdAnnotation, arguments, this, completionMethods);
    }

    private String getCommandName(Command cmdAnnotation, String methodName) {
        String name = cmdAnnotation.name();
        if (!name.isEmpty()) {
            return name;
        }
        return methodName;
    }
}
