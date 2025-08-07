package org.pistonworks.core.api.command;

import java.util.List;

/**
 * Contains metadata about a registered command.
 */
public class CommandInfo
{
    private final String name;
    private final List<String> aliases;
    private final String description;
    private final String usage;
    private final String permission;
    private final boolean playerOnly;
    private final boolean consoleOnly;
    private final List<ArgumentInfo> arguments;

    public CommandInfo(String name, List<String> aliases, String description, String usage,
                       String permission, boolean playerOnly, boolean consoleOnly, List<ArgumentInfo> arguments)
    {
        this.name = name;
        this.aliases = aliases;
        this.description = description;
        this.usage = usage;
        this.permission = permission;
        this.playerOnly = playerOnly;
        this.consoleOnly = consoleOnly;
        this.arguments = arguments;
    }

    public String getName()
    {
        return name;
    }

    public List<String> getAliases()
    {
        return aliases;
    }

    public String getDescription()
    {
        return description;
    }

    public String getUsage()
    {
        return usage;
    }

    public String getPermission()
    {
        return permission;
    }

    public boolean isPlayerOnly()
    {
        return playerOnly;
    }

    public boolean isConsoleOnly()
    {
        return consoleOnly;
    }

    public List<ArgumentInfo> getArguments()
    {
        return arguments;
    }

    /**
     * Information about a command argument.
     */
    public static class ArgumentInfo
    {
        private final String name;
        private final Class<?> type;
        private final boolean optional;
        private final String defaultValue;
        private final Arg.CompletionType completionType;
        private final List<String> completions;
        private final String completionMethod;

        public ArgumentInfo(String name, Class<?> type, boolean optional, String defaultValue,
                            Arg.CompletionType completionType, List<String> completions, String completionMethod)
        {
            this.name = name;
            this.type = type;
            this.optional = optional;
            this.defaultValue = defaultValue;
            this.completionType = completionType;
            this.completions = completions;
            this.completionMethod = completionMethod;
        }

        public String getName()
        {
            return name;
        }

        public Class<?> getType()
        {
            return type;
        }

        public boolean isOptional()
        {
            return optional;
        }

        public String getDefaultValue()
        {
            return defaultValue;
        }

        public Arg.CompletionType getCompletionType()
        {
            return completionType;
        }

        public List<String> getCompletions()
        {
            return completions;
        }

        public String getCompletionMethod()
        {
            return completionMethod;
        }
    }
}
