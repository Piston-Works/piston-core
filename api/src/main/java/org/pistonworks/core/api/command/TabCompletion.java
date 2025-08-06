package org.pistonworks.core.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a method as providing tab completions for command arguments.
 * The method must return List&lt;String&gt; and can optionally take CommandSender, String command, String[] args, and String currentArg parameters.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TabCompletion
{
    /**
     * The name of the completion provider. Use this name in @Arg(completionType = CompletionType.CUSTOM, completions = {"providerName"})
     */
    String value();
}
