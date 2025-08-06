package org.pistonworks.core.api.command;

import org.pistonworks.core.api.model.CommandSender;
import org.pistonworks.core.api.model.ConsoleCommandSender;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Internal class representing a registered command with all its metadata and execution logic.
 */
class RegisteredCommand
{
    final CommandHandler handler;
    private final Method method;
    private final Command annotation;
    private final List<CommandInfo.ArgumentInfo> arguments;
    private final DefaultCommandRegistry registry;
    private final Map<String, Method> completionMethods;

    RegisteredCommand(CommandHandler handler, Method method, Command annotation,
                     List<CommandInfo.ArgumentInfo> arguments, DefaultCommandRegistry registry,
                     Map<String, Method> completionMethods)
    {
        this.handler = handler;
        this.method = method;
        this.annotation = annotation;
        this.arguments = arguments;
        this.registry = registry;
        this.completionMethods = completionMethods;
    }

    boolean execute(CommandSender sender, String commandName, String[] args)
    {
        // Check permissions
        if (!annotation.permission().isEmpty() && !sender.hasPermission(annotation.permission()))
        {
            handler.onCommandError(sender, commandName,
                new CommandError(CommandError.ErrorType.NO_PERMISSION, "No permission"));
            return false;
        }

        // Check sender type restrictions
        if (annotation.playerOnly() && sender instanceof ConsoleCommandSender)
        {
            handler.onCommandError(sender, commandName,
                new CommandError(CommandError.ErrorType.PLAYER_ONLY, "Players only"));
            return false;
        }

        if (annotation.consoleOnly() && !(sender instanceof ConsoleCommandSender))
        {
            handler.onCommandError(sender, commandName,
                new CommandError(CommandError.ErrorType.CONSOLE_ONLY, "Console only"));
            return false;
        }

        // Pre-execution hook
        if (!handler.onBeforeCommand(sender, commandName, args))
        {
            return false;
        }

        try
        {
            // Parse arguments
            Object[] parsedArgs = parseArguments(sender, commandName, args);
            if (parsedArgs == null) return false; // Error already handled

            // Execute the command
            Object[] methodArgs = new Object[parsedArgs.length + 1];
            methodArgs[0] = sender;
            System.arraycopy(parsedArgs, 0, methodArgs, 1, parsedArgs.length);

            method.invoke(handler, methodArgs);

            // Post-execution hook
            handler.onAfterCommand(sender, commandName, args);

            return true;
        }
        catch (Exception e)
        {
            handler.onCommandError(sender, commandName,
                new CommandError(CommandError.ErrorType.EXECUTION_ERROR, e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            return false;
        }
    }

    List<String> getTabCompletions(CommandSender sender, String commandName, String[] args)
    {
        if (args.length == 0 || args.length > arguments.size())
        {
            return Collections.emptyList();
        }

        int argIndex = args.length - 1;
        CommandInfo.ArgumentInfo argInfo = arguments.get(argIndex);
        String currentArg = args[argIndex];

        // Get completions based on the argument type
        List<String> completions = getCompletionsForArgument(sender, commandName, args, argInfo, currentArg);

        // Filter completions that start with the current input
        return completions.stream()
            .filter(completion -> completion.toLowerCase().startsWith(currentArg.toLowerCase()))
            .sorted()
            .collect(Collectors.toList());
    }

    CommandInfo getCommandInfo()
    {
        return new CommandInfo(
            annotation.name().isEmpty() ? method.getName() : annotation.name(),
            Arrays.asList(annotation.aliases()),
            annotation.description(),
            generateUsage(),
            annotation.permission(),
            annotation.playerOnly(),
            annotation.consoleOnly(),
            arguments
        );
    }

    private Object[] parseArguments(CommandSender sender, String commandName, String[] args)
    {
        List<Object> parsedArgs = new ArrayList<>();

        for (int i = 0; i < arguments.size(); i++)
        {
            CommandInfo.ArgumentInfo argInfo = arguments.get(i);

            if (i >= args.length)
            {
                // Missing argument
                if (argInfo.isOptional())
                {
                    // Use default value
                    Object defaultValue = parseArgument(argInfo.getType(), argInfo.getDefaultValue());
                    if (defaultValue == null && !argInfo.getDefaultValue().isEmpty())
                    {
                        handler.onCommandError(sender, commandName,
                            new CommandError(CommandError.ErrorType.INVALID_ARGUMENTS,
                                "Invalid default value for argument: " + argInfo.getName(), generateUsage()));
                        return null;
                    }
                    parsedArgs.add(defaultValue);
                }
                else
                {
                    // Required argument missing
                    handler.onCommandError(sender, commandName,
                        new CommandError(CommandError.ErrorType.INVALID_ARGUMENTS,
                            "Missing required argument: " + argInfo.getName(), generateUsage()));
                    return null;
                }
            }
            else
            {
                // Parse the provided argument
                Object parsedArg = parseArgument(argInfo.getType(), args[i]);
                if (parsedArg == null && !args[i].isEmpty())
                {
                    handler.onCommandError(sender, commandName,
                        new CommandError(CommandError.ErrorType.INVALID_ARGUMENTS,
                            "Invalid " + argInfo.getType().getSimpleName() + " for argument: " + argInfo.getName(), generateUsage()));
                    return null;
                }
                parsedArgs.add(parsedArg);
            }
        }

        return parsedArgs.toArray();
    }

    private Object parseArgument(Class<?> type, String value)
    {
        if (value == null || value.isEmpty())
        {
            return getDefaultValueForType(type);
        }

        try
        {
            if (type == String.class)
            {
                return value;
            }
            else if (type == int.class || type == Integer.class)
            {
                return Integer.parseInt(value);
            }
            else if (type == double.class || type == Double.class)
            {
                return Double.parseDouble(value);
            }
            else if (type == float.class || type == Float.class)
            {
                return Float.parseFloat(value);
            }
            else if (type == long.class || type == Long.class)
            {
                return Long.parseLong(value);
            }
            else if (type == boolean.class || type == Boolean.class)
            {
                return Boolean.parseBoolean(value);
            }
            else
            {
                return value; // Default to string for unknown types
            }
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    private Object getDefaultValueForType(Class<?> type)
    {
        if (type == int.class) return 0;
        if (type == double.class) return 0.0;
        if (type == float.class) return 0.0f;
        if (type == long.class) return 0L;
        if (type == boolean.class) return false;
        return null; // For reference types
    }

    private List<String> getCompletionsForArgument(CommandSender sender, String commandName, String[] args,
                                                  CommandInfo.ArgumentInfo argInfo, String currentArg)
    {
        // Check for method-based completions first
        if (argInfo.getCompletionType() == Arg.CompletionType.METHOD && !argInfo.getCompletionMethod().isEmpty())
        {
            Method completionMethod = completionMethods.get(argInfo.getCompletionMethod());
            if (completionMethod != null)
            {
                try
                {
                    Object result = completionMethod.invoke(handler, sender, commandName, args, currentArg);
                    if (result instanceof List)
                    {
                        return (List<String>) result;
                    }
                }
                catch (Exception e)
                {
                    // Log error and fall back to empty list
                    return Collections.emptyList();
                }
            }
        }

        // Check for custom completions
        if (!argInfo.getCompletions().isEmpty())
        {
            return argInfo.getCompletions();
        }

        // Check for registered tab completers
        String completionTypeName = argInfo.getCompletionType().name().toLowerCase();
        if (registry.tabCompleters.containsKey(completionTypeName))
        {
            return registry.tabCompleters.get(completionTypeName).complete(sender, commandName, args, currentArg);
        }

        // Built-in completion types
        switch (argInfo.getCompletionType())
        {
            case BOOLEAN:
                return Arrays.asList("true", "false");
            case PLAYER:
            case ONLINE_PLAYER:
                // These would be implemented by the platform-specific implementation
                // For now, return empty list
                return Collections.emptyList();
            case WORLD:
                // Platform-specific implementation needed
                return Collections.emptyList();
            default:
                return Collections.emptyList();
        }
    }

    private String generateUsage()
    {
        if (!annotation.usage().isEmpty())
        {
            return annotation.usage();
        }

        StringBuilder usage = new StringBuilder();
        usage.append("/").append(annotation.name().isEmpty() ? method.getName() : annotation.name());

        for (CommandInfo.ArgumentInfo arg : arguments)
        {
            usage.append(" ");
            if (arg.isOptional())
            {
                usage.append("[").append(arg.getName()).append("]");
            }
            else
            {
                usage.append("<").append(arg.getName()).append(">");
            }
        }

        return usage.toString();
    }
}
