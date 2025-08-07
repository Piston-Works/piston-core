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
     *
     * @return true if the argument is optional, false if it is required.
     */
    boolean optional() default false;

    /**
     * Default value if the argument is not provided (only for optional arguments).
     *
     * @return the default value as a string, or an empty string if not set.
     */
    String defaultValue() default "";

    /**
     * Predefined completion values for tab completion.
     *
     * @return an array of strings that will be used for tab completion.
     */
    String[] completions() default {};

    /**
     * Type of completion to use.
     *
     * @return the type of completion, which can be NONE, PLAYER, ONLINE_PLAYER, WORLD, BOOLEAN, INTEGER, DOUBLE, CUSTOM, or METHOD.
     */
    CompletionType completionType() default CompletionType.NONE;

    /**
     * Name of a custom completion method in the same CommandHandler class.
     * The method must be annotated with @TabCompletion and return List&lt;String&gt;.
     *
     * @return the name of the method to use for custom completions, or an empty string if not set.
     */
    String completionMethod() default "";

    /**
     * Types of tab completions available.
     */
    enum CompletionType
    {
        /**
         * No special completion
         */
        NONE,
        /**
         * Complete player names
         */
        PLAYER,
        /**
         * Complete online player names only
         */
        ONLINE_PLAYER,
        /**
         * Complete world names
         */
        WORLD,
        /**
         * Complete true/false
         */
        BOOLEAN,
        /**
         * No completion, but validates as integer
         */
        INTEGER,
        /**
         * No completion, but validates as double
         */
        DOUBLE,
        /**
         * Use the completion array
         */
        CUSTOM,
        /**
         * Use a method in the same class annotated with @TabCompletion
         */
        METHOD
    }
}
