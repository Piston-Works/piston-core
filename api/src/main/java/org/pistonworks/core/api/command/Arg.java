package org.pistonworks.core.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define command argument metadata for automatic parsing and tab completion.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Arg
{
    /**
     * The name of the argument for usage display.
     */
    String value();

    /**
     * Whether this argument is optional.
     */
    boolean optional() default false;

    /**
     * Default value if the argument is not provided (only for optional arguments).
     */
    String defaultValue() default "";

    /**
     * Predefined completion values for tab completion.
     */
    String[] completions() default {};

    /**
     * Type of completion to use.
     */
    CompletionType completionType() default CompletionType.NONE;

    /**
     * Name of a custom completion method in the same CommandHandler class.
     * The method must be annotated with @TabCompletion and return List&lt;String&gt;.
     */
    String completionMethod() default "";

    /**
     * Types of tab completions available.
     */
    enum CompletionType
    {
        NONE,           // No special completion
        PLAYER,         // Complete player names
        ONLINE_PLAYER,  // Complete online player names only
        WORLD,          // Complete world names
        BOOLEAN,        // Complete true/false
        INTEGER,        // No completion, but validates as integer
        DOUBLE,         // No completion, but validates as double
        CUSTOM,         // Use the completions array
        METHOD          // Use a method in the same class annotated with @TabCompletion
    }
}
