package org.pistonworks.core.api.command;

/**
 * Represents an error that occurred during command execution.
 */
public class CommandError
{
    private final ErrorType type;
    private final String message;
    private final String usage;

    public CommandError(ErrorType type, String message)
    {
        this(type, message, "");
    }

    public CommandError(ErrorType type, String message, String usage)
    {
        this.type = type;
        this.message = message;
        this.usage = usage;
    }

    public ErrorType getType()
    {
        return type;
    }

    public String getMessage()
    {
        return message;
    }

    public String getUsage()
    {
        return usage;
    }

    /**
     * Types of command errors.
     */
    public enum ErrorType
    {
        INVALID_ARGUMENTS,
        NO_PERMISSION,
        PLAYER_ONLY,
        CONSOLE_ONLY,
        EXECUTION_ERROR,
        UNKNOWN
    }
}
