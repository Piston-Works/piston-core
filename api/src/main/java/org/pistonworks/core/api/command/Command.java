package org.pistonworks.core.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a method as a command handler.
 * The method must be public and have CommandSender as the first parameter.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command
{
    /**
     * The name of the command. If empty, uses the method name.
     */
    String value() default "";

    /**
     * The name of the command. Alias for value().
     */
    String name() default "";

    /**
     * Command aliases.
     */
    String[] aliases() default {};

    /**
     * Command description for help.
     */
    String description() default "";

    /**
     * Command usage pattern (e.g., "/command <player> [amount]").
     */
    String usage() default "";

    /**
     * Required permission to execute this command.
     */
    String permission() default "";

    /**
     * Whether only players can execute this command.
     */
    boolean playerOnly() default false;

    /**
     * Whether only console can execute this command.
     */
    boolean consoleOnly() default false;
}
